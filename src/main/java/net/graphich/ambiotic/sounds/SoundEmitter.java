package net.graphich.ambiotic.sounds;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.util.IStrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;
import net.minecraft.client.audio.ISound;

import java.lang.reflect.Type;

public abstract class SoundEmitter implements IStrictJson, IScriptedConditional {
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

    @Override
    public void validate() throws StrictJsonException {
        if(mName == null || mName.equals(""))
            throw new StrictJsonException("Name is required");
        if(mSound == null || mSound.equals(""))
            throw new StrictJsonException("Sound is required");
    }

    @Override
    public void initialize() {
        // Default pitch / volume calculators
        if(mVolume == null)
            mVolume = new FloatConstant(1.0f);
        if(mPitch == null)
            mPitch = new FloatConstant(1.0f);
    }

    public String name() { return mName; }

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
