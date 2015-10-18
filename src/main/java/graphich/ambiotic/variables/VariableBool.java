package graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;

public  abstract class VariableBool extends Variable {

    @SerializedName("InitialValue")
    protected Boolean mInitialValue;

    protected transient boolean mValue = false;

    public VariableBool(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        if(mInitialValue == null)
            mInitialValue = false;
    }

    @Override //IVariable
    public Object value() {
        return mValue;
    }

    @Override //IVariable
    public String updateJS() {
        if(mValue)
            return name()+" = true;";
        return name()+" = false;";
    }

    @Override //IVariable
    public String initializeJS() {
        if(mInitialValue)
            return name()+" = true;";
        return name()+" = false;";
    }

    protected boolean setNewValue(boolean newValue) {
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }
}
