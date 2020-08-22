package it.albemiglio.nicky;

import it.albemiglio.nicky.databases.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class Nick {
    private static final SQL database = Nicky.getDatabase();

    private OfflinePlayer offlinePlayer;

    private String uuid;

    public Nick(Player player) {
        this.offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
        this.uuid = this.offlinePlayer.getUniqueId().toString();
    }

    public Nick(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
        this.uuid = offlinePlayer.getUniqueId().toString();
    }

    public boolean load() {
        if (!this.offlinePlayer.isOnline())
            return false;
        Player player = this.offlinePlayer.getPlayer();
        String nickname = get();
        if (nickname != null) {
            if (isBlacklisted(nickname) && !player.hasPermission("nicky.noblacklist")) {
                unSet();
                return false;
            }
            player.setDisplayName(nickname);
            if (Nicky.getInstance().getFileManager().isTabsUsed())
                if (nickname.length() > 16) {
                    player.setPlayerListName(nickname.substring(0, 15));
                } else {
                    player.setPlayerListName(nickname);
                }
            return true;
        }
        return false;
    }

    public boolean unLoad() {
        if (!this.offlinePlayer.isOnline())
            return false;
        Player player = this.offlinePlayer.getPlayer();
        database.removeFromCache(this.uuid);
        player.setDisplayName(player.getName());
        return true;
    }

    public String get() {
        String nickname = database.downloadNick(this.uuid);
        if (nickname != null)
            nickname = format(nickname);
        return nickname;
    }

    public void set(String nickname) {
        if (get() != null)
            unSet();
        nickname = formatWithFlags(nickname, false);
        database.uploadNick(this.uuid, nickname, this.offlinePlayer.getName());
        refresh();
    }

    public void unSet() {
        database.deleteNick(this.uuid);
        refresh();
    }

    public String format(String nickname) {
        return formatWithFlags(nickname, true);
    }

    public String formatWithFlags(String nickname, boolean addPrefix) {
        if (nickname.length() > Nicky.getInstance().getFileManager().getMaxLength())
            nickname = nickname.substring(0, Nicky.getInstance().getFileManager().getMaxLength() + 1);
        nickname = Utils.translateColors(nickname, this.offlinePlayer);
        if (addPrefix && !Nicky.getInstance().getFileManager().getNickPrefix().equals("")) {
            String prefix = color(Nicky.getInstance().getFileManager().getNickPrefix());
            nickname = prefix + nickname;
        }
        if (!Nicky.getInstance().getFileManager().getCharacters().equals(""))
            nickname = nickname.replaceAll(Nicky.getInstance().getFileManager().getCharacters(), "");
        return nickname + ChatColor.RESET;
    }

    public static boolean isUsed(String nick) {
        if (Nicky.getInstance().getFileManager().isUnique())
            return database.isUsed(nick);
        return false;
    }

    public static boolean isBlacklisted(String nick) {
        nick = ChatColor.translateAlternateColorCodes('&', nick);
        for (String word : Nicky.getInstance().getFileManager().getBlacklist()) {
            if (ChatColor.stripColor(nick.toLowerCase()).contains(word.toLowerCase()))
                return true;
        }
        return false;
    }

    public static List<SQL.SearchedPlayer> searchGet(String search) {
        return database.searchNicks(search);
    }

    private void refresh() {
        unLoad();
        load();
    }

    public OfflinePlayer getOfflinePlayer() {
        return this.offlinePlayer;
    }
}

