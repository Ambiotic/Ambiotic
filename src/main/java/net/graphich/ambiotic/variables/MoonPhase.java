package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * What is the current moon phase?
 */
public class MoonPhase extends Variable {

    public MoonPhase(String name) {
        super(name);
    }

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
