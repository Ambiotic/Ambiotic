package graphich.ambiotic.variables.world;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Fractional thunder strength multiplied by scalar (can be negative), values will be [0,scalar)
 */
public class ThunderStrength extends VariableNumber {

    @SerializedName("Scalar")
    protected Integer mScalar;

    public ThunderStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        //Default Scalar
        mNameSpace = Variable.WORLD_NAMESPACE;
        if(mScalar == null)
            mScalar = 1;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        float newValue = world.getWeightedThunderStrength(0f) * mScalar;
        return setNewValue(newValue);
    }

}
