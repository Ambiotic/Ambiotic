package net.graphich.ambiotic.variables;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.EnumSkyBlock;

/**
 * Light value at player coordinates in the world, 3 types
 */
public class LightLevel extends Variable {

    LightTypes mType;

    public LightLevel(String name, LightTypes type) {
        super(name);
        mType = type;
    }

    @Override
    public void update(TickEvent event) {
        int x, y, z;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY;
        z = (int) mPlayer.posZ;
        switch (mType) {
            case SUN:
                mValue = (int) (mWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z) * mWorld.getSunBrightness(1.5f));
                break;
            case LAMP:
                mValue = mWorld.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
                break;
            case TOTAL:
                mValue = mWorld.getBlockLightValue(x, y, z);
                break;
        }
    }

    public enum LightTypes {SUN, LAMP, TOTAL}
}
