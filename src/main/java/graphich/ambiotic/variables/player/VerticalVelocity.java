package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableNumber;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class VerticalVelocity extends VariableNumber {

    protected transient double mLastY;
    protected transient int mLastDim;

    public VerticalVelocity(String name) {
        super(name);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        float newValue = 0;
        if(mLastDim == player.dimension) {
            newValue = (float)Math.ceil(player.posY - mLastY);
        }
        boolean updated = (Math.abs(mValue-newValue) > EQUALITY_LIMIT);
        mValue = newValue;
        mLastY = player.posY;
        return updated;
    }
}
