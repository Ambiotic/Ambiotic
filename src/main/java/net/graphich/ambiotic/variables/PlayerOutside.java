package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class PlayerOutside extends Variable {

    public PlayerOutside(String name) {
        super(name);
    }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z, n, newValue;
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(world == null || player == null)
            return false;
        x = (int) player.posX - 1;
        y = (int) player.posY - 1;
        z = (int) player.posZ - 1;
        n = 0;
        newValue = 0;
        for(int cx = x; cx < x + 2; cx++) {
            for(int cy = y; cy < y + 2; cy++) {
                for(int cz = z; cz < z + 2; cz++) {
                    // Check the light levels of non-solid blocks
                    if(!world.isAirBlock(cx,cy,cz))
                        continue;
                    newValue += world.getSavedLightValue(EnumSkyBlock.Sky, cx, cy, cz);
                    n += 1;
                }
            }
        }
        // Should never happen
        if(n == 0) {
            mValue = 0;
            return true;
        }
        newValue = newValue / n;
        if(newValue >= 10)
            newValue = 1;
        else
            newValue = 0;

        if(mValue == newValue)
            return false;
        mValue = newValue;
        return true;
    }
}
