package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public class RainStrength extends Variable {

    protected int mScalar;

    public RainStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
    }

    @Override
    public void update(TickEvent event) {
        mValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
    }
}
