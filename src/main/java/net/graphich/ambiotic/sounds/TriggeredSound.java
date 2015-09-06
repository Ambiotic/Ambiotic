package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidResource;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.graphich.ambiotic.main.Util;
import net.minecraft.client.Minecraft;

public abstract class TriggeredSound {

    protected String mName = "";
    protected int mCoolDown = 0;
    protected String mSound = "";
    protected int mTicksSinceFire = 0;
    protected FloatProvider mVolume;
    protected FloatProvider mPitch;

    public TriggeredSound(String name, int cooldown, String sound) {
        mName = name;
        mCoolDown = cooldown;
        mSound = sound;
        // Default pitch / volume calculators
        mVolume = new FloatConstant(1.0f);
        mPitch = new FloatConstant(1.0f);
    }

    public static TriggeredSound deserialize(String name, JsonObject json) throws JsonError {
        if(!json.has("Type"))
            throw new JsonMissingRequiredField("Type");
        JsonElement check = json.get("Type");
        if(!check.isJsonPrimitive() || !check.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Type", "string");
        String type = check.getAsString();
        TriggeredSound sound = null;
        if(type.equals("ScriptedSound"))
            sound = new ScriptedSound(name,json);
        else
            throw new JsonError("No such sound event type '"+type+"'");
        return sound;
    }

    public TriggeredSound(String name, JsonObject json) throws JsonError {
        if(!json.has("Sound"))
            throw new JsonMissingRequiredField("Sound");
        JsonElement cur = json.get("Sound");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Sound", "string");
        mSound = cur.getAsString();
        //if(!Util.resourceExists(mSound))
        //    throw new JsonInvalidResource("Sound", mSound);

        if(!json.has("CoolDown"))
            throw new JsonMissingRequiredField("CoolDown");
        cur = json.get("CoolDown");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("CoolDown", "integer");
        mCoolDown = cur.getAsInt();
        if(mCoolDown <= 0)
            throw new JsonError("CoolDown must be greater than zero.");

        //Pitch spec is optional
        mPitch = new FloatConstant(1.0f);
        if(json.has("Pitch")) {
            cur = json.get("Pitch");
            if(!cur.isJsonObject())
                throw new JsonInvalidTypeForField("Pitch", "JSON object");
            try {
                mPitch = FloatProvider.deserialize(cur.getAsJsonObject());
            } catch (JsonError ex) {
                throw new JsonError("Invalid Pitch specification : "+ex.getMessage());
            }
        }

        //Volume spec is optional
        mVolume = new FloatConstant(1.0f);
        if(json.has("Volume")) {
            cur = json.get("Volume");
            if(!cur.isJsonObject())
                throw new JsonInvalidTypeForField("Volume", "JSON object");
            try {
                mPitch = FloatProvider.deserialize(cur.getAsJsonObject());
            } catch (JsonError ex) {
                throw new JsonError("Invalid Volume specification : "+ex.getMessage());
            }
        }
    }

    public abstract boolean play();

    public String name() { return mName; }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        // Not logged in or whatever
        if(Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null)
            return;
        mTicksSinceFire += 1;
        if(mTicksSinceFire < mCoolDown)
            return;
        if(play())
            mTicksSinceFire = 0;
    }
}
