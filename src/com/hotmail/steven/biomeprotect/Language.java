package com.hotmail.steven.biomeprotect;

import org.bukkit.entity.Player;

public class Language {

	public static String tl(String obj)
	{
		if(obj.equals("noBreakPermission"))
		{
			return "You do not have permission to break blocks here";
		} else if(obj.equals("regionRemoved"))
		{
			return "You have removed the protected region";
		} else if(obj.equals("noPlacePermission"))
		{
			return "You do not have permission to place blocks here";
		} else if(obj.equals("playerOnly"))
		{
			return "This command can only be executed as a player";
		} else if(obj.equals("regionDoesntExist"))
		{
			return "No protected regions exist here";
		} else if(obj.equals("noPermission"))
		{
			return "You do not have permission to perform that action";
		}
		return "";
	}
	
	public static void tl(Player player, String obj)
	{
		player.sendMessage(tl(obj));
	}
	
}
