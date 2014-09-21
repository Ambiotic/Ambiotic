package net.graphich.ambiotic.scanners;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jim on 9/14/2014.
 */
public class BlockScanner {

    protected HashMap<Integer, Integer> mCounts;
    protected EntityPlayer mPlayer;

    protected boolean mScanFinished;
    protected int mBlocksPerTick;

    protected CuboidPointIterator mFullRange;

    protected ComplementsPointIterator mNewInRange;
    protected ComplementsPointIterator mNewOutOfRange;

    protected int mXSize = 0;
    protected int mYSize = 0;
    protected int mZSize = 0;

    //Last tick's player coordinates
    protected int mLastX = 0;
    protected int mLastY = 0;
    protected int mLastZ = 0;
    protected int mLastDimension;

    public BlockScanner(int blocksPerTick, int xsize, int ysize, int zsize) {
        mBlocksPerTick = blocksPerTick;
        mCounts = new HashMap<Integer, Integer>();
        mScanFinished = false;
        mXSize = xsize;
        mYSize = ysize;
        mZSize = zsize;
        //Important variables set onLogin event
    }

    //Key in this case is OreDictionary Key or a BlockRegistry Key
    public void registerBlocks(String key) {
        ArrayList<ItemStack> items = OreDictionary.getOres(key);
        if (items.size() > 0) {
            for (ItemStack is : items) {
                int blockId = Block.getIdFromBlock(Block.getBlockFromItem(is.getItem()));
                registerBlock(blockId);
            }
        } else if (GameData.getBlockRegistry().containsKey(key)) {
            int blockId = GameData.getBlockRegistry().getId(key);
            registerBlock(blockId);
        } else {
            throw new IllegalArgumentException("Bad block key '" + key + "'");
        }
    }

    public boolean scanFinished() {
        return mScanFinished;
    }

    protected void registerBlock(int blockId) {
        mCounts.put(blockId, 0);
    }

    protected void resetBlockCounts() {
        for (int blockId : mCounts.keySet()) {
            mCounts.put(blockId, 0);
        }
    }

    protected void updateScan() {
        Point point = mNewOutOfRange.next();
        // Subtract out of range blocks
        while (point != null) {
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            if (mCounts.containsKey(blockId)) {
                int what = mCounts.get(blockId) - 1;
                what = what < 0 ? 0 : what;
                mCounts.put(blockId, what);
            }
            point = mNewOutOfRange.next();
        }
        point = mNewInRange.next();
        while (point != null) {
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            if (mCounts.containsKey(blockId)) {
                mCounts.put(blockId, mCounts.get(blockId) + 1);
            }
            point = mNewInRange.next();
        }
        mScanFinished = true;
    }

    protected void continueFullScan() {
        Point point = mFullRange.next();
        int checked = 0;

        while (point != null && checked < mBlocksPerTick) {
            checked++;
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            if (mCounts.containsKey(blockId)) {
                mCounts.put(blockId, mCounts.get(blockId) + 1);
            }
            point = mFullRange.next();
        }
        if (point == null) {
            mScanFinished = true;
        }
    }

    public Set<Integer> keySet() {
        if (mCounts != null) {
            return mCounts.keySet();
        }
        return new HashSet<Integer>();
    }

    public int getCount(int blockId) {
        if (mCounts != null && mCounts.containsKey(blockId)) {
            return mCounts.get(blockId);
        }
        return -1;
    }

    protected void resetFullScan() {
        mLastX = (int)mPlayer.posX;
        mLastY = (int)mPlayer.posY;
        mLastZ = (int)mPlayer.posZ;
        mLastDimension = mPlayer.dimension;
        mScanFinished = false;
        mFullRange = new CuboidPointIterator(mLastX, mLastY, mLastZ, mXSize, mYSize, mZSize);
        resetBlockCounts();
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        mPlayer = event.player;
        resetFullScan();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        // No scan state
        if (mPlayer == null) {
            return;
        }
        int x,y,z;
        x = (int)mPlayer.posX;
        y = (int)mPlayer.posY;
        z = (int)mPlayer.posZ;
        Cuboid oldVolume = new Cuboid(mLastX,mLastY,mLastZ,mXSize,mYSize,mZSize);
        Cuboid newVolume = new Cuboid(x,y,z,mXSize,mYSize,mZSize);
        Cuboid intersect = oldVolume.intersection(newVolume);
        boolean playerMoved = (x != mLastX || y != mLastY || z != mLastZ);
        boolean fullScanReset = (
                // Player went to a new dimension
                mPlayer.dimension != mLastDimension ||
                // Player teleported in same dimension
                intersect == null ||
                // Update scanning would be more expensive than rescanning
                oldVolume.volume() < (oldVolume.volume() - intersect.volume())*2
        );
        mLastX = x;
        mLastY = y;
        mLastZ = z;
        mLastDimension = mPlayer.dimension;

        if(fullScanReset) {
            //System.out.println("Full scan required ...");
            resetFullScan();
        } else if(!mScanFinished) {
            continueFullScan();
        } else if(playerMoved) {
            //System.out.println("Running update scan "+oldVolume.volume()+" vs "+((oldVolume.volume() - intersect.volume())*2));
            mNewInRange = new ComplementsPointIterator(newVolume,intersect);
            mNewOutOfRange = new ComplementsPointIterator(oldVolume,intersect);
            updateScan();
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled())
            return;
        //Need to check if block is within this scanners
        // volume before firing the below code in case
        // of multiplayer?
        int blockId = Block.getIdFromBlock(event.block);
        if (mCounts.containsKey(blockId)) {
            int c = mCounts.get(blockId);
            c -= 1;
            if (c > 0) mCounts.put(blockId, c);
            else mCounts.put(blockId, 0);
        }
        mLastPlaced = null; //Hack
    }

    //This is a hack until we get a proper PlaceEvent
    // which I think is slated for the 1.8 version of Forge
    protected Point mLastPlaced;
    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent event) {
        if (event.isCanceled())
            return;

        Point where = new Point(event.x, event.y, event.z);
        if (event.isCanceled() || event.useBlock == Event.Result.DENY) {
            return;
        }

        if (mLastPlaced != null && mLastPlaced.equals(where)) {
            mLastPlaced = null;
            return;
        }

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Block what = event.world.getBlock(event.x, event.y, event.z);
            int placedBlockId = Block.getIdFromBlock(Block.getBlockFromItem(event.entityPlayer.getHeldItem().getItem()));
            int worldBlockId = Block.getIdFromBlock(event.world.getBlock(event.x, event.y, event.z));
            if (placedBlockId == worldBlockId && mCounts.containsKey(worldBlockId)) {
                int c = mCounts.get(worldBlockId);
                c += 1;
                mCounts.put(worldBlockId, c);
                mLastPlaced = where;
            }
        }
    }

}
