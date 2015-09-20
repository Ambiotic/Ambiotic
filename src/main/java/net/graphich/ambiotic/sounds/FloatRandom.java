package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.util.Helpers;
import net.graphich.ambiotic.util.StrictJsonException;

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
        if(mMin == null)
            throw new StrictJsonException(COMMON_NAMES+" : Min is required");
        if(mMax == null)
            throw new StrictJsonException(COMMON_NAMES+" : Max is required");
        if(mMax <= mMin)
            throw new StrictJsonException(COMMON_NAMES+" : Max must be greater than Min");
    }
}
