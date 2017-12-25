package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.flag.StateFlag;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

import static com.hotmail.steven.biomeprotect.Language.tl;

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
	
	@EventHandler
	public void onPvp(EntityDamageByEntityEvent evt) {
		if(evt.getEntity() instanceof Player && evt.getDamager() instanceof Player)
		{
			Player p = (Player)evt.getEntity();
			Player d = (Player)evt.getDamager();
			// Get regions at the damaged player
			ProtectedRegionList atPlayer = getPlugin().getRegionContainer().queryRegions(p.getLocation());
			if(!atPlayer.isEmpty())
			{
				// Get the highest priority event
				ProtectedRegion highest = atPlayer.getHighestPriority();
				if(highest.hasFlag("pvp"))
				{
					// Get the stateflag that can have whitelist, allow, deny
					StateFlag pvpFlag = (StateFlag)highest.getFlag("pvp");
					// Examine the states
					if(pvpFlag.getValue().equalsIgnoreCase("deny") || (pvpFlag.getValue().equalsIgnoreCase("whitelist") && !highest.hasMember(d)))
					{
						tl(d, "noPvp");
						evt.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTntPlace(BlockPlaceEvent evt)
	{
		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!atBlock.isEmpty())
		{
			ProtectedRegion highest = atBlock.getHighestPriority();
		}
	}

}
