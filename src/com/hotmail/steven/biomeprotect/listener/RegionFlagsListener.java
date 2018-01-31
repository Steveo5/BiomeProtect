package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Door;

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
			OfflinePlayer owner = Bukkit.getOfflinePlayer(region.getOwner());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title [{text:Entered " + owner.getName() + "'s region,color:gold}]");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " subtitle [{text:" + welcome.getValue() + ",color:blue}");
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
			OfflinePlayer owner = Bukkit.getOfflinePlayer(region.getOwner());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " title [{text:Left " + owner.getName() + "'s region,color:gold}]");
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName() + " subtitle [{text:" + leave.getValue() + ",color:blue}");
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
					if(!highest.hasPermission(p, pvpFlag))
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
		if(evt.getBlock().getType() != Material.TNT) return;
		// Get all regions at the block
		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!atBlock.isEmpty())
		{
			// Get the highest priority region
			ProtectedRegion highest = atBlock.getHighestPriority();
			Player p = evt.getPlayer();
			if(highest.hasFlag("tnt"))
			{
				StateFlag tntFlag = (StateFlag)highest.getFlag("tnt");
				// Tnt flag is set to deny or player is not on the whitelist and the flag is whitelist
				if(highest.hasPermission(p, tntFlag))
				{
					evt.setCancelled(true);
					tl(p, "noBuildPermission");
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent evt)
	{
		// Get ll regions at the block
		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!atBlock.isEmpty())
		{
			Player p = evt.getPlayer();
			// Get the highest priority region
			ProtectedRegion highest = atBlock.getHighestPriority();
			if(highest.hasFlag("build"))
			{
				StateFlag buildFlag = (StateFlag)highest.getFlag("build");
				// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
				if(!highest.hasPermission(p, buildFlag))
				{
					evt.setCancelled(true);
					tl(p, "noBuildPermission");
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent evt)
	{
		// Get all regions at the block
		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(evt.getBlock().getLocation());
		if(!atBlock.isEmpty())
		{
			Player p = evt.getPlayer();
			// Get the highest priority region
			ProtectedRegion highest = atBlock.getHighestPriority();
			if(highest.hasFlag("break"))
			{
				StateFlag buildFlag = (StateFlag)highest.getFlag("break");
				// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
				if(!highest.hasPermission(p, buildFlag))
				{
					
					evt.setCancelled(true);
					tl(p, "noBuildPermission");
				}
			}
		}
	}

	@EventHandler
	public void onDoorOpen(PlayerInteractEvent evt)
	{
        Action action = evt.getAction();
        Block clicked = evt.getClickedBlock();
             
        //Left or Right click?
        if ((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_BLOCK))
        {
            //Door Block?
            if((clicked.getType() == Material.ACACIA_DOOR) || (clicked.getType() == Material.IRON_TRAPDOOR) || (clicked.getType() == Material.TRAP_DOOR)
            		|| (clicked.getType() == Material.BIRCH_DOOR) || (clicked.getType() == Material.DARK_OAK_DOOR) || (clicked.getType() == Material.IRON_DOOR)
            		|| (clicked.getType() == Material.IRON_DOOR) || (clicked.getType() == Material.JUNGLE_DOOR) || (clicked.getType() == Material.SPRUCE_DOOR)
            		|| (clicked.getType() == Material.WOOD_DOOR))
            {
                
        		// Get all regions at the block
        		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(clicked.getLocation());
        		if(!atBlock.isEmpty())
        		{
        			// Get the highest priority region
        			ProtectedRegion highest = atBlock.getHighestPriority();
        			if(highest.hasFlag("doors"))
        			{
        				StateFlag doorsFlag = (StateFlag)highest.getFlag("doors");
        				// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
        				if(!highest.hasPermission(evt.getPlayer(), doorsFlag))
        				{
        					evt.setCancelled(true);
        					tl(evt.getPlayer(), "noInteractPermission");
        				}
        			}
        		}
            }
        }
	}
	
	@EventHandler
	public void onChestOpen(PlayerInteractEvent evt)
	{
        Action action = evt.getAction();
        Block clicked = evt.getClickedBlock();
             
        //Left or Right click?
        if ((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_BLOCK))
        {
            //Door Block?
            if(clicked.getType() == Material.CHEST || clicked.getType() == Material.TRAPPED_CHEST)
            {
                
        		// Get all regions at the block
        		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(clicked.getLocation());
        		if(!atBlock.isEmpty())
        		{
        			// Get the highest priority region
        			ProtectedRegion highest = atBlock.getHighestPriority();
        			if(highest.hasFlag("chests"))
        			{
        				StateFlag doorsFlag = (StateFlag)highest.getFlag("chests");
        				// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
        				if(!highest.hasPermission(evt.getPlayer(), doorsFlag))
        				{
        					evt.setCancelled(true);
        					tl(evt.getPlayer(), "noInteractPermission");
        				}
        			}
        		}
            }
        }
	}
	
	@EventHandler
	public void onOtherInteract(PlayerInteractEvent evt)
	{
        Action action = evt.getAction();
        Block clicked = evt.getClickedBlock();
             
        //Left or Right click?
        if ((action == Action.RIGHT_CLICK_BLOCK) || (action == Action.LEFT_CLICK_BLOCK))
        {
            //Door Block?
            if((clicked.getType() == Material.FURNACE) || (clicked.getType() == Material.BURNING_FURNACE) || clicked.getType() ==   Material.HOPPER
            		|| clicked.getType() == Material.HOPPER_MINECART || clicked.getType() == Material.BEACON || clicked.getType() == Material.HOPPER_MINECART)
            {
                
        		// Get all regions at the block
        		ProtectedRegionList atBlock = getPlugin().getRegionContainer().queryRegions(clicked.getLocation());
        		if(!atBlock.isEmpty())
        		{
        			// Get the highest priority region
        			ProtectedRegion highest = atBlock.getHighestPriority();
        			if(highest.hasFlag("other"))
        			{
        				StateFlag doorsFlag = (StateFlag)highest.getFlag("other");
        				// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
        				if(!highest.hasPermission(evt.getPlayer(), doorsFlag))
        				{
        					evt.setCancelled(true);
        					tl(evt.getPlayer(), "noInteractPermission");
        				}
        			}
        		}
            }
        }
	}
}
