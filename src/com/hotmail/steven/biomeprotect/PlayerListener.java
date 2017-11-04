package com.hotmail.steven.biomeprotect;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {

	// Keep track of the region the player is currently in
	private HashMap<UUID, ProtectedRegion> enteredRegions;
	
	public PlayerListener()
	{
		enteredRegions = new HashMap<UUID, ProtectedRegion>();
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		// Where the block was placed
		Location blockLocation = evt.getBlock().getLocation();
		
		if(BiomeProtect.findRegions(evt.getBlock()).isEmpty())
		{
			ProtectedRegion region = BiomeProtect.defineRegion(evt.getPlayer(), blockLocation, 5, 5);
			evt.getPlayer().sendMessage("New region created at your location");
		} else
		{
			evt.getPlayer().sendMessage("A protected region already exists!");
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Where the block was broken
		Location blockLocation = evt.getBlock().getLocation();
		
		ProtectedRegion foundRegion = BiomeProtect.findRegionExact(blockLocation);
		
		if(foundRegion != null)
		{
			foundRegion.remove();
			evt.getPlayer().sendMessage("You have removed a protected region");
		}
		
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e)
	{
		// Block move
	    if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockY() != e.getTo().getBlockY() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
	        onPlayerMoveBlock(e);
	    }
	}
	
	/**
	 * Called when the player moves to another block
	 * @param evt
	 */
	public void onPlayerMoveBlock(PlayerMoveEvent evt)
	{
		Player player = evt.getPlayer();
		// Get the regions in the block the player is moving to
		List<ProtectedRegion> regionsAtBlock = BiomeProtect.findRegions(evt.getTo().getBlock());
		if(regionsAtBlock.size() == 1)
		{
			ProtectedRegion playersRegion = enteredRegions.get(player.getUniqueId());
			if(playersRegion == null || !playersRegion.equals(regionsAtBlock.get(0)))
			{
				enteredRegions.put(player.getUniqueId(), regionsAtBlock.get(0));
				playerEnteredRegion(player, playersRegion, evt);
			}
		} else if(regionsAtBlock.size() < 1)
		{
			// All the regions at the previous block
			ProtectedRegion playersRegion = enteredRegions.get(player.getUniqueId());
			if(playersRegion != null)
			{
				enteredRegions.remove(player.getUniqueId());
				playerExitedRegion(player, playersRegion, evt);
			}
		}
	}
	
	/**
	 * Called when a player moves into the protected region
	 * @param player
	 * @param region
	 */
	public void playerEnteredRegion(Player player, ProtectedRegion region, PlayerMoveEvent evt)
	{
		evt.getPlayer().sendMessage("You have entered a region");
	}
	
	/**
	 * Called when a player leaves a region they
	 * were previously in
	 * @param player
	 * @param region
	 * @param evt
	 */
	public void playerExitedRegion(Player player, ProtectedRegion region, PlayerMoveEvent evt)
	{
		evt.getPlayer().sendMessage("You have left a region");
	}
	
}
