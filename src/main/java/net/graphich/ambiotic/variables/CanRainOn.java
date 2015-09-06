package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public final class CanRainOn extends Variable {
    public CanRainOn(String name) {
        super(name);
    }

    public CanRainOn(String name, JsonObject json) throws JsonError { super(name, json); }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z;
        EntityPlayer player =  Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;
        if(player == null || world == null)
            return false;
        x = (int) player.posX;
        y = (int) player.posY + 2;
        z = (int) player.posZ;
        int newValue = (world.canBlockSeeTheSky(x, y, z) && !(world.getTopSolidOrLiquidBlock(x, z) > y)) ? 1 : 0;
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
