package net.graphich.ambiotic.registries;

import net.graphich.ambiotic.scanners.BlockScanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jim on 9/19/2014.
 */
public class ScannerRegistry {

    public static ScannerRegistry INSTANCE = new ScannerRegistry();

    protected HashMap<String, BlockScanner> mScanners;

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
