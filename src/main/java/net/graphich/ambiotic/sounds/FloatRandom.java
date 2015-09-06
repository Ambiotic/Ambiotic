package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.graphich.ambiotic.main.Util;

public final class FloatRandom extends FloatProvider {
    protected float mMin = 0.0f;
    protected float mMax = 1.0f;

    public FloatRandom(JsonObject json) throws JsonError {
        if(!json.has("Min"))
            throw new JsonMissingRequiredField("Min");
        JsonElement cur = json.get("Min");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("Min", "float");
        mMin = cur.getAsFloat();

        if(!json.has("Max"))
            throw new JsonMissingRequiredField("Max");
        cur = json.get("Max");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("Max", "float");
        mMax = cur.getAsFloat();
        if(mMin >= mMax)
            throw new JsonError("Min must be smaller than Max");
    }

    public float value() {
        return Util.randomFloatInRange(mMin,mMax);
    }
}
