package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;

/**
 * Is it raining in the world
 */
public final class IsRaining extends PlayerVariable {

    public IsRaining(String name) {
        super(name);
    }

    public IsRaining(String name, JsonObject json) throws JsonError { super(name, json); }

    @Override
    public boolean update(TickEvent event) {
        int newValue = mWorld.getWorldInfo().isRaining() ? 1 : 0;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
