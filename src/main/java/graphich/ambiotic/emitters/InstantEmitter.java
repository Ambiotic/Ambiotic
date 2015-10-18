package graphich.ambiotic.emitters;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatConstant;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.util.IScripted;
import graphich.ambiotic.variables.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;

import java.util.Map;

public class InstantEmitter extends SoundEmitter {

    @SerializedName("CoolDown")
    protected FloatProvider mCoolDown;

    protected transient int mSinceEmission = 0;
    protected transient int mNextEmission = 0;
    protected transient BackgroundSound mLastEmitted = null;

    public InstantEmitter(String name, String sound, String condition) {
        super(name, sound);
        mConditionCode = condition;
    }

    @Override
    public void expandMacros(Map<String,Macro> macros) {
        super.expandMacros(macros);
        if(mCoolDown instanceof IScripted)
            ((IScripted) mCoolDown).expandMacros(macros);
    }
    @Override
    public void initialize() {
        super.initialize();
        //Default cooldown
        if(mCoolDown == null)
            mCoolDown = new FloatConstant(10000.0f);
        mNextEmission = -1;
    }

    public ISound emit() {
        mSinceEmission += 1;

        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        if(mLastEmitted != null && !handler.isSoundPlaying(mLastEmitted))
            mLastEmitted = null;

        if(mNextEmission == -1)
            mNextEmission = (int) mCoolDown.value();

        if(mSinceEmission < mNextEmission)
            return null;

        if(mSinceEmission >= mNextEmission) {
            mSinceEmission = 0;
            mNextEmission = (int) mCoolDown.value();
        }

        if(conditionsMet() && mLastEmitted == null) {
            mSinceEmission = 0;
            mNextEmission = (int)mCoolDown.value();
            mLastEmitted = new BackgroundSound(mSound,mVolume,mPitch,this,mFadeOut,mFadeIn,false);
            return mLastEmitted;
        }
        return null;
    }
}
