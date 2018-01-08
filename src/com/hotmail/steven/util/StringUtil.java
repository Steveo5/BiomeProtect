package com.hotmail.steven.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;

public class StringUtil {

	public static String colorize(String str)
	{
		return ChatColor.translateAlternateColorCodes('&', str);
	}
	
	public static String paginateArray(Object[] arr, int pageSize, int currentPage)
	{
		StringBuilder paginated = new StringBuilder();
		// Generate the maximum amount of pages based on array size
		int maxPages = arr.length > 0 ? arr.length - arr.length % pageSize : 1;
		paginated.append("&6-= Showing page &c" + currentPage + " / " + maxPages + "&6 =-");
		if(arr.length > 0)
		{
			for(int i=0;i<arr.length;i++)
			{
				if(i == 0) paginated.append("\n");
				if(i == arr.length - 1)
				{
					paginated.append("&a" + arr[i]);
				} else
				{
					paginated.append("&a" + arr[i] + ", ");
				}
			}
		} else
		{
			paginated.append("\n&cIt's rather empty");
		}
		paginated.append("\n&6---------------------------");
		return StringUtil.colorize(paginated.toString());
	}
	
	/**
	 * Convert a list to string, the splitting operator is the pipe
	 * symbols ||
	 * @param list
	 * @return
	 */
	public static String listToString(List<String> list)
	{
		StringBuilder str = new StringBuilder();
		for(int i=0;i<list.size();i++)
		{
			String string = list.get(i);
			str.append(string);
			// Insert the pipe symbols but not on the last element
			if(i<list.size()) str.append("\\|");
		}
		return str.toString();
	}
	
	/**
	 * Convert a string to a list of strings, splitting by the pipe symbols |
	 * @param str
	 * @return
	 */
	public static List<String> stringToList(String str)
	{
		return Arrays.asList(str.split("\\|"));
	}
	
}
