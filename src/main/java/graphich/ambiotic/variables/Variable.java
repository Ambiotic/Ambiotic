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
import graphich.ambiotic.variables.special.BiomeAverage;
import graphich.ambiotic.variables.special.BlockCounter;
import graphich.ambiotic.variables.special.Constant;
import graphich.ambiotic.variables.world.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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
        types.put("BiomeAverage", BiomeAverage.class);
        //Player var types
        types.put("CanRainOn", CanRainOn.class);
        types.put("CanSeeSky", CanSeeSky.class);
        types.put("LightLevel", LightLevel.class);
        types.put("Coordinate", Coordinate.class);
        types.put("Exposed", Exposed.class);
        types.put("UnderWater", UnderWater.class);
        types.put("VerticalVelocity",VerticalVelocity.class);
        types.put("InBoat", InBoat.class);
        //World var types
        types.put("GameTime", GameTime.class);
        types.put("IsRaining", IsRaining.class);
        types.put("MoonPhase", MoonPhase.class);
        types.put("RainStrength", RainStrength.class);
        types.put("ThunderStrength", ThunderStrength.class);
        types.put("BiomeName", BiomeName.class);
        STRICT_ADAPTER = new StrictJsonSerializer<Variable>(types, Variable.class);
    }

    public static final String WORLD_NAMESPACE = "World";
    public static final String PLAYER_NAMESPACE = "Player";

    public static List<Variable> defaults() {
        List<Variable> defs = new ArrayList<Variable>();
        Variable var;

        //Player variables
        defs.add(new Coordinate("Y", Coordinate.Coordinates.Y));
        var = new VerticalVelocity("dY");
        var.mTicksPerUpdate = 5;
        defs.add(var);
        defs.add(new Coordinate("X", Coordinate.Coordinates.X));
        defs.add(new Coordinate("Z", Coordinate.Coordinates.Z));
        defs.add(new Coordinate("DIM", Coordinate.Coordinates.DIM));
        defs.add(new LightLevel("Sun", LightLevel.LightTypes.SUN));
        defs.add(new LightLevel("Torch", LightLevel.LightTypes.LAMP));
        defs.add(new LightLevel("MaxSun", LightLevel.LightTypes.MAXSUN));
        defs.add(new LightLevel("Light", LightLevel.LightTypes.TOTAL));
        defs.add(new CanRainOn("CanRainOn"));
        defs.add(new CanSeeSky("CanSeeSky"));
        defs.add(new InBoat("InBoat"));
        defs.add(new UnderWater("Submerged"));

        //World variables
        var = new GameTime("Time", 24000);
        var.mTicksPerUpdate = 50;
        defs.add(var);
        defs.add(new IsRaining("IsRaining"));
        defs.add(new ThunderStrength("ThunderStrength",1000));
        defs.add(new RainStrength("RainStrength",1000));
        defs.add(new MoonPhase("MoonPhase"));
        defs.add(new BiomeName("Biome"));

        return defs;
    }
}
