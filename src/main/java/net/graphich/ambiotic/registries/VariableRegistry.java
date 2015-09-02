package net.graphich.ambiotic.registries;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.variables.PlayerVariable;
import net.graphich.ambiotic.variables.Variable;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.util.*;

/**
 * Holds and updates instances of variables
 */
public class VariableRegistry {

    protected static VariableRegistry INSTANCE = new VariableRegistry();

    protected HashMap<TickRate, List<Variable>> mUpdates;
    protected HashMap<String, Variable> mVariableLookup;
    protected boolean mFrozen;
    protected PythonInterpreter mScriptEnv;

    protected VariableRegistry() {
        mUpdates = new HashMap<TickRate, List<Variable>>();
        mVariableLookup = new HashMap<String, Variable>();
        mScriptEnv = new PythonInterpreter();
    }

    public static VariableRegistry instance() {
        return INSTANCE;
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mVariableLookup.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public int value(String name) {
        Variable var = mVariableLookup.get(name);
        if (var != null) {
            return var.value();
        } else {
            //Log? Exception?
            return 0;
        }
    }

    public void register(Variable variable, int ticksPerUpdate) {
        if (mFrozen) {
            //Log? Exception?
            return;
        }
        if (mVariableLookup.containsKey(variable.name())) {
            //Log? Exception?
            return;
        }
        ticksPerUpdate = Math.abs(ticksPerUpdate);
        TickRate key = new TickRate(ticksPerUpdate);
        if (!mUpdates.containsKey(key)) {
            mUpdates.put(key, new ArrayList<Variable>());
        }
        mUpdates.get(key).add(variable);
        mVariableLookup.put(variable.name(), variable);
    }

    /**
     * Do not allow further registration. Once frozen
     * a registry
     */
    public void freeze() {
        mFrozen = true;
    }

    public boolean isFrozen() {
        return mFrozen;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mUpdates == null || !mFrozen)
            return;
        StringBuilder code = new StringBuilder();
        boolean updated = false;
        for (TickRate rate : mUpdates.keySet()) {
            rate.tick();
            if (rate.trigger()) {
                for (Variable var : mUpdates.get(rate)) {
                    if(var.update(event))
                    {
                        updated = true;
                        code.append(var.pycode());
                    }
                }
            }
        }
        if (updated)
            mScriptEnv.exec(code.toString());
        /** How to use mScriptEnv to eval conditons
         PyObject thing = mScriptEnv.eval("TimeOfDay <= 30");
         System.out.println("Time of Day : "+thing.__int__());
         */
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        // Bind player variables to player and freeze, no more variables can be registered
        for (Variable v : mVariableLookup.values()) {
            if (v instanceof PlayerVariable) {
                ((PlayerVariable) v).setPlayer(event.player);
            }
        }
        freeze();
    }

    protected void initScriptEnv()
    {
        StringBuilder code = new StringBuilder();
        for (Variable v : mVariableLookup.values())
        {
            code.append(v.pycode());
        }
        mScriptEnv.exec(code.toString());
    }

    /**
     * Used to track when certain variables should have
     * update() called.
     */
    protected class TickRate {
        private int mTicksSinceTrigger = -1;
        private int mTicksPerTrigger = 1;

        public TickRate(int ticksPerTrigger) {
            mTicksPerTrigger = ticksPerTrigger;
        }

        protected void rate(int ticksPerTrigger) {
            mTicksPerTrigger = ticksPerTrigger;
        }

        protected void tick() {
            mTicksSinceTrigger += 1;
        }

        protected boolean trigger() {
            if (mTicksSinceTrigger % mTicksPerTrigger == 0) {
                mTicksSinceTrigger = 0;
                return true;
            }
            return false;
        }

        /**
         * I want to use these as keys in a HashMap, one of these is
         * equivalent to another with the same mTicksPerTrigger
         *
         * @return mTicksPerTrigger
         */
        @Override
        public int hashCode() {
            return mTicksPerTrigger;
        }

        @Override
        public boolean equals(Object o) {

            if (!(o instanceof TickRate)) {
                return false;
            }
            return mTicksPerTrigger == ((TickRate) o).mTicksPerTrigger;
        }
    }
}
