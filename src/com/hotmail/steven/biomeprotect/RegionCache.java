package com.hotmail.steven.biomeprotect;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

public class RegionCache {

	private HashMap<UUID, ProtectedRegion> cachedRegions;
	
	/**
	 * For faster handling of regions finding any
	 * regions should be pulled from the region cache
	 * 
	 * RegionCache will keep track of all the possible
	 * protected regions that the players can be in
	 */
	public RegionCache()
	{
		cachedRegions = new HashMap<UUID, ProtectedRegion>();
	}
	
	public void add(ProtectedRegion region)
	{
		cachedRegions.put(region.getId(), region);
	}
	
	/**
	 * Cache a bunch of regions
	 * @param regions
	 */
	public void addAll(HashMap<UUID, ProtectedRegion> regions)
	{
		for(Entry<UUID, ProtectedRegion> region : regions.entrySet())
		{
			cachedRegions.put(region.getKey(), region.getValue());
		}
	}
	
	public boolean isCached(UUID id)
	{
		return cachedRegions.containsKey(id);
	}
	
	public HashMap<UUID, ProtectedRegion> getCache()
	{
		return cachedRegions;
	}
	
	public ProtectedRegion getCachedRegion(UUID id)
	{
		return cachedRegions.get(id);
	}
	
	
}
