package graphich.ambiotic.emitters;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class InstantSound extends MovingSound {

    protected InstantSound(String sound, float x, float y, float z, float p, float v) {
        super(new ResourceLocation(sound));
        this.volume = v;
        this.field_147663_c = p;
        this.xPosF = (float)x;
        this.yPosF = (float)y;
        this.zPosF = (float)z;
    }

    @Override
    public void update() {

    }
}
