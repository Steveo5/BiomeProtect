package com.hotmail.steven.biomeprotect.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class InputButton extends Button {
	
	public InputButton(String name, int position, ItemStack icon, String title, String lore) {
		super(name, position, icon, title, lore);
	}

	/**
	 * Called when this button is clicked
	 * @param player
	 */
	public void onClick(Player player) {}
	
	/**
	 * Called when bukkit receives input (player sends a message)
	 * after clicking this button
	 * 
	 * @param player
	 */
	public void onInput(Player player, String message) {}
}
