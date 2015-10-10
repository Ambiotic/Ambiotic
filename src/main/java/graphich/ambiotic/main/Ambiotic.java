package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import graphich.ambiotic.emitters.SoundLoaderThread;
import graphich.ambiotic.registries.EmitterRegistry;
import graphich.ambiotic.registries.ScannerRegistry;
import graphich.ambiotic.registries.VariableRegistry;
import graphich.ambiotic.scanners.Scanner;
import graphich.ambiotic.emitters.SoundEmitter;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.util.DebugGui;
import graphich.ambiotic.util.EvalCommand;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.ShowOreDictCommand;
import graphich.ambiotic.variables.Variable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.command.CommandHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION, name = Ambiotic.NAME, acceptableRemoteVersions="*")
public class Ambiotic implements IResourceManagerReloadListener {

    public static final String MODID = "ambiotic";
    public static final String NAME = "Ambiotic";
    public static final String VERSION = "@VERSION@";

    protected static Logger logger;
    public static Logger logger() {
        return Ambiotic.logger;
    }

    protected static SoundLoaderThread soundloader;
    public static boolean soundLoaded(String sound) {
        if(soundloader == null)
            return false;
        return soundloader.soundLoaded(sound);
    }

    protected static ScriptEngine scripter;
    public static Object evalJS(String js)
    {
        try {
            return Ambiotic.scripter.eval(js);
        } catch(ScriptException ex) {
            Ambiotic.logger().error("Script failed\n"+js+"\n"+ex.getMessage());
        }
        return null;
    }

    //Hack to skipp "first" reload, resources are always reloaded twice
    protected boolean mPastFirstReload = false;

    // Flags for displaying messages on ticks
    protected boolean mWarnedOfLag = false;
    protected boolean mNotifiedLoadComplete = false;

    protected static final GsonBuilder gsonbuilder;
    public static Gson gson() {
        return Ambiotic.gsonbuilder.create();
    }
    static {
        gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Variable.class, Variable.STRICT_ADAPTER);
        gsonbuilder.registerTypeAdapter(SoundEmitter.class, SoundEmitter.STRICT_ADAPTER);
        gsonbuilder.registerTypeAdapter(FloatProvider.class, FloatProvider.STRICT_ADAPTER);
        gsonbuilder.registerTypeAdapter(Scanner.class, Scanner.STRICT_ADAPTER);
        gsonbuilder.setPrettyPrinting();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Ambiotic.logger = event.getModLog();
    }

    protected boolean passesPackCheck() {
        String pack = "ambiotic:ambiotic.json";
        try {
            ResourceLocation rl = new ResourceLocation(pack);
            InputStreamReader isr = Helpers.resourceAsStreamReader(rl);
        } catch(IOException ex) {
            Ambiotic.logger().warn("Cannot read ambiotic.json: no ambiotic information will be loaded.");
            return false;
        }
        //TODO: Actually check min_version / max_version
        return true;
    }

    @EventHandler
    protected void postInit(FMLPostInitializationEvent event) {
        if(!FMLCommonHandler.instance().getEffectiveSide().isClient())
            return;
        //We need to watch for when resources have been reloaded / refreshed
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
        ClientCommandHandler.instance.registerCommand(new EvalCommand());
        ClientCommandHandler.instance.registerCommand(new ShowOreDictCommand());
        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);
        FMLCommonHandler.instance().bus().register(this);
    }

    protected void loadAll() {
        // Only contiune with load if we pass the pack check
        if(!passesPackCheck())
            return;

        //Reset all registries
        ScannerRegistry.INSTANCE.reset();
        VariableRegistry.INSTANCE.reset();
        EmitterRegistry.INSTANCE.reset();

        //Load all registry data from json
        ScannerRegistry.INSTANCE.load();
        VariableRegistry.INSTANCE.load();
        EmitterRegistry.INSTANCE.load();

        // Subscribe all event handling classes
        VariableRegistry.INSTANCE.subscribeAll();
        ScannerRegistry.INSTANCE.subscribeAll();
        EmitterRegistry.INSTANCE.subscribeAll();

        //Initialize the scripting environment
        ScriptEngineManager man = new ScriptEngineManager(null);
        Ambiotic.scripter = man.getEngineByName("JavaScript");
        String envjs = "ambiotic:config/env.js";
        Ambiotic.logger().info("Evaluating javascript environment");
        try {
            ResourceLocation rl = new ResourceLocation(envjs);
            InputStreamReader isr = Helpers.resourceAsStreamReader(rl);
            Ambiotic.scripter.eval(isr);
        } catch(IOException ex) {
            Ambiotic.logger().error("Couldn't read env.js ("+envjs+")");
        } catch(ScriptException ex) {
            Ambiotic.logger().error("Error when executing env.js:\n"+ex.getMessage());
        }
        VariableRegistry.INSTANCE.initializeJSAll();
        ScannerRegistry.INSTANCE.initializeConstantJSAll();
        if(Ambiotic.soundloader == null) {
            Ambiotic.soundloader = new SoundLoaderThread(EmitterRegistry.INSTANCE.sounds());
            soundloader.start();
        }

    }

    @Override
    public void onResourceManagerReload(IResourceManager resman) {
        // Skip very first reload call, for some stupid reason reload is called twice during
        // plugin / mc init
        if(!mPastFirstReload) {
            mPastFirstReload = true;
            return;
        }
        loadAll();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player == null)
            return;
        if(!mWarnedOfLag && !soundloader.loadFinished()) {
            player.addChatMessage(new ChatComponentText("Ambiotic: Sounds are currently being loaded, expect lag and some sounds not to play"));
            mWarnedOfLag = true;
        }
        if(!mNotifiedLoadComplete && soundloader.loadFinished()) {
            player.addChatMessage(new ChatComponentText("Ambiotic: Sound load completed"));
            mNotifiedLoadComplete = true;
            FMLCommonHandler.instance().bus().unregister(this);
        }
    }
}
