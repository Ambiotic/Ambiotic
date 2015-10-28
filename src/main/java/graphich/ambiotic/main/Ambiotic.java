package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import graphich.ambiotic.emitters.SoundEmitter;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.scanners.Scanner;
import graphich.ambiotic.util.EvalCommand;
import graphich.ambiotic.util.ShowOreDictCommand;
import graphich.ambiotic.variables.Variable;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystemConfig;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION, name = Ambiotic.NAME, acceptableRemoteVersions="*")
public class Ambiotic {

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

    //Message to notify player about resource pack needed
    protected static final String RESPACK_NOTE =
        "Ambiotic: no engine has been loaded\n" +
        "You may need an ambiotic resource pack\n"+
        "Or your ambiotic resource pack maybe corrupt (check logs)\n";
    //Message for silly user who installed on server
    protected static final String SERVER_NOTE =
        "Aborting startup, this mod only runs on the client, installing on the server has no effect.";

    //Engine configuration
    protected static Engine engine;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Ambiotic.logger = event.getModLog();
    }

    @EventHandler
    protected void postInit(FMLPostInitializationEvent event) {
        if(!FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            Ambiotic.logger().error(Ambiotic.SERVER_NOTE);
            return;
        }
        //We need to watch for when resources have been reloaded / refreshed
        engine = new Engine();
        ClientCommandHandler.instance.registerCommand(new EvalCommand());
        ClientCommandHandler.instance.registerCommand(new ShowOreDictCommand());
        MinecraftForge.EVENT_BUS.register(this);
        SoundSystemConfig.setNumberStreamingChannels(10);
        SoundSystemConfig.setNumberNormalChannels(22);
        Ambiotic.logger().info("Setting normal channels to 22, streaming channels to 10");
    }

    @SubscribeEvent
    public void playerJoin(EntityJoinWorldEvent event) {
        if(!(event.entity instanceof EntityClientPlayerMP) || event.isCanceled())
            return;
        if(!engine.loaded()) {
            EntityClientPlayerMP player = (EntityClientPlayerMP)event.entity;
            for(String line : RESPACK_NOTE.split("\n")) {
                player.addChatMessage(new ChatComponentText(line));
            }
        }
        //Don't annoy the player, unregister us so the message never shows again
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
