package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.sounds.FloatProvider;
import net.graphich.ambiotic.util.Helpers;
import net.graphich.ambiotic.sounds.AmbioticSoundEvent;
import net.graphich.ambiotic.util.StrictJsonSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;


import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class SoundRegistry {
    public static SoundRegistry INSTANCE = new SoundRegistry();

    protected HashMap<String, AmbioticSoundEvent> mRegistry;
    protected boolean mFrozen = false;

    protected SoundRegistry() {
        mRegistry = new LinkedHashMap<String, AmbioticSoundEvent>();
    }

    public void register(AmbioticSoundEvent sound) {
        if(mFrozen) {
            //Log? Exception?
            return;
        }

        if(mRegistry.containsKey(sound.name())) {
            //Log? Exception?
            return;
        }

        mRegistry.put(sound.name(),sound);
    }

    public void load() {
        ResourceLocation rl = new ResourceLocation(Ambiotic.MODID, "config/events.json");

        String[] includeList = null;
        try {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(Helpers.resourceAsStreamReader(rl));
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            includeList = gson.fromJson(json,String[].class);
        } catch (Exception ex) {
            Ambiotic.logger().error("Could not read event include list : " + ex.getMessage());
            return;
        }

        for(String include : includeList) {
            rl = new ResourceLocation(include);
            Ambiotic.logger().info("Loading event include '" + rl + "'");
            try {
                load(rl);
            } catch (Exception ex) {
                Ambiotic.logger().error("Loading '" + rl + "' failed : "+ex.getMessage());
                continue;
            }
        }
    }

    protected void load(ResourceLocation rl) throws JsonParseException, IOException {
        JsonArray events = Helpers.getRootJsonArray(rl);
        Gson gson = Ambiotic.gson();
        for(JsonElement eventElm : events) {
            AmbioticSoundEvent event = gson.fromJson(eventElm, AmbioticSoundEvent.class);
            register(event);
        }
    }

    public void subscribeAll() {
        mFrozen = true;
        for(AmbioticSoundEvent sound  : mRegistry.values()) {
            FMLCommonHandler.instance().bus().register(sound);
            MinecraftForge.EVENT_BUS.register(sound);
        }
    }
}
