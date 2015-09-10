package net.graphich.ambiotic.sounds;

import com.google.gson.annotations.SerializedName;
import net.graphich.ambiotic.main.Ambiotic;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import javax.script.ScriptException;

public class ScriptedSound extends AmbioticSoundEvent {

    @SerializedName("Conditions")
    protected String mConditionCode = "";

    public ScriptedSound(String name, String sound, String condition) {
        super(name, sound);
        mConditionCode = condition;
    }

    public boolean play() {
        Boolean canplay = false;
        try {
            canplay = (Boolean)Ambiotic.scripter().eval(mConditionCode);
        } catch (ScriptException ex) {
            Ambiotic.logger().error("Script error in Sound Event '"+mName+"' : "+ex.getMessage());
        }
        // Conditions not met
        if(!canplay)
            return false;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        player.playSound(mSound, mVolume.value(), mPitch.value());
        //Minecraft.getMinecraft().theWorld.playSound(player.posX, player.posY, player.posZ,mSound,mVolume.value(),mPitch.value(),true);
        return true;
    }
}
