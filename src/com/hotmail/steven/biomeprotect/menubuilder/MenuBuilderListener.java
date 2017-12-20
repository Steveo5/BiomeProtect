package com.hotmail.steven.biomeprotect.menubuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

public class MenuBuilderListener implements Listener {

	private static HashSet<MenuBuilder> menus;
	
	public MenuBuilderListener(Plugin plugin)
	{
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		menus = new HashSet<MenuBuilder>();
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent evt)
	{
		if(!(evt.getWhoClicked() instanceof Player)) return;
		Player player = (Player)evt.getWhoClicked();
		Iterator<MenuBuilder> invItr = menus.iterator();
		int slot = evt.getRawSlot();
		// Loop over all the menus we know of
		while(invItr.hasNext())
		{
			MenuBuilder next = invItr.next();
			if(next.title().equals(evt.getInventory().getTitle()))
			{
				// Check if the inventory has a button at the slot
				if(next.hasButton(slot))
				{
					Button b = next.getButton(slot);
					if(b.hasClickListener())
					{
						// Run the click event
						b.getClickListener().onClick(player);
						evt.setCancelled(true);
					}
				}
			}
		}
	}
	
	/**
	 * Start listening on this menu for click events etc
	 * @param menu
	 */
	public static void listen(MenuBuilder menu)
	{
		menus.add(menu);
	}
	
}
