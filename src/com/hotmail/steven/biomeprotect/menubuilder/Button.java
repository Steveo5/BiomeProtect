package com.hotmail.steven.biomeprotect.menubuilder;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.util.StringUtil;

public class Button {

	private int position;
	private ItemStack item;
	private ButtonListener listener;
	
	public Button(int position, ItemStack icon)
	{
		this.position = position;
		this.item = icon;
	}
	
	/**
	 * Sets the buttons icon title
	 * @param title
	 * @return
	 */
	public Button title(String title)
	{
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(StringUtil.colorize(title));
		item.setItemMeta(im);
		return this;
	}
	
	/**
	 * Set the buttons icon lore
	 * @param lore
	 * @return
	 */
	public Button lore(String lore)
	{
		ItemMeta im = item.getItemMeta();
		im.setLore(Arrays.asList(StringUtil.colorize(lore).split("\\|")));
		item.setItemMeta(im);
		return this;
	}
	
	public int getPosition()
	{
		return position;
	}
	
	public ItemStack getIcon()
	{
		return item;
	}
	
	/**
	 * Check if this button has a certain listener type
	 * @param name
	 * @return
	 */
	public boolean hasListener()
	{
		return listener != null;
				
	}
	
	/**
	 * Get a listener for the button
	 * @param name
	 * @return
	 */
	public ButtonListener getListener()
	{
		return listener;
	}
	
	/**
	 * Make this button start listening on a certain listener
	 * @param name
	 * @param listener
	 */
	public void setListener(ButtonListener listener)
	{
		this.listener = listener;
	}
	
}
