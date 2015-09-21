package net.graphich.ambiotic.variables.world;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.variables.Variable;
import net.graphich.ambiotic.variables.VariableInt;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class GameTime extends VariableInt {

    @SerializedName("Modulus")
    private Integer mModulus = 1;

    public GameTime(String name) {
        super(name);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
        // Default modulus
        if(mModulus == null)
            mModulus = 1;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = (int) (world.getWorldInfo().getWorldTime() % mModulus);
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
