package graphich.ambiotic.variables.player;

import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableString;
import net.minecraft.client.Minecraft;

public class DimensionName extends VariableString {
    public DimensionName(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
    }

    @Override
    public boolean updateValue(TickEvent event) {
        return setNewValue(Minecraft.getMinecraft().theWorld.provider.getDimensionName());
    }
}
