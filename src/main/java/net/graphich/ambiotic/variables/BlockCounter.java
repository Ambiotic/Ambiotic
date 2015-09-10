package net.graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.util.StrictJsonException;

import java.util.ArrayList;
import java.util.List;

public class BlockCounter extends Variable {

    @SerializedName("Blocks")
    protected String[] mBlockSpecifiers = null;
    @SerializedName("Scanner")
    protected String mScannerName = "";

    protected transient List<Integer> mBlockIds = null;
    protected transient BlockScanner mScanner = null;

    public BlockCounter(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
        mBlockIds = new ArrayList<Integer>();
    }

    @Override
    public void validate() throws StrictJsonException {
        super.validate();
        if(mBlockSpecifiers == null)
            throw new StrictJsonException("Blocks list must be defined and not empty for BlockCounter variable");
        if(mScannerName == null || mScannerName.equals(""))
            throw new StrictJsonException("BlockCounter variable requires a Scanner to be defined");
    }

    @Override
    public void initialize() {
        super.initialize();
        //Transients
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

    public String[] getBlockSpecs() {
        return mBlockSpecifiers;
    }

    public String getScannerName() {
        return mScannerName;
    }

    public void setScanner(BlockScanner scanner) {
        mScanner = scanner;
    }

    public void addBlockIds(List<Integer> blockIds) {
        if(mBlockIds == null)
            mBlockIds = new ArrayList<Integer>();
        for (Integer id : blockIds) {
            mBlockIds.add(id);
        }
    }
}
