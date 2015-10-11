package graphich.ambiotic.emitters;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatFadeIn;
import graphich.ambiotic.emitters.effects.FloatFadeOut;
import net.minecraft.client.audio.ISound;

public class LoopingEmitter extends SoundEmitter {

    @SerializedName("FadeOut")
    protected Float mFadeOut;
    @SerializedName("FadeIn")
    protected Float mFadeIn;

    protected transient LoopingSound mEmitted = null;

    public LoopingEmitter(String name, String sound) {
        super(name, sound);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        // Default fades
        if(mFadeIn == null)
            mFadeIn = 0.01f;
        if(mFadeOut == null)
            mFadeOut = 0.01f;
    }


    @Override
    public ISound emit() {
        if(conditionsMet() && mEmitted == null) {
            mEmitted = new LoopingSound(mSound, mVolume, mPitch, this, mFadeOut, mFadeIn);
            return mEmitted;
        } else if(mEmitted != null) {
            if(!mEmitted.isDonePlaying())
                return null;
        }
        if(!conditionsMet())
            mEmitted = null;
        return null;
    }
}
