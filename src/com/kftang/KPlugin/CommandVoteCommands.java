package com.kftang.KPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Kenny Tang (c) 2017
 */
public class CommandVoteCommands implements CommandExecutor, Runnable {
    private enum VoteType {WEATHER, TIME, KICK, MUTE, NONE};
    private final Server server;
    private final double percentage;
    private final long voteExpireTime;
    private boolean voting;
    private int supporters;
    private long expireTime;
    private Set<Player> votedPlayers;
    private VoteType currentVote;
    private String currentVoteTarget;
    private Thread timerThread;

    CommandVoteCommands(FileConfiguration config, Server server) {
        this.server = server;
        percentage = 100d / config.getDouble("votePercentageNeeded");
        voteExpireTime = config.getInt("voteExpireTime");
        votedPlayers = new HashSet<>();
        init(false);
        timerThread = new Thread(this);
    }

    private void init(boolean expired) {
        supporters = 0;
        currentVote = VoteType.NONE;
        currentVoteTarget = "";
        voting = false;
        votedPlayers.clear();
        if(expired)
            server.broadcastMessage("Vote has expired!");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String currentVoteDescription = currentVote.toString().toLowerCase() + " " + currentVoteTarget;
        Player player = (Player) commandSender;
        //Number of people required to allow vote through
        int voteThreshold = (int)(server.getOnlinePlayers().size() / percentage);
        //If we are currently in a vote and the command is by itself
        if(voting && args.length == 0) {
            //If the player has already voted...
            if(!votedPlayers.add(player)) {
                player.sendMessage("You have already voted!");
                return true;
            }
            supporters++;
            player.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Vote Casted");
            if(supporters >= voteThreshold) {
                votePassed();
                return true;
            } else {
                server.broadcastMessage(String.format("%s%d/%d has voted for %s!", ChatColor.DARK_GRAY, supporters, voteThreshold, currentVoteDescription));
            }
        }
        if((args.length != 2 && !voting) || (voting && args.length != 0 )) {
            printUsage(player);
            return true;
        }
        //First arg is what the vote is for
        //Check for a valid second argument
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
                if(server.getPlayer(args[1]) == null){
                    player.sendMessage("Invalid target, player " + args[1] + " is not online.");
                    return true;
                }
                currentVote = VoteType.KICK;
                currentVoteTarget = args[1];
                break;
            case "mute":
                if(server.getPlayer(args[1]) == null){
                    player.sendMessage("Invalid target, player " + args[1] + " is not online.");
                    return true;
                }
                currentVote = VoteType.MUTE;
                currentVoteTarget = args[1];
                break;
            default:
                printUsage(player);
                return true;
        }
        currentVoteDescription = currentVote.toString().toLowerCase() + " " + currentVoteTarget;
        voting = true;
        supporters++;
        votedPlayers.add(player);
        expireTime = System.currentTimeMillis() / 1000 + voteExpireTime;
        server.broadcastMessage(String.format("%s%s has initiated a vote for %s, %d more players needed. Type /vote to support this vote.", ChatColor.DARK_GRAY, player.getDisplayName(), currentVoteDescription, Math.max(voteThreshold - 1, 0)));
        if(supporters >= voteThreshold) {
            votePassed();
            return true;
        }
        return true;
    }

    private void votePassed() {
        server.broadcastMessage(String.format("%sVote for %s %s has passed!", ChatColor.DARK_GRAY, currentVote.toString().toLowerCase(), currentVoteTarget));
        String execCommand = currentVote.toString();
        if(currentVote == VoteType.TIME)
            execCommand += " set";
        if(currentVote == VoteType.WEATHER && currentVoteTarget.equals("storm"))
            server.dispatchCommand(server.getConsoleSender(), execCommand + " " + "rain");
        if(currentVote == VoteType.WEATHER && currentVoteTarget.equals("clear"))
            server.dispatchCommand(server.getConsoleSender(), execCommand + " " + "sun");
        server.dispatchCommand(server.getConsoleSender(), execCommand + " " + currentVoteTarget);
        init(false);
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

    @Override
    public void run() {
        while(true) {
            if(voting && System.currentTimeMillis() / 1000 > expireTime) {
                init(true);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
