package net.graphich.ambiotic.variables;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Variables requiring player/world reference
 */
public abstract class PlayerVariable extends Variable {
    protected EntityPlayer mPlayer;
    protected World mWorld;

    public PlayerVariable(String name, EntityPlayer player) {
        super(name);
        setPlayer(player);
    }

    public void setPlayer(EntityPlayer player) {
        mPlayer = player;
        if (player.getEntityWorld() == null)
            System.out.println("World is null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        else
            mWorld = player.getEntityWorld();
    }

}
