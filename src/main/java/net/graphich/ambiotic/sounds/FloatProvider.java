package net.graphich.ambiotic.sounds;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import net.graphich.ambiotic.util.StrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

public abstract class FloatProvider implements StrictJson {

    public abstract float value();

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(FloatProvider.class , STRICT_ADAPTER);
        Gson gson = gsonBuilder.create();
        return gson.toJson(gson.toJsonTree(this));
    }

    @Override
    public void validate() throws StrictJsonException {}
    @Override
    public void initialize() throws StrictJsonException {}

    public static final StrictJsonSerializer<FloatProvider> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Random", FloatRandom.class);
        types.put("Constant", FloatConstant.class);
        STRICT_ADAPTER = new StrictJsonSerializer<FloatProvider>(types,FloatProvider.class);
    }

}
