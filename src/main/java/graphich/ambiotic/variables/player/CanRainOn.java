package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * At the player's current coordinates in the world, can she be rained on?
 */
public class CanRainOn extends VariableBool {
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
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;
        if (player == null || world == null)
            return false;
        x = (int) player.posX;
        y = (int) player.posY + 2;
        z = (int) player.posZ;
        boolean newValue = (world.canBlockSeeTheSky(x, y, z) && !(world.getTopSolidOrLiquidBlock(x, z) > y)) ? true : false;
        return setNewValue(newValue);
    }
}
