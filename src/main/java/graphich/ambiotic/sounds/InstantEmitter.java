package graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;

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
    public void initialize() {
        //Default cooldown
        if(mCoolDown == null)
            mCoolDown = new FloatConstant(10000.0f);
        mNextEmission = (int)mCoolDown.value();
    }

    public ISound emit() {
        mSinceEmission += 1;
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
