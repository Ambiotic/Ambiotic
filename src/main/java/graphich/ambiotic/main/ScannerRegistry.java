package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import cpw.mods.fml.common.FMLCommonHandler;
import graphich.ambiotic.scanners.Scanner;
import graphich.ambiotic.util.StrictJsonException;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ScannerRegistry {
    public static final ScannerRegistry INSTANCE = new ScannerRegistry();

    protected HashMap<String, Scanner> mScanners;
    protected Logger mLogger;
    protected boolean mFrozen = false;

    protected ScannerRegistry() {
        mScanners = new HashMap<String, Scanner>();
    }

    public void register(Scanner scanner) {
        String name = scanner.name();
        if (mScanners.containsKey(name)) {
            //TODO: Log? Exception?
            return;
        }
        mScanners.put(name, scanner);
    }

    public void initializeConstantJSAll() {
        for (Scanner scanner : mScanners.values())
            Ambiotic.evalJS(scanner.constantsJS());
    }

    public void reset() {
        mFrozen = false;
        mScanners.clear();
    }

    public void load(Engine engine) {
        JsonArray scannerList = engine.section("Scanners").getAsJsonArray();
        if (scannerList == null)
            return;
        Ambiotic.logger().info("Loading scanner definitions");
        Gson gson = Ambiotic.gson();
        int scannerPos = 0;
        for (JsonElement scannerElm : scannerList) {
            Scanner scanner = null;
            try {
                scanner = gson.fromJson(scannerElm, Scanner.class);
            } catch (StrictJsonException ex) {
                String errPrefix = "Skipping variable # " + scannerPos + " because ";
                Ambiotic.logger().error(errPrefix + " it is invalid : " + ex.getMessage());
                continue;
            }
            register(scanner);
            scannerPos += 1;
        }
    }

    public void subscribeAll() {
        for (Scanner scanner : mScanners.values()) {
            FMLCommonHandler.instance().bus().register(scanner);
            MinecraftForge.EVENT_BUS.register(scanner);
        }
        mFrozen = true;
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mScanners.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public boolean isRegistered(String name) {
        return mScanners.containsKey(name);
    }

    public Scanner scanner(String name) {
        if (!mScanners.containsKey(name))
            return null;
        return mScanners.get(name);
    }
}
