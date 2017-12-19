package com.hotmail.steven.biomeprotect.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActionButton extends Button {

	public ActionButton(String name, int position, ItemStack icon, String title, String lore) {
		super(name, position, icon, title, lore);
	}
	
	/**
	 * Called when the button receives a click event
	 */
	public void onClick(Player player) {}

}
