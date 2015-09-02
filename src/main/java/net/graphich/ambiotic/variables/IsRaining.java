package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Is it raining in the world
 */
public class IsRaining extends PlayerVariable {

    public IsRaining(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event) {
        int newValue = mWorld.getWorldInfo().isRaining() ? 1 : 0;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
