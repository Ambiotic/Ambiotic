package net.graphich.ambiotic.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.variables.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.python.util.PythonInterpreter;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        FMLCommonHandler.instance().bus().register(VariableRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(VariableRegistry.INSTANCE);

        FMLCommonHandler.instance().bus().register(ScannerRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ScannerRegistry.INSTANCE);
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ScannerRegistry.INSTANCE.load();
        initVariables();
        initJAMs();
        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);
    }

    protected void initJAMs() {
        String jamPath = "assets/"+Ambiotic.MODID+"/config/variables.json";
        JSONParser parser = new JSONParser();
        ResourceLocation loc = new ResourceLocation(Ambiotic.MODID,"config/variables.json");
        Map json = null;
        try {
            InputStreamReader reader = new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream());
            json = (Map) parser.parse(reader);
        } catch (IOException ex) {
            mLogger.warn("Skipping '" + jamPath + "' IO Error: " + ex.getMessage());
            return;
        } catch (ParseException ex) {
            mLogger.warn("Skipping '" + jamPath + "' Parse Error: " + ex.getMessage());
            return;
        }
        Map variableDefs = (Map) json.get("Variables");
        if (variableDefs != null) {
            // We parse block counters first
            parseBlockCounters(variableDefs);
        }
    }

    protected void parseBlockCounters(Map json) {
        Set<String> keys = (Set<String>) json.keySet();
        BlockScanner bsc = null;
        for (String name : keys) {
            Map blockCounter = (Map) json.get(name);
            if (blockCounter == null) {
                continue;
            }
            String type = (String) blockCounter.get("Type");
            // Skipping typeless variables and non-blockCounter variables
            if (type == null || !type.equals("BlockCounter")) {
                continue;
            }
            mLogger.info("Parsing block counter '" + name + "'");
            String scanner = (String) blockCounter.get("Scanner");
            if (scanner == null) {
                mLogger.warn("Skipping '" + name + "' missing required key 'Scanner'");
                continue;
            }
            bsc = ScannerRegistry.INSTANCE.scanner(scanner);
            if (bsc == null) {
                mLogger.warn("Skipping '" + name + "' no such scanner : '" + scanner + "'");
                continue;
            }
            List<String> blocks = (List<String>) blockCounter.get("Blocks");
            if (blocks == null) {
                mLogger.warn("Skipping '" + name + "' missing required key 'Blocks'");
                continue;
            }

            BlockCounterVariable bcv = new BlockCounterVariable(name, bsc);
            for (String block : blocks) {
                List<Integer> ids = bsc.registerBlocks(block);
                if (ids.size() == 0) {
                    mLogger.warn("Skipping bad block key '" + block + "' for counter '" + name + "'");
                    continue;
                }
                bcv.addBlockIds(ids);
            }
            VariableRegistry.INSTANCE.register(bcv, 1);
        }
    }

    protected void initVariables() {
        //Need to read from main config;
        int ticksPerUpdate = 1;
        int scale = 1000;

        VariableRegistry vr = VariableRegistry.INSTANCE;
        vr.register(new CanRainOn("CanRainOn"), ticksPerUpdate);
        vr.register(new CanSeeSky("CanSeeSky"), ticksPerUpdate);
        vr.register(new IsRaining("IsRaining"), ticksPerUpdate);
        vr.register(new LightLevel("MaximumSunLevel", LightLevel.LightTypes.MAXSUN), ticksPerUpdate);
        vr.register(new LightLevel("SunLightLevel", LightLevel.LightTypes.SUN), ticksPerUpdate);
        vr.register(new LightLevel("TorchLight", LightLevel.LightTypes.LAMP), ticksPerUpdate);
        vr.register(new LightLevel("TotalLight", LightLevel.LightTypes.TOTAL), ticksPerUpdate);
        vr.register(new MoonPhase("MoonPhase"), ticksPerUpdate);
        vr.register(new PlayerCoordinate("PlayerX", PlayerCoordinate.Coordinates.X), ticksPerUpdate);
        vr.register(new PlayerCoordinate("PlayerY", PlayerCoordinate.Coordinates.Y), ticksPerUpdate);
        vr.register(new PlayerCoordinate("PlayerZ", PlayerCoordinate.Coordinates.Z), ticksPerUpdate);
        vr.register(new PlayerCoordinate("PlayerDim", PlayerCoordinate.Coordinates.DIM), ticksPerUpdate);
        vr.register(new RainStrength("RainStrength", scale), ticksPerUpdate);
        vr.register(new ThunderStrength("ThunderStrength", scale), ticksPerUpdate);
        vr.register(new TimeOfDay("TimeOfDay"), 480);
        mScriptEnv = new PythonInterpreter();
        vr.initScriptEnv(mScriptEnv);
    }

}
