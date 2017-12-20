package com.hotmail.steven.biomeprotect.menubuilder;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
					if(b.hasListener())
					{
						// Run the click event
						b.getListener().onClick(b, player);
						evt.setCancelled(true);
						// Check if this is an input button
						if(b.getListener() instanceof InputListener)
						{
							InputListener inputListener = (InputListener)b.getListener();
							inputListener.waitInput(player);
							player.closeInventory();
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onMessage(AsyncPlayerChatEvent evt)
	{
		Iterator<MenuBuilder> invItr = menus.iterator();
		// Loop over all the menus we know of
		while(invItr.hasNext())
		{
			MenuBuilder next = invItr.next();
			// Check if any button is waiting input
			for(Button btn : next.getButtons())
			{
				if(btn.hasListener() && btn.getListener() instanceof InputListener)
				{
					InputListener inputListener = (InputListener)btn.getListener();
					// Check the button is waiting for our event player
					if(inputListener.isWaiting() && inputListener.waiting().getUniqueId().equals(evt.getPlayer().getUniqueId()))
					{
						inputListener.onInput(evt.getPlayer(), evt.getMessage());
						inputListener.stopWaiting(evt.getPlayer());
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
