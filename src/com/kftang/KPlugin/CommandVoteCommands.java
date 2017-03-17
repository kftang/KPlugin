package com.kftang.KPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Kenny Tang (c) 2017
 */
public class CommandVoteCommands implements CommandExecutor {
    private enum VoteType {WEATHER, TIME, KICK, MUTE, NONE};
    private final Server server;
    private final double percentage;
    private final long voteExpireTime;
    private boolean voting;
    private int supporters;
    private VoteType currentVote;
    private Object currentVoteTarget;
    private TimerTask expireTask;
    private Timer expireTimer;

    public CommandVoteCommands(FileConfiguration config, Server server) {
        expireTimer = new Timer("expire");
        this.server = server;
        percentage = 100d / config.getDouble("votePercentageNeeded");
        voting = false;
        supporters = 0;
        voteExpireTime = config.getInt("voteExpireTime");
        expireTask = new TimerTask() {
            @Override
            public void run() {
                supporters = 0;
                currentVote = VoteType.NONE;
                currentVoteTarget = null;
            }
        };
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;
        int voteThreshold = (int)(server.getOnlinePlayers().size() / percentage);
        if(voting  && args.length == 0) {
            supporters++;
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Vote Casted");
            if(supporters >= voteThreshold) {
                server.broadcastMessage(String.format("%sVote for %s has passed!", ChatColor.DARK_GRAY, currentVote.toString().toLowerCase()));
                server.dispatchCommand(server.getConsoleSender(), currentVote.toString() + " " + currentVoteTarget.toString());
                expireTimer.cancel();
                expireTask.run();
                return true;
            } else {
                server.broadcastMessage(String.format("%s%d/%d has voted for %s", ChatColor.DARK_GRAY, supporters, voteThreshold, currentVote.toString().toLowerCase()));
            }
        }
        if(args.length != 2 && !voting) {
            printUsage(player);
            return true;
        }
        voting = true;
        switch (args[0]) {
            case "weather":
                if(!(args[1].equals("clear") || args[1].equals("storm"))) {
                    player.sendMessage("Invalid weather.");
                    return true;
                }
                currentVote = VoteType.WEATHER;
                currentVoteTarget = args[1];
                break;
            case "time":
                if(!(args[1].equals("day") || args[1].equals("night"))) {
                    player.sendMessage("Invalid time.");
                    return true;
                }
                currentVote = VoteType.TIME;
                currentVoteTarget = args[1];
                break;
            case "kick":
                if(!server.getPlayer(args[1]).isOnline()){
                    player.sendMessage("Invalid target, player " + args[1] + " is not online.");
                    return true;
                }
                currentVote = VoteType.KICK;
                currentVoteTarget = server.getPlayer(args[1]);
                break;
            case "mute":
                if(!server.getPlayer(args[1]).isOnline()){
                    player.sendMessage("Invalid target, player " + args[1] + " is not online.");
                    return true;
                }
                currentVote = VoteType.MUTE;
                currentVoteTarget = server.getPlayer(args[1]);
                break;
            default:
                printUsage(player);
                return true;
        }
        expireTimer.schedule(expireTask, voteExpireTime);
        server.broadcastMessage(String.format("%s%s has initiated a vote for %s, %d more players needed to pass vote for %s. Type /vote to support this vote.", ChatColor.DARK_GRAY, player.getDisplayName(), args[0], voteThreshold, args[0]));
        return true;
    }

    private void printUsage(Player player) {
        if(voting) {
            player.sendMessage("Voting in progress, wait until current vote expires or passes.");
        } else {
            player.sendMessage(String.format("%s======== %sVote%s ========", ChatColor.YELLOW, ChatColor.GOLD, ChatColor.YELLOW));
            player.sendMessage(String.format("%s/vote weather %s[clear/storm]", ChatColor.GREEN, ChatColor.YELLOW));
            player.sendMessage(String.format("%s/vote time %s[day/night]", ChatColor.GREEN, ChatColor.YELLOW));
            player.sendMessage(String.format("%s/vote kick %s[player]", ChatColor.GREEN, ChatColor.YELLOW));
            player.sendMessage(String.format("%s/vote mute %s[player]", ChatColor.GREEN, ChatColor.YELLOW));
        }
    }
}
