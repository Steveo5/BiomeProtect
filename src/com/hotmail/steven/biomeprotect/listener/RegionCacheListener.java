package com.hotmail.steven.biomeprotect.listener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public class RegionCacheListener extends BiomeProtectListener {
	
	/**
	 * The RegionCacheListener is responsible for adding regions to
	 * various levels of caching
	 * @param plugin
	 */
	public RegionCacheListener(BiomeProtect plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent evt)
	{
		Block to = evt.getTo().getBlock();
		Block from = evt.getFrom().getBlock();
		if(to.getX() != from.getX() || to.getY() != from.getY() || to.getZ() != from.getY())
		{
			onMoveBlock(evt);
		}
	}
	
	/**
	 * Called when the player has moved a block
	 * @param evt
	 */
	public void onMoveBlock(PlayerMoveEvent evt)
	{
		Chunk from = evt.getFrom().getChunk();
		Chunk to = evt.getTo().getChunk();
		if(from.getX() != to.getX() || from.getZ() != to.getZ())
		{
			onMoveChunk(to);
		}
	}
	
	/**
	 * Called when the player has move a chunk
	 * @param evt
	 */
	public void onMoveChunk(Chunk center)
	{
		int radius = 2;
		// Hold the chunk boundaries
		int minX = center.getX() - radius;
		int minZ = center.getZ() - radius;
		int maxX = center.getX() + radius;
		int maxZ = center.getZ() + radius;
		Iterator<ProtectedRegion> regionItr = getPlugin().getRegionContainer().getRegions().iterator();
		// Cache all regions within the chunk boundary
		while(regionItr.hasNext())
		{
			ProtectedRegion next = regionItr.next();
			inner:
			for(Chunk chunk : next.getExistingChunks())
			{
				// The region has a chunk that exists inside the boundaries
				if(chunk.getX() > minX && chunk.getX() < maxX && chunk.getZ() > minZ && chunk.getZ() < maxZ)
				{
					// Swap the region to cache
					getPlugin().getRegionContainer().getCache().add(next);
					Logger.Log(Level.INFO, "Region was cached at " + next.getCenter().getBlockX() + " " + next.getCenter().getBlockY() + " " + next.getCenter().getBlockZ());
					regionItr.remove();
					break inner;
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent evt)
	{
		onMoveChunk(evt.getPlayer().getLocation().getChunk());
	}

}
