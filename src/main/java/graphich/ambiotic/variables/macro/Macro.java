package graphich.ambiotic.variables.macro;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

public class Macro {
    @SerializedName("Code")
    protected String mCode;
    @SerializedName("Name")
    protected String mName;

    public String name() {
        return mName;
    }

    public String expand(String toMacro) {
        return toMacro.replaceAll("#"+mName+"#",mCode);
    }

    public static final StrictJsonSerializer<Macro> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Macro", Macro.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Macro>(types, Macro.class);
    }
}
