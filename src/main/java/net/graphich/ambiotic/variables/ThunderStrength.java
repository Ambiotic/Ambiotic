package net.graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Fractional thunder strength multiplied by scalar (can be negative), values will be [0,scalar)
 */
public class ThunderStrength extends VariableInt {

    @SerializedName("Scalar")
    protected Integer mScalar;

    public ThunderStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        //Default Scalar
        mNameSpace = Variable.WORLD_NAMESPACE;
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
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }

}
