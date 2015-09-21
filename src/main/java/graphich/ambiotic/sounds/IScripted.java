package graphich.ambiotic.sounds;

import graphich.ambiotic.variables.macro.Macro;

import java.util.Collection;

public interface IScripted {
    public void expandMacros(Collection<Macro> macros);
}
