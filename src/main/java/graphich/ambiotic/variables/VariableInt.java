package graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;

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
