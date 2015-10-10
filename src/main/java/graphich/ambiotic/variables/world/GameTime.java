package graphich.ambiotic.variables.world;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class GameTime extends VariableNumber {

    @SerializedName("Modulus")
    private Integer mModulus = 1;

    public GameTime(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.WORLD_NAMESPACE;
        // Default modulus
        if(mModulus == null)
            mModulus = 1;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        if(world == null)
            return false;
        float newValue = (world.getWorldInfo().getWorldTime() % mModulus);
        boolean updated = (Math.abs(mValue-newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        return updated;
    }
}
