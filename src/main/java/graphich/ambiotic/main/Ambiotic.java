package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import graphich.ambiotic.registries.EmitterRegistry;
import graphich.ambiotic.registries.ScannerRegistry;
import graphich.ambiotic.registries.VariableRegistry;
import graphich.ambiotic.scanners.Scanner;
import graphich.ambiotic.sounds.SoundEmitter;
import graphich.ambiotic.sounds.FloatProvider;
import graphich.ambiotic.util.DebugGui;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.variables.Variable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION, name = Ambiotic.NAME, acceptableRemoteVersions="*")
public class Ambiotic {

    public static final String MODID = "ambiotic";
    public static final String NAME = "Ambiotic";
    public static final String VERSION = "0.0.2";

    protected static Logger logger;
    public static Logger logger() {
        return Ambiotic.logger;
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

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //Load all registry data from json
        ScannerRegistry.INSTANCE.load();
        VariableRegistry.INSTANCE.load();
        EmitterRegistry.INSTANCE.load();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);

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
    }
}
