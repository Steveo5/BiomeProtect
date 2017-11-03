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
	public static ProtectedRegion defineRegion(Player owner, Location point1, Location point2)
	{
		return defineRegion(owner.getUniqueId(), point1, point2);
	}
	
	
	/**
	 * Create a protected region for a player
	 * @param owner
	 * @param point1
	 * @param point2
	 */
	public static ProtectedRegion defineRegion(UUID owner, Location point1, Location point2)
	{
		// Create the new base region
		ProtectedRegion region = new ProtectedRegion(owner, point1, point2);
		regions.add(region);
		return region;
	}
	
	/**
	 * Find all regions that intercept a block
	 * @param block
	 * @return
	 */
	public static List<ProtectedRegion> findRegions(Block block)
	{
		return regions.intercepts(block.getLocation());
	}
	
}
