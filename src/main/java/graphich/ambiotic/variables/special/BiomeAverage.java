package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.variables.VariableScanning;

public class BiomeAverage extends VariableScanning {

    @SerializedName("SubType")
    protected AverageTypes mType;

    public BiomeAverage(String name, BlockScanner scanner) {
        super(name, scanner);
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = mScannerName;
    }

    @Override
    public void linkToScanner(BlockScanner scanner) {
        mScanner = scanner;
        mScannerName = scanner.name();
        mNameSpace = scanner.name();
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        if (mScanner == null)
            return false;
        float newValue = 0.0f;
        switch(mType) {
            case HUMIDITY:    newValue = mScanner.averageHumidity(); break;
            case TEMPERATURE: newValue = mScanner.averageTemperature(); break;
            case SALINITY:    newValue = mScanner.averageSalinity(); break;
        }
        return setNewValue(newValue);
    }

    public enum AverageTypes {HUMIDITY, TEMPERATURE, SALINITY}
}
