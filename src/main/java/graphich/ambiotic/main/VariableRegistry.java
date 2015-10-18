package graphich.ambiotic.main;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.scanners.BlockScanner;
import graphich.ambiotic.scanners.Scanner;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.IVariable;
import graphich.ambiotic.variables.Macro;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableScanning;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import java.util.*;

/**
 * Holds and updates instances of variables
 */
public class VariableRegistry {

    public static VariableRegistry INSTANCE = new VariableRegistry();

    protected HashMap<TickRate, List<IVariable>> mUpdates;
    protected HashMap<String, IVariable> mVariableLookup;
    protected HashMap<String, Macro> mMacroLookup;
    protected boolean mFrozen = false;

    protected VariableRegistry() {
        mUpdates = new HashMap<TickRate, List<IVariable>>();
        mVariableLookup = new HashMap<String, IVariable>();
        mMacroLookup = new HashMap<String, Macro>();
    }

    public List<String> names() {
        List<String> names = Arrays.asList(mVariableLookup.keySet().toArray(new String[0]));
        Collections.sort(names);
        return names;
    }

    public void reset() {
        mFrozen = false;
        mUpdates.clear();
        mVariableLookup.clear();
        mMacroLookup.clear();
    }

    public List<String> macroSymbols() {
        List<String> macros = new ArrayList<String>();
        for(Macro macro : mMacroLookup.values()) {
            macros.add(macro.symbol());
        }
        return macros;
    }

    public List<String> fullVariableNames() {
        List<String> names = new ArrayList<String>();
        for(IVariable var : mVariableLookup.values())
            names.add(var.name());
        return names;
    }

    public void expandMacroMacros() {
        List<String> broken = new ArrayList<String>();
        for(Macro macro : mMacroLookup.values()) {
            macro.expandMacros(mMacroLookup);
            if(macro.code().contains("#")) {
                Ambiotic.logger().warn("Macro '"+macro.name()+"' could not be expanded.");
                broken.add(macro.name());
            }
        }
        for(String bad : broken)
            mMacroLookup.remove(bad);
    }

    public void registerMacro(Macro macro) {
        if (mFrozen) {
            //TODO: throw exception
            return;
        }
        if(mMacroLookup.containsKey(macro.name())) {
            //TODO: throw exception
            return;
        }
        mMacroLookup.put(macro.name(), macro);
    }

    public void load() {
        JsonArray section = Ambiotic.engineSection("Variables").getAsJsonArray();
        if(section != null)
            loadVariables(section);

        if(Ambiotic.engineBoolean("DefaultVars", false))
            loadDefaultVariables();

        section = Ambiotic.engineSection("Macros").getAsJsonArray();
        if(section != null)
            loadMacros(section);

        expandMacroMacros();
    }

    protected void loadVariables(JsonArray variableList) {
        Ambiotic.logger().info("Loading variable definitions");
        //Deserialize and registerVariable each variable
        Gson gson = Ambiotic.gson();
        int variablePos = 0;
        for(JsonElement element : variableList) {
            Variable variable = null;
            String errPrefix = "Skipping variable # " + variablePos + " because ";

            //Fails strict json
            try {
                variable = gson.fromJson(element, Variable.class);
            } catch (StrictJsonException ex) {
                Ambiotic.logger().error(errPrefix + " it's invalid : " + ex.getMessage());
                continue;
            }

            //Variable name is taken
            if(mVariableLookup.containsKey(variable.name())) {
                Ambiotic.logger().error(errPrefix + " another is already registered with name '"+variable.name()+"'");
                continue;
            }

            // Need to link block counter to block scanner
            if(variable instanceof VariableScanning) {
                VariableScanning scanning = (VariableScanning) variable;
                Scanner scanner = ScannerRegistry.INSTANCE.scanner(scanning.getScannerName());
                if(scanner == null || !(scanner instanceof BlockScanner)) {
                    Ambiotic.logger().error(errPrefix + " no block scanner named '"+scanning.getScannerName()+"' is registered");
                    continue;
                }
                scanning.linkToScanner((BlockScanner)scanner);
            }
            //Finally registerVariable variable
            registerVariable(variable);
            Ambiotic.logger().debug("Loaded variable : \n" + variable);
            variablePos += 1;
        }
    }

    protected void loadDefaultVariables() {
        Ambiotic.logger().info("Loading default variable definitions");
        for(Variable var : Variable.defaults())
            registerVariable(var);
    }

    protected void loadMacros(JsonArray macroList) {
        Ambiotic.logger().info("Loading macro definitions");

        Gson gson = Ambiotic.gson();
        int macroPos = 0;
        for(JsonElement element : macroList) {
            Macro macro = null;
            String errPrefix = "Skipping macro # " + macroPos + " because ";
            //Fails strict json
            try {
                macro = gson.fromJson(element, Macro.class);
            } catch (StrictJsonException ex) {
                Ambiotic.logger().error(errPrefix + " it's invalid : "+ex.getMessage());
                continue;
            }
            if(mMacroLookup.containsKey(macro.name())) {
                Ambiotic.logger().error(errPrefix + " another is already registered with name '"+macro.name()+"'");
                continue;
            }
            registerMacro(macro);
            Ambiotic.logger().debug("Loaded variable : \n" + macro);
            macroPos += 1;
        }
    }

    public Map<String, Macro> macros() {
        return mMacroLookup;
    }

    public void subscribeAll() {
        //To reduce congestion, variables only have update() called from this classes onTick()
        FMLCommonHandler.instance().bus().register(VariableRegistry.INSTANCE);
        MinecraftForge.EVENT_BUS.register(VariableRegistry.INSTANCE);
        mFrozen = true;
    }

    public Object value(String name) {
        IVariable var = mVariableLookup.get(name);
        if (var != null) {
            return var.value();
        } else {
            //Log? Exception?
            return null;
        }
    }

    public void registerVariable(IVariable variable) {
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
            mUpdates.put(key, new ArrayList<IVariable>());
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
    public void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (mUpdates == null || !mFrozen || player == null)
            return;
        if(Minecraft.getMinecraft().theWorld == null)
            return;
        if(player.posY <= 0)
            return;
        StringBuilder code = new StringBuilder();
        boolean updated = false;
        for (TickRate rate : mUpdates.keySet()) {
            rate.tick();
            if (rate.trigger()) {
                for (IVariable var : mUpdates.get(rate)) {
                    if(var.updateValue(event))
                    {
                        updated = true;
                        code.append(var.updateJS());
                    }
                }
            }
        }
        if(!updated)
            return;

        Ambiotic.evalJS(code.toString());
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        freeze();
    }

    public void updateJSAll() {
        StringBuilder code = new StringBuilder();
        for (IVariable v : mVariableLookup.values())
        {
            code.append(v.updateJS());
        }
        Ambiotic.evalJS(code.toString());
    }

    public void initializeJSAll() {
        String nmsjs = "";
        String varjs = "";
        for (IVariable v : mVariableLookup.values())
        {
            String nmsinit = v.namespace()+" = {};\n";
            if(nmsjs.indexOf(nmsinit) == -1)
                nmsjs += nmsinit;
            varjs += v.initializeJS()+"\n";
        }
        //Setup namespaces
        Ambiotic.evalJS(nmsjs);
        //Initialize all variables
        Ambiotic.evalJS(varjs);
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
