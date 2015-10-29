package graphich.ambiotic.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Helpers {
    protected static final Random RNG = new Random();

    public static float randomFloatInRange(float minf, float maxf) {
        float rv = Helpers.RNG.nextFloat() * (maxf - minf) + minf;
        return rv;
    }

    public static boolean resourceExists(String resource) {
        ResourceLocation rl = new ResourceLocation(resource);
        try {
            Minecraft.getMinecraft().getResourceManager().getAllResources(rl);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    public static ArrayList<Integer> buildBlockIdList(String specifier) {
        ArrayList<ItemStack> items = OreDictionary.getOres(specifier);
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        int blockId;
        if (items.size() > 0) {
            for (ItemStack is : items) {
                Block block = Block.getBlockFromItem(is.getItem());
                blockId = Block.getIdFromBlock(block);
                if (!blockIds.contains(blockId))
                    blockIds.add(blockId);
                List<ItemStack> subblockitems = new ArrayList<ItemStack>();
                block.getSubBlocks(is.getItem(), null, subblockitems);
                for (ItemStack sis : subblockitems) {
                    block = Block.getBlockFromItem(sis.getItem());
                    blockId = Block.getIdFromBlock(block);
                    if (!blockIds.contains(blockId))
                        blockIds.add(blockId);
                }
            }
        } else if (GameData.getBlockRegistry().containsKey(specifier)) {
            blockId = GameData.getBlockRegistry().getId(specifier);
            if (!blockIds.contains(blockId))
                blockIds.add(blockId);
        }
        return blockIds;
    }

    public static InputStreamReader resourceAsStreamReader(ResourceLocation rl) throws IOException {
        InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        return isr;
    }

    public static JsonArray getRootJsonArray(ResourceLocation rl) throws IOException {
        JsonParser parser = new JsonParser();
        InputStreamReader isr = Helpers.resourceAsStreamReader(rl);
        return parser.parse(isr).getAsJsonArray();
    }
}
