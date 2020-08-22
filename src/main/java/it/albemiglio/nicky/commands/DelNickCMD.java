package it.albemiglio.nicky.commands;

import it.albemiglio.nicky.FileManager;
import it.albemiglio.nicky.Nick;
import it.albemiglio.nicky.Nicky;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class DelNickCMD implements CommandExecutor {

    private Nicky plugin;
    private FileManager fm;

    public DelNickCMD(Nicky plugin) {
        this.plugin = plugin;
        this.fm = this.plugin.getFileManager();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            runAsConsole(args);
        } else if (args.length >= 1) {
            runAsAdmin(sender, args);
        } else {
            runAsPlayer(sender);
        }
        return true;
    }

    public void runAsConsole(String[] args) {
        if (args.length >= 1) {
            OfflinePlayer receiver = this.plugin.getServer().getOfflinePlayer(args[0]);
            if (!receiver.hasPlayedBefore()) {
                this.plugin.log(
                        new Message(color(this.fm.getMessages().getString("Could-Not-Find-Player") + ""))
                                .addPlaceHolder("{name}", args[0]).getText());
                return;
            }
            Nick nick = new Nick(receiver);
            nick.unSet();
            if (receiver.isOnline()) {
                String yourNickHasBeenDeleted = this.fm.getMessages().getString("Your-Nick-Has-Been-Deleted");
                new Message(color(this.fm.getPluginPrefix() + yourNickHasBeenDeleted))
                        .addPlaceHolder("{sender}", "Console")
                        .send(receiver.getPlayer());
            }
            String playerNickHasBeenDeleted = this.fm.getMessages().getString("Player-Nick-Has-Been-Deleted");
            this.plugin.log(
                    new Message(color(this.fm.getPluginPrefix() + playerNickHasBeenDeleted))
                            .addPlaceHolder("{name}", receiver.getName()).getText());
        } else {
            this.plugin.log("Usage: /delnick <name>");
        }
    }

    public void runAsAdmin(CommandSender sender, String[] args) {
        OfflinePlayer receiver = this.plugin.getServer().getOfflinePlayer(args[0]);
        if (!receiver.hasPlayedBefore()) {
            new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Could-Not-Find-Player")))
                    .addPlaceHolder("{name}", args[0]).send(sender);
            return;
        }
        if (sender.hasPermission("nicky.del.other")) {
            Nick nick = new Nick(receiver);
            nick.unSet();
            if (receiver.isOnline()) {
                String yourNickHasBeenDeleted = this.fm.getMessages().getString("Your-Nick-Has-Been-Deleted");
                new Message(color(this.fm.getPluginPrefix() + yourNickHasBeenDeleted))
                        .addPlaceHolder("{sender}", "Console")
                        .send(receiver.getPlayer());
                String playerNickHasBeenDeleted = this.fm.getMessages().getString("Player-Nick-Has-Been-Deleted");
                new Message(color(this.fm.getPluginPrefix() + playerNickHasBeenDeleted))
                        .addPlaceHolder("{name}", receiver.getName()).send(sender);
            }
        } else {
            String notEnoughPermissions = this.fm.getMessages().getString("Not-Enough-Permissions");
            new Message(color(this.fm.getPluginPrefix() + notEnoughPermissions))
                    .addPlaceHolder("{perm}", "nicky.del.other")
                    .send(sender);
        }
    }

    public void runAsPlayer(CommandSender sender) {
        if (sender.hasPermission("nicky.del")) {
            Nick nick = new Nick((Player) sender);
            nick.unSet();
            String yourNickHasBeenDeleted = this.fm.getMessages().getString("Your-Nick-Has-Been-Deleted");
            new Message(color(this.fm.getPluginPrefix() + yourNickHasBeenDeleted))
                    .addPlaceHolder("{sender}", sender.getName())
                    .send(sender);
        } else {
            String notEnoughPermissions = this.fm.getMessages().getString("Not-Enough-Permissions");
            new Message(color(this.fm.getPluginPrefix() + notEnoughPermissions))
                    .addPlaceHolder("{perm}", "nicky.del")
                    .send(sender);
        }
    }
}

