package graphich.ambiotic.emitters.effects;

import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.util.IScripted;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Macro;

import java.util.Map;

public class FloatScripted extends FloatProvider implements IScripted {
    @SerializedName("Code")
    protected String mJSCode;

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        if(mJSCode == null)
            throw new StrictJsonException("Scripted "+COMMON_NAMES+" : Code is required");
    }

    @Override
    public float value() {
        Object rv = Ambiotic.evalJS(mJSCode);
        if(rv == null)
            return 0.0f;
        else if(rv instanceof Float)
            return ((Float) rv).floatValue();
        else if(rv instanceof Integer)
            return ((Integer) rv).floatValue();
        else if(rv instanceof Double)
            return ((Double) rv).floatValue();
        return 0.0f;
    }

    @Override //IScripted
    public void expandMacros(Map<String, Macro> macros) {
        for(Macro macro : macros.values()) {
            mJSCode = macro.expand(mJSCode);
        }
    }
}
