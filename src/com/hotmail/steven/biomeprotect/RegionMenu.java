package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.util.StringUtil;

public class RegionMenu implements Listener {

	private static HashMap<UUID, Integer> inventories;
	
	public RegionMenu()
	{
		inventories = new HashMap<UUID, Integer>();
	}
	
	public static void show(Player player, ProtectedRegion region)
	{
		Inventory inv = Bukkit.createInventory(null, 18, StringUtil.colorize("&c&lManage protected region"));
		Player owner = Bukkit.getPlayer(region.getOwner());
		
		ItemStack btnBreak = new ItemStack(Material.LOG, 1);
		ItemMeta btnBreakMeta = btnBreak.getItemMeta();
		btnBreakMeta.setDisplayName(StringUtil.colorize("&aAllow break"));
		btnBreakMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.allowsBreak() ? "&cenabled" : "&cdisabled"))));
		btnBreak.setItemMeta(btnBreakMeta);
		ItemStack btnPlace = new ItemStack(Material.LOG, 1);
		ItemMeta btnPlaceMeta = btnBreak.getItemMeta();
		btnPlaceMeta.setDisplayName(StringUtil.colorize("&aAllow place"));
		btnPlaceMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.allowsPlace() ? "&cenabled" : "&cdisabled"))));
		btnPlace.setItemMeta(btnPlaceMeta);
	
		ItemStack btnPvp = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta btnPvpMeta = btnPvp.getItemMeta();
		btnPvpMeta.setDisplayName(StringUtil.colorize("&aAllow pvp"));
		btnPvpMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.allowsPvp() ? "&cenabled" : "&cdisabled"))));
		btnPvp.setItemMeta(btnPvpMeta);
		
		ItemStack btnEntry = new ItemStack(Material.PAPER, 1);
		ItemMeta btnEntryMeta = btnEntry.getItemMeta();
		btnEntryMeta.setDisplayName(StringUtil.colorize("&aEntry message"));
		btnEntryMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.hasWelcomeMessage() ? "&7" + region.getWelcomeMessage().replaceAll("%player%", owner.getName()) : "&7none"))));
		btnEntry.setItemMeta(btnEntryMeta);
		
		ItemStack btnLeave = new ItemStack(Material.PAPER, 1);
		ItemMeta btnLeaveMeta = btnLeave.getItemMeta();
		btnLeaveMeta.setDisplayName(StringUtil.colorize("&aLeave message"));
		btnLeaveMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.hasLeaveMessage() ? "&7" + region.getLeaveMessage().replaceAll("%player%", owner.getName()) : "&7none"))));
		btnLeave.setItemMeta(btnLeaveMeta);
		
		inv.setItem(0, btnBreak);
		inv.setItem(1, btnPlace);
		inv.setItem(2, btnPvp);
		inv.setItem(3, btnEntry);
		inv.setItem(4, btnLeave);
		
		player.openInventory(inv);
		inventories.put(player.getUniqueId(), region.getId());
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent evt)
	{
		// Player is clicking this specific inventory
		if(evt.getInventory().getTitle().equals(StringUtil.colorize("&c&lManage protected region")) && evt.getRawSlot() < 18)
		{
			if(evt.getWhoClicked() instanceof Player)
			{
				evt.setCancelled(true);
				Player player = (Player)evt.getWhoClicked();
				ProtectedRegion managingRegion = BiomeProtect.getRegion(inventories.get(evt.getWhoClicked().getUniqueId()));
				if(evt.getRawSlot() == 0)
				{
					managingRegion.setAllowsBreak(!managingRegion.allowsBreak());
					show(player, managingRegion);
				}
				if(evt.getRawSlot() == 1)
				{
					managingRegion.setAllowsPlace(!managingRegion.allowsPlace());
					show(player, managingRegion);
				}
				if(evt.getRawSlot() == 2)
				{
					managingRegion.setAllowsPvp(!managingRegion.allowsPvp());
					show(player, managingRegion);
				}
			}
		}
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent evt)
	{
		// Player is clicking this specific inventory
		if(evt.getInventory().getTitle().equals(StringUtil.colorize("&c&lManage protected region")))
		{
			inventories.remove(evt.getPlayer().getUniqueId());
		}		
	}
	
}
