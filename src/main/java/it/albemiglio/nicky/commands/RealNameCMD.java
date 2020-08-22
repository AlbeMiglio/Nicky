package it.albemiglio.nicky.commands;

import it.albemiglio.nicky.FileManager;
import it.albemiglio.nicky.Nick;
import it.albemiglio.nicky.Nicky;
import it.albemiglio.nicky.databases.SQL;
import it.mycraft.powerlib.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class RealNameCMD implements CommandExecutor {
    private HashMap<String, HashMap<String, String>> foundPlayers = new HashMap<>();

    private HashMap<String, String> onlinePlayers = new HashMap<>();

    private HashMap<String, String> offlinePlayers = new HashMap<>();

    private FileManager fm = Nicky.getInstance().getFileManager();

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        runAsPlayer(sender, args);
        return true;
    }

    private void runAsPlayer(CommandSender sender, String[] args) {
        if (sender.hasPermission("nicky.realname")) {
            if (args.length < 1) {
                new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-Syntax")))
                        .send(sender);
                return;
            }
            String search = args[0];
            int minSearchLength = 3;
            if (3 < this.fm.getMinLength())
                minSearchLength = this.fm.getMinLength();
            if (search.length() < minSearchLength) {
                String searchMustBeMoreCharacters = this.fm.getMessages().getString("Search-Must-Be-More-Characters");
                new Message(color(this.fm.getPluginPrefix() + searchMustBeMoreCharacters))
                        .addPlaceHolder("{min}", minSearchLength)
                        .send(sender);
                return;
            }
            findPlayers(search);
            if (this.foundPlayers.isEmpty()) {
                new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-No-Match-Found")))
                        .addPlaceHolder("{search}", args[0])
                        .send(sender);
            } else {
                new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-Online-Matches-Found")))
                        .addPlaceHolder("{search}", args[0])
                        .send(sender);
                for (Map.Entry<String, String> player : this.foundPlayers.get("online").entrySet()) {
                    new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-Match")))
                            .addPlaceHolder("{name}", player.getKey())
                            .addPlaceHolder("{value}", player.getValue())
                            .send(sender);
                }
                if (!this.foundPlayers.get("offline").isEmpty()) {
                    new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-Offline-Matches-Found")))
                            .addPlaceHolder("{search}", args[0])
                            .send(sender);
                    for (Map.Entry<String, String> player : this.foundPlayers.get("offline").entrySet()) {
                        new Message(color(this.fm.getPluginPrefix() + this.fm.getMessages().getString("Realname-Match")))
                                .addPlaceHolder("{name}", player.getKey())
                                .addPlaceHolder("{value}", player.getValue())
                                .send(sender);
                    }
                }
            }
        } else {
            String notEnoughPermissions = this.fm.getMessages().getString("Not-Enough-Permissions");
            new Message(color(this.fm.getPluginPrefix() + notEnoughPermissions))
                    .addPlaceHolder("{perm}", "nicky.realname")
                    .send(sender);
        }
    }

    private void findPlayers(String searchWord) {
        this.offlinePlayers.clear();
        this.onlinePlayers.clear();
        List<SQL.SearchedPlayer> searchedPlayers = Nick.searchGet(searchWord);
        if (searchedPlayers == null)
            return;
        for (SQL.SearchedPlayer searchedPlayer : searchedPlayers) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(searchedPlayer.getUuid()));
            String playersNick = searchedPlayer.getNick();
            if (offlinePlayer.isOnline()) {
                Nick nick = new Nick(offlinePlayer.getPlayer());
                playersNick = nick.format(playersNick);
                this.onlinePlayers.put(playersNick, searchedPlayer.getName());
                continue;
            }
            playersNick = ChatColor.translateAlternateColorCodes('&', playersNick);
            this.offlinePlayers.put(playersNick, searchedPlayer.getName());
        }
        this.foundPlayers = new HashMap<>();
        if (!this.onlinePlayers.isEmpty() || !this.offlinePlayers.isEmpty()) {
            this.foundPlayers.put("online", this.onlinePlayers);
            this.foundPlayers.put("offline", this.offlinePlayers);
        }
    }
}

