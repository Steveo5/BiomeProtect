package com.hotmail.steven.util;

import org.bukkit.ChatColor;

public class StringUtil {

	public static String colorize(String str)
	{
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
}