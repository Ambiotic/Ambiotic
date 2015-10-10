package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Is it raining in the world
 */
public class IsRaining extends VariableBool {

    public IsRaining(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        boolean newValue = world.getWorldInfo().isRaining() ? true : false;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
