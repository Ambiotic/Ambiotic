package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.util.Helpers;
import net.graphich.ambiotic.util.StrictJsonException;

public class FloatRandom extends FloatProvider {
    @SerializedName("Min")
    protected float mMin = 0.0f;
    @SerializedName("Max")
    protected float mMax = 1.0f;

    public float value() {
        return Helpers.randomFloatInRange(mMin, mMax);
    }

    @Override
    public void validate() throws StrictJsonException {
        if(mMax <= mMin)
            throw new StrictJsonException("Max must be greater than Min");
    }
}
