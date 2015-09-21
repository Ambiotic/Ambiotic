package graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.StrictJsonException;

public class FloatRandom extends FloatProvider {
    @SerializedName("Min")
    protected Float mMin = 0.0f;
    @SerializedName("Max")
    protected Float mMax = 1.0f;

    public float value() {
        return Helpers.randomFloatInRange(mMin, mMax);
    }

    @Override
    public void validate() throws StrictJsonException {
        String prefix = "Random "+COMMON_NAMES;
        if(mMin == null)
            throw new StrictJsonException(prefix+" : Min is required");
        if(mMax == null)
            throw new StrictJsonException(prefix+" : Max is required");
        if(mMax <= mMin)
            throw new StrictJsonException(prefix+" : Max must be greater than Min");
    }
}
