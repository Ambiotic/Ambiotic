package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Created by jim on 9/21/2014.
 */
public class TimeOfDay extends PlayerVariable {

    public TimeOfDay(String name) {
        super(name);
    }

    @Override
    public void update(TickEvent event) {
        mValue = (int) (mWorld.getWorldInfo().getWorldTime() % 23000);
    }
}
