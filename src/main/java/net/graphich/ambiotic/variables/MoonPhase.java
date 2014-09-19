package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * What is the current moon phase?
 */
public class MoonPhase extends PlayerVariable {

    public MoonPhase(String name, EntityPlayer player) {
        super(name, player);
    }

    @Override
    public void update(TickEvent event) {
        mValue = mWorld.getMoonPhase();
    }
}
