package net.graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.graphich.ambiotic.registries.SoundRegistry;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.Scanner;
import net.graphich.ambiotic.sounds.SoundEmitter;
import net.graphich.ambiotic.sounds.FloatProvider;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import net.graphich.ambiotic.variables.Variable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION, name = Ambiotic.NAME)
public class Ambiotic {

    public static final String MODID = "ambiotic";
    public static final String NAME = "Ambiotic";
    public static final String VERSION = "0.0.2";

    //Utilities : logger, script engine, json builder
    protected static Logger logger;
    public static Logger logger() {
        return Ambiotic.logger;
    }

    protected static ScriptEngine scripter;
    public static ScriptEngine scripter() {
        return Ambiotic.scripter;
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
        ScriptEngineManager man = new ScriptEngineManager();
        Ambiotic.logger = event.getModLog();
        Ambiotic.scripter = man.getEngineByName("JavaScript");

        //Load all registry data from json
        ScannerRegistry.INSTANCE.load();
        VariableRegistry.INSTANCE.load();
        SoundRegistry.INSTANCE.load();
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);

        // Subscribe all event handling classes
        VariableRegistry.INSTANCE.subscribeAll();
        ScannerRegistry.INSTANCE.subscribeAll();
        SoundRegistry.INSTANCE.subscribeAll();

        //Initialize the scripting environment
        VariableRegistry.INSTANCE.refreshScripter();
    }
}
