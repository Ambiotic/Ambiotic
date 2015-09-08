package net.graphich.ambiotic.variables;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
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

    @SerializedName("Name")
    protected String mName;
    protected transient int mValue = 0;
    @SerializedName("TicksPerUpdate")
    protected int mTicksPerUpdate = 1;

    public Variable(String name) {
        mName = name;
    }

    public abstract boolean update(TickEvent event);

    public void validate() throws Exception {
        if(mName == null || mName.equals(""))
            throw new Exception("No name or blank name specified");
        //Default ticks per update
        if(mTicksPerUpdate == 0)
            mTicksPerUpdate = 1;
    }

    public int value() {
        return mValue;
    }

    public String name() {
        return mName;
    }

    public int ticksPerUpdate() {
        return mTicksPerUpdate;
    }

    public String jsAssignCode() {
        return mName+" = "+mValue+";";
    }

    @Override
    public String toString() {
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Variable.class, new VariableSerializer());
        Gson gson = gsonBuilder.create();
        return gson.toJson(gson.toJsonTree(this));
    }
}
