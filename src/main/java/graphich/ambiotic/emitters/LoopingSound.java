package graphich.ambiotic.emitters;

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

    protected LoopingSound(String sound, FloatProvider vcalc, FloatProvider pcalc, LoopingEmitter scripted) {
        super(new ResourceLocation(sound));
        mVolumeCalc = vcalc;
        mPitchCalc = pcalc;
        mScripted = scripted;
        repeat = true;
        this.field_147666_i = AttenuationType.NONE;
        this.volume = mVolumeCalc.value();
        if(this.volume == 0.0f)
            this.volume = 0.0000001f;
        this.field_147663_c = mPitchCalc.value();
    }

    @Override
    public void update() {
        if(donePlaying)
            return;
        if(!mScripted.conditionsMet()) {
            volume = 0;
            donePlaying = true;
            return;
        }
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        volume = mVolumeCalc.value();
        field_147663_c = mPitchCalc.value();
        xPosF = (float)p.posX;
        //This forces the sound to be played in "mono"
        yPosF = (float)p.posY+5000;
        zPosF = (float)p.posZ;
    }
}
