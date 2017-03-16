package com.kftang.KPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * Kenny Tang (c) 2017
 */
public class BetterChatListener implements Listener {
    @EventHandler
    public void chatFormatter(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PermissionUser user = PermissionsEx.getUser(player);
        event.setFormat(String.format("%s%s: %s%s", user.getPrefix(), player.getDisplayName(), ChatColor.GOLD ,event.getMessage()));
    }

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent event) {
        event.setJoinMessage(String.format("%s[%s✔%s] %s", ChatColor.GRAY, ChatColor.GREEN, ChatColor.GRAY, event.getPlayer().getDisplayName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(String.format("%s[%s✘%s] %s", ChatColor.GRAY, ChatColor.RED, ChatColor.GRAY, event.getPlayer().getDisplayName()));
    }
}
