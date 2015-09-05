package net.graphich.ambiotic.variables;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.errors.JsonInvalidTypeForField;
import net.graphich.ambiotic.errors.JsonMissingRequiredField;

/**
 * Related to player's coordinates in space.
 */
public class PlayerCoordinate extends PlayerVariable {

    protected Coordinates mCoordinate;

    public PlayerCoordinate(String name, Coordinates coordinate) {
        super(name);
        mCoordinate = coordinate;
    }

    public PlayerCoordinate(String name, JsonObject json) throws JsonError {
        super(name);
        if(!json.has("SubType"))
            throw new JsonMissingRequiredField("SubType");
        JsonElement subtype = json.get("SubType");
        if(!subtype.isJsonPrimitive() || !subtype.getAsJsonPrimitive().isString())
            throw new JsonInvalidTypeForField("Subtype","string");

        String coordinate = subtype.getAsString();
        if(coordinate.equals("X"))
            mCoordinate = Coordinates.X;
        else if(coordinate.equals("Y"))
            mCoordinate = Coordinates.Y;
        else if(coordinate.equals("Z"))
            mCoordinate = Coordinates.Z;
        else if(coordinate.equals("DIM"))
            mCoordinate = Coordinates.DIM;
        else
            throw new JsonError("Invalid subtype for player coordinate '"+coordinate+"'");
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
