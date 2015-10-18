package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
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
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import com.google.gson.*;

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

    //GSON Builder Init
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

    //Mod Logger
    protected static Logger logger;
    public static Logger logger() {
        return Ambiotic.logger;
    }

    //JS Engine
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

    //Engine configuration
    protected static JsonObject enginejson;
    public static JsonElement engineSection(String section)
    {
        if(enginejson == null)
            return null;
        if(enginejson.has(section))
            return enginejson.get(section);
        return null;
    }
    public static Boolean engineBoolean(String name, boolean def)
    {
        if(enginejson == null)
            return def;
        if(enginejson.has(name))
            return enginejson.get(name).getAsBoolean();
        return def;
    }
    public static String engineString(String name, String def)
    {
        if(enginejson == null)
            return def;
        if(enginejson.has(name))
            return enginejson.get(name).getAsString();
        return def;
    }

    //Hack to skip "first" reload, resources are always reloaded twice at startup
    protected boolean mPastFirstReload = false;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Ambiotic.logger = event.getModLog();
    }

    protected boolean passesPackCheck() {
        String pack = "ambiotic:engine.json";
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
    }

    protected void loadAll() {
        //Reload engine.json
        try {
            Ambiotic.logger().info("Loading engine.json");
            InputStreamReader reader = null;
            JsonParser parser = new JsonParser();
            reader = Helpers.resourceAsStreamReader(new ResourceLocation("ambiotic:engine.json"));
            enginejson = (parser.parse(reader)).getAsJsonObject();
        } catch (IOException ex) {
            Ambiotic.logger().error("Load aborting, could not read engine.json : "+ex.getMessage());
            return;
        }

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
        String envjs = Ambiotic.engineString("HelperJS", null);
        if(envjs != null) {
            Ambiotic.logger().info("Evaluating javascript helper file");
            try {
                ResourceLocation rl = new ResourceLocation(envjs);
                InputStreamReader isr = Helpers.resourceAsStreamReader(rl);
                Ambiotic.scripter.eval(isr);
            } catch (IOException ex) {
                Ambiotic.logger().error("Couldn't read helpers.js (" + envjs + ")");
            } catch (ScriptException ex) {
                Ambiotic.logger().error("Error when executing helpers.js:\n" + ex.getMessage());
            }
        }
        VariableRegistry.INSTANCE.initializeJSAll();
        ScannerRegistry.INSTANCE.initializeConstantJSAll();
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
}
