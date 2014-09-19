package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Block at player position can see the sky
 */
public class CanSeeSky extends PlayerVariable {

    public CanSeeSky(String name, EntityPlayer player) {
        super(name, player);
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
