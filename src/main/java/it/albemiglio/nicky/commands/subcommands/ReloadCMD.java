package it.albemiglio.nicky.commands.subcommands;

import it.albemiglio.nicky.FileManager;
import it.albemiglio.nicky.Nicky;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class ReloadCMD extends SubCommand {
    private final Nicky plugin;
    private FileManager fm;

    public ReloadCMD(Nicky plugin) {
        super("reload", "nicky.reload");
        this.plugin = plugin;
        this.fm = this.plugin.getFileManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        this.fm.reloadConfig();
        this.fm.reloadMessages();
        Message m = new Message(color(this.fm.getMessages().getStringList("Configurations-Reloaded-Successfully")));
        m.send(sender);
        return true;
    }
}

