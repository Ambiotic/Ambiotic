package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Is it raining in the world
 */
public class IsRaining extends PlayerVariable {

    public IsRaining(String name, EntityPlayer player) {
        super(name, player);
    }

    @Override
    public void update(TickEvent event) {
        mValue = mWorld.getWorldInfo().isRaining() ? 1 : 0;
    }
}
