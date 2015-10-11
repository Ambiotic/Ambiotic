package graphich.ambiotic.emitters;

import graphich.ambiotic.emitters.effects.FloatFadeIn;
import graphich.ambiotic.emitters.effects.FloatFadeOut;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.IConditional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LoopingSound extends MovingSound {
    protected FloatProvider mVolumeCalc;
    protected FloatProvider mPitchCalc;
    protected IConditional mScripted;
    protected FloatFadeOut mFadeOut;
    protected FloatFadeIn mFadeIn;

    protected LoopingSound(String sound, FloatProvider vcalc, FloatProvider pcalc, LoopingEmitter scripted, FloatFadeOut fadeOut, FloatFadeIn fadeIn) {
        super(new ResourceLocation(sound));
        mVolumeCalc = vcalc;
        mPitchCalc = pcalc;
        mScripted = scripted;
        repeat = true;
        this.field_147666_i = AttenuationType.NONE;
        this.volume = mVolumeCalc.value();
        mFadeOut = fadeOut;
        mFadeIn = fadeIn;
        if(this.volume <= 0.0f)
            this.volume = 0.0000001f;
        this.field_147663_c = mPitchCalc.value();
    }

    @Override
    public void update() {
        if(donePlaying)
            return;
        //Do NOT call conditionsMet() multiple times: it's expensive
        boolean conditonsMet = mScripted.conditionsMet();

        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        xPosF = (float)p.posX;
        //This forces the sound to be played in "mono"
        yPosF = (float)p.posY+5000;
        zPosF = (float)p.posZ;

        //We don't fade out
        if(!conditonsMet && mFadeOut == null) {
            volume = 0;
            donePlaying = true;
            return;
        }

        field_147663_c = mPitchCalc.value();
        volume = mVolumeCalc.value();

        // Apply fade out
        if(mFadeOut != null) {
            if(!conditonsMet)
                volume *= mFadeOut.value();
            else
                mFadeOut.reset();
        }

        // Apply fade in
        if(mFadeIn != null) {
            if(!conditonsMet)
                mFadeIn.reset();
            else
                volume *= mFadeIn.value();
        }

        if(volume <= 0.0f) {
            donePlaying = true;
            volume = 0.0f;
            return;
        }
    }
}
