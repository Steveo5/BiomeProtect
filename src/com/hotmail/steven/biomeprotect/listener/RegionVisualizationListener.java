package com.hotmail.steven.biomeprotect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class RegionVisualizationListener extends BiomeProtectListener {

	public RegionVisualizationListener(BiomeProtect plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt)
	{
		if(BiomeProtect.getVisualizer().getQueue().contains(evt.getBlock())
				|| BiomeProtect.getVisualizer().getRemoveQueue().contains(evt.getBlock()))
		{
			tl(evt.getPlayer(), "errorVisualization");
			evt.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		if(BiomeProtect.getVisualizer().getQueue().contains(evt.getBlock())
				|| BiomeProtect.getVisualizer().getRemoveQueue().contains(evt.getBlock()))
		{
			tl(evt.getPlayer(), "errorVisualization");
			evt.setCancelled(true);
		}
	}
	
}
