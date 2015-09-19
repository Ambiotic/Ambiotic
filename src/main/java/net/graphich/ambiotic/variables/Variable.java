package net.graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.IStrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;

import java.lang.reflect.Type;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class Variable implements IStrictJson {

    @SerializedName("Name")
    protected String mName;
    @SerializedName("TicksPerUpdate")
    protected int mTicksPerUpdate = 1;

    protected transient int mValue = 0;

    public Variable(String name) {
        mName = name;
    }

    public abstract boolean update(TickEvent event);

    @Override
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
    }

    @Override
    public void initialize() {
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
        Gson gson = Ambiotic.gson();
        return gson.toJson(gson.toJsonTree(this));
    }

    public static final StrictJsonSerializer<Variable> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("BlockCounter", BlockCounter.class);
        types.put("CanRainOn", CanRainOn.class);
        types.put("CanSeeSky", CanSeeSky.class);
        types.put("GameTime", GameTime.class);
        types.put("IsRaining", IsRaining.class);
        types.put("LightLevel", LightLevel.class);
        types.put("MoonPhase", MoonPhase.class);
        types.put("PlayerCoordinate", PlayerCoordinate.class);
        types.put("RainStrength", RainStrength.class);
        types.put("ThunderStrength", ThunderStrength.class);
        types.put("PlayerExposed", PlayerExposed.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Variable>(types, Variable.class);
    }
}
