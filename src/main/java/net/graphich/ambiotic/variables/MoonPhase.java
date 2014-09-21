package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * What is the current moon phase?
 */
public class MoonPhase extends Variable {

    public MoonPhase(String name) {
        super(name);
    }

    @Override
    public void update(TickEvent event) {
        mValue = mWorld.getMoonPhase();
    }
}
