package net.graphich.ambiotic.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.registries.VariableRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.scanners.ComplementsPointIterator;
import net.graphich.ambiotic.scanners.Cuboid;
import net.graphich.ambiotic.scanners.Point;
import net.minecraftforge.common.MinecraftForge;
import org.python.util.PythonInterpreter;

@Mod(modid = Ambiotic.MODID, version = Ambiotic.VERSION)
public class Ambiotic {
    public static final String MODID = "Ambiotic";
    public static final String VERSION = "0.0.1";

    private PythonInterpreter mPyInterp = new PythonInterpreter();
    private long mTicksSinceUpdate = -1;
    private boolean mPlayerLoggedIn = false;

    //public VariableRegistry mVariableRegistry;
    //private BlockScanner mScanner;

    @EventHandler
    public void init(FMLInitializationEvent event) {
/*        mScanner = new BlockScanner(4096);
        mScanner.registerBlocks("minecraft:stone");
        FMLCommonHandler.instance().bus().register(mScanner);
        MinecraftForge.EVENT_BUS.register(mScanner);*/

        FMLCommonHandler.instance().bus().register(VariableRegistry.instance());
        MinecraftForge.EVENT_BUS.register(VariableRegistry.instance());

        FMLCommonHandler.instance().bus().register(ScannerRegistry.instance());
        MinecraftForge.EVENT_BUS.register(ScannerRegistry.instance());

        DebugGui gui = new DebugGui();
        FMLCommonHandler.instance().bus().register(gui);
        MinecraftForge.EVENT_BUS.register(gui);

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        Cuboid test1 = new Cuboid(new Point(0,0,0), new Point(10,10,10));
        Cuboid test2 = test1.translated(1, 2, 3);
        Cuboid test3 = test1.intersection(test2);
        ComplementsPointIterator c = new ComplementsPointIterator(test1,test3);
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {

        if (event.phase != TickEvent.Phase.END)
            return;
        if (!mPlayerLoggedIn)
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
