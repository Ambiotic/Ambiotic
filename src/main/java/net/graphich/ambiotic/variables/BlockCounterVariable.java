package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.graphich.ambiotic.registries.ScannerRegistry;
import net.graphich.ambiotic.scanners.BlockScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jim on 9/23/2014.
 */
public final class BlockCounterVariable extends Variable {

    List<Integer> mBlockIds = null;
    BlockScanner mScanner = null;

    public BlockCounterVariable(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
        mBlockIds = new ArrayList<Integer>();
    }

    public BlockCounterVariable(String name, JsonObject json) throws JsonError {
        super(name);
        mBlockIds = new ArrayList<Integer>();
        if(!json.has("Scanner"))
            throw new JsonMissingRequiredField("Scanner");
        if(!json.has("Blocks"))
            throw new JsonMissingRequiredField("Blocks");

        JsonElement cur = json.get("Scanner");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Scanner", "string");
        String scannerName = cur.getAsString();
        if(!ScannerRegistry.INSTANCE.isRegistered(scannerName))
            throw new JsonError("No scanner registered with name '"+scannerName+"'");
        mScanner = ScannerRegistry.INSTANCE.scanner(scannerName);

        cur = json.get("Blocks");
        if(!cur.isJsonArray())
            throw new JsonInvalidTypeForField("Blocks","list of strings");
        int elmNo = 0;
        for(JsonElement elm : cur.getAsJsonArray()) {
            if(!elm.isJsonPrimitive() || !elm.getAsJsonPrimitive().isString()) {
                Ambiotic.logger().warn("Skipping invalid Blocks list element ("+elmNo+") for variable '"+name+"'");
                continue;
            }
            String blocksDef = elm.getAsString();
            List<Integer> blockIds = mScanner.registerBlocks(blocksDef);
            if(blockIds.size() == 0) {
                Ambiotic.logger().warn("Ignoring bad block definition '"+blocksDef+" for variable '"+name+"'");
                continue;
            }
            elmNo += 1;
            addBlockIds(blockIds);
        }
    }

    @Override
    public boolean update(TickEvent event) {
        int newValue = 0;
        if (mScanner == null)
            return false;
        for (Integer id : mBlockIds) {
            newValue += mScanner.getCount(id);
        }
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }

    public void addBlockIds(List<Integer> blockIds) {
        for (Integer id : blockIds) {
            mBlockIds.add(id);
        }
    }
}
