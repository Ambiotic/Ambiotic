package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.util.StrictJsonException;

public class FloatConstant extends FloatProvider {
    @SerializedName("Value")
    protected Float mValue;

    public FloatConstant(float value) {
        mValue = value;
    }

    @Override
    public void validate() throws StrictJsonException {
        if(mValue == null);
            throw new StrictJsonException("Value is required");
    }

    @Override
    public float value() {
        return mValue;
    }
}
