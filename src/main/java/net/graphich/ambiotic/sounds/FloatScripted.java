package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.StrictJsonException;
import net.minecraft.client.Minecraft;

import javax.script.ScriptException;

public class FloatScripted extends FloatProvider {
    @SerializedName("Code")
    protected String mJSCode;

    @Override
    public void validate() throws StrictJsonException {
        if(mJSCode == null)
            throw new StrictJsonException(COMMON_NAMES+" : Code is required");
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
}
