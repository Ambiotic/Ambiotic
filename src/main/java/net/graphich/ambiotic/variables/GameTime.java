package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * Created by jim on 9/21/2014.
 */
public final class GameTime extends Variable {

    private int mModulus = 1;

    public GameTime(String name) {
        super(name);
    }

    public GameTime(String name, JsonObject json) throws JsonError {
        super(name, json);
        if(!json.has("Modulus"))
            return;
        JsonElement cur = json.get("Modulus");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("Modulus", "integer");
        mModulus = cur.getAsInt();
        if(mModulus <= 0)
            throw new JsonError("Modulus must be greater than or equal to 0");
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
