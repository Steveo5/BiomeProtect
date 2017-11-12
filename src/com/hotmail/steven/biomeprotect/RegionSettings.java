package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.hotmail.steven.util.StringUtil;

public class RegionSettings {

	private static List<ProtectionStone> protectionStones;
	
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
	
	// Load the protection stones from the database
	protected static void loadProtectionStones()
	{
		protectionStones = new ArrayList<ProtectionStone>();
		int loaded = 0;
		for(ConfigurationSection configSection : getProtectionList())
		{
			Material mat = Material.valueOf(configSection.getString("block").toUpperCase());
			int data = configSection.contains("data") ? data = configSection.getInt("data") : 0;
			int radius = configSection.getInt("radius");
			
			ProtectionStone protectionStone = new ProtectionStone(configSection.getName(), mat, data, radius);
			
			if(configSection.contains("custom-height")) protectionStone.setCustomHeight(configSection.getInt("custom-height"));
			if(configSection.contains("prevent-place")) protectionStone.setAllowsPlace(configSection.getBoolean("prevent-place"));
			if(configSection.contains("prevent-break")) protectionStone.setAllowsBreak(configSection.getBoolean("prevent-break"));
			if(configSection.contains("meta.title")) protectionStone.setTitle(configSection.getString("meta.title"));
			if(configSection.contains("welcome-message")) protectionStone.setWelcomeMessage(configSection.getString("welcome-message"));
			if(configSection.contains("leave-message")) protectionStone.setLeaveMessage(configSection.getString("leave-message"));
			if(configSection.isList("meta.lore")) protectionStone.setLore(configSection.getStringList("meta.lore"));
			protectionStones.add(protectionStone);
			System.out.println("Loaded " + protectionStone.getTitle());
			loaded++;
		}
		
		Logger.Log(Level.INFO, loaded + " protection stones were loaded");
	}
	
	// Get the loaded protection stones
	public static List<ProtectionStone> getProtectionStones()
	{
		return protectionStones;
	}
	
	/**
	 * Get a list of protection names
	 * @return
	 */
	private static List<ConfigurationSection> getProtectionList()
	{
		List<ConfigurationSection> protectionNames = new ArrayList<ConfigurationSection>();
		// Get all the protection headers
		for(String key : BiomeProtect.getRegionConfig().getConfigurationSection("protection-stones").getKeys(false))
		{
			protectionNames.add(BiomeProtect.getRegionConfig().getConfigurationSection("protection-stones." + key));
		}
		return protectionNames;
	}
	
	/**
	 * Check
	 * @param item
	 * @return
	 */
	public static boolean isProtectionStone(ItemStack item)
	{
		for(ProtectionStone pStone : getProtectionStones())
		{
			if(item.getType() == pStone.getMaterial())
			{
				if(pStone.hasTitle())
				{
					if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(pStone.getTitle()))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static ProtectionStone getProtectionStone(ItemStack item)
	{
		for(ProtectionStone pStone : getProtectionStones())
		{
			if(item.getType() == pStone.getMaterial())
			{
				if(pStone.hasTitle())
				{
					if(item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(pStone.getTitle()))
					{
						return pStone;
					}
				}
			}
		}	
		return null;
	}
}
