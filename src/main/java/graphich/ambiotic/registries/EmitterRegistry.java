package graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.emitters.SoundEmitter;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Macro;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class EmitterRegistry {
    public static EmitterRegistry INSTANCE = new EmitterRegistry();

    protected HashMap<String, SoundEmitter> mRegistry;
    protected boolean mFrozen = false;
    protected boolean mGamePaused = false;

    protected EmitterRegistry() {
        mRegistry = new LinkedHashMap<String, SoundEmitter>();
    }

    public void register(SoundEmitter sound) {
        if(mFrozen) {
            //TODO: Exception
            return;
        }

        if(mRegistry.containsKey(sound.name())) {
            //Log? Exception?
            return;
        }

        mRegistry.put(sound.name(),sound);
    }

    public void reset() {
        mFrozen = false;
        mRegistry.clear();
    }

    public ArrayList<String> sounds()
    {
        ArrayList<String> soundlist = new ArrayList<String>();
        for(SoundEmitter emitter : mRegistry.values())
            soundlist.add(emitter.sound());
        return soundlist;
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/emitterlist.json");

        Ambiotic.logger().info("Loading event include list file '" + rl + "'");
        String[] includeList = null;
        try {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(Helpers.resourceAsStreamReader(rl));
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            includeList = gson.fromJson(json,String[].class);
        } catch (IOException ex) {
            Ambiotic.logger().error("Error reading '" + rl + " : " + ex.getMessage());
            return;
        }

        for(String include : includeList) {
            rl = new ResourceLocation(include);
            Ambiotic.logger().info("Loading event include '" + rl + "'");
            load(rl);
        }

        Ambiotic.logger().info("Expanding emitter macros");
        Map<String, Macro> macros = VariableRegistry.INSTANCE.macros();
        for(SoundEmitter emitter : mRegistry.values()) {
            emitter.expandMacros(macros);
        }
    }

    protected void load(ResourceLocation rl)  {
        JsonArray events = null;
        try {
            events = Helpers.getRootJsonArray(rl);
        } catch (IOException ex) {
            Ambiotic.logger().warn("Can't load '" + rl + "' : " + ex.getMessage());
            return;
        }
        Gson gson = Ambiotic.gson();
        int eventNo = -1;
        for(JsonElement eventElm : events) {
            try {
                eventNo += 1;
                SoundEmitter event = gson.fromJson(eventElm, SoundEmitter.class);
                register(event);
            } catch (StrictJsonException ex) {
                Ambiotic.logger().warn("Skipping sound event # " + eventNo + " : " + ex.getMessage());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.isCanceled() || Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null)
            return;
        if(mGamePaused)
            return;
        SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
        for(SoundEmitter emitter : mRegistry.values()) {
            ISound emitted = emitter.emit();
            if(emitted != null && !handler.isSoundPlaying(emitted)) {
                Ambiotic.logger().info("Playing "+emitter.name());
                handler.playSound(emitted);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        // gui being null means the last gui opened was closed
        if(event.gui == null) {
            mGamePaused = false;
            return;
        }
        if(event.gui.doesGuiPauseGame())
            mGamePaused = true;
    }

    public void subscribeAll() {
        mFrozen = true;
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

}
