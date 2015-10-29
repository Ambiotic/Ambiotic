package graphich.ambiotic.variables.player;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Related to player's coordinates in space.
 */
public class Coordinate extends VariableNumber {
    @SerializedName("SubType")
    protected Coordinates mCoordinate;

    public Coordinate(String name, Coordinates coordinate) {
        super(name);
        mCoordinate = coordinate;
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if (mCoordinate == null)
            throw new StrictJsonException("SubType is required and must be one of " + Coordinates.names);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        double newValue = 0;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null)
            return false;
        switch (mCoordinate) {
            case X:
                newValue = player.posX;
                break;
            case Y:
                newValue = player.posY;
                break;
            case Z:
                newValue = player.posZ;
                break;
            case DIM:
                newValue = player.dimension;
                break;
        }
        return setNewValue(newValue);
    }

    public enum Coordinates {
        X, Y, Z, DIM;
        public static final String names = "X, Y, Z, DIM";
    }
}
