package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;

public final class FloatConstant extends FloatProvider {
    float mValue;

    public FloatConstant(float value) {
        mValue = value;
    }

    public FloatConstant(JsonObject json) throws JsonError {
        if(!json.has("Value"))
            throw new JsonMissingRequiredField("Value");
        JsonElement cur = json.get("Value");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("Value", "float");
        mValue = cur.getAsFloat();
    }

    @Override
    public float value() {
        return mValue;
    }
}
