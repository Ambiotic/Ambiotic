package net.graphich.ambiotic.registries;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import javafx.scene.effect.InnerShadow;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jim on 9/19/2014.
 */
public class ScannerRegistry {

    protected static ScannerRegistry INSTANCE = new ScannerRegistry();

    protected HashMap<String, BlockScanner> mScanners;

    protected ScannerRegistry() {
        mScanners = new HashMap<String, BlockScanner>();
        initBuiltIns();
    }

    public static ScannerRegistry instance() {return INSTANCE;}

    public void register(String name, BlockScanner scanner) {
        if(mScanners.containsKey(name)) {
            //Log? Exception?
            return;
        }
        mScanners.put(name,scanner);
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mScanners.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public BlockScanner scanner(String name) {
        if(!mScanners.containsKey(name)) {
            //Log? Exception?
            return null;
        }
        return mScanners.get(name);
    }

    public void initBuiltIns(){
        BlockScanner s = new BlockScanner( (64*16*64)/4, 64, 16, 64);
        s.registerBlocks("minecraft:stone");
        s.registerBlocks("minecraft:dirt");
        register("Large", s);
        FMLCommonHandler.instance().bus().register(s);
        MinecraftForge.EVENT_BUS.register(s);

        //register("Small", new BlockScanner(  (16*4*16)/4, 16,  4, 16) );
        // Just for testing
        //scanner("Large").registerBlocks("woodLog");

    }
}
