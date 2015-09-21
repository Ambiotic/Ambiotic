package graphich.ambiotic.util;

import com.google.common.collect.BiMap;
import com.google.gson.*;

import java.lang.reflect.Type;

public class StrictJsonSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private BiMap<String, Type> mTypeMap;
    private Class<T> mRootType;

    public StrictJsonSerializer(BiMap<String, Type> typeMap, Class<T> rootType) {
        mTypeMap = typeMap;
        mRootType = rootType;
    }

    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws StrictJsonException {
        JsonElement type = json.getAsJsonObject().get("Type");
        String rootName = mRootType.getSimpleName();
        if(!type.isJsonPrimitive() || !type.getAsJsonPrimitive().isString())
            throw new StrictJsonException(rootName + " Type must be string");
        if(!mTypeMap.containsKey(type.getAsString()))
            throw new StrictJsonException("No such " + rootName + " Type '"+type.getAsString()+"'");
        Type varClass = mTypeMap.get(type.getAsString());
        T result = context.deserialize(json,varClass);
        if(result instanceof IStrictJson) {
            ((IStrictJson) result).validate();
            ((IStrictJson) result).initialize();
        }
        return result;
    }

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        //Default to the types simple name
        String strType = typeOfSrc.getClass().getSimpleName();
        if(mTypeMap.containsValue(typeOfSrc))
            strType = mTypeMap.inverse().get(typeOfSrc);
        JsonElement outElm = context.serialize(src,typeOfSrc);
        outElm.getAsJsonObject().add("Type",  context.serialize(strType));
        return outElm;
    }
}
