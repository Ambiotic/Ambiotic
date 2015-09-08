package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class GameTime extends Variable {

    @SerializedName("Modulus")
    private int mModulus = 1;

    public GameTime(String name) {
        super(name);
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        // Default modulus
        if(mModulus == 0)
            mModulus = 1;
    }

    @Override
    public boolean update(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        int newValue = (int) (world.getWorldInfo().getWorldTime() % mModulus);
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
