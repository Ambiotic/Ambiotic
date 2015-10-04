package graphich.ambiotic.emitters;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatConstant;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.util.IScripted;
import graphich.ambiotic.variables.macro.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.Map;

public class InstantEmitter extends SoundEmitter {

    @SerializedName("CoolDown")
    protected FloatProvider mCoolDown;

    protected transient int mSinceEmission = 0;
    protected transient int mNextEmission = 0;

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
        // So instants don't fire automatically as soon as conditions
        // are met after long periods of conditions not being met
        if(mNextEmission == -1 || mSinceEmission/2.0 > mNextEmission) {
            mSinceEmission = 0;
            mNextEmission = (int) mCoolDown.value();
        }

        if(mSinceEmission < mNextEmission)
            return null;

        EntityPlayer ply = Minecraft.getMinecraft().thePlayer;
        //TODO: x/y/z dynamically calculated
        float x = (float)ply.posX;
        float y = (float)ply.posY;
        float z = (float)ply.posZ;
        float v = mVolume.value();
        float p = mPitch.value();
        if(conditionsMet()) {
            mSinceEmission = 0;
            mNextEmission = (int)mCoolDown.value();
            return new InstantSound(mSound, x, y, z, p, v);
        }
        return null;
    }
}
