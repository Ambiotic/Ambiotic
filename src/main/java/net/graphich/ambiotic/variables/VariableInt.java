package net.graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.IStrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class VariableInt extends Variable {

    @SerializedName("InitialValue")
    protected Integer mInitialValue;

    protected transient int mValue = 0;

    public VariableInt(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        if(mInitialValue == null)
            mInitialValue = 0;
    }

    @Override //IVariable
    public Object value() {
        return mValue;
    }

    @Override //IVariable
    public String updateJS() {
        return name()+" = "+mValue+";";
    }

    @Override //IVariable
    public String initializeJS() {
        return name()+" = "+mInitialValue+";";
    }
}
