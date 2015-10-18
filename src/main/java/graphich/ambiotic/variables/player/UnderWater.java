package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class UnderWater extends VariableBool {

    public UnderWater(String name) {
        super(name);
        initialize();
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
        boolean newValue = false;
        if(block instanceof BlockLiquid)
            newValue = true;
        return setNewValue(newValue);
    }
}
