package com.hotmail.steven.biomeprotect.listener;

import static com.hotmail.steven.biomeprotect.Language.tl;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.biomeprotect.region.RegionCreator;
import com.hotmail.steven.biomeprotect.storage.RegionConfig;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class RegionCreateListener extends BiomeProtectListener {

	public RegionCreateListener(BiomeProtect plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		Player player = evt.getPlayer();
		/**
		 * Handles creating a new protected region
		 */
		// Check if the player is placing a valid protection stone
		ConfigurationSection stoneConfig = getRegionConfig().getConfigurationSection("protection-stones", evt.getItemInHand());
		if(stoneConfig != null)
		{
			// Get the required settings for the new region
			// Start creating the new region
			RegionCreator regionCreator = new RegionCreator(stoneConfig.getName().toLowerCase());
			// Set the required values
			regionCreator.radius(stoneConfig.getInt("radius"));
			regionCreator.type(Material.valueOf(stoneConfig.getString("block").toUpperCase()));
			// Set the optional values
			if(stoneConfig.contains("meta.title")) regionCreator.title(stoneConfig.getString("meta.title"));
			if(stoneConfig.contains("meta.lore")) regionCreator.lore(stoneConfig.getStringList("meta.lore"));
			if(stoneConfig.contains("height")) 
			{
				regionCreator.height(stoneConfig.getInt("height"));
			} else
			{
				regionCreator.height(regionCreator.radius());
			}
			//TODO set the priority
			ProtectedRegion region = regionCreator.createRegion(player.getUniqueId(), evt.getBlock().getLocation(), evt.getBlock().getType());
			// Get highest priority region intercepting this region
			ProtectedRegionList intercepting = getPlugin().getRegionContainer().queryRegions(region);
			// Add one to the highest priority for this region
			if(!intercepting.isEmpty()) region.setPriority(intercepting.getHighestPriority().getPriority() + 1);
			player.sendMessage("New priority " + region.getPriority());
			// Now the region is known
			this.getPlugin().getRegionContainer().getCache().add(region);
			Location blockLoc = evt.getBlock().getLocation();
			player.sendMessage(tl("regionPlaced", new String[] {"%x%", String.valueOf(blockLoc.getBlockX()), "%y%", String.valueOf(blockLoc.getBlockY()),
					"%z%", String.valueOf(blockLoc.getBlockZ())}));
			// Finally save the region to database
			getPlugin().getRegionData().getConnection().saveRegion(region);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Query to see if the player removed a region
		ProtectedRegion region = getPlugin().getRegionContainer().queryRegion(evt.getBlock().getLocation());
		if(region != null)
		{
			// Delete the region
			getPlugin().getRegionContainer().removeRegion(region);
			tl(evt.getPlayer(), "regionRemoved");
			// Remove from the database
			getPlugin().getRegionData().getConnection().removeRegion(region.getId());
		}
	}
	
}
