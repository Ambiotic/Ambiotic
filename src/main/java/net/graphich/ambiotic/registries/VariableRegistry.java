package net.graphich.ambiotic.registries;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.variables.Variable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.python.util.PythonInterpreter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Holds and updates instances of variables
 */
public class VariableRegistry {

    public static VariableRegistry INSTANCE = new VariableRegistry();

    protected HashMap<TickRate, List<Variable>> mUpdates;
    protected HashMap<String, Variable> mVariableLookup;
    protected boolean mFrozen;
    protected PythonInterpreter mScriptEnv;

    protected VariableRegistry() {
        mUpdates = new HashMap<TickRate, List<Variable>>();
        mVariableLookup = new HashMap<String, Variable>();
        mScriptEnv = null;
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mVariableLookup.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/variables.json");
        JsonParser parser = new JsonParser();
        JsonObject json = null;
        Ambiotic.logger().info("Loading variables file '" + rl + "'");
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            json = parser.parse(isr).getAsJsonObject();
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '" + rl + "' : " + ex.getMessage());
            return;
        }
        for(Map.Entry<String, JsonElement> variable : json.entrySet()) {
            String name = variable.getKey();
            Ambiotic.logger().info("Loading variable '"+name+"'");
            if(!variable.getValue().isJsonObject()) {
                Ambiotic.logger().warn("Skipping variable '" + name + "' it is not an object");
                continue;
            }
            try {
                Variable var = Variable.deserialize(name,variable.getValue().getAsJsonObject());
                register(var); // Need to desiralize ticks
            } catch(JsonError ex) {
                Ambiotic.logger().warn("Skipping variable '"+name+"' : "+ex.getMessage());
            }
        }
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

    public void register(Variable variable) {
        if (mFrozen) {
            //Log? Exception?
            return;
        }
        if (mVariableLookup.containsKey(variable.name())) {
            //Log? Exception?
            return;
        }
        int ticksPerUpdate = variable.ticksPerUpdate();
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
/*        for (Variable v : mVariableLookup.values()) {
            if (v instanceof PlayerVariable) {
                ((PlayerVariable) v).setPlayer(event.player);
            }
        }*/
        freeze();
    }

    public void initScriptEnv(PythonInterpreter scriptEnv)
    {
        mScriptEnv = scriptEnv;
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
