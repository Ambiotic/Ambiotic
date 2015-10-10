package graphich.ambiotic.emitters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import graphich.ambiotic.emitters.effects.FloatConstant;
import graphich.ambiotic.emitters.effects.FloatProvider;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.*;
import graphich.ambiotic.variables.Macro;
import net.minecraft.client.audio.ISound;

import java.lang.reflect.Type;
import java.util.Map;

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
    @SerializedName("Restrict")
    protected String mRestrictCode = "";


    public SoundEmitter(String name, String sound) {
        mName = name;
        mSound = sound;
        initialize();
    }

    public abstract ISound emit();

    public String sound()
    {
        return mSound;
    }

    @Override //IScripted
    public void expandMacros(Map<String, Macro> macros) {
        for(Macro macro : macros.values()) {
            mConditionCode = macro.expand(mConditionCode);
            if(!mRestrictCode.equals(""))
                mRestrictCode = macro.expand(mRestrictCode);
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
        if(mConditionCode == null || mConditionCode.equals(""))
            throw new StrictJsonException("Conditions is required");
    }

    @Override //IStrictJson
    public void initialize() {
        // Default pitch / volume calculators
        if(mVolume == null)
            mVolume = new FloatConstant(1.0f);
        if(mPitch == null)
            mPitch = new FloatConstant(1.0f);
        if(mRestrictCode == null)
            mRestrictCode = "";
    }

    public String name() { return mName; }

    @Override //IConditional
    public boolean conditionsMet() {
        boolean rv = getResult(mConditionCode);
        if(rv && !mRestrictCode.equals(""))
            rv = (rv && !getResult(mRestrictCode));
        return rv;
    }

    protected boolean getResult(String js) {
        Object rv = Ambiotic.evalJS(js);
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
