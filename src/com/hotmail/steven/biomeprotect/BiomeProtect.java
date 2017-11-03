package com.hotmail.steven.biomeprotect;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BiomeProtect extends JavaPlugin {
	
	private static ProtectedRegionList<ProtectedRegion> regions;
	
	@Override
	public void onEnable()
	{
		regions = new ProtectedRegionList<ProtectedRegion>();
		
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	/**
	 * Create a protected region for a player
	 * @param owner
	 * @param point1
	 * @param point2
	 */
	public static ProtectedRegion defineRegion(Player owner, Location center, int height, int radius)
	{
		return defineRegion(owner.getUniqueId(), center, height, radius);
	}
	
	
	/**
	 * Create a protected region for a player
	 * @param owner
	 * @param point1
	 * @param point2
	 */
	public static ProtectedRegion defineRegion(UUID owner, Location center, int height, int radius)
	{
		// Create the new base region
		ProtectedRegion region = new ProtectedRegion(owner, center, height, radius);
		regions.add(region);
		return region;
	}

	/**
	 * Find all regions that intercept a location
	 * @param block
	 * @return
	 */
	public static List<ProtectedRegion> findRegions(Location loc)
	{
		return regions.intercepts(loc);
	}
	
	/**
	 * Find all regions that intercept a block
	 * @param block
	 * @return
	 */
	public static List<ProtectedRegion> findRegions(Block block)
	{
		return findRegions(block.getLocation());
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
	
	/**
	 * Remove a protected region from the database
	 * @param region
	 */
	public static void removeProtectedRegion(ProtectedRegion region)
	{
		regions.remove(region);
	}
	
}
