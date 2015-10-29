package graphich.ambiotic.util;

import graphich.ambiotic.variables.Macro;

import java.util.Map;

public interface IScripted {
    public void expandMacros(Map<String, Macro> macros);
}
