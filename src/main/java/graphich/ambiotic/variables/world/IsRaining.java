package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableInt;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Is it raining in the world
 */
public class IsRaining extends VariableInt {

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
    }

    public IsRaining(String name) {
        super(name);
    }

    @Override
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = world.getWorldInfo().isRaining() ? 1 : 0;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
