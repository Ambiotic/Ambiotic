package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public class RainStrength extends PlayerVariable {

    protected int mScalar;

    public RainStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
    }

    @Override
    public boolean update(TickEvent event)
    {
        int newValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
