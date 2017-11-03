package com.hotmail.steven.biomeprotect;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		// Where the block was placed
		Location blockLocation = evt.getBlock().getLocation();
		
		if(BiomeProtect.findRegions(evt.getBlock()).isEmpty())
		{
			Location point1 = blockLocation.clone().subtract(10, 10, 10);
			Location point2 = blockLocation.clone().add(10, 10, 10);
			ProtectedRegion region = BiomeProtect.defineRegion(evt.getPlayer(), point1, point2);
			evt.getPlayer().sendMessage("New region created at your location");
		} else
		{
			evt.getPlayer().sendMessage("A protected region already exists!");
		}
	}
	
}
