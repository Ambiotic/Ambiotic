package net.graphich.ambiotic.variables;


import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public class RainStrength extends VariableInt {

    @SerializedName("Scalar")
    protected Integer mScalar;

    public RainStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
        //Default Scalar
        if(mScalar == null)
            mScalar = 1;
    }

    @Override
    public boolean updateValue(TickEvent event)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = (int) world.getWeightedThunderStrength(0f) * mScalar;
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
