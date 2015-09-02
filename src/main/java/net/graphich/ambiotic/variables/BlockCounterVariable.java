package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.scanners.BlockScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jim on 9/23/2014.
 */
public class BlockCounterVariable extends Variable {

    List<Integer> mBlockIds = null;
    BlockScanner mScanner = null;

    public BlockCounterVariable(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
        mBlockIds = new ArrayList<Integer>();
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
