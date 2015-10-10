package graphich.ambiotic.emitters;


import com.typesafe.config.ConfigException;
import cpw.mods.fml.relauncher.ReflectionHelper;
import graphich.ambiotic.main.Ambiotic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundSystem;
import paulscode.sound.Library;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundLoaderThread extends Thread {
    protected Map<String,Boolean> mLoaded = new HashMap<String, Boolean>();
    protected boolean mFinishedLoading = false;

    public boolean loadFinished() {
        return mFinishedLoading;
    }

    public SoundLoaderThread(List<String> toload) {
        mLoaded = new HashMap<String, Boolean>();
        for(String sound : toload)
            mLoaded.put(sound,false);
    }

    public boolean soundLoaded(String sound) {
        if(mLoaded == null)
            return false;
        if(mLoaded.containsKey(sound))
            return mLoaded.get(sound);
        return false;
    }

    public void run() {
        setPriority(1);
        Minecraft minecraft = Minecraft.getMinecraft();
        SoundManager man =  (SoundManager) ReflectionHelper.getPrivateValue(
            SoundHandler.class,
            minecraft.getSoundHandler(),
            new String[]{"sndManager", "field_147694_f"}
        );
        SoundSystem sys = ReflectionHelper.getPrivateValue(
            SoundManager.class,
            man,
            new String[] { "sndSystem", "field_148620_e" }
        );
        Library lib = null;
        while(lib == null) {
            lib = ReflectionHelper.getPrivateValue(SoundSystem.class, sys, new String[] { "soundLibrary" });
            try {
                sleep(1L);
            } catch(InterruptedException ex) {
                Ambiotic.logger().info("Killing sound load thread.");
                return;
            }
        }
        load(man,lib);
    }

    protected void load(SoundManager man, Library lib) {
        Ambiotic.logger().info("Loading sounds in sub thread...");
        for(String sound : mLoaded.keySet()) {
            if(mLoaded.get(sound))
                continue;
            ResourceLocation resloc = new ResourceLocation(sound);
            SoundEventAccessorComposite sac = man.sndHandler.getSound(resloc);
            if(sac != null) {
                SoundPoolEntry spe = sac.func_148720_g();
                resloc = spe.getSoundPoolEntryLocation();
            }
            Method getURL = ReflectionHelper.findMethod(
                SoundManager.class,
                null,
                new String[]{"getURLForSoundResource", "func_148612_a"},
                new Class[]{ResourceLocation.class}
            );
            try {
                FilenameURL fnurl = new FilenameURL((URL) getURL.invoke(null, new Object[]{resloc}), resloc.toString());
                lib.loadSound(fnurl);
                mLoaded.put(sound, true);
                sleep(1L);
            } catch(InterruptedException ex) {
                Ambiotic.logger().info("Killing sound load thread.");
                return;
            } catch(NullPointerException ex) {
            } catch(IllegalAccessException ex) {
            } catch(InvocationTargetException ex) {
                Ambiotic.logger().error("Error when loading "+sound+" : ");
                ex.printStackTrace();
            }
        }
        mFinishedLoading = true;
        Ambiotic.logger().info("Done loading sounds");
    }
}
