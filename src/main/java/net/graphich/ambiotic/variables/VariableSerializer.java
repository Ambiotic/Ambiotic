package net.graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;

import java.lang.reflect.Type;

public class VariableSerializer implements JsonDeserializer<Variable>, JsonSerializer<Variable> {

    protected static final BiMap<String, Type> VARIABLE_TYPE_MAP;
    static {
        VARIABLE_TYPE_MAP = HashBiMap.create();
        VARIABLE_TYPE_MAP.put("BlockCounter", BlockCounter.class);
        VARIABLE_TYPE_MAP.put("CanRainOn", CanRainOn.class);
        VARIABLE_TYPE_MAP.put("CanSeeSky", CanSeeSky.class);
        VARIABLE_TYPE_MAP.put("GameTime", GameTime.class);
        VARIABLE_TYPE_MAP.put("IsRaining", IsRaining.class);
        VARIABLE_TYPE_MAP.put("LightLevel", LightLevel.class);
        VARIABLE_TYPE_MAP.put("MoonPhase", MoonPhase.class);
        VARIABLE_TYPE_MAP.put("PlayerCoordinate", PlayerCoordinate.class);
        VARIABLE_TYPE_MAP.put("RainStrength", RainStrength.class);
        VARIABLE_TYPE_MAP.put("ThunderStrength", ThunderStrength.class);
    }

    @Override
    public Variable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement type = json.getAsJsonObject().get("Type");
        if(!type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString())
            throw new JsonParseException("Variable Type must be string");
        if(!VARIABLE_TYPE_MAP.containsKey(type.getAsString()))
            throw new JsonParseException("No such variable Type '"+type.getAsString()+"'");
        Type varClass = VARIABLE_TYPE_MAP.get(type.getAsString());
        return context.deserialize(json,varClass);
    }

    @Override
    public JsonElement serialize(Variable src, Type typeOfSrc, JsonSerializationContext context) {
        String strType = VARIABLE_TYPE_MAP.inverse().get(typeOfSrc);
        JsonElement outElm = context.serialize(src,typeOfSrc);
        outElm.getAsJsonObject().add("Type",  context.serialize(strType));
        return outElm;
    }
}
