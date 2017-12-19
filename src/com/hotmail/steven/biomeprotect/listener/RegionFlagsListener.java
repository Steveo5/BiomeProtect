package com.hotmail.steven.biomeprotect.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;

public class RegionFlagsListener extends BiomeProtectListener {

	public RegionFlagsListener(BiomeProtect plugin) {
		super(plugin);
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
		Block from = evt.getFrom().getBlock();
		
		ProtectedRegionList regionsTo = getPlugin().getRegionContainer().queryRegions(to.getLocation());
		ProtectedRegionList regionsFrom = getPlugin().getRegionContainer().queryRegions(from.getLocation());
		if((!regionsTo.isEmpty() && regionsFrom.isEmpty()) ||
				(!regionsTo.isEmpty() && !regionsFrom.isEmpty() && !regionsTo.getHighestPriority().equals(regionsFrom.getHighestPriority())))
		{
			evt.getPlayer().sendMessage("You have entered a region " + regionsTo.getHighestPriority().toString());
		}
	}

}
