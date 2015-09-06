package net.graphich.ambiotic.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.FMLCommonHandler;
import net.graphich.ambiotic.errors.JsonError;
import net.graphich.ambiotic.main.Ambiotic;
import net.graphich.ambiotic.main.Util;
import net.graphich.ambiotic.sounds.TriggeredSound;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SoundRegistry {
    public static SoundRegistry INSTANCE = new SoundRegistry();

    protected HashMap<String, TriggeredSound> mRegistry;
    protected boolean mFrozen = false;

    protected SoundRegistry() {
        mRegistry = new LinkedHashMap<String, TriggeredSound>();
    }

    public void register(TriggeredSound sound) {
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
        JsonParser parser = new JsonParser();
        JsonArray includes = null;
        Ambiotic.logger().info("Loading main events file '" + rl + "'");
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            includes = parser.parse(isr).getAsJsonArray();
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '" + rl + "' : "+ex.getMessage());
            return;
        }
        int includeno = 0;
        for(JsonElement include : includes) {
            if(!include.isJsonPrimitive() || !include.getAsJsonPrimitive().isString()) {
                Ambiotic.logger().error("Skipping include number "+includeno+" : Bad JSON type, must be string");
                continue;
            }
            String resString = include.getAsString();
            if(!Util.resourceExists(resString)) {
                Ambiotic.logger().error("Skipping '"+resString+"' : No such resource file");
                continue;
            }
            load(new ResourceLocation(resString));
            includeno += 1;
        }
    }

    protected void load(ResourceLocation rl) {
        Ambiotic.logger().info("Loading event include '" + rl +"'");
        JsonParser parser = new JsonParser();
        JsonObject soundevents = null;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            soundevents = parser.parse(isr).getAsJsonObject();
        } catch (Exception ex) {
            Ambiotic.logger().error("Error reading '" + rl + "' : "+ex.getMessage());
            return;
        }
        for(Map.Entry<String, JsonElement> soundevent : soundevents.entrySet()) {
            String name = soundevent.getKey();
            Ambiotic.logger().info("Loading sound event '"+name+"'");
            if(!soundevent.getValue().isJsonObject()) {
                Ambiotic.logger().warn("Skipping sound event '" + name + "' : it is not a JSON object");
                continue;
            }
            try {
                TriggeredSound sound = TriggeredSound.deserialize(name,soundevent.getValue().getAsJsonObject());
                register(sound);
            } catch (JsonError ex) {
                Ambiotic.logger().warn("Skipping sound event '"+name+"' : "+ex.getMessage());
            }
        }
    }

    public void subscribeAll() {
        mFrozen = true;
        for(TriggeredSound sound  : mRegistry.values()) {
            FMLCommonHandler.instance().bus().register(sound);
            MinecraftForge.EVENT_BUS.register(sound);
        }
    }
}
