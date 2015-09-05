package net.graphich.ambiotic.variables;

import com.google.gson.JsonObject;
import net.graphich.ambiotic.errors.JsonError;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by jim on 9/23/2014.
 */
public abstract class PlayerVariable extends Variable {

    protected EntityPlayer mPlayer;
    protected World mWorld;

    public PlayerVariable(String name) {
        super(name);
    }

    public PlayerVariable(String name, JsonObject json) throws JsonError { super(name, json); }

    public void setPlayer(EntityPlayer player) {
        mPlayer = player;
        mWorld = player.getEntityWorld();
    }
}
