package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.VariableNumber;
import graphich.ambiotic.variables.VariableScanning;

import java.util.ArrayList;
import java.util.List;

public class BlockCounter extends VariableScanning {

    @SerializedName("Blocks")
    protected String[] mBlockSpecifiers = null;

    protected transient List<Integer> mBlockIds = null;
    protected transient List<String> mBadBlockSpecs = null;

    public BlockCounter(String name, BlockScanner scanner) {
        super(name, scanner);
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if(mBlockSpecifiers == null || mBlockSpecifiers.length == 0)
            throw new StrictJsonException("Blocks list is required and must have at least one entry");
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        //Transients
        mBlockIds = new ArrayList<Integer>();
        mBadBlockSpecs = new ArrayList<String>();
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
        return setNewValue(newValue);
    }

    @Override //VariableScanning
    public void linkToScanner(BlockScanner scanner) {
        mScanner = scanner;
        mScannerName = scanner.name();
        mBlockIds.clear();
        mBadBlockSpecs.clear();

        for(String spec : mBlockSpecifiers) {
            ArrayList<Integer> blockIds = Helpers.buildBlockIdList(spec);
            if(blockIds.size() == 0) {
                mBadBlockSpecs.add(spec);
                continue;
            }
            mScanner.registerBlockIds(blockIds);
            addBlockIds(blockIds);
        }
    }

    public String[] getBlockSpecs() {
        return mBlockSpecifiers;
    }

    public void addBlockIds(List<Integer> blockIds) {
        if(mBlockIds == null)
            mBlockIds = new ArrayList<Integer>();
        for (Integer id : blockIds) {
            if(!mBlockIds.contains(id))
                mBlockIds.add(id);
        }
    }
}
