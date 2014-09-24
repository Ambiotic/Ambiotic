package net.graphich.ambiotic.variables;

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

    public void setPlayer(EntityPlayer player) {
        mPlayer = player;
        mWorld = player.getEntityWorld();
    }
}
