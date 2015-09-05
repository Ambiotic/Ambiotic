package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public final class CanRainOn extends PlayerVariable {
    public CanRainOn(String name) {
        super(name);
    }

    public CanRainOn(String name, JsonObject json) throws JsonError { super(name, json); }

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
