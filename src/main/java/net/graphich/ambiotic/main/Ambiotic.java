package net.graphich.ambiotic.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.minecraftforge.common.MinecraftForge;
import org.python.util.PythonInterpreter;
import org.apache.logging.log4j.Logger;


@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION, name = Ambiotic.NAME)
public class Ambiotic {

    public static final String MODID = "ambiotic";
    public static final String NAME = "Ambiotic";
    public static final String VERSION = "0.0.1";

    protected static Logger log;
    public static Logger logger() {return Ambiotic.log;}

    protected PythonInterpreter scripter;
    public static PythonInterpreter scripter() {return Ambiotic.scripter();}

    private Logger mLogger;
    private PythonInterpreter mScriptEnv;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Ambiotic.log = event.getModLog();
        mLogger = event.getModLog();
        mScriptEnv = new PythonInterpreter();
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ScannerRegistry.INSTANCE.load();
        VariableRegistry.INSTANCE.load();

        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);

        FMLCommonHandler.instance().bus().register(VariableRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(VariableRegistry.INSTANCE);

        FMLCommonHandler.instance().bus().register(ScannerRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ScannerRegistry.INSTANCE);
        mScriptEnv = new PythonInterpreter();
        VariableRegistry.INSTANCE.initScriptEnv(mScriptEnv);
    }
}
