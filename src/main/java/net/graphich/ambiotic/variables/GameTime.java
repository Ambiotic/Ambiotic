package net.graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class GameTime extends VariableInt {

    @SerializedName("Modulus")
    private Integer mModulus = 1;

    public GameTime(String name) {
        super(name);
    }

    @Override
    public void initialize() {
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
