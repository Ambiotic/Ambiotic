package graphich.ambiotic.variables.world;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * What is the current moon phase?
 */
public class MoonPhase extends VariableNumber {

    public MoonPhase(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        float newValue = world.getMoonPhase();
        boolean updated = (Math.abs(mValue-newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        return updated;
    }
}
