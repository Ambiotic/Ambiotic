package graphich.ambiotic.util;

import com.google.gson.*;
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

    public static String makeCodeFromJsonList(JsonArray json) throws Exception {
        int index = 0;
        StringBuilder code = new StringBuilder();
        for (JsonElement cur : json) {
            if (!cur.isJsonPrimitive() || !cur.getAsJsonPrimitive().isString())
                throw new Exception("This function is being removed");
            index += 1;
            String line = cur.getAsString();
            line = line.trim() + " "; //We want exactly 1 trailing space
            code.append(line);
        }
        return "(" + code.toString().trim() + ");";
    }

    public static ArrayList<Integer> buildBlockIdList(String specifier) {
        ArrayList<ItemStack> items = OreDictionary.getOres(specifier);
        ArrayList<Integer> blockIds = new ArrayList<Integer>();
        if (items.size() > 0) {
            for (ItemStack is : items) {
                int blockId = Block.getIdFromBlock(Block.getBlockFromItem(is.getItem()));
                blockIds.add(blockId);
            }
        } else if (GameData.getBlockRegistry().containsKey(specifier)) {
            int blockId = GameData.getBlockRegistry().getId(specifier);
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
