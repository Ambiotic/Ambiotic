package graphich.ambiotic.variables;

import com.google.gson.annotations.SerializedName;

public abstract class VariableString extends Variable {

    @SerializedName("InitialValue")
    protected String mInitialValue;

    protected transient String mValue = "";

    public VariableString(String name) {
        super(name);
    }

    @Override //IVariable
    public Object value() {
        return mValue;
    }

    @Override //IVariable
    public String updateJS() {
        return name()+" = '"+mValue+"';";
    }

    @Override //IVariable
    public String initializeJS() {
        return name()+" = '"+mInitialValue+"';";
    }

    public boolean setNewValue(String newValue) {
        boolean updated = mValue.equals(newValue);
        mValue = newValue;
        return updated;
    }
}
