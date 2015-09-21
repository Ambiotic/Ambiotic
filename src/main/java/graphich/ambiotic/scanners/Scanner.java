package graphich.ambiotic.scanners;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.util.IStrictJson;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

public abstract class Scanner implements IStrictJson {
    @SerializedName("Name")
    protected String mName = "";

    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
    }

    public String name() {
        return mName;
    }

    public static final StrictJsonSerializer<Scanner> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("BlockScanner", BlockScanner.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Scanner>(types, Scanner.class);
    }

}
