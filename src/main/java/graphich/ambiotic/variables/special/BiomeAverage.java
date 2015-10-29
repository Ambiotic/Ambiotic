package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.VariableScanning;

public class BiomeAverage extends VariableScanning {
    @SerializedName("SubType")
    protected AverageTypes mType;

    public BiomeAverage(String name, BlockScanner scanner) {
        super(name, scanner);
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if (mType == null)
            throw new StrictJsonException("SubType is required and must be one of " + AverageTypes.names);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = mScannerName;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        if (mScanner == null)
            return false;
        float newValue = 0.0f;
        switch (mType) {
            case HUMIDITY:
                newValue = mScanner.averageHumidity();
                break;
            case TEMPERATURE:
                newValue = mScanner.averageTemperature();
                break;
            case SALINITY:
                newValue = mScanner.averageSalinity();
                break;
            case MAXSUN:
                newValue = mScanner.averageSunLevel();
                break;
        }
        return setNewValue(newValue);
    }

    public enum AverageTypes {
        HUMIDITY, TEMPERATURE, SALINITY, MAXSUN;
        public static String names = "HUMIDITY, TEMPERATURE, SALINITY, MAXSUN";
    }
}
