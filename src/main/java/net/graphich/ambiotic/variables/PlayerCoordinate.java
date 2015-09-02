package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Related to player's coordinates in space.
 */
public class PlayerCoordinate extends PlayerVariable {

    protected Coordinates mCoordinate;

    public PlayerCoordinate(String name, Coordinates coordinate) {
        super(name);
        mCoordinate = coordinate;
    }

    @Override
    public boolean update(TickEvent event) {
        int newValue = 0;
        switch (mCoordinate) {
            case X:
                newValue = (int) mPlayer.posX;
                break;
            case Y:
                newValue = (int) mPlayer.posY;
                break;
            case Z:
                newValue = (int) mPlayer.posZ;
                break;
            case DIM:
                newValue = mPlayer.dimension;
                break;
        }
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }

    public enum Coordinates {X, Y, Z, DIM}

}
