package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

import static com.hotmail.steven.biomeprotect.Language.tl;

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
				if(region.isOwner(evt.getPlayer()))
				{
					editingRegions.put(evt.getPlayer().getUniqueId(), region);
					region.showMenu(evt.getPlayer());
				} else
				{
					tl(evt.getPlayer(), "noPermission");
				}
			}
		}
	}
	
	@EventHandler (priority=EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Get all the regions at the block location
		ProtectedRegionList regions = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!regions.isEmpty())
		{
			Player p = evt.getPlayer();
			// Get the highest priority region as higher priorities override lower ones
			ProtectedRegion region = regions.getHighestPriority();
			if(!region.isOwner(p) && !region.hasMember(p))
			{
				evt.setCancelled(true);
				tl(p, "noBuildPermission");
			}
		}
	}
	
	@EventHandler (priority=EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		// Get all the regions at the block location
		ProtectedRegionList regions = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!regions.isEmpty())
		{
			Player p = evt.getPlayer();
			// Get the highest priority region as higher priorities override lower ones
			ProtectedRegion region = regions.getHighestPriority();
			if(!region.isOwner(p) && !region.hasMember(p))
			{
				evt.setCancelled(true);
				tl(p, "noBuildPermission");
			}
		}
	}
	
}
