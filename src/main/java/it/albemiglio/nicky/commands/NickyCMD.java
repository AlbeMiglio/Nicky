package it.albemiglio.nicky.commands;

import it.albemiglio.nicky.Nicky;
import it.albemiglio.nicky.commands.subcommands.HelpCMD;
import it.albemiglio.nicky.commands.subcommands.ReloadCMD;
import it.albemiglio.nicky.commands.subcommands.SubCommand;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class NickyCMD implements CommandExecutor {

    private Nicky plugin;
    private final List<SubCommand> commands;

    public NickyCMD(Nicky plugin) {
        this.plugin = plugin;
        this.commands = new ArrayList<>();
        this.commands.add(new HelpCMD());
        this.commands.add(new ReloadCMD(plugin));
    }

    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
        String subCommand = "help";
        if (args.length > 0)
            subCommand = args[0];
        for (SubCommand command : this.commands) {
            if (command.getName().equalsIgnoreCase(subCommand)) {
                if (!sender.hasPermission(command.getPermission())) {
                    String notEnoughPermissions = this.plugin.getFileManager().getMessages().getString("Not-Enough-Permissions");
                    new Message(color(this.plugin.getFileManager().getPluginPrefix()+notEnoughPermissions))
                            .addPlaceHolder("{perm}", command.getPermission())
                            .send(sender);
                    return true;
                }
                command.onCommand(sender, arg1, arg2, args);
            }
        }
        return true;
    }
}

