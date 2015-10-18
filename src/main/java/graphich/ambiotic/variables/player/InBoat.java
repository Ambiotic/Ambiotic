package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;

public class InBoat extends VariableBool {

    public InBoat(String name) {
        super(name);
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Entity riding = player.ridingEntity;
        boolean newValue;
        newValue = (riding != null && riding instanceof EntityBoat);
        if(newValue != mValue) {
            mValue = newValue;
            return true;
        }
        return false;
    }
}
