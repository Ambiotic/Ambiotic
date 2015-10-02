package graphich.ambiotic.util;

import graphich.ambiotic.variables.macro.Macro;

import java.util.Collection;
import java.util.Map;

public interface IScripted {
    public void expandMacros(Map<String,Macro> macros);
}
