package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;

/**
 * What is the current moon phase?
 */
public final class MoonPhase extends PlayerVariable {

    public MoonPhase(String name) {
        super(name);
    }

    public MoonPhase(String name, JsonObject json) throws JsonError { super(name, json); }

    @Override
    public boolean update(TickEvent event)
    {
        int newValue = mWorld.getMoonPhase();
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
