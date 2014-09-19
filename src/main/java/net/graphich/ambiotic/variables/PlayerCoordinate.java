package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Related to player's coordinates in space.
 */
public class PlayerCoordinate extends PlayerVariable {

    public enum Coordinates {X, Y, Z, DIM}

    ;

    protected Coordinates mCoordinate;

    public PlayerCoordinate(String name, EntityPlayer player, Coordinates coordinate) {
        super(name, player);
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

}
