package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;

/**
 * Fractional rain strength multiplied by scalar, values will be [0,scalar)
 */
public final class RainStrength extends PlayerVariable {

    protected int mScalar;

    public RainStrength(String name, int scalar) {
        super(name);
        mScalar = scalar;
    }

    public RainStrength(String name, JsonObject json) throws JsonError {
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
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }
}
