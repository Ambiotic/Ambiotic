package net.graphich.ambiotic.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.variables.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.python.util.PythonInterpreter;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION)
public class Ambiotic {

    public static final String MODID = "Ambiotic";
    public static final String VERSION = "0.0.1";

    private Configuration mConfiguration;
    private org.apache.logging.log4j.Logger mLogger;
    private PythonInterpreter mPyInterp = new PythonInterpreter();


    @EventHandler
    public void init(FMLInitializationEvent event) {

        FMLCommonHandler.instance().bus().register(VariableRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(VariableRegistry.INSTANCE);

        FMLCommonHandler.instance().bus().register(ScannerRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(ScannerRegistry.INSTANCE);

        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        mLogger = event.getModLog();
        mConfiguration = new Configuration(event.getSuggestedConfigurationFile());
        mConfiguration.load();
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        initScanners();
        initVariables();
        initJAMs();
    }

    protected void initJAMs() {
        String[] jamPaths = new String[]{"config/default.jam"};
        jamPaths = mConfiguration.getStringList("JAMPaths","Variables", jamPaths, "A list of file paths to JSON encoded ambiotic machine configurations.");
        mConfiguration.save();
        JSONParser parser = new JSONParser();
        for (String jamPath : jamPaths) {
            parser.reset();
            Map json = null;
            try {
                json = (Map) parser.parse(new FileReader(jamPath));
            } catch (IOException ex) {
                mLogger.warn("Skipping '" + jamPath + "' IO Error: " + ex.getMessage());
                continue;
            } catch (ParseException ex) {
                mLogger.warn("Skipping '" + jamPath + "' Parse Error: " + ex.getMessage());
                continue;
            }
            Map variableDefs = (Map) json.get("Variables");
            if (variableDefs != null) {
                // We parse block counters first
                parseBlockCounters(variableDefs);
            }
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
        int ticksPerUpdate = mConfiguration.get("Variables", "TicksPerUpdate", 1).getInt();
        int scale = mConfiguration.get("Variables", "Scalar", 1000).getInt();

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
    }

    protected void initScanners() {
        ScannerRegistry sr = ScannerRegistry.INSTANCE;

        int xsize = mConfiguration.get("Scanners", "LargeX", 64).getInt();
        int ysize = mConfiguration.get("Scanners", "LargeY", 16).getInt();
        int zsize = mConfiguration.get("Scanners", "LargeZ", 64).getInt();
        mConfiguration.save();

        BlockScanner bs = new BlockScanner((xsize * ysize * zsize) / 4, xsize, ysize, zsize);
        sr.register("Large", bs);
        FMLCommonHandler.instance().bus().register(bs);
        MinecraftForge.EVENT_BUS.register(bs);
    }
}
