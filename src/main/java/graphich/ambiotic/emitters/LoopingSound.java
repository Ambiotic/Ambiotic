package graphich.ambiotic.emitters;

import graphich.ambiotic.emitters.effects.FloatFadeIn;
import graphich.ambiotic.emitters.effects.FloatFadeOut;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.util.IConditional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LoopingSound extends MovingSound {
    protected FloatProvider mVolumeCalc;
    protected FloatProvider mPitchCalc;
    protected IConditional mScripted;
    protected float mFadeOut;
    protected float mFadeIn;
    protected float mFadeFactor;

    protected LoopingSound(String sound, FloatProvider vcalc, FloatProvider pcalc, LoopingEmitter scripted, float fadeOut, float fadeIn) {
        super(new ResourceLocation(sound));
        mVolumeCalc = vcalc;
        mPitchCalc = pcalc;
        mScripted = scripted;
        mFadeOut = fadeOut;
        mFadeIn = fadeIn;
        mFadeFactor = fadeIn;
        repeat = true;
        this.field_147666_i = AttenuationType.NONE;
        this.update();
    }

    @Override
    public void update() {
        if(donePlaying)
            return;

        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        xPosF = (float)p.posX;
        //This forces the sound to be played in "mono"
        yPosF = (float)p.posY+5000;
        zPosF = (float)p.posZ;

        field_147663_c = mPitchCalc.value();
        volume = mVolumeCalc.value();

        // Apply fade in / fade out
        if(!mScripted.conditionsMet()) {
            mFadeFactor -= mFadeOut;
            if(mFadeFactor <= 0.0f)
                mFadeFactor = 0.0f;
        } else {
            mFadeFactor += mFadeIn;
            if(mFadeFactor >= 1.0f)
                mFadeFactor = 1.0f;
        }
        volume *= mFadeFactor;

        if(volume <= 0.0f) {
            donePlaying = true;
            volume = 0.0f;
            return;
        }
    }
}
