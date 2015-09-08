package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Is it raining in the world
 */
public class IsRaining extends Variable {

    public IsRaining(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = world.getWorldInfo().isRaining() ? 1 : 0;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
