package it.albemiglio.nicky.commands.subcommands;

import it.albemiglio.nicky.Nicky;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class ReloadCMD extends SubCommand {
    private final Nicky plugin;

    public ReloadCMD(Nicky plugin) {
        super("reload", "nicky.reload");
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        this.plugin.getFileManager().reloadConfig();
        this.plugin.getFileManager().reloadMessages();
        Message m = new Message(color(
                this.plugin.getFileManager().getMessages().getStringList("Configurations-Reloaded-Successfully")));
        m.send(sender);
        return true;
    }
}

