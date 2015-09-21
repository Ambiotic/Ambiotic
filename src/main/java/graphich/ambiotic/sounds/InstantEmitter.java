package graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.variables.macro.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Collection;
import java.util.List;
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
    public void expandMacros(Collection<Macro> macros) {
        super.expandMacros(macros);
        if(mCoolDown instanceof IScripted)
            ((IScripted) mCoolDown).expandMacros(macros);
    }
    @Override
    public void initialize() {
        //Default cooldown
        if(mCoolDown == null)
            mCoolDown = new FloatConstant(10000.0f);
        mNextEmission = -1;
    }

    public ISound emit() {
        mSinceEmission += 1;
        if(mNextEmission == -1 || mSinceEmission/2.0 > mNextEmission)
            mNextEmission = (int)mCoolDown.value();

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
