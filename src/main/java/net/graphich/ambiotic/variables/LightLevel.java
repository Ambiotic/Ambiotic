package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.EnumSkyBlock;

/**
 * Light value at player coordinates in the world, 3 types
 */
public class LightLevel extends PlayerVariable {

    LightTypes mType;

    public LightLevel(String name, LightTypes type) {
        super(name);
        mType = type;
    }

    @Override
    public boolean update(TickEvent event) {
        int x, y, z, newValue;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY;
        z = (int) mPlayer.posZ;
        newValue = 0;
        switch (mType) {
            case SUN:
                newValue = (int) (mWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) * mWorld.getSunBrightness(1.5f));
                break;
            case LAMP:
                newValue = mWorld.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
                break;
            case TOTAL:
                newValue = mWorld.getBlockLightValue(x, y, z);
                break;
            case MAXSUN:
                newValue = (int) (mWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
                break;
        }
        boolean updated = (mValue != newValue);
        mValue = newValue;
        return updated;
    }

    public enum LightTypes {SUN, LAMP, TOTAL, MAXSUN}
}
