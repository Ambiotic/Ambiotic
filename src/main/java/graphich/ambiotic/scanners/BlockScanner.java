package graphich.ambiotic.scanners;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.util.StrictJsonException;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
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
    @SerializedName("YStartsAtPlayer")
    protected Boolean mYStartsAtPlayer = false;

    protected transient HashMap<Integer, Integer> mCounts;

    //Iteration variables
    protected transient boolean mScanFinished = false;
    protected transient int mTicksSinceFull = -1;
    protected transient CuboidPointIterator mFullRange;

    protected transient int mVolume = 0; //xsize*ysize*zsize

    //Last tick's player coordinates
    protected transient int mLastX = 0;
    protected transient int mLastY = 0;
    protected transient int mLastZ = 0;
    protected transient int mLastDimension = 0;

    //Special variables and funcs
    protected transient float mBiomeTemperatureSum = 0;
    public float averageTemperature() { return mBiomeTemperatureSum / mVolume; }
    protected transient float mBiomeHumiditySum = 0;
    public float averageHumidity() { return mBiomeHumiditySum / mVolume; }
    protected transient float mBiomeSalinity = 0;
    public float averageSalinity() { return mBiomeSalinity / mVolume; }
    protected transient float mAverageSunLevel = 0;
    public float averageSunLevel() {return mAverageSunLevel / mVolume; }
    protected transient Map<BiomeDictionary.Type,Integer> mBiomeTagCounts;
    public int biomeTagCount(BiomeDictionary.Type type) {return mBiomeTagCounts.get(type); }

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
        if(mYStartsAtPlayer == null)
            mYStartsAtPlayer = false;
        if(mBlocksPerTick == null)
            mBlocksPerTick = (mXSize*mZSize*mYSize)/4;
        //Nonserialized stuff must be initialized
        mCounts = new HashMap<Integer, Integer>();
        mBiomeTagCounts = new HashMap<BiomeDictionary.Type, Integer>();
        resetBiomeTypeCounts();
        mScanFinished = false;
        mLastDimension = 0;
        mLastX = 0;
        mLastY = 0;
        mLastZ = 0;
        mTicksSinceFull = -1;
        mBiomeTemperatureSum = 0;
        mBiomeHumiditySum = 0;
        mBiomeSalinity = 0;
        mAverageSunLevel = 0;
        mVolume = mXSize*mYSize*mZSize;
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

    protected void resetBiomeTypeCounts() {
        for(BiomeDictionary.Type type : BiomeDictionary.Type.values()) {
            mBiomeTagCounts.put(type, 0);
        }
    }

    protected void resetBlockCounts() {
        for (int blockId : mCounts.keySet()) {
            mCounts.put(blockId, 0);
        }
    }

    protected void updateBiomeAverages(Point point, boolean subtract) {

        int x,y,z,sign;
        if(point == null)
            return;

        sign = 1;
        if(subtract)
            sign = -1;

        x = point.x;
        y = point.y;
        z = point.z;

        BiomeGenBase base = Minecraft.getMinecraft().theWorld.getBiomeGenForCoords(x,z);

        float temp = base.getFloatTemperature(x,y,z);
        float humidity = base.getFloatRainfall();
        float salinity = 0.0f;
        float maxsun = Minecraft.getMinecraft().theWorld.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);

        //Update Type counts
        for(BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome(base)) {
            addToBiomeTagCount(type, sign * 1);
            if(type == BiomeDictionary.Type.BEACH || type == BiomeDictionary.Type.OCEAN)
                salinity += 1;
        }

        //Update characteristic counts
        mBiomeHumiditySum += sign*humidity;
        mBiomeTemperatureSum += sign*temp;
        mBiomeSalinity += sign*salinity;
        mAverageSunLevel += sign*maxsun;
    }

    protected void updateScan(Cuboid newVolume, Cuboid oldVolume, Cuboid intersect) {
        ComplementsPointIterator newInRange = new ComplementsPointIterator(newVolume, intersect);
        ComplementsPointIterator newOutOfRange = new ComplementsPointIterator(oldVolume, intersect);
        Point point = newOutOfRange.next();
        World world =  Minecraft.getMinecraft().theWorld;
        // Subtract out of range blocks
        while (point != null) {
            if(point.y > 0) {
                int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
                addToCount(blockId, -1);
                updateBiomeAverages(point, true);
            }
            point = newOutOfRange.next();
        }
        point = newInRange.next();
        while (point != null) {
            if(point.y > 0) {
                int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
                addToCount(blockId, 1);
                updateBiomeAverages(point, true);
            }
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
            if(point.y > 0) {
                int blockId = Block.getIdFromBlock(world.getBlock(point.x, point.y, point.z));
                addToCount(blockId, 1);
                updateBiomeAverages(point, false);
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
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        mTicksSinceFull = 0;
        mLastX = (int) player.posX;
        mLastY = (int) player.posY;
        mLastZ = (int) player.posZ;
        mLastDimension = player.dimension;
        mScanFinished = false;
        mFullRange = new CuboidPointIterator(getVolumeFor(mLastX, mLastY, mLastZ));
        resetBlockCounts();
        resetBiomeTypeCounts();
        mBiomeTemperatureSum = 0;
        mBiomeHumiditySum = 0;
        mBiomeSalinity = 0;
        mAverageSunLevel = 0;
    }

    protected void addToBiomeTagCount(BiomeDictionary.Type type, Integer count) {
        int c = mBiomeTagCounts.get(type);
        c += count;
        //Correct "drift"
        if(c < 0) c = 0;
        else if(c > mVolume) c = mVolume;
        mBiomeTagCounts.put(type, c);
    }

    protected void addToCount(Integer  blockId, Integer count) {
        if(mCounts.containsKey(blockId)) {
            int c = mCounts.get(blockId);
            c += count;
            //Correct "drift"
            if(c < 0) c = 0;
            else if(c > mVolume) c = mVolume;
            mCounts.put(blockId,c);
        }
    }

    protected Cuboid getVolumeFor(int x, int y, int z) {
        int minX,maxX,minY,maxY,minZ,maxZ;
        Point min, max;
        if(mYStartsAtPlayer) {
            minY = y;
            maxY = y+mYSize;
        } else {
            minY = y - mYSize / 2;
            maxY = y + mYSize / 2;
        }
        //World bottom truncate
        if(minY < 0)
            minY = 0;
        minX = x - mXSize / 2;
        maxX = x + mXSize / 2;
        minZ = z - mZSize / 2;
        maxZ = z + mZSize / 2;
        return new Cuboid(new Point(minX,minY,minZ), new Point(maxX,maxY,maxZ));
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        // No scan state
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        World world = Minecraft.getMinecraft().theWorld;
        if (player == null || world == null || event.isCanceled() || player.posY < 0) {
            mFullRange = null;
            return;
        }
        mTicksSinceFull += 1;
        int x, y, z;
        x = (int) player.posX;
        y = (int) player.posY;
        z = (int) player.posZ;


        Cuboid oldVolume = getVolumeFor(mLastX, mLastY, mLastZ);
        Cuboid newVolume = getVolumeFor(x, y, z);
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

        // First scan or full scan reset conditions detected
        if(fullScanReset || mFullRange == null)
            resetFullScan();

        if (!mScanFinished) {
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
        addToCount(0,1);
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event.isCanceled()) {
            return;
        }
        int blockId = Block.getIdFromBlock(event.block);
        addToCount(blockId,1);
        addToCount(0,-1);
    }

    @SubscribeEvent
    public void onLeafDecay(BlockEvent.HarvestDropsEvent event) {
        if(event.isCanceled()) {
            return;
        }
        //Fake player means leaf decay / environmental cause
        if(event.harvester != Minecraft.getMinecraft().thePlayer) {
            int blockId = Block.getIdFromBlock(event.block);
            addToCount(blockId, -1);
            addToCount(0, 1);
        }
    }
}
