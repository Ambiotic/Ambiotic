package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.scanners.BlockScanner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.minecraft.client.Minecraft;
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
        JsonParser parser = new JsonParser();
        JsonObject json = null;
        Ambiotic.logger().info("Loading scanners file '" + rl + "'");
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            json = parser.parse(isr).getAsJsonObject();
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '"+rl+"' : "+ex.getMessage());
            return;
        }
        for(Map.Entry<String, JsonElement> scanner : json.entrySet())
        {
            String name = scanner.getKey();
            Ambiotic.logger().info("Loading scanner '"+name+"'");
            if(!scanner.getValue().isJsonObject()) {
                Ambiotic.logger().warn("Skipping scanner '" + name + "' it is not an object");
                continue;
            }
            try {
                BlockScanner bs = BlockScanner.deserialize(name,scanner.getValue().getAsJsonObject());
                register(bs);
            } catch(JsonError ex) {
                Ambiotic.logger().warn("Skipping scanner '"+name+"' : "+ex.getMessage());
            }
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
