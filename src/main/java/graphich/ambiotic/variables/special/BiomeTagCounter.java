package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.VariableScanning;
import net.minecraftforge.common.BiomeDictionary;

public class BiomeTagCounter extends VariableScanning {
    protected static String tagnames;
    static {
        tagnames = null;
        for (BiomeDictionary.Type type : BiomeDictionary.Type.values()) {
            if (tagnames == null)
                tagnames = type.name();
            else
                tagnames += ", " + type.name();
        }
    }

    @SerializedName("Tag")
    protected BiomeDictionary.Type mTag;

    public BiomeTagCounter(String name, BlockScanner scanner) {
        super(name, scanner);
        initialize();
    }

    @Override
    public void validate() throws StrictJsonException {
        super.validate();
        if (mTag == null)
            throw new StrictJsonException("Tag is required and must be one of " + tagnames);
    }

    @Override
    public boolean updateValue(TickEvent event) {
        float newValue = mScanner.biomeTagCount(mTag);
        return setNewValue(newValue);
    }
}
