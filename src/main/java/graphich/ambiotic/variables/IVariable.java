package graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;

public interface IVariable {
    public String initializeJS();
    public String updateJS();
    public Object value();
    public boolean updateValue(TickEvent event);
    public String name();
    public int ticksPerUpdate();
    public String namespace();
}
