package com.hotmail.steven.biomeprotect.manager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.cache.RegionCache;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.util.LocationUtil;

public class RegionContainer {

	private BiomeProtect plugin;
	private RegionCache cache;
	private HashSet<ProtectedRegion> regions;
	
	/**
	 * Allows querying for protected regions
	 * The region container pulls directly from the cache
	 * @param plugin
	 */
	public RegionContainer(BiomeProtect plugin)
	{
		this.plugin = plugin;
		cache = new RegionCache();
		regions = new HashSet<ProtectedRegion>();
	}
	
	public RegionCache getCache()
	{
		return cache;
	}
	
	/**
	 * Get all the regions loaded on the server
	 * @return
	 */
	public HashSet<ProtectedRegion> getRegions()
	{
		return regions;
	}
	
	/**
	 * Removes and deletes a region from the database
	 * @param region
	 */
	public void removeRegion(ProtectedRegion region)
	{
		if(cache.isCached(region.getId())) cache.remove(region);
		if(regions.contains(region))
			regions.remove(region);
		//TODO database remove
	}
	
	/**
	 * Adds a region to the list of loaded regions and
	 * to the data source
	 * @param region
	 */
	public void addRegion(ProtectedRegion region)
	{
		regions.add(region);
		//TODO database add
	}
	
	/**
	 * Query all regions that the specified location reside in
	 * @param location
	 * @return ProtectedRegionList
	 */
	public ProtectedRegionList queryRegions(Location location)
	{
		// Create the set of regions that was found
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
		// Create the iterator
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			// Location is within the smaller point
			if(location.getBlockX() >= next.getSmallerPoint().getBlockX() && location.getBlockY() >= next.getSmallerPoint().getBlockY()
					&& location.getBlockZ() >= next.getSmallerPoint().getBlockZ())
			{
				// Location is within the larger point
				if(location.getBlockX() <= next.getLargerPoint().getBlockX() && location.getBlockY() <= next.getLargerPoint().getBlockY()
						&& location.getBlockZ() <= next.getLargerPoint().getBlockZ())
				{
					regions.add(next);
				}
			}
		}
		
		return new ProtectedRegionList(regions);
	}
	
	/**
	 * Get all regions that a list of blocks reside in
	 * @param blocks
	 * @return ProtectedRegionList
	 */
	public ProtectedRegionList queryRegions(List<Block> blocks)
	{
		// Create the set of regions that was found
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
		// Create the iterator
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			// Location is within the smaller point
			inner:
			for(Block b : blocks)
			{
				Location location = b.getLocation();
				if(location.getBlockX() >= next.getSmallerPoint().getBlockX() && location.getBlockY() >= next.getSmallerPoint().getBlockY()
						&& location.getBlockZ() >= next.getSmallerPoint().getBlockZ())
				{
					// Location is within the larger point
					if(location.getBlockX() <= next.getLargerPoint().getBlockX() && location.getBlockY() <= next.getLargerPoint().getBlockY()
							&& location.getBlockZ() <= next.getLargerPoint().getBlockZ())
					{
						regions.add(next);
						break inner;
					}
				}
			}
		}
		
		return new ProtectedRegionList(regions);
	}
	
	/**
	 * Query all regions that reside in a specified chunk
	 * @param chunk
	 * @return ProtectedRegionList
	 */
	public ProtectedRegionList queryRegions(Chunk chunk)
	{
		// Create the set of regions that was found
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
		// Create the iterator
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			// Loop over all of the chunks in the region
			inner:
			for(Chunk c : next.getExistingChunks())
			{
				if(c.getX() == chunk.getX() && c.getZ() == chunk.getZ())
				{
					regions.add(next);
					break inner;
				}
			}
		}
		
		return new ProtectedRegionList(regions);
	}
	
	/**
	 * Query all regions that reside between 2 locations
	 * @param point1 smaller point
	 * @param point2 larger point
	 * @return ProtectedRegionList
	 */
	public ProtectedRegionList queryRegions(Location point1, Location point2)
	{
		// Create the set of regions that was found
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
		// Create the iterator
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			if(LocationUtil.boxContains(next.getSmallerPoint(), next.getLargerPoint(), point1) &&
					LocationUtil.boxContains(next.getSmallerPoint(), next.getLargerPoint(), point2))
			{
				regions.add(next);
			}
		}
		
		return new ProtectedRegionList(regions);
	}
	
	/**
	 * Query all regions that intercept a specified region
	 * @param region
	 * @return
	 */
	public ProtectedRegionList queryRegions(ProtectedRegion region)
	{
		// Create the set of regions that was found
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>();
		// Create the iterator
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			BlockVector small = next.getSmallerPoint();
			BlockVector large = next.getLargerPoint();
			
			BlockVector rSmall = region.getSmallerPoint();
			BlockVector rLarge = region.getLargerPoint();
			
			// Smaller point is within this regions boundary
			if(rSmall.getBlockX() >= small.getBlockX() && rSmall.getBlockY() >= small.getBlockY() && rSmall.getBlockZ() >= small.getBlockZ())
			{
				if(rSmall.getBlockX() <= large.getBlockX() && rSmall.getBlockY() <= large.getBlockY() && rSmall.getBlockZ() <= large.getBlockZ())
				{
					regions.add(next);
					continue;
				}
			}
			
			// Larger point is within this regions boundary
			if(rLarge.getBlockX() >= small.getBlockX() && rLarge.getBlockY() >= small.getBlockY() && rLarge.getBlockZ() >= small.getBlockZ())
			{
				if(rLarge.getBlockX() <= large.getBlockX() && rLarge.getBlockY() <= large.getBlockY() && rLarge.getBlockZ() <= large.getBlockZ())
				{
					regions.add(next);
					continue;
				}
			}
			
		}
		
		return new ProtectedRegionList(regions);
	}
	
	/**
	 * Query a region which the center block is the same as
	 * the specified location
	 * @param location
	 * @return ProtectedRegion
	 */
	public ProtectedRegion queryRegion(Location location)
	{
		Iterator<ProtectedRegion> regionItr = cache.getAll().iterator();
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			// Check if the center is the same as the arg location
			if(next.getCenter().getBlockX() == location.getBlockX() && next.getCenter().getBlockY() == location.getBlockY()
					&& location.getBlockZ() == next.getCenter().getBlockZ())
			{
				return next;
			}
		}
		return null;
	}
	
}
