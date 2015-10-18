package graphich.ambiotic.emitters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;

public class LoopingEmitter extends SoundEmitter {

    protected transient BackgroundSound mEmitted = null;

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
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        if(mEmitted != null && !handler.isSoundPlaying(mEmitted))
            mEmitted = null;
        if(conditionsMet() && mEmitted == null) {
            mEmitted = new BackgroundSound(mSound, mVolume, mPitch, this, mFadeOut, mFadeIn);
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
