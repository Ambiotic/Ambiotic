package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableString;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BiomeName extends VariableString {

    public BiomeName(String name) {
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

        String newValue = world.getBiomeGenForCoords((int)player.posX, (int)player.posZ).biomeName;
        boolean changed = (newValue != mValue);
        mValue = newValue;
        return changed;
    }
}
