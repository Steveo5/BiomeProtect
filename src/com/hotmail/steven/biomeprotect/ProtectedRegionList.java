package com.hotmail.steven.biomeprotect;

import java.util.HashSet;

import org.bukkit.entity.Player;

import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public class ProtectedRegionList extends HashSet<ProtectedRegion> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create an instance of a protected region list with the specified regions
	 * @param regions
	 */
	public ProtectedRegionList(HashSet<ProtectedRegion> regions)
	{
		addAll(regions);
	}
	
	/**
	 * Gets the highest priority region in the queue
	 * 
	 * @return ProtectedRegion or null if the list is empty
	 */
	public ProtectedRegion getHighestPriority()
	{
		ProtectedRegion highest = null;
		for(ProtectedRegion region : this)
		{
			if(highest == null || region.getPriority() > highest.getPriority()) highest = region;
		}
		return highest;
	}
	
	/**
	 * Check whether a player owns all the protected regions in this list
	 * 
	 * @param player
	 * @return
	 */
	public boolean ownsAll(Player player)
	{
		for(ProtectedRegion region : this)
		{
			if(!region.isOwner(player.getUniqueId())) return false;
		}
		
		return true;
	}

}