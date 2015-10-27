package graphich.ambiotic.util;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class ShowOreDictCommand implements ICommand {
    @Override
    public String getCommandName() {
        return "ambord";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "ambord";
    }

    @Override
    public List getCommandAliases() {
        List rv = new ArrayList<String>();
        rv.add("ambord");
        return rv;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            String[] oreNames = OreDictionary.getOreNames();
            for (String oreName : oreNames)
                sender.addChatMessage(new ChatComponentText(oreName));
            return;
        }
        String first = args[0];
        if(args.length == 1) {
            List<ItemStack> stacks = OreDictionary.getOres(args[0]);
            if (stacks.size() == 0)
                sender.addChatMessage(new ChatComponentText("No ores registered to '" + args[0] + "'"));
            for (ItemStack is : stacks) {
                Block block = Block.getBlockFromItem(is.getItem());
                sender.addChatMessage(new ChatComponentText(block.getUnlocalizedName()));
            }
        } else if (args.length == 2 && first.equals("fndblk")) {
            String search = args[1];
            for(Object name : GameData.getBlockRegistry().getKeys()) {
                if(name.toString().contains(search))
                    sender.addChatMessage(new ChatComponentText(name.toString()));
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Invalid arguments"));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
