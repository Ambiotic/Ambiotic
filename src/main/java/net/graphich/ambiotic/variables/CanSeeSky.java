package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Block at player position can see the sky
 */
public class CanSeeSky extends VariableInt {

    public CanSeeSky(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
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
