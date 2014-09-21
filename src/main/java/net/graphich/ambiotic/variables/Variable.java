package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class Variable {
    protected EntityPlayer mPlayer;
    protected World mWorld;

    protected String mName;
    protected int mValue;

    public Variable(String name) {
        mName = name;
    }

    public abstract void update(TickEvent event);

    public int value() {
        return mValue;
    }

    public String name() {
        return mName;
    }

    public void setPlayer(EntityPlayer player) {
        mPlayer = player;
        mWorld = player.getEntityWorld();
    }
}
