package graphich.ambiotic.emitters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatConstant;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.IStrictJson;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.util.StrictJsonSerializer;
import graphich.ambiotic.variables.macro.Macro;
import net.minecraft.client.audio.ISound;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class SoundEmitter implements IStrictJson, IConditional, IScripted {
    @SerializedName("Name")
    protected String mName = "";
    @SerializedName("Sound")
    protected String mSound = "";
    @SerializedName("Volume")
    protected FloatProvider mVolume;
    @SerializedName("Pitch")
    protected FloatProvider mPitch;
    @SerializedName("Conditions")
    protected String mConditionCode = "";


    public SoundEmitter(String name, String sound) {
        mName = name;
        mSound = sound;
        initialize();
    }

    public abstract ISound emit();

    @Override //IScripted
    public void expandMacros(Collection<Macro> macros) {
        for(Macro macro : macros) {
            mConditionCode = macro.expand(mConditionCode);
        }
        if(mVolume instanceof IScripted)
            ((IScripted)mVolume).expandMacros(macros);
        if(mPitch instanceof IScripted)
            ((IScripted)mPitch).expandMacros(macros);
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
        if(mSound == null || mSound.equals(""))
            throw new StrictJsonException("Sound is required");
    }

    @Override //IStrictJson
    public void initialize() {
        // Default pitch / volume calculators
        if(mVolume == null)
            mVolume = new FloatConstant(1.0f);
        if(mPitch == null)
            mPitch = new FloatConstant(1.0f);
    }

    public String name() { return mName; }

    @Override //IConditional
    public boolean conditionsMet() {
        Object rv = Ambiotic.evalJS(mConditionCode);
        if(rv == null)
            return false;
        else if(rv instanceof Boolean)
            return (Boolean)rv;
        else if(rv instanceof Number)
           return ((Number)rv).intValue() == 0;
        return false;
    }

    public static final StrictJsonSerializer<SoundEmitter> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Instant", InstantEmitter.class);
        types.put("Looping", LoopingEmitter.class);
        STRICT_ADAPTER = new StrictJsonSerializer<SoundEmitter>(types, SoundEmitter.class);
    }
}
