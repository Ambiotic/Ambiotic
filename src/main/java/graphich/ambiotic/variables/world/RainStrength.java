package graphich.ambiotic.variables.world;


import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public class RainStrength extends VariableNumber {
    @SerializedName("Scalar")
    protected Integer mScalar;

    public RainStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
        //Default Scalar
        if (mScalar == null)
            mScalar = 1;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
            return false;
        float newValue = world.getWeightedThunderStrength(0f) * mScalar;
        return setNewValue(newValue);
    }
}
