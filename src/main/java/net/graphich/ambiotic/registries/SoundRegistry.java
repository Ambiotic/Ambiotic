package net.graphich.ambiotic.registries;

import com.google.gson.*;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.sounds.FloatProvider;
import net.graphich.ambiotic.util.Helpers;
import net.graphich.ambiotic.sounds.AmbioticSoundEvent;
import net.graphich.ambiotic.util.StrictJsonException;
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
                AmbioticSoundEvent event = gson.fromJson(eventElm, AmbioticSoundEvent.class);
                register(event);
            } catch (StrictJsonException ex) {
                Ambiotic.logger().warn("Skipping sound event # " + eventNo + " : " + ex.getMessage());
            }
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
