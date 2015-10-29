package graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class VariableNumber extends Variable {
    public final static float EQUALITY_LIMIT = 0.00001f;

    @SerializedName("InitialValue")
    protected Double mInitialValue;

    protected transient double mValue = 0.0f;

    public VariableNumber(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        if (mInitialValue == null)
            mInitialValue = 0.0;
    }

    @Override //IVariable
    public Object value() {
        return mValue;
    }

    @Override //IVariable
    public String updateJS() {
        return name() + " = " + mValue + ";";
    }

    @Override //IVariable
    public String initializeJS() {
        return name() + " = " + mInitialValue + ";";
    }

    protected boolean setNewValue(double newValue) {
        boolean updated = (Math.abs(mValue - newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        return updated;
    }
}
