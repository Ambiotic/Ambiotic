package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public class CanRainOn extends PlayerVariable {
    public CanRainOn(String name, EntityPlayer player) {
        super(name, player);
    }

    @Override
    public void update(TickEvent event) {
        int x, y, z;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY + 2;
        z = (int) mPlayer.posZ;
        mValue = (mWorld.canBlockSeeTheSky(x, y, z) && !(mWorld.getTopSolidOrLiquidBlock(x, z) > y)) ? 1 : 0;
    }
}
