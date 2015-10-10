package graphich.ambiotic.variables;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.IStrictJson;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.util.StrictJsonSerializer;
import graphich.ambiotic.variables.player.*;
import graphich.ambiotic.variables.special.BlockCounter;
import graphich.ambiotic.variables.special.Constant;
import graphich.ambiotic.variables.world.*;

import java.lang.reflect.Type;

public abstract class Variable implements IVariable, IStrictJson {
    @SerializedName("Name")
    protected String mName;
    @SerializedName("TicksPerUpdate")
    protected Integer mTicksPerUpdate = 1;

    protected transient String mNameSpace;

    public Variable(String name) {
        mName = name;
        initialize(); //Call initialize function
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
    }

    @Override //IStrictJson
    public void initialize() {
        mNameSpace = "";
        //Default ticks per update
        if(mTicksPerUpdate == null)
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

    public static final StrictJsonSerializer<Variable> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        //Special var types
        types.put("Constant", Constant.class);
        types.put("BlockCounter", BlockCounter.class);
        //Player var types
        types.put("CanRainOn", CanRainOn.class);
        types.put("CanSeeSky", CanSeeSky.class);
        types.put("LightLevel", LightLevel.class);
        types.put("PlayerCoordinate", Coordinate.class);
        types.put("PlayerExposed", Exposed.class);
        types.put("UnderWater", UnderWater.class);
        types.put("VerticalVelocity",VerticalVelocity.class);
        //World var types
        types.put("GameTime", GameTime.class);
        types.put("IsRaining", IsRaining.class);
        types.put("MoonPhase", MoonPhase.class);
        types.put("RainStrength", RainStrength.class);
        types.put("ThunderStrength", ThunderStrength.class);
        types.put("BiomeName", BiomeName.class);
        types.put("BiomeTemperature", BiomeTemperature.class);
        types.put("BiomeRainFall", BiomeRainFall.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Variable>(types, Variable.class);
    }

    public static final String WORLD_NAMESPACE = "World";
    public static final String PLAYER_NAMESPACE = "Player";
}
