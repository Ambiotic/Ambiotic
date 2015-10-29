package graphich.ambiotic.variables.special;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Variable;

public class Constant extends Variable {
    @SerializedName("Value")
    String mValue;

    public Constant(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        if (mValue == null)
            throw new StrictJsonException("Value is required and must be a string");
    }

    @Override //IVariable
    public String initializeJS() {
        return name() + " = " + mValue + ";";
    }

    @Override //IVariable
    public String updateJS() {
        return name() + " = " + mValue + ";";
    }

    @Override //IVariable
    public Object value() {
        return mValue;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        return false;
    }
}
