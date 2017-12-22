package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public class RegionFlagsListener extends BiomeProtectListener {

	private HashMap<UUID, ProtectedRegion> enteredRegions;
	
	public RegionFlagsListener(BiomeProtect plugin) {
		super(plugin);
		enteredRegions = new HashMap<UUID, ProtectedRegion>();
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent evt)
	{
		if(evt.getFrom().getBlockX() != evt.getTo().getBlockX() || evt.getFrom().getBlockY() != evt.getTo().getBlockY()
				|| evt.getFrom().getBlockZ() != evt.getTo().getBlockZ())
		{
			onBlockMove(evt);
		}
	}
	
	/**
	 * When the player moves a block coordinate
	 * @param evt
	 */
	public void onBlockMove(PlayerMoveEvent evt)
	{
		Block to = evt.getTo().getBlock();
		Player p = evt.getPlayer();
		ProtectedRegionList regionsTo = getPlugin().getRegionContainer().queryRegions(to.getLocation());
		if(!regionsTo.isEmpty())
		{
			ProtectedRegion highest = regionsTo.getHighestPriority();
			if(!enteredRegions.containsKey(p.getUniqueId()))
			{
				// Can only enter a region at this point
				onRegionEntry(p, highest);
				enteredRegions.put(p.getUniqueId(), highest);
			} else
			{
				if(!enteredRegions.get(p.getUniqueId()).equals(highest))
				{
					onRegionEntry(p, highest);
					enteredRegions.put(p.getUniqueId(), highest);
				}
			}
		} else if(enteredRegions.containsKey(p.getUniqueId()))
		{
			onRegionLeave(p, enteredRegions.get(p.getUniqueId()));
			enteredRegions.remove(p.getUniqueId());
		}
	}
	
	/**
	 * Called when the player enters a new region
	 * @param player
	 * @param region
	 */
	public void onRegionEntry(Player player, ProtectedRegion region)
	{
		if(region.hasFlag("welcome-message"))
		{
			StringFlag welcome = (StringFlag)region.getFlag("welcome-message");
			player.sendMessage(welcome.getValue());
		}
	}
	
	/**
	 * Called when the player leaves any region
	 * @param player
	 * @param region
	 */
	public void onRegionLeave(Player player, ProtectedRegion region)
	{
		if(region.hasFlag("leave-message"))
		{
			StringFlag leave = (StringFlag)region.getFlag("leave-message");
			player.sendMessage(leave.getValue());
		}
	}

}
