package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * What is the current moon phase?
 */
public class MoonPhase extends PlayerVariable {

    public MoonPhase(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event)
    {
        int newValue = mWorld.getMoonPhase();
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
