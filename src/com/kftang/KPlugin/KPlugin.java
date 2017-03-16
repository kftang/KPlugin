package com.kftang.KPlugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Kenny Tang (c) 2017
 */
public class KPlugin extends JavaPlugin {
    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        //Setup config
        List<String> blocks = new ArrayList<>();
        blocks.add("DIAMOND_BLOCK");
        blocks.add("EMERALD_BLOCK");
        getConfig().addDefault("elevatorBlocks", blocks);
        getConfig().addDefault("elevatorMaxDistance", -1);
        getConfig().addDefault("rtpMaxRadius", 10000);
        getConfig().addDefault("rtpCooldown", 60);

        //Register Events
        getServer().getPluginManager().registerEvents(new ElevatorListener(getConfig()), this);
        getServer().getPluginManager().registerEvents(new BetterChatListener(), this);

        //Register Commands
        getCommand("rtp").setExecutor(new CommandRandomTeleport(getConfig(), getServer()));
    }
}
