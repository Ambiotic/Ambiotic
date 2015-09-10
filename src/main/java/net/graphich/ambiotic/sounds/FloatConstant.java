package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.util.StrictJsonException;

public class FloatConstant extends FloatProvider {
    @SerializedName("Value")
    protected float mValue;

    public FloatConstant(float value) {
        mValue = value;
    }

    @Override
    public float value() {
        return mValue;
    }
}
