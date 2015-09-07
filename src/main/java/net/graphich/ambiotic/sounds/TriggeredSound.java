package net.graphich.ambiotic.sounds;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;
import net.graphich.ambiotic.main.Ambiotic;
import net.minecraft.client.Minecraft;

public abstract class TriggeredSound {

    protected String mName = "";
    protected String mSound = "";
    protected int mTicksSincePlayed = 0;
    protected FloatProvider mVolume;
    protected FloatProvider mPitch;
    protected FloatProvider mCoolDown;
    protected int mCanPlayAgain = 0;

    public TriggeredSound(String name,String sound) {
        mName = name;
        mSound = sound;
        // Default pitch / volume calculators
        mVolume = new FloatConstant(1.0f);
        mPitch = new FloatConstant(1.0f);
        mCoolDown = new FloatConstant(10000.0f);
        mCanPlayAgain = -1;
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
        mCanPlayAgain = -1;
        mName = name;
        if(!json.has("Sound"))
            throw new JsonMissingRequiredField("Sound");
        JsonElement cur = json.get("Sound");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Sound", "string");
        mSound = cur.getAsString();
        //if(!Util.resourceExists(mSound))
        //    throw new JsonInvalidResource("Sound", mSound);

        //Cooldown spec is optional
        mCoolDown = new FloatConstant(10000.0f);
        if(json.has("CoolDown")) {
            cur = json.get("CoolDown");
            if(!cur.isJsonObject())
                throw new JsonInvalidTypeForField("CoolDown", "JSON object");
            try {
                mCoolDown = FloatProvider.deserialize(cur.getAsJsonObject());
            } catch (JsonError ex) {
                throw new JsonError("Invalid CoolDown specification : "+ex.getMessage());
            }
        }

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
                mVolume = FloatProvider.deserialize(cur.getAsJsonObject());
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
        if(mCanPlayAgain == -1)
            mCanPlayAgain = (int)mCoolDown.value();
        mTicksSincePlayed += 1;
        if(mTicksSincePlayed < mCanPlayAgain)
            return;
        if(play()) {
            mTicksSincePlayed = 0;
            mCanPlayAgain = (int)mCoolDown.value();
        }
    }
}
