package net.graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.IStrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;
import net.graphich.ambiotic.variables.player.*;
import net.graphich.ambiotic.variables.special.BlockCounter;
import net.graphich.ambiotic.variables.world.*;

import java.lang.reflect.Type;

public abstract class Variable implements IVariable, IStrictJson {
    @SerializedName("Name")
    protected String mName;
    @SerializedName("TicksPerUpdate")
    protected int mTicksPerUpdate = 1;

    protected transient String mNameSpace;

    public Variable(String name) {
        mName = name;
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
    }

    @Override //IStrictJson
    public void initialize() {
        //Default ticks per update
        if(mTicksPerUpdate == 0)
            mTicksPerUpdate = 1;
    }

    @Override //IVariable
    public String name() {
        String fullName = mName;
        if(!mNameSpace.isEmpty())
            fullName = mNameSpace+"."+mName;
        return fullName;
    }

    @Override //IVariable
    public String namespace() {
        return mNameSpace;
    }

    @Override //IVariable
    public int ticksPerUpdate() {
        return mTicksPerUpdate;
    }

    @Override //Object
    public String toString() {
        Gson gson = Ambiotic.gson();
        return gson.toJson(gson.toJsonTree(this));
    }

    public static final StrictJsonSerializer<VariableInt> STRICT_ADAPTER;
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
        STRICT_ADAPTER = new StrictJsonSerializer<VariableInt>(types, VariableInt.class);
    }

    public static final String WORLD_NAMESPACE = "World";
    public static final String PLAYER_NAMESPACE = "Player";
}
