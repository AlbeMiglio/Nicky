package it.albemiglio.nicky.commands;

import it.albemiglio.nicky.Nick;
import it.albemiglio.nicky.Nicky;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static it.mycraft.powerlib.utils.ColorAPI.color;
import static it.mycraft.powerlib.utils.ColorAPI.decolor;

public class NickCMD implements CommandExecutor {

    private Nicky plugin;

    public NickCMD(Nicky plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            runAsConsole(args);
        } else if (args.length >= 2) {
            runAsAdmin(sender, args);
        } else {
            runAsPlayer(sender, args);
        }
        return true;
    }

    private void runAsConsole(String[] args) {
        if (args.length >= 2) {
            OfflinePlayer receiver = this.plugin.getServer().getOfflinePlayer(args[0]);
            if (!receiver.hasPlayedBefore()) {
                Message m = new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Could-Not-Find-Player")))
                        .addPlaceHolder("{name}", args[0]);
                this.plugin.log(m.getText());
                return;
            }
            String nickname = args[1].trim();
            if (nickname.equals(receiver.getName())) {
                (new DelNickCMD(this.plugin)).runAsConsole(args);
                return;
            }
            String strippedNickname = decolor(color(nickname));
            if (strippedNickname.length() < this.plugin.getFileManager().getMinLength()) {
                Message m = new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Must-Be-More-Characters")))
                        .addPlaceHolder("{min}", this.plugin.getFileManager().getMinLength());
                this.plugin.log(m.getText());
                return;
            }
            Nick nick = new Nick(receiver);
            if (Nick.isUsed(nickname)) {
                Message m = new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Already-In-Use")))
                        .addPlaceHolder("{name}", nick.format(nickname));
                this.plugin.log(m.getText());
                return;
            }
            nick.set(nickname);
            nickname = nick.get();
            if (receiver.isOnline()) {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Your-Nick-Has-Been-Set")))
                        .addPlaceHolder("{name}", nickname)
                        .send(receiver.getPlayer());
            }
            Message m = new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                    this.plugin.getFileManager().getMessages().getString("Set-Player-Nick-To")))
                    .addPlaceHolder("{player}", receiver.getName())
                    .addPlaceHolder("{name}", nickname);
            this.plugin.log(m.getText());

        } else {
            this.plugin.log(color("&cUsage: /nick <player> <nickname>"));
        }
    }

    private void runAsAdmin(CommandSender sender, String[] args) {
        OfflinePlayer receiver = this.plugin.getServer().getOfflinePlayer(args[0]);
        if (!receiver.hasPlayedBefore()) {
            new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                    this.plugin.getFileManager().getMessages().getString("Could-Not-Find-Player")))
                    .addPlaceHolder("{name}", args[0])
                    .send(sender);
        }
        String nickname = args[1];
        if (sender.hasPermission("nicky.set.other")) {
            if (nickname.equals(receiver.getName())) {
                (new DelNickCMD(this.plugin)).runAsAdmin(sender, args);
                return;
            }
            String strippedNickname = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', nickname));
            if (strippedNickname.length() < this.plugin.getFileManager().getMinLength()) {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Must-Be-More-Characters")))
                        .addPlaceHolder("{min}", this.plugin.getFileManager().getMinLength())
                        .send(sender);
                return;
            }
            Nick nick = new Nick(receiver);
            if (Nick.isBlacklisted(nickname) && !sender.hasPermission("nicky.noblacklist")) {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Contains-Blacklisted-Word")))
                        .addPlaceHolder("{name}", nick.format(nickname))
                        .send(sender);
                return;
            }
            if (Nick.isUsed(nickname)) {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Already-In-Use")))
                        .addPlaceHolder("{name}", nick.format(nickname))
                        .send(sender);
                return;
            }
            nick.set(nickname);
            nickname = nick.get();
            if (receiver.isOnline()) {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Your-Nick-Has-Been-Set")))
                        .addPlaceHolder("{name}", nickname)
                        .send(receiver.getPlayer());
            }
            new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                    this.plugin.getFileManager().getMessages().getString("Set-Player-Nick-To")))
                    .addPlaceHolder("{player}", receiver.getName())
                    .addPlaceHolder("{name}", nickname)
                    .send(sender);
        } else {
            String notEnoughPermissions = this.plugin.getFileManager().getMessages().getString("Not-Enough-Permissions");
            new Message(color(this.plugin.getFileManager().getPluginPrefix() + notEnoughPermissions))
                    .addPlaceHolder("{perm}", "nicky.set.other")
                    .send(sender);
        }
    }

    private void runAsPlayer(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (sender.hasPermission("nicky.set")) {
            if (args.length >= 1) {
                String nickname = args[0];
                if (nickname.equals(sender.getName())) {
                    (new DelNickCMD(this.plugin)).runAsPlayer(sender);
                    return;
                }
                String strippedNickname = decolor(color(nickname));
                if (strippedNickname.length() < this.plugin.getFileManager().getMinLength()) {
                    new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                            this.plugin.getFileManager().getMessages().getString("Your-Nick-Must-Be-More-Characters")))
                            .addPlaceHolder("{min}", this.plugin.getFileManager().getMinLength())
                            .send(player);
                    return;
                }
                Nick nick = new Nick(player);
                if (Nick.isBlacklisted(nickname) && !player.hasPermission("nicky.noblacklist")) {
                    new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                            this.plugin.getFileManager().getMessages().getString("Nick-Contains-Blacklisted-Word")))
                            .addPlaceHolder("{name}", nick.format(nickname))
                            .send(player);
                    return;
                }
                if (Nick.isUsed(nickname)) {
                    new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                            this.plugin.getFileManager().getMessages().getString("Nick-Already-In-Use")))
                            .addPlaceHolder("{name}", nick.format(nickname))
                            .send(player);
                    return;
                }
                nick.set(nickname);
                nickname = nick.get();
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Your-Nick-Has-Been-Set")))
                        .addPlaceHolder("{name}", nickname)
                        .send(player);
            } else {
                new Message(color(this.plugin.getFileManager().getPluginPrefix() +
                        this.plugin.getFileManager().getMessages().getString("Nick-Syntax")))
                        .send(player);
            }
        } else {
            String notEnoughPermissions = this.plugin.getFileManager().getMessages().getString("Not-Enough-Permissions");
            new Message(color(this.plugin.getFileManager().getPluginPrefix() + notEnoughPermissions))
                    .addPlaceHolder("{perm}", "nicky.realname")
                    .send(player);
        }
    }
}

