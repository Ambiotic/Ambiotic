package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Block at player position can see the sky
 */
public class CanSeeSky extends Variable {

    public CanSeeSky(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;
        if(player == null || world == null)
            return false;
        x = (int) player.posX;
        y = (int) player.posY + 2;
        z = (int) player.posZ;
        int newValue = world.canBlockSeeTheSky(x, y, z) ? 1 : 0;
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
