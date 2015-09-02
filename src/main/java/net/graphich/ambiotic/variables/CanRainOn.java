package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public class CanRainOn extends PlayerVariable {
    public CanRainOn(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY + 2;
        z = (int) mPlayer.posZ;
        int newValue = (mWorld.canBlockSeeTheSky(x, y, z) && !(mWorld.getTopSolidOrLiquidBlock(x, z) > y)) ? 1 : 0;
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
