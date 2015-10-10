package graphich.ambiotic.emitters;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatFadeIn;
import graphich.ambiotic.emitters.effects.FloatFadeOut;
import net.minecraft.client.audio.ISound;

public class LoopingEmitter extends SoundEmitter {

    @SerializedName("FadeOut")
    protected FloatFadeOut mFadeOut;
    @SerializedName("FadeIn")
    protected FloatFadeIn mFadeIn;

    protected transient LoopingSound mEmitted = null;

    public LoopingEmitter(String name, String sound) {
        super(name, sound);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
    }


    @Override
    public ISound emit() {
        if(conditionsMet() && mEmitted == null) {
            mEmitted = new LoopingSound(mSound, mVolume, mPitch, this, mFadeOut, mFadeIn);
            return mEmitted;
        }
        if(!conditionsMet())
            mEmitted = null;
        return null;
    }
}
