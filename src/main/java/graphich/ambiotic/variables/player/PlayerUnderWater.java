package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerUnderWater extends VariableBool {

    public PlayerUnderWater(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int x = (int)Math.floor(player.posX);
        int y = (int)Math.floor(player.posY + player.getEyeHeight());
        int z = (int)Math.floor(player.posZ);
        Block block = Minecraft.getMinecraft().theWorld.getBlock(x,y,z);
        boolean newValue;
        if(block instanceof BlockLiquid)
            newValue = true;
        else
            newValue = false;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
