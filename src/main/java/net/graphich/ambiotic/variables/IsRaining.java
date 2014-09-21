package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Is it raining in the world
 */
public class IsRaining extends Variable {

    public IsRaining(String name) {
        super(name);
    }

    @Override
    public void update(TickEvent event) {
        mValue = mWorld.getWorldInfo().isRaining() ? 1 : 0;
    }
}
