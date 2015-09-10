package net.graphich.ambiotic.sounds;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.util.StrictJson;
import net.graphich.ambiotic.util.StrictJsonException;
import net.graphich.ambiotic.util.StrictJsonSerializer;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Type;

public abstract class AmbioticSoundEvent implements StrictJson {
    @SerializedName("Name")
    protected String mName = "";
    @SerializedName("Sound")
    protected String mSound = "";
    @SerializedName("Volume")
    protected FloatProvider mVolume;
    @SerializedName("Pitch")
    protected FloatProvider mPitch;
    @SerializedName("CoolDown")
    protected FloatProvider mCoolDown;

    public abstract boolean play();

    protected transient int mTicksSincePlayed = 0;
    protected transient int mCanPlayAgain = 0;

    public AmbioticSoundEvent(String name, String sound) {
        mName = name;
        mSound = sound;
        initialize();
    }

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
        if(mCoolDown == null)
            mCoolDown = new FloatConstant(10000.0f);
        mCanPlayAgain = -1;
        mTicksSincePlayed = 0;
        mCanPlayAgain = 0;
    }

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

    public static final StrictJsonSerializer<AmbioticSoundEvent> STRICT_ADAPTER;
    static {
        BiMap<String, Type> types = HashBiMap.create();
        types.put("Scripted", ScriptedSound.class);
        STRICT_ADAPTER = new StrictJsonSerializer<AmbioticSoundEvent>(types, AmbioticSoundEvent.class);
    }
}
