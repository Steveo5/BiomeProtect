package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public class RegionProtectionListener extends BiomeProtectListener {

	private HashMap<UUID, ProtectedRegion> editingRegions;
	private HashMap<UUID, Long> timeoutHolder;
	
	public RegionProtectionListener(BiomeProtect plugin) {
		super(plugin);
		
		editingRegions = new HashMap<UUID, ProtectedRegion>();
		timeoutHolder = new HashMap<UUID, Long>();
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent evt)
	{
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK && evt.getHand() == EquipmentSlot.OFF_HAND)
		{
			// Check if the player is clicking a protected region
			ProtectedRegion region = getPlugin().getRegionContainer().queryRegion(evt.getClickedBlock().getLocation());
			if(region != null)
			{
				editingRegions.put(evt.getPlayer().getUniqueId(), region);
				region.showMenu(evt.getPlayer());
			}
		}
	}
	
}
