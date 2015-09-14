package net.graphich.ambiotic.sounds;

import net.graphich.ambiotic.main.Ambiotic;
import net.minecraft.client.audio.ISound;

public class LoopingEmitter extends SoundEmitter {

    protected transient LoopingSound mEmitted = null;
    protected transient boolean mActive;

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
            mEmitted = new LoopingSound(mSound, mVolume, mPitch, this);
            return mEmitted;
        }
        if(!conditionsMet())
            mEmitted = null;
        return null;
    }
}
