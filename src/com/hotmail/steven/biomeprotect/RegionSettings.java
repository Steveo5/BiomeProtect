package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class RegionSettings {

	/**
	 * Get the database type, either flatfile, mysql
	 * @return
	 */
	public static String getStorageType()
	{
		return "mysql";
	}

	/**
	 * If using mysql, get the url of the server
	 */
	public static String getMysqlUrl()
	{
		return "localhost";
	}
	
	/**
	 * If using mysql, the username
	 * @return
	 */
	public static String getMysqlUser()
	{
		return "root";
	}
	
	/**
	 * If using mysql, thhe password
	 * @return
	 */
	public static String getMysqlPass()
	{
		return "";
	}
	
	public static int getMysqlPort()
	{
		return 3306;
	}
	
	public static String getMysqlDb()
	{
		return "regions";
	}
	
	/**
	 * Get a list of protection names
	 * @return
	 */
	public static List<String> getProtectionList()
	{
		List<String> protectionNames = new ArrayList<String>();
		// Get all the protection headers
		for(String key : BiomeProtect.getRegionConfig().getConfigurationSection("protection-stones").getKeys(false))
		{
			protectionNames.add(key);
		}
		return protectionNames;
	}
	
	public static boolean hasProtectionStone(String protectionName)
	{
		return getProtectionList().contains(protectionName);
	}
	
	/**
	 * Get the material for a specific protection stone
	 * @param name
	 * @return air if the name isn't found
	 */
	public static Material getMaterial(String protectionName)
	{
		Material mat = Material.AIR;
		if(getProtectionList().contains(protectionName))
		{
			try
			{
				mat = Material.valueOf(BiomeProtect.getRegionConfig().getString("protection-stones." + protectionName + ".block").toUpperCase());
			} catch(Exception e) {};
		}
		
		return mat;
	}
	
	/**
	 * Get the radius for a protection stone
	 * @param protectionName
	 * @return -1 if the protection stone isn't found
	 */
	public static int getRadius(String protectionName)
	{
		int radius = -1;
		if(getProtectionList().contains(protectionName))
		{
			try
			{
				radius = BiomeProtect.getRegionConfig().getInt("protection-stones." + protectionName);
			} catch(Exception e) {};
		}		
		return radius;
	}
	
}
