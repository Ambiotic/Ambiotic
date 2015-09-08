package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.main.Util;
import net.graphich.ambiotic.scanners.BlockScanner;
import net.graphich.ambiotic.variables.BlockCounter;
import net.graphich.ambiotic.variables.Variable;
import net.graphich.ambiotic.variables.VariableSerializer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import javax.script.ScriptException;
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
    protected boolean mFrozen = false;

    protected VariableRegistry() {
        mUpdates = new HashMap<TickRate, List<Variable>>();
        mVariableLookup = new HashMap<String, Variable>();
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mVariableLookup.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/variables.json");
        JsonParser parser = new JsonParser();
        JsonArray variableList = null;
        Ambiotic.logger().info("Loading variables file '" + rl + "'");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Variable.class, new VariableSerializer());
        Gson gson = gsonBuilder.create();
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            variableList = parser.parse(isr).getAsJsonArray();
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '" + rl + "' : " + ex.getMessage());
            return;
        }
        //Deserialize and register each variable
        int variablePos = 0;
        for(JsonElement element : variableList) {
            Variable variable = null;
            String errPrefix = "Skipping variable # " + variablePos;

            try {
                variable = gson.fromJson(element, Variable.class);
            } catch(JsonParseException ex) {
                Ambiotic.logger().error(errPrefix + " Json Error : " + ex.getCause().getMessage());
                continue;
            }

            try {
                variable.validate();
            } catch(Exception ex) {
                Ambiotic.logger().error(errPrefix + " Variable invalid : "+ex.getCause().getMessage());
                continue;
            }

            //Variable name is taken
            if(mVariableLookup.containsKey(variable.name())) {
                Ambiotic.logger().error(errPrefix + " : Variable already registered with name '"+variable.name()+"'");
                continue;
            }

            // Need to register block types with scanner
            if(variable instanceof BlockCounter) {
                BlockCounter counter = (BlockCounter) variable;
                BlockScanner scanner = ScannerRegistry.INSTANCE.scanner(counter.getScannerName());
                if(scanner == null) {
                    Ambiotic.logger().error(errPrefix + " No scanner named '"+counter.getScannerName()+"' registered");
                    continue;
                }
                List<Integer> blockIds = new ArrayList<Integer>();
                int oldsize = 0;
                for(String spec : counter.getBlockSpecs()) {
                    blockIds.addAll(Util.buildBlockIdList(spec));
                    if(oldsize == blockIds.size())
                        Ambiotic.logger().warn("In block counter variable '"+counter.name()+"' : Ignoring bad block ID '"+spec+"'");
                    oldsize = blockIds.size();
                }
                if(oldsize == 0) {
                    Ambiotic.logger().error(errPrefix+" No valid blocks ID were specified");
                    continue;
                }
                // Link scanner and variable
                counter.setScanner(scanner);
                counter.addBlockIds(blockIds);
                scanner.registerBlockIds(blockIds);
            }
            //Finally register variable
            register(variable);
            Ambiotic.logger().info("Loaded variable : \n"+variable);
            variablePos += 1;
        }
    }

    public void subscribeAll() {
        //To reduce congestion, variables only have update() called from this classes onTick()
        FMLCommonHandler.instance().bus().register(VariableRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(VariableRegistry.INSTANCE);
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
                        code.append(var.jsAssignCode());
                    }
                }
            }
        }
        if(!updated)
            return;

        try {
            Ambiotic.scripter().eval(code.toString());
        } catch(ScriptException ex) {
            Ambiotic.logger().error("Script error when updating variables : "+ex.getMessage());
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        freeze();
    }

    public void refreshScripter() {
        StringBuilder code = new StringBuilder();
        for (Variable v : mVariableLookup.values())
        {
            code.append(v.jsAssignCode());
        }
        try {
            Ambiotic.scripter().eval(code.toString());
        } catch (ScriptException ex) {
            Ambiotic.logger().error("Script error when refreshing script environment : "+ex.getMessage());
        }
    }

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
