package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class Variable {

    protected String mName;
    protected int mValue = 0;
    protected int mTicksPerUpdate = 1;

    public Variable(String name) {
        mName = name;
    }

    public Variable(String name, JsonObject json) throws JsonError {
        mName = name;
        if(!json.has("TicksPerUpdate"))
            return;
        JsonElement cur = json.get("TicksPerUpdate");
        if(!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isNumber())
            throw new JsonInvalidTypeForField("TicksPerUpdate", "integer");
        mTicksPerUpdate = cur.getAsInt();
    }

    public abstract boolean update(TickEvent event);

    public int value() {
        return mValue;
    }

    public String name() {
        return mName;
    }

    public int ticksPerUpdate() {return mTicksPerUpdate;}

    public String pycode() { return mName+" = "+mValue+"\n"; }

    public static Variable deserialize(String name, JsonObject json) throws JsonError {
        if(!json.has("Type"))
            throw new JsonMissingRequiredField("Type");
        JsonElement check = json.get("Type");
        if(!check.isJsonPrimitive() || !check.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Type", "string");
        String type = check.getAsString();
        Variable variable = null;
        if(type.equals("BlockCounter"))
            variable = new BlockCounterVariable(name, json);
        else if(type.equals("GameTime"))
            variable = new GameTime(name, json);
        else if(type.equals("ThunderStrength"))
            variable = new ThunderStrength(name, json);
        else if(type.equals("RainStrength"))
            variable = new RainStrength(name, json);
        else if(type.equals("PlayerCoordinate"))
            variable = new PlayerCoordinate(name, json);
        else if(type.equals("MoonPhase"))
            variable = new MoonPhase(name, json);
        else if(type.equals("LightLevel"))
            variable = new LightLevel(name, json);
        else if(type.equals("IsRaining"))
            variable = new IsRaining(name, json);
        else if(type.equals("CanSeeSky"))
            variable = new CanSeeSky(name, json);
        else if(type.equals("CanRainOn"))
            variable = new CanRainOn(name, json);
        else
            throw new JsonError("No such variable type '"+type+"'");
        return variable;
    }
}
