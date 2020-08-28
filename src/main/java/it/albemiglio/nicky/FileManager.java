package it.albemiglio.nicky;

import it.mycraft.powerlib.config.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class FileManager {

    private Nicky main;

    @Getter
    private ConfigManager configManager;

    @Getter
    private String pluginPrefix;

    @Getter
    private String nickPrefix;

    @Getter
    private String leaveMessage;

    @Getter
    private String joinMessage;

    @Getter
    private boolean useJoinLeaveEnabled;

    @Getter
    @Setter
    private List<String> blacklist;

    @Getter
    private String characters;

    @Getter
    private int minLength;

    @Getter
    private int maxLength;

    @Getter
    private boolean unique;

    @Getter
    private boolean tabsUsed;

    public FileConfiguration getConfig() {
        return this.configManager.get("config.yml");
    }

    public FileConfiguration getMessages() {
        return this.configManager.get("messages.yml");
    }

    public FileManager(Nicky main) {
        this.main = main;
        this.configManager = new ConfigManager(this.main);
        this.configManager.create("config.yml");
        this.configManager.create("messages.yml");
        this.setBlacklist(new ArrayList<>());
        reloadConfig();
        reloadMessages();
    }

    public void reloadMessages() {
        this.configManager.reload("messages.yml");
    }

    public void reloadConfig() {
        this.configManager.reload("config.yml");
        try {
            pluginPrefix = color(getConfig().getString("nicky-prefix"));
            tabsUsed = getConfig().getBoolean("tab");
            unique = getConfig().getBoolean("unique");
            nickPrefix = getConfig().get("prefix").toString();
            minLength = getConfig().getInt("min-length");
            maxLength = getConfig().getInt("max-length");
            characters = getConfig().get("characters").toString();
            blacklist.clear();
            blacklist = getConfig().getStringList("blacklist");
            useJoinLeaveEnabled = getConfig().getBoolean("enable-join-leave-messages");
            joinMessage = color(getConfig().getString("join-message"));
            leaveMessage = color(getConfig().getString("leave-message"));
        } catch (Exception ex) {
            ex.printStackTrace();
            this.main.log("Warning - You have an error in your config.");
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Nick nick = new Nick(player);
            nick.load();
        }
    }
}
