package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.main.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import javax.script.ScriptException;

public final class ScriptedSound extends TriggeredSound {

    protected String mConditionCode = "";

    public ScriptedSound(String name, String sound, String condition) {
        super(name, sound);
        mConditionCode = condition;
    }

    public ScriptedSound(String name, JsonObject json) throws JsonError {
        super(name, json);
        if(!json.has("Conditions"))
            throw new JsonMissingRequiredField("Conditions");
        JsonElement cur = json.get("Conditions");
        if(!cur.isJsonArray())
            throw new JsonInvalidTypeForField("Conditions", "list of strings");
        try {
            mConditionCode = Util.makeCodeFromJsonList(cur.getAsJsonArray());
        } catch (JsonError ex) {
            throw new JsonError("Invalid Conditions specification : "+ex.getMessage());
        }
        Ambiotic.logger().debug("Conditions for Sound '" + name + "' : \n  " + mConditionCode);
    }

    public boolean play() {
        Boolean canplay = false;
        try {
            canplay = (Boolean)Ambiotic.scripter().eval(mConditionCode);
        } catch (ScriptException ex) {
            Ambiotic.logger().error("Script error in Sound Event '"+mName+"' : "+ex.getMessage());
        }
        // Conditions not met
        if(!canplay)
            return false;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        player.playSound(mSound, mVolume.value(), mPitch.value());
        return true;
    }
}
