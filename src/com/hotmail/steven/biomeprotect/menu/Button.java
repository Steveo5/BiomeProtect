package com.hotmail.steven.biomeprotect.menu;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.util.StringUtil;

public class Button {

	private String name, title, lore;
	private ItemStack icon;
	private int position;
	
	/**
	 * Create a standard button that fits into a BiomeMenu. Does nothing
	 * and is effectively just an information icon
	 * 
	 * @param name
	 * @param icon
	 */
	public Button(String name, int position, ItemStack icon, String title, String lore)
	{
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.title = title;
		this.lore = StringUtil.colorize(lore);
		
		ItemMeta im = icon.getItemMeta();
		im.setDisplayName(StringUtil.colorize(title));
		if(!lore.isEmpty())
			im.setLore(Arrays.asList(lore.split("|")));
		icon.setItemMeta(im);
	}
	
	/**
	 * Get the unique button name
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * Get the icon that is actually displayed in the menu
	 * @return
	 */
	public ItemStack getIcon()
	{
		return icon;
	}
	
	/**
	 * Get this buttons inventory position
	 * @return
	 */
	public int getPosition()
	{
		return position;
	}
	
	public void setLore(String lore)
	{
		this.lore = lore;
	}
	
	/**
	 * Called when the menu gets opened and button loaded
	 * for a particular region
	 * @param Player who opened the inventory
	 */
	public void onEnable(Player player)
	{
		
	}
}
