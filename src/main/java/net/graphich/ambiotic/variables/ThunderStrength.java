package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;

/**
 * Fractional thunder strength multiplied by scalar (can be negative), values will be [0,scalar)
 */
public final class ThunderStrength extends PlayerVariable {

    protected int mScalar;

    public ThunderStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
    }

    public ThunderStrength(String name, JsonObject json) throws JsonError{
        super(name, json);
        mScalar = 1000;
        if(!json.has("Scalar"))
            return;
        JsonElement scalar = json.get("Scalar");
        if(!scalar.isJsonPrimitive() || !scalar.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("Scalar","integer");
        mScalar = scalar.getAsInt();
    }

    @Override
    public boolean update(TickEvent event)
    {
        int newValue = (int) mWorld.getWeightedThunderStrength(0f) * mScalar;
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
