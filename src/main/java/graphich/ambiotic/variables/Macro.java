package graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.IScripted;
import graphich.ambiotic.util.IStrictJson;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Macro implements IScripted, IStrictJson {
    @SerializedName("Code")
    protected String mCode;
    @SerializedName("Name")
    protected String mName;

    protected final static Pattern SYMBOL_PATTERN = Pattern.compile("#([A-Za-z0-9]+)#");

    public String name() {
        return mName;
    }

    public String symbol() { return "#"+mName+"#"; }

    public String code() { return mCode; }

    public String expand(String toMacro) {
        return toMacro.replaceAll(symbol(),mCode);
    }

    public static final StrictJsonSerializer<Macro> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Macro", Macro.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Macro>(types, Macro.class);
    }

    @Override
    public void expandMacros(Map<String,Macro> macros) {
        HashSet<String> seen = new HashSet<String>();
        boolean broken = false;
        while(mCode.contains("#") && !broken) {
            Matcher matcher = SYMBOL_PATTERN.matcher(mCode);
            while(matcher.find()) {
                String name = matcher.group(1);
                if(name == mName) {
                    broken = true;
                    break;
                }
                if(seen.contains(name)) {
                    broken = true;
                    break;
                }
                if(!macros.containsKey(name)) {
                    broken = true;
                    break;
                }
                Macro macro = macros.get(name);
                mCode = macro.expand(mCode);
                seen.add(name);
            }
        }
    }

    @Override
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
        if(mCode == null || mCode.equals(""))
            throw new StrictJsonException("Code is required");
    }

    @Override
    public void initialize() {;}

    @Override //Object
    public String toString() {
        Gson gson = Ambiotic.gson();
        return gson.toJson(gson.toJsonTree(this));
    }
}
