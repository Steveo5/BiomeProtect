package com.hotmail.steven.biomeprotect.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public class RegionCache {

	private HashSet<ProtectedRegion> cachedRegions;
	
	/**
	 * For faster handling of regions finding any
	 * regions should be pulled from the region cache
	 * 
	 * RegionCache will keep track of all the possible
	 * protected regions that the players can be in
	 */
	public RegionCache()
	{
		cachedRegions = new HashSet<ProtectedRegion>();
	}
	
	public void add(ProtectedRegion region)
	{
		cachedRegions.add(region);
	}
	
	/**
	 * Cache a bunch of regions
	 * @param regions
	 */
	public void addAll(HashSet<ProtectedRegion> regions)
	{
		cachedRegions.addAll(regions);
	}
	
	public HashSet<ProtectedRegion> getAll()
	{
		return cachedRegions;
	}
	
	/**
	 * Check if the cache has a region
	 * @param id
	 * @return
	 */
	public boolean isCached(UUID id)
	{
		for(ProtectedRegion region : cachedRegions)
		{
			if(region.getId().equals(id)) return true;
		}
		return false;
	}
	
	public void remove(ProtectedRegion region)
	{
		cachedRegions.remove(region);
	}
	
	
}
