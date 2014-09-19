package net.graphich.ambiotic.main;

import java.security.SecureRandom;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.scanners.CuboidPointIterator;
import net.graphich.ambiotic.scanners.PointIterator;
import net.graphich.ambiotic.scanners.Point;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import org.python.util.PythonInterpreter;

import java.util.ArrayList;
import java.util.HashMap;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION)
public class Ambiotic {
    public static final String MODID = "Ambiotic";
    public static final String VERSION = "0.0.1";
    private PythonInterpreter mPyInterp = new PythonInterpreter();
    private long mTicksSinceUpdate = -1;
    private boolean mPlayerLoggedIn = false;
    private BlockScanner mScanner;
    public VariableRegistry mVariableRegistry;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        mScanner = new BlockScanner(4096);
        mScanner.registerBlocks("minecraft:stone");
        FMLCommonHandler.instance().bus().register(mScanner);
        MinecraftForge.EVENT_BUS.register(mScanner);
        FMLCommonHandler.instance().bus().register(VariableRegistry.instance());
        DebugGui gui = new DebugGui();
        MinecraftForge.EVENT_BUS.register(gui);
        FMLCommonHandler.instance().bus().register(gui);
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        gui.addScanner(mScanner);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

        if(event.phase != TickEvent.Phase.END)
            return;
        if(!mPlayerLoggedIn)
            return;
/*
        mTicksSinceUpdate += 1;
        if(mTicksSinceUpdate % 5 == 0 && mScanner.scanFinished()) {
            for(Integer blockId : mScanner.keySet()) {
                String name = GameData.getBlockRegistry().getObjectById(blockId).getUnlocalizedName();
                System.out.println(name+" : "+mScanner.getCount(blockId));
            }
            mTicksSinceUpdate = 0;
        }
*/
    }


    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        mPlayerLoggedIn = true;
    }

    /*
            PointIterator locations = new CuboidPointIterator(2,2,2,5,5,5);
        Point current = locations.next();
        int test = 5*5*10;
        int what = -1;
        while(current != null && ++what < test) {
            System.out.println("Point : "+current.toString());
            current = locations.next();
        }
        if(what > 5*5*5) {
            System.out.println("Bad iteration. : "+what);
        }
     */

}
