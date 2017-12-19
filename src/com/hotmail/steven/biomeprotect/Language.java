package com.hotmail.steven.biomeprotect;

import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.hotmail.steven.util.StringUtil;

public class Language {

	public static String tl(String obj, String... replacers)
	{
		String message = "";
		if(obj.equals("noBreakPermission"))
		{
			message = "You do not have permission to break blocks here";
		} else if(obj.equals("regionRemoved"))
		{
			message = "&cYou have removed the protected region";
		} else if(obj.equals("noPlacePermission"))
		{
			message = "You do not have permission to place blocks here";
		} else if(obj.equals("playerOnly"))
		{
			message = "This command can only be executed as a player";
		} else if(obj.equals("regionDoesntExist"))
		{
			message = "No protected regions exist here";
		} else if(obj.equals("noPermission"))
		{
			message = "You do not have permission to perform that action";
		} else if(obj.equals("regionPlaced"))
		{
			message =  "&6You have placed a new region at &cx %x% y %y% z %z%";
		}
		return replacers(StringUtil.colorize(message), replacers);
	}
	
	public static void tl(Player player, String obj)
	{
		player.sendMessage(tl(obj));
	}
	
	private static String replacers(String str, String...replacers)
	{
		for(int i=0;i<replacers.length;i+=2)
		{
			String replacer = replacers[i];
			String replacement = replacers[i+1];
			if(replacer != null && replacement != null)
			{
				str = str.replaceAll(replacer, replacement);
			}
		}
		return str;
	}
	
}
