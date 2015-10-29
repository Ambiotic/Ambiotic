package graphich.ambiotic.emitters.effects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import graphich.ambiotic.util.IStrictJson;
import graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

public abstract class FloatProvider implements IStrictJson {
    public static final StrictJsonSerializer<FloatProvider> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Random", FloatRandom.class);
        types.put("Constant", FloatConstant.class);
        types.put("Scripted", FloatScripted.class);
        STRICT_ADAPTER = new StrictJsonSerializer<FloatProvider>(types, FloatProvider.class);
    }
    //Used to make strict JSON errors a little more descriptive
    public static String COMMON_NAMES = "Volume, Pitch, or CoolDown";

    public abstract float value();

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(FloatProvider.class, STRICT_ADAPTER);
        Gson gson = gsonBuilder.create();
        return gson.toJson(gson.toJsonTree(this));
    }

    @Override
    public void initialize() {
        //Most float providers don't need this
        return;
    }
}
