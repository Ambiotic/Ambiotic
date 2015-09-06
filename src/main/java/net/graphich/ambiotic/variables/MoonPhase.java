package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * What is the current moon phase?
 */
public final class MoonPhase extends Variable {

    public MoonPhase(String name) { super(name); }

    public MoonPhase(String name, JsonObject json) throws JsonError { super(name, json); }

    @Override
    public boolean update(TickEvent event)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = world.getMoonPhase();
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
