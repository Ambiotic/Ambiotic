package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Related to player's coordinates in space.
 */
public class PlayerCoordinate extends Variable {

    protected Coordinates mCoordinate;

    public PlayerCoordinate(String name, Coordinates coordinate) {
        super(name);
        mCoordinate = coordinate;
    }

    @Override
    public void update(TickEvent event) {
        switch (mCoordinate) {
            case X:
                mValue = (int) mPlayer.posX;
                break;
            case Y:
                mValue = (int) mPlayer.posY;
                break;
            case Z:
                mValue = (int) mPlayer.posZ;
                break;
            case DIM:
                mValue = mPlayer.dimension;
                break;
        }
    }

    public enum Coordinates {X, Y, Z, DIM}

}
