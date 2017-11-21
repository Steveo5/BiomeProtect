package com.hotmail.steven.util;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class PlayerUtil {

    public static final Block getTarget(Player player, int range) {
        BlockIterator bi= new BlockIterator(player, range);
        Block lastBlock = bi.next();
        while (bi.hasNext()) {
            lastBlock = bi.next();
            if (lastBlock.getType() == Material.AIR)
                continue;
            break;
        }
        return lastBlock;
    }
    
    // Converts a list of UUIDs to a list of OfflinePlayer objects
    public static HashSet<OfflinePlayer> uuidListAsPlayers(HashSet<UUID> uuidList)
    {
    	HashSet<OfflinePlayer> players = new HashSet<OfflinePlayer>();
    	for(UUID uuid : uuidList)
    	{
    		players.add(Bukkit.getPlayer(uuid));
    	}
    	return players;
    }
    
    // Converts a list of offlineplayers to a list of their names instead
    public static HashSet<String> playersAsNames(HashSet<OfflinePlayer> playerList)
    {
    	HashSet<String> playerNames = new HashSet<String>();
    	for(OfflinePlayer player : playerList)
    	{
    		playerNames.add(player.getName());
    	}
    	return playerNames;
    }
	
}
