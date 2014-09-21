package net.graphich.ambiotic.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.variables.*;
import net.minecraftforge.common.MinecraftForge;
import org.python.util.PythonInterpreter;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION)
public class Ambiotic {

    public static final String MODID = "Ambiotic";
    public static final String VERSION = "0.0.1";

    private PythonInterpreter mPyInterp = new PythonInterpreter();


    @EventHandler
    public void init(FMLInitializationEvent event) {

        FMLCommonHandler.instance().bus().register(VariableRegistry.instance());
        MinecraftForge.EVENT_BUS.register(VariableRegistry.instance());

        FMLCommonHandler.instance().bus().register(ScannerRegistry.instance());
        MinecraftForge.EVENT_BUS.register(ScannerRegistry.instance());

        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);

        initVariables();
    }

    public void initVariables() {
        int tpt = 1;
        VariableRegistry vr = VariableRegistry.instance();
        vr.register(new CanRainOn("CanRainOn"), tpt);
        vr.register(new CanSeeSky("CanSeeSky"), tpt);
        vr.register(new IsRaining("IsRaining"), tpt);
        vr.register(new LightLevel("NaturalLight", LightLevel.LightTypes.SUN), tpt);
        vr.register(new LightLevel("TorchLight", LightLevel.LightTypes.LAMP), tpt);
        vr.register(new LightLevel("TotalLight", LightLevel.LightTypes.TOTAL), tpt);
        vr.register(new MoonPhase("MoonPhase"), tpt);
        vr.register(new PlayerCoordinate("PlayerX", PlayerCoordinate.Coordinates.X), tpt);
        vr.register(new PlayerCoordinate("PlayerY", PlayerCoordinate.Coordinates.Y), tpt);
        vr.register(new PlayerCoordinate("PlayerZ", PlayerCoordinate.Coordinates.Z), tpt);
        vr.register(new PlayerCoordinate("PlayerDim", PlayerCoordinate.Coordinates.DIM), tpt);
        vr.register(new RainStrength("RainStrength", 1000), tpt);
        vr.register(new ThunderStrength("ThunderStrength", 1000), tpt);
    }
}
