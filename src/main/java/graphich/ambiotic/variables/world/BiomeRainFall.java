package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BiomeRainFall extends VariableNumber {

    public BiomeRainFall(String name) {
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
        float newValue =world.getBiomeGenForCoords((int)player.posX, (int)player.posZ).getFloatRainfall();
        boolean changed = (Math.abs(mValue-newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        return changed;
    }
}
