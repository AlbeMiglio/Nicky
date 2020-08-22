package it.albemiglio.nicky.commands.subcommands;

import it.albemiglio.nicky.Nicky;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class HelpCMD extends SubCommand {
    public HelpCMD() {
        super("help", "nicky.help");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        for(String s : Nicky.getInstance().getFileManager().getMessages().getStringList("Nicky-Help")) {
            Message m = new Message(color(s));
            if(s.contains("|")) {
                String perm = s.split("|")[0];
                String line = s.split("|")[1];
                if(sender.hasPermission(perm)) {
                    m = new Message(color(line));
                    m.send(sender);
                }
            }
            else m.send(sender);
        }
        return true;
    }
}

