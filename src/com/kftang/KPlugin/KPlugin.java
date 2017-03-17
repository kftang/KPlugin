package com.kftang.KPlugin;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
        try {
            getConfig().load("KPlugin/config.yml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        getConfig().addDefault("elevatorBlocks", blocks);
        getConfig().addDefault("elevatorMaxDistance", -1);
        getConfig().addDefault("rtpMaxRadius", 10000);
        getConfig().addDefault("rtpCooldown", 60);
        getConfig().addDefault("votePercentageNeeded", 50.0);
        getConfig().addDefault("voteExpireTime", 30);
        try {
            getConfig().save("KPlugin/config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Register Events
        getServer().getPluginManager().registerEvents(new ElevatorListener(getConfig()), this);
        getServer().getPluginManager().registerEvents(new BetterChatListener(), this);

        //Register Commands
        getCommand("rtp").setExecutor(new CommandRandomTeleport(getConfig(), getServer()));
    }
}
