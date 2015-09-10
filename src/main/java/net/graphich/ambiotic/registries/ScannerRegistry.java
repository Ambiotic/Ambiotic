package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.Helpers;
import net.graphich.ambiotic.scanners.BlockScanner;

import java.util.*;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

public class ScannerRegistry {

    public static final ScannerRegistry INSTANCE = new ScannerRegistry();

    protected HashMap<String, BlockScanner> mScanners;
    protected Logger mLogger;

    protected ScannerRegistry() {
        mScanners = new HashMap<String, BlockScanner>();
    }

    public void register(BlockScanner scanner) {
        String name = scanner.name();
        if(mScanners.containsKey(name)) {
            //Log? Exception?
            return;
        }
        mScanners.put(name, scanner);
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/scanners.json");
        JsonArray scannerList = null;
        Ambiotic.logger().info("Loading scanners file '" + rl + "'");
        try {
            scannerList = Helpers.getRootJsonArray(rl);
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '" + rl + "' : "+ex.getCause().getMessage());
            return;
        }
        //Deserialize and register each scanner
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        int scannerPos = 0;
        for(JsonElement scannerElm : scannerList) {
            String errPrefix = "Skipping variable # " + scannerPos + " because ";
            BlockScanner scanner = null;
            try {
                scanner = gson.fromJson(scannerElm, BlockScanner.class);
                scanner.validate();
            } catch (JsonParseException ex) {
                Ambiotic.logger().error(errPrefix+" of parse error : "+ex.getCause().getMessage());
                continue;
            } catch (Exception ex) {
                Ambiotic.logger().error(errPrefix+" it's invalid : "+ex.getCause().getMessage());
                continue;
            }
            register(scanner);
            scannerPos += 1;
        }
    }

    public void subscribeAll() {
        for(BlockScanner scanner : mScanners.values()) {
            FMLCommonHandler.instance().bus().register(scanner);
            MinecraftForge.EVENT_BUS.register(scanner);
        }
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mScanners.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public boolean isRegistered(String name) { return mScanners.containsKey(name); }

    public BlockScanner scanner(String name) {
        if (!mScanners.containsKey(name)) {
            return null;
        }
        return mScanners.get(name);
    }
}
