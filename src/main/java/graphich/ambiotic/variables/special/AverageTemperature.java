package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.variables.VariableNumber;

public class AverageTemperature extends VariableNumber {

    @SerializedName("Scanner")
    protected String mScannerName = "";

    protected transient BlockScanner mScanner;

    public AverageTemperature(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = mScannerName;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        int newValue = 0;
        if (mScanner == null)
            return false;
        boolean updated = (Math.abs(mValue-newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        return updated;
    }
}
