package com.hotmail.steven.util;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

	public static ItemStack item(Material mat, int amount, String title)
	{
		ItemStack item = new ItemStack(mat, amount);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(StringUtil.colorize(title));
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack item(Material mat, int amount, String title, String lore)
	{
		ItemStack item = item(mat, amount, title);
		ItemMeta im = item.getItemMeta();
		im.setLore(Arrays.asList(StringUtil.colorize(lore).split("\\|")));
		item.setItemMeta(im);
		return item;
	}
	
}
