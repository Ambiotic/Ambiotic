package net.graphich.ambiotic.sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LoopingSound extends MovingSound {
    protected FloatProvider mVolumeCalc;
    protected FloatProvider mPitchCalc;
    protected LoopingEmitter mScripted;

    protected LoopingSound(String sound, FloatProvider vcalc, FloatProvider pcalc, LoopingEmitter scripted) {
        super(new ResourceLocation(sound));
        mVolumeCalc = vcalc;
        mPitchCalc = pcalc;
        mScripted = scripted;
        repeat = true;
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
        yPosF = (float)p.posY;
        zPosF = (float)p.posZ;
    }
}
