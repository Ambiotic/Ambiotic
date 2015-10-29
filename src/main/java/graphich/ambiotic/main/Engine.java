package graphich.ambiotic.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import graphich.ambiotic.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Engine implements IResourceManagerReloadListener {
    //Hack to skip "first" reload, resources are always reloaded twice at startup
    protected boolean mPastFirstReload = false;
    protected JsonObject mEngineJson;

    public Engine() {
        mEngineJson = null;
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
    }

    public JsonElement section(String name) {
        if (mEngineJson == null)
            return null;
        if (!mEngineJson.has(name))
            return null;
        return mEngineJson.get(name);
    }

    public boolean useDefaults() {
        if (mEngineJson == null)
            return false;
        if (!mEngineJson.has("DefaultVars"))
            return false;
        JsonElement element = mEngineJson.get("DefaultVars");
        if (!element.isJsonPrimitive())
            return false;
        return element.getAsBoolean();
    }

    public boolean loaded() {
        return mEngineJson != null;
    }

    private void loadAll() {
        mEngineJson = null;
        //Load main JSON
        try {
            Ambiotic.logger().info("Loading engine.json");
            InputStreamReader reader = null;
            JsonParser parser = new JsonParser();
            reader = Helpers.resourceAsStreamReader(new ResourceLocation("ambiotic:engine.json"));
            mEngineJson = (parser.parse(reader)).getAsJsonObject();
        } catch (IOException ex) {
            Ambiotic.logger().error("Load aborting, could not read engine.json : " + ex.getMessage());
            return;
        }

        //Reset all registries
        ScannerRegistry.INSTANCE.reset();
        VariableRegistry.INSTANCE.reset();
        EmitterRegistry.INSTANCE.reset();

        //Load all registry data from json
        ScannerRegistry.INSTANCE.load(this);
        VariableRegistry.INSTANCE.load(this);
        EmitterRegistry.INSTANCE.load(this);

        //Subscribe all event handling classes
        VariableRegistry.INSTANCE.subscribeAll();
        ScannerRegistry.INSTANCE.subscribeAll();
        EmitterRegistry.INSTANCE.subscribeAll();

        //Initialize the scripting environment
        ScriptEngineManager man = new ScriptEngineManager(null);
        Ambiotic.scripter = man.getEngineByName("JavaScript");

        if (mEngineJson.has("HelperJS") && mEngineJson.get("HelperJS").isJsonPrimitive()) {
            String js = mEngineJson.get("HelperJS").getAsString();
            Ambiotic.logger().info("Evaluating javascript helper file");
            try {
                ResourceLocation rl = new ResourceLocation(js);
                InputStreamReader isr = Helpers.resourceAsStreamReader(rl);
                Ambiotic.scripter.eval(isr);
            } catch (IOException ex) {
                Ambiotic.logger().error("Couldn't read helpers.js (" + js + ")");
            } catch (ScriptException ex) {
                Ambiotic.logger().error("Error when executing helpers.js:\n" + ex.getMessage());
            }
        }
        VariableRegistry.INSTANCE.initializeJSAll();
        ScannerRegistry.INSTANCE.initializeConstantJSAll();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resman) {
        //Skip very first reload call, for some stupid reason reload is called twice during
        //plugin / mc init
        if (!mPastFirstReload) {
            mPastFirstReload = true;
            return;
        }
        loadAll();
    }
}
