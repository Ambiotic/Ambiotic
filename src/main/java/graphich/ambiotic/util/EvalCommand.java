package graphich.ambiotic.util;

import graphich.ambiotic.main.Ambiotic;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class EvalCommand implements ICommand {

    private List mAliases;

    public EvalCommand() {
        mAliases = new ArrayList();
        mAliases.add("ambeval");
    }

    @Override
    public String getCommandName() {
        return "ambeval";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "ambeval <javascript to eval>";
    }

    @Override
    public List getCommandAliases() {
        return mAliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Invalid arguments"));
            return;
        }
        String js = "";
        for(String arg : args)
            js += " "+arg;
        Object res = Ambiotic.evalJS(js);
        if(res != null)
            sender.addChatMessage(new ChatComponentText("Result : "+res.toString()));
        else
            sender.addChatMessage(new ChatComponentText("Result was null"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if(sender instanceof EntityPlayer)
            return true;
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return new ArrayList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
