package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public class RainStrength extends PlayerVariable {

    protected int mScalar;

    public RainStrength(String name, EntityPlayer player, int scalar) {
        super(name, player);
        mScalar = scalar;
    }

    @Override
    public void update(TickEvent event) {
        mValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
    }
}
