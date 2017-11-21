package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.util.PlayerUtil;
import com.hotmail.steven.util.StringUtil;
import com.mysql.fabric.xmlrpc.base.Array;

public class RegionMenu implements Listener {

	private HashMap<UUID, UUID> inventories;
	private HashMap<UUID, Integer> inputWaiting;
	
	public RegionMenu()
	{
		inventories = new HashMap<UUID, UUID>();
		inputWaiting = new HashMap<UUID, Integer>();
	}
	
	public void show(Player player, ProtectedRegion region)
	{
		Inventory inv = Bukkit.createInventory(null, 18, StringUtil.colorize("&c&lManage protected region"));
		Player owner = Bukkit.getPlayer(region.getOwner());
		
		// Create all of the buttons
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
		
		ItemStack btnTnt = new ItemStack(Material.TNT, 1);
		ItemMeta btnTntMeta = btnTnt.getItemMeta();
		btnTntMeta.setDisplayName(StringUtil.colorize("&aAllow tnt"));
		btnTntMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize(region.allowsTnt() ? "&cenabled" : "&cdisabled"))));
		btnTnt.setItemMeta(btnTntMeta);
		
		ItemStack btnShow = new ItemStack(Material.GLASS, 1);
		ItemMeta btnShowMeta = btnShow.getItemMeta();
		btnShowMeta.setDisplayName(StringUtil.colorize("&aShow protection"));
		btnShowMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize("&cDisplay the protected region"))));
		btnShow.setItemMeta(btnShowMeta);
		
		ItemStack btnDisable = new ItemStack(Material.BARRIER, 1);
		ItemMeta btnDisableMeta = btnDisable.getItemMeta();
		btnDisableMeta.setDisplayName(StringUtil.colorize("&aToggle protection"));
		btnDisableMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize("&cenabled"))));
		btnDisable.setItemMeta(btnDisableMeta);
		
		ItemStack btnPreventEntry = new ItemStack(Material.ACACIA_DOOR_ITEM, 1);
		ItemMeta btnPreventEntryMeta = btnPreventEntry.getItemMeta();
		btnPreventEntryMeta.setDisplayName(StringUtil.colorize("&aEnable entry"));
		btnPreventEntryMeta.setLore(new ArrayList<String>(Arrays.asList(StringUtil.colorize("&cenabled"))));
		btnPreventEntry.setItemMeta(btnPreventEntryMeta);
		
		ItemStack btnListMembers = new ItemStack(Material.SKULL_ITEM, 1);
		ItemMeta btnListMembersMeta = btnListMembers.getItemMeta();
		btnListMembersMeta.setDisplayName(StringUtil.colorize("&aList members"));
		btnListMembers.setItemMeta(btnListMembersMeta);
		
		ItemStack btnAddMember = new ItemStack(Material.SKULL_ITEM, 1);
		ItemMeta btnAddMemberMeta = btnAddMember.getItemMeta();
		btnAddMemberMeta.setDisplayName(StringUtil.colorize("&aAdd member"));
		btnAddMember.setItemMeta(btnAddMemberMeta);
		
		ItemStack btnRemoveMember = new ItemStack(Material.SKULL_ITEM, 1);
		ItemMeta btnRemoveMemberMeta = btnRemoveMember.getItemMeta();
		btnRemoveMemberMeta.setDisplayName(StringUtil.colorize("&aRemove member"));
		btnRemoveMember.setItemMeta(btnRemoveMemberMeta);
		
		// Add buttons to the inventory
		inv.setItem(0, btnBreak);
		inv.setItem(1, btnPlace);
		inv.setItem(2, btnPvp);
		inv.setItem(3, btnEntry);
		inv.setItem(4, btnLeave);
		inv.setItem(5, btnTnt);
		inv.setItem(6, btnShow);
		inv.setItem(7, btnDisable);
		inv.setItem(8, btnPreventEntry);
		inv.setItem(9, btnListMembers);
		inv.setItem(10, btnAddMember);
		inv.setItem(11, btnRemoveMember);
		
		inventories.put(player.getUniqueId(), region.getId());
		player.openInventory(inv);
		System.out.println("Opening menu for " + region.getId() + " player " + player.getUniqueId().toString());
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
				switch(evt.getRawSlot())
				{
				case 0:
					managingRegion.setAllowsBreak(!managingRegion.allowsBreak());
					show(player, managingRegion);
					break;
				case 1:
					managingRegion.setAllowsPlace(!managingRegion.allowsPlace());
					show(player, managingRegion);
					break;
				case 2:
					managingRegion.setAllowsPvp(!managingRegion.allowsPvp());
					show(player, managingRegion);
					break;
				case 3:
					inputWaiting.put(player.getUniqueId(), 3);
					player.sendMessage("Please enter a message (type none to disable):");
					player.closeInventory();
					break;
				case 4:
					inputWaiting.put(player.getUniqueId(), 4);
					player.sendMessage("Please enter a message (type none to disable):");
					player.closeInventory();
					break;
				case 5:
					managingRegion.setAllowsTnt(!managingRegion.allowsTnt());
					show(player, managingRegion);
					break;
				case 6:
					managingRegion.show();
					player.closeInventory();
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					System.out.println("Member size " + managingRegion.getMembers().size());
					player.sendMessage(StringUtil.paginateArray(PlayerUtil.playersAsNames(PlayerUtil.uuidListAsPlayers(managingRegion.getMembers())).toArray(), 12, 1));
					player.closeInventory();
					break;
				case 10:
					inputWaiting.put(player.getUniqueId(), 10);
					player.sendMessage("Please enter a players name:");
					player.closeInventory();
					break;
				case 11:
					inputWaiting.put(player.getUniqueId(), 11);
					player.sendMessage("Please enter a players name:");
					player.closeInventory();
					break;
				}
				
				inventories.put(player.getUniqueId(), managingRegion.getId());
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent evt)
	{
		Player player = evt.getPlayer();
		if(inputWaiting.containsKey(player.getUniqueId()))
		{
			// The message type we are waiting for
			int inputType = inputWaiting.get(player.getUniqueId());
			// The region the player is managing
			UUID regionId = inventories.get(player.getUniqueId());
			System.out.println("Getting region id " + regionId);
			final ProtectedRegion managingRegion = BiomeProtect.getRegion(regionId);
			System.out.println("Region id " + managingRegion.getId() + " was found");
			inputWaiting.remove(player.getUniqueId());
			
			switch(inputType)
			{
			case 3:
				managingRegion.setWelcomeMessage(evt.getMessage());
				player.sendMessage("Welcome message updated");
				break;
			case 4:
				managingRegion.setLeaveMessage(evt.getMessage());
				player.sendMessage("Welcome message updated");
				break;
			case 10:
				OfflinePlayer member = Bukkit.getPlayer(evt.getMessage());
				if(member == null)
				{
					player.sendMessage("Player doesn't exist");
				} else
				{
					managingRegion.addMember(member.getUniqueId());
					player.sendMessage("Player added to the member list");
				}
				break;
			case 11:
				OfflinePlayer deletingMember = Bukkit.getPlayer(evt.getMessage());
				if(deletingMember == null || !managingRegion.hasMember(deletingMember.getUniqueId()))
				{
					player.sendMessage("Player doesn't exist in the member list");
				} else
				{
					managingRegion.removeMember(deletingMember.getUniqueId());
					player.sendMessage("Player removed from the member list");
				}
				break;
			}
			
			evt.setCancelled(true);
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
