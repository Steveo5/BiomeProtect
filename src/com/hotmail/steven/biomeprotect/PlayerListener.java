package com.hotmail.steven.biomeprotect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class PlayerListener implements Listener {

	// Keep track of the region the player is currently in
	private HashMap<UUID, ProtectedRegion> enteredRegions;
	private HashMap<UUID, Long> interactLimit;
	
	public PlayerListener()
	{
		enteredRegions = new HashMap<UUID, ProtectedRegion>();
		interactLimit = new HashMap<UUID, Long>();
	}
	
	@EventHandler
	public void onBlockClick(PlayerInteractEvent evt)
	{
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Player p = evt.getPlayer();
			// Get the time millis since last interact
			long timeSinceLastInteract = interactLimit.containsKey(p.getUniqueId()) ? interactLimit.get(p.getUniqueId()) : 0;
			// Calculate that in seconds
			long timeInSeconds = (System.currentTimeMillis() - timeSinceLastInteract) / 1000;
			// Check if they have interacted too much
			if(timeInSeconds >= RegionSettings.getInteractLimit())
			{
				ProtectedRegion region = BiomeProtect.findRegionExact(evt.getClickedBlock());
				if(region != null)
				{
					if(!region.isOwner(p.getUniqueId()))
					{
						p.sendMessage("You have no permission to access this players region");
					} else
					{
						BiomeProtect.getMenu().show(evt.getPlayer(), region);
					}
					evt.setCancelled(true);
				}
				interactLimit.put(p.getUniqueId(), System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		// Where the block was placed
		Location blockLocation = evt.getBlock().getLocation();
		Player player = evt.getPlayer();
		// Check if the player can perform a break action at the location
		if(!canPerformAction(evt.getBlock(), 1, player))
		{
			evt.setCancelled(true);
			tl(player, "noPermission");
		} else if(evt.getBlock().getType() == Material.TNT && !canPerformAction(evt.getBlock(), 5, player))
		{
			evt.setCancelled(true);
			tl(player, "noPermission");
			
		// Check if the player can perform a region create action at the location
		} else if(RegionSettings.isProtectionStone(evt.getItemInHand()))
		{
			// Get the flags/settings for the protection stone
			ProtectionStone protectionStone = RegionSettings.getProtectionStone(evt.getItemInHand());
			System.out.println("Checking reigon palce");
			if(!canPerformRegionPlace(evt.getBlock(), player, protectionStone))
			{
				evt.setCancelled(true);
				tl(player, "noPermission");
			} else
			{
				System.out.println(protectionStone.getWelcomeMessage());
				// Create the protected region physically
				ProtectedRegion region = BiomeProtect.defineRegion(protectionStone, evt.getPlayer(), blockLocation);
				// Generate a uuid for the region
				UUID newId = UUID.randomUUID();
				region.setUUID(newId);
				// Find the intercepting regions
				List<ProtectedRegion> interceptingRegions = BiomeProtect.findInterceptingRegions(region);
				// Get the highest priority from the intercepting regions
				ProtectedRegion highestPriorityRegion = BiomeProtect.getRegionList().getHighestPriority(interceptingRegions);
				int highestPriority = highestPriorityRegion == null ? 0 : highestPriorityRegion.getPriority();
				System.out.println(highestPriority + " highest priorty");
				region.setPriority(highestPriority + 1);
				// Cache the region
				BiomeProtect.getRegionCache().add(region);
				evt.getPlayer().sendMessage("You have placed " + region.getName());
				// Save the region to database
				BiomeProtect.getRegionData().saveRegion(region, true);
				
				// Some debug
				List<ProtectedRegion> intercepting = BiomeProtect.findInterceptingRegions(region);
				player.sendMessage("Region priority " + region.getPriority());
				player.sendMessage("Total intercepting " + intercepting.size());
				for(ProtectedRegion interceptingRegion : intercepting)
				{
					player.sendMessage("- " + interceptingRegion.getId());
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerPvp(EntityDamageByEntityEvent evt)
	{
		if(evt.getDamager() instanceof Player && evt.getEntity() instanceof Player)
		{
			Player damager = (Player)evt.getDamager();
			// Check if the user can perform a pvp action at the damaged players block
			if(!canPerformAction(evt.getEntity().getLocation().getBlock(), 3, damager))
			{
				evt.setCancelled(true);
				tl(damager, "noPermission");
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Check if the user can perform a break action at the specific block
		if(!canPerformAction(evt.getBlock(), 2, evt.getPlayer()))
		{
			evt.setCancelled(true);
			tl(evt.getPlayer(), "noPermission");
		} else
		{
			ProtectedRegion brokenRegion = BiomeProtect.findRegionExact(evt.getBlock());
			if(brokenRegion != null)
			{
				Player p = evt.getPlayer();
				// Check if the user can break the region
				if(!p.hasPermission("biomeprotect.break") || !brokenRegion.isOwner(p.getUniqueId()))
				{
					tl(evt.getPlayer(), "noPermission");
					evt.setCancelled(true);
				} else
				{
					brokenRegion.remove();
					tl(evt.getPlayer(), "regionRemoved");
				}
			}
		}
	}
	
	/**
	 * Check whether or not a player can perform an action at
	 * a specified block, this could include break, place, light fire
	 * pvp etc
	 * @param block
	 * @param action 1 = place, 2 = break, 3 = pvp
	 * 5 = tnt place, 6 = access region menu
	 * @param player
	 * @return
	 */
	private boolean canPerformAction(Block block, int action, Player player)
	{
		ProtectedRegion region = BiomeProtect.findRegion(block);

		if(region != null)
		{
			switch(action)
			{
			case 1:
				if(!(region.isOwner(player.getUniqueId()) || (region.hasMember(player.getUniqueId()) && region.allowsPlace())))
				{
					return false;
				}
				break;
			case 2:
				if(!(region.isOwner(player.getUniqueId()) || (region.hasMember(player.getUniqueId()) && region.allowsBreak())))
				{
					return false;
				}
				break;
			}
		}
		
		
		return true;
	}
	
	/**
	 * Check if a player can place a region at the specific block
	 * @param block
	 * @param player
	 * @param stone
	 * @return
	 */
	private boolean canPerformRegionPlace(Block block, Player player, ProtectionStone stone)
	{
		int x1 =  block.getX() - stone.getRadius();
		int y1 = block.getY() - stone.getRadius();
		int z1 = block.getZ() - stone.getRadius();
		
		int x2 = block.getX() + stone.getRadius();
		int y2 = block.getY() + stone.getRadius();
		int z2 = block.getZ() + stone.getRadius();
		
		int minX = x1 < x2 ? x1 : x2;
		int minY = y1 < y2 ? y1 : y2;
		int minZ = z1 < z2 ? z1 : z2;
		
		int maxX = x1 > x2 ? x1 : x2;
		int maxY = y1 > y2 ? y1 : y2;
		int maxZ = z1 > z2 ? z1 : z2;
		
		// Distance check all regions in the players current chunk
		for(ProtectedRegion region : BiomeProtect.findRegions(block.getChunk()))
		{
			if(region.isOwner(player.getUniqueId())) continue;
			// Get smaller and larger points
			Location smaller = region.getSmallerPoint();
			Location larger = region.getLargerPoint();
			// Compare distances for each coordinate
			int minXDiff = smaller.getBlockX() < minX ? minX - smaller.getBlockX() : smaller.getBlockX() - minX;
			int minYDiff = smaller.getBlockY() < minY ? minY - smaller.getBlockY() : smaller.getBlockY() - minY;
			int minZDiff = smaller.getBlockZ() < minZ ? minZ - smaller.getBlockZ() : smaller.getBlockZ() - minZ;
			// All four sides
			int maxXDiff = larger.getBlockX() < maxX ? maxX - larger.getBlockX() : larger.getBlockX() - maxX;
			int maxYDiff = larger.getBlockY() < maxY ? maxY - larger.getBlockY() : larger.getBlockY() - maxY;
			int maxZDiff = larger.getBlockZ() < maxZ ? maxZ - larger.getBlockZ() : larger.getBlockZ() - maxZ;
			// Distance allowed is sum of both protection stones radius
			int distanceAllowed = region.getRadius() + stone.getRadius();
			
			if(minXDiff < distanceAllowed || minYDiff < distanceAllowed || minZDiff < distanceAllowed
					|| maxXDiff < distanceAllowed || maxYDiff < distanceAllowed || maxZDiff < distanceAllowed)
			{
				// Return false if the distance between any one side is smaller then allowed distance
				return false;
			}
		}
		return true;
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
		Location locTo = evt.getTo();
		Location locFrom = evt.getFrom();

		// Call player exited or entered region if required
		for(ProtectedRegion region : BiomeProtect.getRegionCache().getCache().values())
		{
			// locFrom is in the region and locTo is not in the region > The player has left the region
			if(region.isLocationInside(locFrom) && !region.isLocationInside(locTo))
			{
				playerExitedRegion(player, region, evt);
			// Vice versa
			} else if(!region.isLocationInside(locFrom) && region.isLocationInside(locTo))
			{
				playerEnteredRegion(player, region, evt);
			}
		}
		
		Chunk from = evt.getFrom().getChunk();
		Chunk to = evt.getTo().getChunk();
		
		// If the player moved a chunk
		if(from.getX() != to.getX() || from.getZ() != to.getZ())
		{
			onPlayerMoveChunk(evt);
		}
	}
	
	/**
	 * When the player moves a chunk
	 * @param evt
	 */
	public void onPlayerMoveChunk(PlayerMoveEvent evt)
	{

		cacheRegions(evt.getPlayer());
	}
	
	/**
	 * Called when a player moves into the protected region
	 * @param player
	 * @param region
	 */
	public void playerEnteredRegion(Player player, ProtectedRegion region,  PlayerMoveEvent evt)
	{
		if(region.hasWelcomeMessage())
		{
			OfflinePlayer owner = Bukkit.getPlayer(region.getOwner());
			if(region.hasWelcomeMessage())
			{
				evt.getPlayer().sendMessage(region.getWelcomeMessage().replaceAll("%player%", owner.getName()) + " id " + region.getId());
			}
		}
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
		if(region.hasLeaveMessage())
		{
			OfflinePlayer owner = Bukkit.getPlayer(region.getOwner());
			String leaveMessage = region.getLeaveMessage();
			leaveMessage = leaveMessage.replaceAll("%player%", owner.getName());
			evt.getPlayer().sendMessage(leaveMessage + " id " + region.getId());
		}
	}
	
	@EventHandler
	public void playerJoinEvent(PlayerJoinEvent evt)
	{
		cacheRegions(evt.getPlayer());
	}
	
	@EventHandler
	public void playerTeleport(PlayerTeleportEvent evt)
	{
		cacheRegions(evt.getPlayer());
	}
	
	/**
	 * Caches all regions the player is currently standing in.
	 * Shouldn't be called too often as it loops the entire database
	 * @param player
	 */
	private void cacheRegions(Player player)
	{
		Chunk playerChunk = player.getLocation().getChunk();
		
		// Cache the regions the player joins on
		for(ProtectedRegion region : BiomeProtect.getRegionList().getAll())
		{
			// Skip if seperate world
			if(!region.getCenter().getWorld().getUID().equals(player.getWorld().getUID())) continue;
			// Loop over existing chunks in the region and cache the region if it exists in the same chunk
			HashSet<Chunk> regionChunks = region.getExistingChunks();
			for(Chunk regionChunk : regionChunks)
			{
				// Check chunks are same as players
				if(regionChunk.getX() == playerChunk.getX() && regionChunk.getZ() == playerChunk.getZ())
				{
					BiomeProtect.getRegionCache().add(region);
					break;
				}
			}
		}		
	}
	
}
