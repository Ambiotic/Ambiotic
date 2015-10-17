package graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.util.StrictJsonException;

public abstract class VariableScanning extends VariableNumber {

    @SerializedName("Scanner")
    protected String mScannerName = "";

    protected transient BlockScanner mScanner = null;

    public VariableScanning(String name, BlockScanner scanner) {
        super(name);
        mScanner = scanner;
        mScannerName = scanner.name();
        initialize();
    }
    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if(mScannerName == null || mScannerName.equals(""))
            throw new StrictJsonException("Scanner is required");
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        //Transients
        mNameSpace = mScannerName;
    }

    public abstract void linkToScanner(BlockScanner scanner);

    public String getScannerName() {
        return mScannerName;
    }
}
