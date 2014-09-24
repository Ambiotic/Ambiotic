package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Fractional thunder strength multiplied by scalar (can be negative), values will be [0,scalar)
 */
public class ThunderStrength extends PlayerVariable {

    protected int mScalar;

    public ThunderStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
    }

    @Override
    public void update(TickEvent event) {
        mValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
    }
}
