package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Fractional thunder strength multiplied by scalar (can be negative), values will be [0,scalar)
 */
public class ThunderStrength extends PlayerVariable {

    protected int mScalar;

    public ThunderStrength(String name, EntityPlayer player, int scalar) {
        super(name, player);
        mScalar = scalar;
    }

    @Override
    public void update(TickEvent event) {
        mValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
    }
}
