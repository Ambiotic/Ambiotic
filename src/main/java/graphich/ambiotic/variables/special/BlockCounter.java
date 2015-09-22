package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.VariableInt;

import java.util.ArrayList;
import java.util.List;

public class BlockCounter extends VariableInt {

    @SerializedName("Blocks")
    protected String[] mBlockSpecifiers = null;
    @SerializedName("Scanner")
    protected String mScannerName = "";

    protected transient List<Integer> mBlockIds = null;
    protected transient BlockScanner mScanner = null;

    public BlockCounter(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if(mBlockSpecifiers == null || mBlockSpecifiers.length == 0)
            throw new StrictJsonException("Blocks list is required and must have at least one entry");
        if(mScannerName == null || mScannerName.equals(""))
            throw new StrictJsonException("Scanner is required");
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        //Transients
        mBlockIds = new ArrayList<Integer>();
        mNameSpace = mScannerName;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
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

    public List<String> linkToScanner(BlockScanner scanner) {
        mScanner = scanner;
        List<String> badSpecs = new ArrayList<String>();
        for(String spec : mBlockSpecifiers) {
            ArrayList<Integer> blockIds = Helpers.buildBlockIdList(spec);
            if(blockIds.size() == 0) {
                badSpecs.add(spec);
                continue;
            }
            mScanner.registerBlockIds(blockIds);
            addBlockIds(blockIds);
        }
        return badSpecs;
    }

    public String[] getBlockSpecs() {
        return mBlockSpecifiers;
    }

    public String getScannerName() {
        return mScannerName;
    }

    public void addBlockIds(List<Integer> blockIds) {
        if(mBlockIds == null)
            mBlockIds = new ArrayList<Integer>();
        for (Integer id : blockIds) {
            mBlockIds.add(id);
        }
    }
}
