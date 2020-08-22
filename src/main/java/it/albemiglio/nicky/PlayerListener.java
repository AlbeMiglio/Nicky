package it.albemiglio.nicky;

import it.mycraft.powerlib.chat.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static it.mycraft.powerlib.utils.ColorAPI.color;

public class PlayerListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Nick nick = new Nick(player);
        if (Nicky.getInstance().getFileManager().isUseJoinLeaveEnabled()) {
            event.setJoinMessage(null);
            String message = new Message(Nicky.getInstance().getFileManager().getJoinMessage())
                    .addPlaceHolder("{name}", getNicknameOrName(nick)).getText();
            Bukkit.broadcastMessage(color(message));
        }
        nick.load();
        Nicky.getDatabase().updatePlayerName(player.getUniqueId().toString(), player.getName());
    }

    @EventHandler
    public void onExit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Nick nick = new Nick(player);
        if (Nicky.getInstance().getFileManager().isUseJoinLeaveEnabled()) {
            event.setQuitMessage(null);
            Message m = new Message(color(Nicky.getInstance().getFileManager().getLeaveMessage()))
                    .addPlaceHolder("{name}", getNicknameOrName(nick));
            Bukkit.broadcastMessage(m.getText());
        }
        nick.unLoad();
    }

    private String getNicknameOrName(Nick nick) {
        String name = nick.get();
        if (name == null)
            name = nick.getOfflinePlayer().getName();
        return name;
    }
}

