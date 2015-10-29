package graphich.ambiotic.util;

import graphich.ambiotic.main.Ambiotic;
import graphich.ambiotic.main.VariableRegistry;
import graphich.ambiotic.variables.Macro;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Invalid arguments"));
            return;
        }
        String js = "";
        for (String arg : args)
            js += " " + arg;
        Map<String, Macro> macros = VariableRegistry.INSTANCE.macros();
        for (Macro macro : macros.values())
            js = macro.expand(js);
        Object res = Ambiotic.evalJS(js);
        if (res != null)
            sender.addChatMessage(new ChatComponentText("Result : " + res.toString()));
        else
            sender.addChatMessage(new ChatComponentText("Result was null"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayer)
            return true;
        return false;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        String last = args[args.length - 1];
        List<String> rv = new ArrayList<String>();
        if (last.equals("&")) {
            rv.add("&&");
        } else if (last.equals("|")) {
            rv.add("||");
        } else if (last.startsWith("#")) {
            List<String> macros = VariableRegistry.INSTANCE.macroSymbols();
            for (String symbol : macros) {
                if (symbol.startsWith(last))
                    rv.add(symbol);
            }
        } else {
            List<String> names = VariableRegistry.INSTANCE.fullVariableNames();
            for (String name : names) {
                if (name.startsWith(last))
                    rv.add(name);
            }
        }
        return rv;
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
