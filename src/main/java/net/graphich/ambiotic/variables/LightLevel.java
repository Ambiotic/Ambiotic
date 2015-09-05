package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.minecraft.world.EnumSkyBlock;

/**
 * Light value at player coordinates in the world, 3 types
 */
public final class LightLevel extends PlayerVariable {

    LightTypes mType;

    public LightLevel(String name, LightTypes type) {
        super(name);
        mType = type;
    }

    public LightLevel(String name, JsonObject json) throws JsonError {
        super(name, json);
        if(!json.has("SubType"))
            throw new JsonMissingRequiredField("SubType");
        JsonElement subtype = json.get("SubType");
        if(!subtype.isJsonPrimitive() || !subtype.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Subtype","string");

        String lighttype = subtype.getAsString();
        if(lighttype.equals("LAMP"))
            mType = LightTypes.LAMP;
        else if(lighttype.equals("SUN"))
            mType = LightTypes.SUN;
        else if(lighttype.equals("MAXSUN"))
            mType = LightTypes.MAXSUN;
        else if(lighttype.equals("TOTAL"))
            mType = LightTypes.TOTAL;
        else
            throw new JsonError("Invalid subtype for light level '"+lighttype+"'");
    }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z, newValue;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY;
        z = (int) mPlayer.posZ;
        newValue = 0;
        switch (mType) {
            case SUN:
                newValue = (int) (mWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) * mWorld.getSunBrightness(1.5f));
                break;
            case LAMP:
                newValue = mWorld.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
                break;
            case TOTAL:
                newValue = mWorld.getBlockLightValue(x, y, z);
                break;
            case MAXSUN:
                newValue = (int) (mWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
                break;
        }
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }

    public enum LightTypes {SUN, LAMP, TOTAL, MAXSUN}
}
