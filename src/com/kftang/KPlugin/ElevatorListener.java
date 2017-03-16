package com.kftang.KPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Kenny Tang (c) 2017
 */
public class ElevatorListener implements Listener {
    private List<Material> elevatorBlocks;
    private int maxDistance;

    ElevatorListener(FileConfiguration config) {
        List<String> blocks = config.getStringList("elevatorBlocks");
        elevatorBlocks = new ArrayList<>(blocks.size());
        for (String block : blocks)
            elevatorBlocks.add(Material.valueOf(block));
        maxDistance = config.getInt("elevatorMaxDistance");
        if(maxDistance < 0)
            maxDistance = Integer.MAX_VALUE;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getY() < event.getTo().getY())
            elevate(true, event.getPlayer());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        elevate(false, event.getPlayer());
    }

    private boolean elevate(boolean up, Player player) {
        //Check for the elevator block
        if (elevatorBlocks.contains(player.getLocation().subtract(0, 1, 0).getBlock().getType())) {
            if (up) {
                Location nextBlock = player.getLocation().clone().add(0, 1, 0);
                for (int i = 0; !elevatorBlocks.contains(nextBlock.getBlock().getType()) && i < maxDistance; i++) {
                    nextBlock.add(0, 1, 0);
                    if (nextBlock.getY() > 256)
                        return false;
                }
                nextBlock.add(0, 1, 0);
                player.teleport(nextBlock);
                return true;
            } else {
                Location nextDiamond = player.getLocation().clone().add(0, -2, 0);
                for (int i = 0; !elevatorBlocks.contains(nextDiamond.getBlock().getType()) && i < maxDistance; i++) {
                    nextDiamond.add(0, -1, 0);
                    if (nextDiamond.getY() < 1)
                        return false;
                }
                nextDiamond.add(0, 1, 0);
                player.teleport(nextDiamond);
                return true;
            }
        }
        return false;
    }
}
