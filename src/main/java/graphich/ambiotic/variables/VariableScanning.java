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
        linkToScanner(scanner);
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

    public void linkToScanner(BlockScanner scanner) {
        mScanner = scanner;
        mScannerName = scanner.name();
        mNameSpace = scanner.name();
    }

    public String getScannerName() {
        return mScannerName;
    }
}
