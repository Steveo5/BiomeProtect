package com.hotmail.steven.biomeprotect;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
					BiomeProtect.getMenu().show(evt.getPlayer(), region);
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
		
		if(ProtectedRegion.hasPlacePermission(player.getUniqueId(), blockLocation))
		{
			if(RegionSettings.isProtectionStone(evt.getItemInHand()))
			{
				// Get the flags/settings for the protection stone
				ProtectionStone protectionStone = RegionSettings.getProtectionStone(evt.getItemInHand());
				System.out.println(protectionStone.getWelcomeMessage());
				// Create the protected region physically
				ProtectedRegion region = BiomeProtect.defineRegion(protectionStone, evt.getPlayer(), blockLocation);
				UUID newId = UUID.randomUUID();
				region.setUUID(newId);
				// Cache the region
				BiomeProtect.getRegionCache().add(region);
				evt.getPlayer().sendMessage("You have placed " + region.getName());
				BiomeProtect.getRegionData().saveRegion(region, true);
				
				List<ProtectedRegion> intercepting = BiomeProtect.findInterceptingRegions(region);
				player.sendMessage("Total intercepting " + intercepting.size());
				for(ProtectedRegion interceptingRegion : intercepting)
				{
					player.sendMessage("- " + interceptingRegion.getId());
				}
			}
		} else
		{
			tl(player, "noPlacePermission");
		}
	}
	
	@EventHandler
	public void onPlayerPvp(EntityDamageByEntityEvent evt)
	{
		if(evt.getDamager() instanceof Player && evt.getEntity() instanceof Player)
		{
			Player damager = (Player)evt.getDamager();
			// Check if the damaged player is in a region that does't allow pvp
			List<ProtectedRegion> playerRegions = BiomeProtect.findRegions(damager.getLocation());
			for(ProtectedRegion region : playerRegions)
			{
				if(!region.allowsPvp())
				{
					damager.sendMessage("You're in a region that doesn't allow pvp");
					evt.setCancelled(true);
				}
			}
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

		for(ProtectedRegion region : BiomeProtect.getRegionList().getAll())
		{
			for(Chunk chunkInRegion : region.getExistingChunks())
			{
				Location to = evt.getTo();
				Chunk toChunk = to.getChunk();
				// Cache the region if the player steps in a chunk where regions exist
				if(toChunk.getX() == chunkInRegion.getX() && toChunk.getZ() == chunkInRegion.getZ())
				{
					// Generate a region id based on the center block
					int regionId = region.getCenter().getBlockX() + region.getCenter().getBlockY() + region.getCenter().getBlockZ();
					// Cache the region for faster all round checks
					BiomeProtect.getRegionCache().add(region);					
				}
			}
		}
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
	
	public void playerJoinEvent(PlayerJoinEvent evt)
	{
		Player player = evt.getPlayer();
		Chunk pChunk = evt.getPlayer().getLocation().getChunk();
		// Cache the region if the player enters in on it
		for(ProtectedRegion region : BiomeProtect.getRegionList().getAll())
		{
			for(Chunk chunkInRegion : region.getExistingChunks())
			{
				if(chunkInRegion.getX() == pChunk.getX() && chunkInRegion.getZ() == pChunk.getZ())
				{
					BiomeProtect.getRegionCache().add(region);
					break;
				}
			}
		}
	}
	
}
