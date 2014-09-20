package net.graphich.ambiotic.registries;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.variables.*;
import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

/**
 * Holds and updates instances of variables
 */
public class VariableRegistry {

    protected static VariableRegistry INSTANCE = new VariableRegistry();

    protected HashMap<TickRate, List<Variable>> mUpdates;
    protected HashMap<String, Variable> mVariableLookup;
    protected boolean mFrozen;

    protected VariableRegistry() {
        mUpdates = new HashMap<TickRate, List<Variable>>();
        mVariableLookup = new HashMap<String, Variable>();
    }

    public static VariableRegistry instance() {
        return INSTANCE;
    }

    protected void initBuiltIns(EntityPlayer player) {
        int tpt = 1;
        register(new CanRainOn("CanRainOn", player), tpt);
        register(new CanSeeSky("CanSeeSky", player), tpt);
        register(new IsRaining("IsRaining", player), tpt);
        register(new LightLevel("NaturalLight", player, LightLevel.LightTypes.SUN), tpt);
        register(new LightLevel("TorchLight", player, LightLevel.LightTypes.LAMP), tpt);
        register(new LightLevel("TotalLight", player, LightLevel.LightTypes.TOTAL), tpt);
        register(new MoonPhase("MoonPhase", player), tpt);
        register(new PlayerCoordinate("PlayerX", player, PlayerCoordinate.Coordinates.X), tpt);
        register(new PlayerCoordinate("PlayerY", player, PlayerCoordinate.Coordinates.Y), tpt);
        register(new PlayerCoordinate("PlayerZ", player, PlayerCoordinate.Coordinates.Z), tpt);
        register(new PlayerCoordinate("PlayerDim", player, PlayerCoordinate.Coordinates.DIM), tpt);
        register(new RainStrength("RainStrength", player, 1000), tpt);
        register(new ThunderStrength("ThunderStrength", player, 1000), tpt);
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
        if(mVariableLookup.containsKey(variable.name())) {
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

        for (TickRate rate : mUpdates.keySet()) {
            rate.tick();
            if (rate.trigger()) {
                for (Variable var : mUpdates.get(rate)) {
                    var.update(event);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        initBuiltIns(event.player);
        freeze();
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
