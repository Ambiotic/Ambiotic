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

    protected boolean mScanFinished = false;
    protected int mBlocksPerTick = 0;
    protected int mTicksSinceFull = 0;

    protected CuboidPointIterator mFullRange;

    protected int mXSize = 0;
    protected int mYSize = 0;
    protected int mZSize = 0;

    //Last tick's player coordinates
    protected int mLastX = 0;
    protected int mLastY = 0;
    protected int mLastZ = 0;
    protected int mLastDimension = 0;

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
    public ArrayList<Integer> registerBlocks(String key) {
        ArrayList<ItemStack> items = OreDictionary.getOres(key);
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        if (items.size() > 0) {
            for (ItemStack is : items) {
                int blockId = Block.getIdFromBlock(Block.getBlockFromItem(is.getItem()));
                registerBlock(blockId);
                blockIds.add(blockId);
            }
        } else if (GameData.getBlockRegistry().containsKey(key)) {
            int blockId = GameData.getBlockRegistry().getId(key);
            registerBlock(blockId);
            blockIds.add(blockId);
        }
        return blockIds;
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

    protected void updateScan(Cuboid newVolume, Cuboid oldVolume, Cuboid intersect) {
        ComplementsPointIterator newInRange = new ComplementsPointIterator(newVolume, intersect);
        ComplementsPointIterator newOutOfRange = new ComplementsPointIterator(oldVolume, intersect);
        Point point = newOutOfRange.next();
        // Subtract out of range blocks
        while (point != null) {
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            addToCount(blockId,-1);
            point = newOutOfRange.next();
        }
        point = newInRange.next();
        while (point != null) {
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            addToCount(blockId,1);
            point = newInRange.next();
        }
        mScanFinished = true;
    }

    protected void continueFullScan() {
        Point point = mFullRange.next();
        int checked = 0;

        while (point != null && checked < mBlocksPerTick) {
            checked++;
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            addToCount(blockId,1);
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
        mTicksSinceFull = 0;
        mLastX = (int) mPlayer.posX;
        mLastY = (int) mPlayer.posY;
        mLastZ = (int) mPlayer.posZ;
        mLastDimension = mPlayer.dimension;
        mScanFinished = false;
        mFullRange = new CuboidPointIterator(mLastX, mLastY, mLastZ, mXSize, mYSize, mZSize);
        resetBlockCounts();
    }

    protected void addToCount(Integer  blockId, Integer count) {
        if(mCounts.containsKey(blockId)) {
            int c = mCounts.get(blockId);
            c += count;
            if(c < 0) mCounts.put(blockId,0);
            else mCounts.put(blockId,c);
        }
    }

    @SubscribeEvent
    public void onLeafDecay(BlockEvent.HarvestDropsEvent event) {
        if(event.isCanceled()) {
            return;
        }
        //Fake player means leaf decay / environmental cause
        if(event.harvester != mPlayer) {
            int blockId = Block.getIdFromBlock(event.block);
            addToCount(blockId,-1);
        }
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.isCanceled()) {
            return;
        }
        mPlayer = event.player;
        resetFullScan();

    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        // No scan state
        if (mPlayer == null || event.isCanceled()) {
            return;
        }
        mTicksSinceFull += 1;
        int x, y, z;
        x = (int) mPlayer.posX;
        y = (int) mPlayer.posY;
        z = (int) mPlayer.posZ;
        Cuboid oldVolume = new Cuboid(mLastX, mLastY, mLastZ, mXSize, mYSize, mZSize);
        Cuboid newVolume = new Cuboid(x, y, z, mXSize, mYSize, mZSize);
        Cuboid intersect = oldVolume.intersection(newVolume);
        boolean playerMoved = (x != mLastX || y != mLastY || z != mLastZ);

        //Player went to a new dimension
        boolean fullScanReset = mPlayer.dimension != mLastDimension;
        //Player teleported in the same dimension
        fullScanReset = fullScanReset || intersect == null;
        // Update scanning would be more expensive than rescanning, happens when player is moving very fast
        fullScanReset = fullScanReset || oldVolume.volume() < (oldVolume.volume() - intersect.volume()) * 2;

        mLastX = x;
        mLastY = y;
        mLastZ = z;
        mLastDimension = mPlayer.dimension;

        if (fullScanReset) {
            resetFullScan();
            continueFullScan();
        } else if (!mScanFinished) {
            continueFullScan();
        } else if (playerMoved) {
            updateScan(newVolume,oldVolume,intersect);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled()) {
            return;
        }
        //Need to check if block is within this scanners
        // volume before firing the below code in case
        // of multiplayer?
        int blockId = Block.getIdFromBlock(event.block);
        addToCount(blockId,-1);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        Point where = new Point(event.x, event.y, event.z);
        ItemStack inhand = event.itemInHand;
        if(inhand == null)
            return;
        int placedBlockId = Block.getIdFromBlock(Block.getBlockFromItem(inhand.getItem()));
        int worldBlockId = Block.getIdFromBlock(event.world.getBlock(event.x, event.y, event.z));
        if (placedBlockId == worldBlockId)
            addToCount(placedBlockId,1);
    }

}
