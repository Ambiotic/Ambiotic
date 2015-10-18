package graphich.ambiotic.variables.player;

import com.google.gson.annotations.SerializedName;
import cpw.mods.fml.common.gameevent.TickEvent;
import graphich.ambiotic.util.Helpers;
import graphich.ambiotic.util.StrictJsonException;
import graphich.ambiotic.variables.Variable;
import graphich.ambiotic.variables.VariableBool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import java.util.*;

public class Exposed extends VariableBool {

    @SerializedName("AirPermeableBlocks")
    protected String[] mPermeableBlockSpecs;
    @SerializedName("SearchDepth")
    protected Integer mDepth;

    protected transient List<Integer> mPermeableBlockIds;
    protected transient Deque<Pos> mOpen;
    protected transient List<Pos> mClosed;

    public Exposed(String name) {
        super(name);
        initialize();
    }

    @Override //IStrictJson
    public void validate() throws StrictJsonException {
        super.validate();
        if(mPermeableBlockSpecs == null)
            throw new StrictJsonException("AirPermeableBlocks must be defined");
    }

    @Override //IStrictJson
    public void initialize() {
        super.initialize();
        mNameSpace = Variable.PLAYER_NAMESPACE;
        mPermeableBlockIds = new ArrayList<Integer>();
        for(String spec : mPermeableBlockSpecs) {
            ArrayList<Integer> ids = Helpers.buildBlockIdList(spec);
            mPermeableBlockIds.addAll(ids);
        }
        mClosed = new ArrayList<Pos>();
        mOpen = new ArrayDeque<Pos>();
        if(mDepth == null)
            mDepth = 10;
    }


    @Override //IVariable
    public boolean updateValue(TickEvent event) {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if(world == null || player == null)
            return false;

        boolean newValue = quickCheck();
        if(newValue)
            newValue = slowCheck();

        if(mValue == newValue)
            return false;
        mValue = newValue;
        return true;
    }

    protected boolean quickCheck() {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        //When in the nether, you're never "exposed"
        if(player.dimension == -1)
            return false;
        int mx,my,mz;
        mx = (int)player.posX + 1;
        my = (int)player.posY + 1;
        mz = (int)player.posZ + 1;
        //float aircount = 0;
        //float sltotal = 0;
        for(int cx = mx-2; cx <= mx; cx++) {
            for(int cy = my-2; cy <= my; cy++) {
                for(int cz = mz-2; cz <= mz; cz++) {
                    if(!world.isAirBlock(cx,cy,cz))
                        continue;
                    if(world.getSavedLightValue(EnumSkyBlock.Sky, cx,cy,cz) > 0)
                        return true;
                }
            }
        }
        return false;
    }

    protected boolean slowCheck() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        mOpen.clear();
        mClosed.clear();
        boolean newValue = false;
        // Boundry problems because doubles to ints suck, always pick the "good position"
        Pos current = new Pos(Math.ceil(player.posX), Math.ceil(player.posY), Math.ceil(player.posZ));
        if(!goodSuccessor(current, null))
            current = new Pos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
        while(current != null && !newValue) {
            if(current.isExposed()) {
                newValue = true;
                break;
            }
            mOpen.addAll(successors(current));
            mClosed.add(current);
            current = mOpen.poll();
        }
        return newValue;
    }

    public List<Pos> successors(Pos p) {
        ArrayList<Pos> rv = new ArrayList<Pos>();
        Pos possible = new Pos(p.X+1,p.Y,p.Z);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        possible = new Pos(p.X-1,p.Y,p.Z);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        possible = new Pos(p.X,p.Y+1,p.Z);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        possible = new Pos(p.X,p.Y-1,p.Z);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        possible = new Pos(p.X,p.Y,p.Z+1);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        possible = new Pos(p.X,p.Y,p.Z-1);
        if(goodSuccessor(possible, p))
            rv.add(possible);
        return rv;
    }

    protected boolean doorIsBlocking(Pos into, Pos from) {
        World world = Minecraft.getMinecraft().theWorld;
        BlockDoor door = (BlockDoor)world.getBlock(into.X,into.Y,into.Z);
        int dir = -1;
        Vec3 fromVec, intoVec;
        if(into.X != from.X) // Moving in X
        {
            if(from.X < into.X)
                dir = 1;
            intoVec = Vec3.createVectorHelper((dir*1.75)+into.X,into.Y+.5,into.Z+.5);
            fromVec = Vec3.createVectorHelper(from.X+(-dir*0.5),from.Y+.5,from.Z+.5);
        }
        else // Moving in Z
        {
            if(into.Z > from.Z)
                dir = 1;
            intoVec = Vec3.createVectorHelper(into.X+.5,into.Y+.5,into.Z+(dir*1.75));
            fromVec = Vec3.createVectorHelper(from.X+.5,from.Y+.5,from.Z+(-dir*0.5));
        }

        return (door.collisionRayTrace(world, into.X, into.Y, into.Z, fromVec, intoVec) != null);
    }

    protected boolean goodSuccessor(Pos into, Pos from) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Block block = Minecraft.getMinecraft().theWorld.getBlock(into.X,into.Y,into.Z);
        int id = Block.getIdFromBlock(block);

        if(mOpen.contains(into))
            return false;
        if(mClosed.contains(into))
            return false;
        if(block instanceof BlockDoor && from != null && into.Y == from.Y)
            return !doorIsBlocking(into, from);
        if(!mPermeableBlockIds.contains(id))
            return false;
        if(into.X > player.posX+mDepth || into.X < player.posX-mDepth)
            return false;
        if(into.Y > player.posY+mDepth || into.Y < player.posY-mDepth)
            return false;
        if(into.Z > player.posZ+mDepth || into.Z < player.posZ-mDepth)
            return false;

        return true;
    }

    protected class Pos {
        public int X;
        public int Y;
        public int Z;

        public Pos(double x, double y, double z) {
            X = (int)x;
            Y = (int)y;
            Z = (int)z;
        }

        public boolean isExposed() {
            World world = Minecraft.getMinecraft().theWorld;
            return world.canBlockSeeTheSky(X,Y,Z);
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof Pos))
                return false;
            Pos ot = (Pos)o;
            return X == ot.X && Y == ot.Y && Z == ot.Z;
        }

        @Override
        public String toString() {
            return "X: "+X+", Y: "+Y+", Z: "+Z;
        }
    }
}
