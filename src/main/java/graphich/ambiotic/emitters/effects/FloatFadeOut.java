package graphich.ambiotic.emitters.effects;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.StrictJsonException;

public class FloatFadeOut extends FloatProvider {
    @SerializedName("FadeFactor")
    protected Float mFadeFactor;
    @SerializedName("InitialValue")
    protected Float mInitialValue;

    protected transient float mValue;

    @Override
    public float value() {
        mValue -= mFadeFactor;
        if(mValue < 0.0f)
            mValue = 0.0f;
        return mValue;
    }

    @Override
    public void validate() throws StrictJsonException {
        if(mFadeFactor == null)
            throw new StrictJsonException("FadeFactor is required");
        if(mFadeFactor > 1.0)
            throw new StrictJsonException("FadeFactor cannot be greater than 1.0");
        if(mInitialValue != null && (mInitialValue <= 0.0f || mInitialValue > 1.0f))
            throw new StrictJsonException("InitialValue must be greater than 0 and smaller than or equal to 1.0");
    }

    @Override
    public void initialize() {
        super.initialize();
        if(mInitialValue == null)
            mInitialValue = 1.0f;
        mValue = mInitialValue;
    }

    public void reset() {
        initialize();
    }
}
