package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.minecraft.client.Minecraft;

import javax.script.ScriptException;

public class FloatScripted extends FloatProvider {
    @SerializedName("Code")
    protected String mJSCode;

    @Override
    public float value() {
        try {
            Double rv = (Double)Ambiotic.scripter().eval(mJSCode);
            return rv.floatValue();
        } catch(ScriptException ex) {
            Ambiotic.logger().debug("Script error in Float Scripted : "+ex.getMessage());
            return Float.NaN;
        }
    }
}
