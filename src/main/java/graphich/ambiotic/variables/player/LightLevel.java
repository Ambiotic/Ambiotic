package graphich.ambiotic.variables.player;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableInt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/**
 * Light value at player coordinates in the world, 3 types
 */
public class LightLevel extends VariableInt {

    @SerializedName("SubType")
    LightTypes mType;

    public LightLevel(String name, LightTypes type) {
        super(name);
        mType = type;
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public void validate() throws StrictJsonException {
        super.validate();
        if(mType == null)
            throw new StrictJsonException("No SubType specified, valid subtypes are SUN, LAMP, TOTAL, or MAXSUN");
    }

    @Override
    public boolean updateValue(TickEvent event) {
        int x, y, z, newValue;
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(world == null || player == null)
            return false;
        x = (int) player.posX;
        y = (int) player.posY;
        z = (int) player.posZ;
        newValue = 0;
        switch (mType) {
            case SUN:
                newValue = (int) (world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) * world.getSunBrightness(1.5f));
                break;
            case LAMP:
                newValue = world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
                break;
            case TOTAL:
                newValue = world.getBlockLightValue(x, y, z);
                break;
            case MAXSUN:
                newValue = (int) (world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
                break;
        }
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }

    public enum LightTypes {SUN, LAMP, TOTAL, MAXSUN}
}
