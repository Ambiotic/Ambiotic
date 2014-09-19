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
    protected CuboidPointIterator mPoints;
    protected int mBlocksPerTick;
    protected boolean mScanFinished;
    protected int mScanDimension; //Dimension the scan was started in

    protected Point mLastPlaced; //This is a hack because forge is retarded

    public BlockScanner(int blocksPerTick) {
        mBlocksPerTick = blocksPerTick;
        mCounts = new HashMap<Integer, Integer>();
        mScanFinished = false;
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

    protected void continueScan() {
        Point point = mPoints.next();
        int checked = 0;
        while (point != null && checked < mBlocksPerTick) {
            checked++;
            int blockId = Block.getIdFromBlock(mPlayer.worldObj.getBlock(point.x, point.y, point.z));
            if (mCounts.containsKey(blockId)) {
                mCounts.put(blockId, mCounts.get(blockId) + 1);
            }
            point = mPoints.next();
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

    protected void resetScan() {
        Point oldCenter = mPoints.center();
        int x = (int) mPlayer.posX;
        int dx = x - oldCenter.x;
        int y = (int) mPlayer.posY;
        int dy = y - oldCenter.y;
        int z = (int) mPlayer.posZ;
        int dz = z - oldCenter.z;
        if (dx == 0 && dy == 0 && dz == 0 && mScanDimension == mPlayer.dimension)
            return;
        mScanFinished = false;
        mPoints = new CuboidPointIterator(x, y, z, 32, 16, 32);
        mScanDimension = mPlayer.dimension;
        resetBlockCounts();
    }

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        mPlayer = event.player;
        int x = (int) mPlayer.posX;
        int y = (int) mPlayer.posY;
        int z = (int) mPlayer.posZ;
        mPoints = new CuboidPointIterator(x, y, z, 32, 16, 32);
        resetScan();
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (mPlayer == null || mPoints == null || event.phase != TickEvent.Phase.START) {
            return;
        }
        if (!mScanFinished && mScanDimension == mPlayer.dimension) {
            continueScan();
        } else {
            resetScan();
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
        mLastPlaced = null;
    }

    //This is a hack until we get a proper PlaceEvent
    // which I think is slated for the 1.8 version of Forge
    @SubscribeEvent
    public void onBlockPlace(PlayerInteractEvent event) {
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
