package net.graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.variables.Variable;
import net.graphich.ambiotic.variables.VariableInt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public class CanRainOn extends VariableInt {

    public CanRainOn(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
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