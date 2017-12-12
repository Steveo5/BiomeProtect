package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BiomeProtect extends JavaPlugin {
	
	private static ProtectedRegionList<ProtectedRegion> regions;
	private static RegionData regionData;
	private static BiomeProtect plugin;
	private static RegionCache regionCache;
	private static RegionMenu menu;

	@Override
	public void onEnable()
	{
		regionData = new RegionData(this);
		regions = new ProtectedRegionList<ProtectedRegion>();
		menu = new RegionMenu();
		// Save default config if needed
		this.saveDefaultConfig();
		// Register player listener
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(menu, this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		this.getCommand("biomeprotect").setExecutor(new CommandHandler());
		
		plugin = this;
		
		RegionSettings.loadProtectionStones();
		regionCache = new RegionCache();
		
		Logger.Log(Level.INFO, "Loading regions shortly...");
		regionData.loadRegions();
		
	}
	
	@Override
	public void onDisable()
	{
		Logger.Log(Level.INFO, "Saving all regions to the database");
		for(ProtectedRegion region : regions.getAll())
		{
			regionData.saveRegion(region, false);
		}
	}
	
	public static BiomeProtect instance()
	{
		return plugin;
	}
	
	/**
	 * Holds the database/flatfile methods to store
	 * and retrieve protection stones
	 * @return
	 */
	public static RegionData getRegionData()
	{
		return regionData;
	}
	
	/**
	 * Get all the cached regions for
	 * faster checking
	 * @return
	 */
	public static RegionCache getRegionCache()
	{
		return regionCache;
	}
	
	public static RegionMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Get all of the protected regions that exist
	 * @return
	 */
	public static ProtectedRegionList<ProtectedRegion> getRegionList()
	{
		return regions;
	}
	
	/**
	 * Get the main config.yml
	 * @return
	 */
	public static FileConfiguration getRegionConfig()
	{
		return plugin.getConfig();
	}
	
	/**
	 * Create a protected region for a player
	 * @param owner
	 * @param point1
	 * @param point2
	 */
	public static ProtectedRegion defineRegion(ProtectionStone settings, Player owner, Location center)
	{
		return defineRegion(settings, owner.getUniqueId(), center);
	}
	
	/**
	 * Create a protected region for a player
	 * @param owner
	 * @param point1
	 * @param point2
	 */
	public static ProtectedRegion defineRegion(ProtectionStone settings, UUID owner, Location center)
	{
		ProtectedRegion region = new ProtectedRegion(settings, owner, center);
		// Add the region to the loaded regions list
		regions.add(region);
		return region;		
	}

	/**
	 * Find all regions that intercept a location
	 * Orders by region priority
	 * @param block
	 * @return
	 */
	public static HashSet<ProtectedRegion> findRegions(Location loc)
	{
		return regions.intercepts(loc);
	}

	/**
	 * Find all regions that intercept a block
	 * @param block
	 * @return
	 */
	public static HashSet<ProtectedRegion> findRegions(Block block)
	{
		return findRegions(block.getLocation());
	}
	
	/**
	 * Find all regions intercepting a chunk
	 * @param chunk
	 * @return
	 */
	public static HashSet<ProtectedRegion> findRegions(Chunk chunk)
	{
		HashSet<ProtectedRegion> intercepting = new HashSet<ProtectedRegion>();
		for(ProtectedRegion region : getRegionCache().getCache().values())
		{
			for(Chunk c : region.getExistingChunks())
			{
				if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ())
				{
					intercepting.add(region);
				}
			}
		}
		return intercepting;
	}
	
	/**
	 * Find all regions in a set of chunks
	 * @param chunks
	 * @return
	 */
	public static HashSet<ProtectedRegion> findRegions(HashSet<Chunk> chunks)
	{
		HashSet<ProtectedRegion> inChunk = new HashSet<ProtectedRegion>();
		for(Chunk c : chunks)
		{
			inChunk.addAll(findRegions(c));
		}
		return inChunk;
	}
	
	public static HashSet<ProtectedRegion> findIntercepting(ProtectedRegion region)
	{
		HashSet<ProtectedRegion> intercepting = new HashSet<ProtectedRegion>();
		// Distance check all regions in the players current chunk
		for(ProtectedRegion compare : findRegions(region.getExistingChunks()))
		{
			if(compare.interceptBoundingBox(region)) intercepting.add(compare);
		}
		
		return intercepting;
	}
	
	/**
	 * Finds a region at the specified block
	 * if more then one region then the max
	 * priority region is returned
	 * @return
	 */
	public static ProtectedRegion findRegion(Block block)
	{
		return regions.getHighestPriority(findRegions(block));
	}

	/**
	 * Get the region that exists with the center block
	 * at the first parameter
	 * @param block
	 * @return
	 */	
	public static ProtectedRegion findRegionExact(Location loc)
	{
		for(ProtectedRegion region : findRegions(loc))
		{
			if(region.getCenter().getBlockX() == loc.getBlockX() && region.getCenter().getBlockY() == loc.getBlockY()
					&& region.getCenter().getBlockZ() == loc.getBlockZ())
			{
				return region;
			}
		}
		
		return null;		
	}
	
	/**
	 * Get the region that exists with the center block
	 * at the first parameter
	 * @param block
	 * @return
	 */
	public static ProtectedRegion findRegionExact(Block block)
	{
		return findRegionExact(block.getLocation());
	}
	
	public static ProtectedRegion getRegion(UUID id)
	{
		return getRegionCache().getCachedRegion(id);
	}
	
	/**
	 * Remove a protected region from the database
	 * @param region
	 */
	public static void removeProtectedRegion(ProtectedRegion region)
	{
		regionData.removeRegion(region.getId());
		regions.remove(region);
	}
	
}
