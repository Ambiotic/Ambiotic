package graphich.ambiotic.emitters;

import graphich.ambiotic.variables.macro.Macro;

import java.util.Collection;

public interface IScripted {
    public void expandMacros(Collection<Macro> macros);
}
