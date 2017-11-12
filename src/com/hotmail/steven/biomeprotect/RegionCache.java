package com.hotmail.steven.biomeprotect;

import java.util.HashMap;

public class RegionCache {

	private HashMap<Integer, ProtectedRegion> cachedRegions;
	
	/**
	 * For faster handling of regions finding any
	 * regions should be pulled from the region cache
	 * 
	 * RegionCache will keep track of all the possible
	 * protected regions that the players can be in
	 */
	public RegionCache()
	{
		cachedRegions = new HashMap<Integer, ProtectedRegion>();
	}
	
	public void add(int id, ProtectedRegion region)
	{
		cachedRegions.put(id, region);
	}
	
	public boolean isCached(int id)
	{
		return cachedRegions.containsKey(id);
	}
	
	public HashMap<Integer, ProtectedRegion> getCache()
	{
		return cachedRegions;
	}
	
	public ProtectedRegion getCachedRegion(int id)
	{
		return cachedRegions.get(id);
	}
	
	
}
