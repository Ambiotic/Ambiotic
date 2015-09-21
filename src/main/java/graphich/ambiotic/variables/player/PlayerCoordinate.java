package graphich.ambiotic.variables.player;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableInt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Related to player's coordinates in space.
 */
public class PlayerCoordinate extends VariableInt {

    @SerializedName("SubType")
    protected Coordinates mCoordinate;

    public PlayerCoordinate(String name, Coordinates coordinate) {
        super(name);
        mCoordinate = coordinate;
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if(mCoordinate == null)
            throw new  StrictJsonException("No SubType specified, valid subtypes are X, Y, Z or DIM");
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        int newValue = 0;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(player == null)
            return false;
        switch (mCoordinate) {
            case X:
                newValue = (int) player.posX;
                break;
            case Y:
                newValue = (int) player.posY;
                break;
            case Z:
                newValue = (int) player.posZ;
                break;
            case DIM:
                newValue = player.dimension;
                break;
        }
        boolean updated = (newValue != mValue);
        mValue = newValue;
        return updated;
    }

    public enum Coordinates {X, Y, Z, DIM}

}
