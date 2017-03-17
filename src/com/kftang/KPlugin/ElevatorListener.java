package com.kftang.KPlugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
    private final int maxDistance;

    ElevatorListener(FileConfiguration config) {
        List<String> blocks = config.getStringList("elevatorBlocks");
        elevatorBlocks = new ArrayList<>(blocks.size());
        for (String block : blocks)
            elevatorBlocks.add(Material.valueOf(block));
        maxDistance = config.getInt("elevatorMaxDistance");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getY() < event.getTo().getY())
            elevate(true, event.getPlayer());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking())
            elevate(false, event.getPlayer());
    }

    private boolean elevate(boolean up, Player player) {
        //Check for the elevator block
        int originaloffset = up ? 1 : -2;
        int nextamt = up ? 1 : -1;
        //If the block under the player is one of the elevator blocks
        if (elevatorBlocks.contains(player.getLocation().subtract(0, 1, 0).getBlock().getType())) {
            //Iterator for finding the next block
            Location nextBlock = player.getLocation().clone().add(0, originaloffset, 0);
            //Loop to get the next block
            for (int i = 0; !elevatorBlocks.contains(nextBlock.getBlock().getType()) && (i < maxDistance || i == -1); i++) {
                nextBlock.add(0, nextamt, 0);
                if (nextBlock.getY() > 256 && nextBlock.getY() < 1)
                    return false;
                //If the next block is an elevator block
                if (elevatorBlocks.contains(nextBlock.getBlock().getType())) {
                    //If blocks on top of the elevator block are not both air
                    Location temp = nextBlock.clone();
                    if(!temp.add(0, 1, 0).getBlock().getType().equals(Material.AIR) || !temp.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                        //Skip this elevator block
                        i++;
                        nextBlock.add(0, nextamt, 0);
                    }
                }
            }
            //Get to the location where the player will teleport
            nextBlock.add(0, 1, 0);
            player.teleport(nextBlock);
            //Make the cool sound
            player.playSound(player.getLocation(), Sound.ENTITY_IRONGOLEM_ATTACK, 1, 0);
            return true;
        }
        return false;
    }
}
