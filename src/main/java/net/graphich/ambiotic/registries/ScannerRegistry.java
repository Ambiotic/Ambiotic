package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.main.AmbioticJsonError;
import net.graphich.ambiotic.scanners.BlockScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import org.json.simple.parser.ParseException;

/**
 * Created by jim on 9/19/2014.
 */
public class ScannerRegistry {

    public static final ScannerRegistry INSTANCE = new ScannerRegistry();

    protected HashMap<String, BlockScanner> mScanners;
    protected Logger mLogger;

    protected ScannerRegistry() {
        mScanners = new HashMap<String, BlockScanner>();
    }

    public void register(String name, BlockScanner scanner) {
        if (mScanners.containsKey(name)) {
            //Log? Exception?
            return;
        }
        mScanners.put(name, scanner);
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/scanners.json");
        JsonParser parser = new JsonParser();
        JsonObject json = null;
        Ambiotic.logger().info("Loading scanners file '"+rl+"'");
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
            try {
                BlockScanner bs = BlockScanner.deserialize(name,scanner.getValue().getAsJsonObject());
                register(name,bs);
                FMLCommonHandler.instance().bus().register(bs);
                MinecraftForge.EVENT_BUS.register(bs);
            } catch(AmbioticJsonError ex) {
                Ambiotic.logger().warn("Skipping scanner '"+name+"' : "+ex.getMessage());
            }
        }
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mScanners.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public BlockScanner scanner(String name) {
        if (!mScanners.containsKey(name)) {
            return null;
        }
        return mScanners.get(name);
    }
}
