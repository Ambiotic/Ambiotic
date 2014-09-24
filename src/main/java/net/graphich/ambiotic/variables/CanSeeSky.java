package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Block at player position can see the sky
 */
public class CanSeeSky extends PlayerVariable {

    public CanSeeSky(String name) {
        super(name);
    }

    @Override
    public void update(TickEvent event) {
        int x, y, z;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY + 2;
        z = (int) mPlayer.posZ;
        mValue = mWorld.canBlockSeeTheSky(x, y, z) ? 1 : 0;
    }
}
