package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BiomeTemperature extends VariableNumber {

    public BiomeTemperature(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(world == null)
            return false;
        int x,y,z;
        x = (int)player.posX;
        y = (int)player.posY;
        z = (int)player.posZ;
        float newValue = world.getBiomeGenForCoords(x,z).getFloatTemperature(x, y, z);
        boolean changed = (Math.abs(mValue-newValue) < EQUALITY_LIMIT);
        mValue = newValue;
        return changed;
    }
}
