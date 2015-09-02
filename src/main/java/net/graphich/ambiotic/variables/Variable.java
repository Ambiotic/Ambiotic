package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * A variable in our vernacular is an integer value associated with
 * some game value (player.posX, world.getInfo().isRaining(), ect...)
 * that is exposed by the API and to the python engine.
 */
public abstract class Variable {
    protected String mName;
    protected int mValue;

    public Variable(String name) {
        mName = name;
    }

    public abstract boolean update(TickEvent event);

    public int value() {
        return mValue;
    }

    public String name() {
        return mName;
    }

    public String pycode() { return mName+" = "+mValue+"\n"; }

}
