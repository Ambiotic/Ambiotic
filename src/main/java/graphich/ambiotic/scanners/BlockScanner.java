package graphich.ambiotic.scanners;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.StrictJsonException;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

import java.util.*;

public class BlockScanner extends Scanner {

    @SerializedName("XSize")
    protected Integer mXSize = 0;
    @SerializedName("YSize")
    protected Integer mYSize = 0;
    @SerializedName("ZSize")
    protected Integer mZSize = 0;
    @SerializedName("BlocksPerTick")
    protected Integer mBlocksPerTick = 0;

    protected transient HashMap<Integer, Integer> mCounts;

    protected transient boolean mScanFinished = false;
    protected transient int mTicksSinceFull = 0;

    protected transient CuboidPointIterator mFullRange;

    //Last tick's player coordinates
    protected transient int mLastX = 0;
    protected transient int mLastY = 0;
    protected transient int mLastZ = 0;
    protected transient int mLastDimension = 0;

    public BlockScanner(String name, int blocksPerTick, int xsize, int ysize, int zsize) {
        mBlocksPerTick = blocksPerTick;
        mXSize = xsize;
        mYSize = ysize;
        mZSize = zsize;
        mName = name;
        initialize();
    }

    @Override
    public void validate() throws StrictJsonException {
        super.validate();
        if(mXSize == null || mXSize <= 0)
            throw new StrictJsonException("XSize is required and must be greater than 0");
        if(mYSize == null || mYSize <= 0)
            throw new StrictJsonException("YSize is required and must be greater than 0");
        if(mZSize == null || mZSize <= 0)
            throw new StrictJsonException("ZSize is required and must be greater than 0");
        if(mBlocksPerTick != null && mBlocksPerTick <= 0)
            throw new StrictJsonException("BlocksPerTick must be greater than 0");
    }

    @Override
    public void initialize() {
        // BlocksPerTick is optional
        if(mBlocksPerTick == null)
            mBlocksPerTick = (mXSize*mZSize*mYSize)/4;
        //Nonserialized stuff must be initialized
        mCounts = new HashMap<Integer, Integer>();
        mScanFinished = false;
    }


    public String name() {
        return mName;
    }

    public int size() {
        return mXSize*mYSize*mZSize;
    }

    public boolean scanFinished() {
        return mScanFinished;
    }

    protected void registerBlock(int blockId) {
        mCounts.put(blockId, 0);
    }

    public void registerBlockIds(List<Integer> blockIds) {
        for(Integer id : blockIds) {
            registerBlock(id);
        }
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
        World world =  Minecraft.getMinecraft().theWorld;
        // Subtract out of range blocks
        while (point != null) {
            int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
            addToCount(blockId,-1);
            point = newOutOfRange.next();
        }
        point = newInRange.next();
        while (point != null) {
            int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
            addToCount(blockId,1);
            point = newInRange.next();
        }
        mScanFinished = true;
    }

    @Override
    public String constantsJS() {
        String js = "";
        js += mName +".XSize = "+mXSize+";\n";
        js += mName +".YSize = "+mYSize+";\n";
        js += mName +".ZSize = "+mZSize+";\n";
        js += mName +".Size = "+mXSize*mYSize*mZSize+";\n";
        return js;
    }

    protected void continueFullScan() {
        Point point = mFullRange.next();
        int checked = 0;

        World world = Minecraft.getMinecraft().theWorld;

        while (point != null && checked < mBlocksPerTick) {
            checked++;
            int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
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
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        mTicksSinceFull = 0;
        mLastX = (int) player.posX;
        mLastY = (int) player.posY;
        mLastZ = (int) player.posZ;
        mLastDimension = player.dimension;
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
    public void onTick(TickEvent.ClientTickEvent event) {
        // No scan state
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null || player.worldObj == null || event.isCanceled()) {
            return;
        }
        mTicksSinceFull += 1;
        int x, y, z;
        x = (int) player.posX;
        y = (int) player.posY;
        z = (int) player.posZ;
        Cuboid oldVolume = new Cuboid(mLastX, mLastY, mLastZ, mXSize, mYSize, mZSize);
        Cuboid newVolume = new Cuboid(x, y, z, mXSize, mYSize, mZSize);
        Cuboid intersect = oldVolume.intersection(newVolume);
        boolean playerMoved = (x != mLastX || y != mLastY || z != mLastZ);

        //Player went to a new dimension
        boolean fullScanReset = player.dimension != mLastDimension;
        //Player teleported in the same dimension
        fullScanReset = fullScanReset || intersect == null;
        // Update scanning would be more expensive than rescanning, happens when player is moving very fast
        if(intersect != null)
            fullScanReset = fullScanReset || oldVolume.volume() < (oldVolume.volume() - intersect.volume()) * 2;

        mLastX = x;
        mLastY = y;
        mLastZ = z;
        mLastDimension = player.dimension;

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
        int blockId = Block.getIdFromBlock(event.block);
        addToCount(blockId,-1);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        int placedBlockId = Block.getIdFromBlock(event.block);
        addToCount(placedBlockId,1);
    }

    @SubscribeEvent
    public void onLeafDecay(BlockEvent.HarvestDropsEvent event) {
        if(event.isCanceled()) {
            return;
        }
        //Fake player means leaf decay / environmental cause
        if(event.harvester != Minecraft.getMinecraft().thePlayer) {
            int blockId = Block.getIdFromBlock(event.block);
            addToCount(blockId,-1);
        }
    }
}
