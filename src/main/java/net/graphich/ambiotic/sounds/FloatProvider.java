package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;

public abstract class FloatProvider {

    public abstract float value();

    public static FloatProvider deserialize(JsonObject json) throws JsonError {
        if(!json.has("Type"))
            throw new JsonMissingRequiredField("Type");
        JsonElement cur = json.get("Type");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Type","string");
        String type = cur.getAsString();
        if(type.equals("Random"))
            return new FloatRandom(json);
        else if(type.equals("Constant"))
            return new FloatConstant(json);
        else
            throw new JsonError("No such Type '"+type+"'");
    }
}
