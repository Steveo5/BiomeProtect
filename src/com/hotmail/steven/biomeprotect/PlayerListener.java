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
import static com.hotmail.steven.biomeprotect.Language.tl;

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
		Player player = evt.getPlayer();
		
		if(ProtectedRegion.hasPlacePermission(player.getUniqueId(), blockLocation))
		{
			if(RegionSettings.isProtectionStone(evt.getItemInHand()))
			{
				ProtectionStone protectionStone = RegionSettings.getProtectionStone(evt.getItemInHand());
				ProtectedRegion region = BiomeProtect.defineRegion(protectionStone, evt.getPlayer(), blockLocation);
				evt.getPlayer().sendMessage("You have placed " + region.getName());
			}
		} else
		{
			tl(player, "noPlacePermission");
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Where the block was broken
		Location blockLocation = evt.getBlock().getLocation();
		Player player = evt.getPlayer();
		if(ProtectedRegion.hasBreakPermission(player.getUniqueId(), blockLocation))
		{
			// Check if the player is breaking the internal region
			ProtectedRegion innerRegion = BiomeProtect.findRegionExact(blockLocation);
			if(innerRegion != null && innerRegion.getOwner().equals(player.getUniqueId()))
			{
				innerRegion.remove();
				tl(player, "regionRemoved");
			}			
		} else
		{
			tl(player, "noBreakPermission");
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
