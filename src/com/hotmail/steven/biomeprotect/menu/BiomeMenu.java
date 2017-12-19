package com.hotmail.steven.biomeprotect.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.util.StringUtil;

public class BiomeMenu implements Listener {

	private String name, title;
	private int size;
	private HashMap<UUID, Inventory> inventories;
	private List<Button> buttons;
	private HashMap<UUID, InputButton> waitingInput;
	
	/**
	 * Create a new BiomeMenu
	 * 
	 * @param name - Unique menu name
	 * @param title - Displayed at the top of the inventory
	 * @param size - Multiples of 9, the actually inventory size
	 */
	public BiomeMenu(Plugin plugin, String name, String title, int size)
	{
		this.name = name;
		this.title = StringUtil.colorize(title);
		this.size = size;
		// Register events
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		buttons = new ArrayList<Button>();
		inventories = new HashMap<UUID, Inventory>();
		waitingInput = new HashMap<UUID, InputButton>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getSize()
	{
		return size;
	}
	
	/**
	 * Add a button to the inventory
	 * @param btn
	 */
	public void addButton(Button btn)
	{
		buttons.add(btn);
	}
	
	/**
	 * Add an array of buttons to the inventory
	 * @param btns
	 */
	public void addButtons(Button...btns)
	{
		for(Button btn : btns)
		{
			buttons.add(btn);
		}
	}
	
	/**
	 * Get all the buttons for this inventory
	 * @return
	 */
	public List<Button> getButtons()
	{
		return buttons;
	}
	
	/**
	 * Opens the inventory for a player
	 * @param player
	 */
	public void open(Player player, ProtectedRegion region)
	{
		Inventory inv = Bukkit.createInventory(null, size, getTitle());
		// Populate the inventory with buttons
		for(Button button : buttons)
		{
			inv.setItem(button.getPosition(), button.getIcon());
		}
		
		inventories.put(player.getUniqueId(), inv);
		player.openInventory(inv);
		//onOpen(player, region);
	}
	
	@EventHandler
	private void onInventoryClick(InventoryClickEvent evt)
	{
		System.out.println("click");
		if(!(evt.getWhoClicked() instanceof Player)) return;
		Player player = (Player)evt.getWhoClicked();
		// Get the inventory that was clicked
		Inventory clicked = inventories.containsKey(player.getUniqueId()) ? inventories.get(player.getUniqueId()) : null;
		System.out.println(clicked.getTitle() + " " + evt.getInventory().getTitle());
		// Check if we are clicking this menu
		if(clicked != null && clicked.getTitle().equals(evt.getInventory().getTitle()))
		{
			int clickedItem = evt.getRawSlot();
			// Get the button that was clicked
			for(Button btn : buttons)
			{
				if(btn.getPosition() == clickedItem)
				{
					onButtonClick(player, clicked, btn);
				}
			}
			evt.setCancelled(true);
		}
	}
	
	/**
	 * Called when a button is clicked in the inventory
	 * @param player
	 * @param button
	 */
	private void onButtonClick(Player player, Inventory inv, Button button)
	{
		if(button instanceof ActionButton)
		{
			ActionButton actionButton = (ActionButton)button;
			actionButton.onClick(player);
		} else if(button instanceof InputButton)
		{
			InputButton inputButton = (InputButton)button;
			inputButton.onClick(player);
			waitingInput.put(player.getUniqueId(), inputButton);
		}
	}
	
	@EventHandler
	public void onMessage(AsyncPlayerChatEvent event)
	{
		if(waitingInput.containsKey(event.getPlayer().getUniqueId()))
		{
			InputButton btnInput = waitingInput.get(event.getPlayer().getUniqueId());
			btnInput.onInput(event.getPlayer(), event.getMessage());
			waitingInput.remove(event.getPlayer().getUniqueId());
			
		}
	}
	
	/**
	 * Called when the menu opens
	 * @param player
	 * @param region
	 */
	public void onOpen(Player player, ProtectedRegion region) {}
	
}
