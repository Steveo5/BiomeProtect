package com.hotmail.steven.biomeprotect.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.region.RegionCreator;
import com.hotmail.steven.util.StringUtil;

public class RegionConfig {
	
	private BiomeProtect plugin;
	private String mysqlHeader = "database.settings.mysql";
	private FileConfiguration cfg;
	
	public RegionConfig(BiomeProtect plugin)
	{
		this.plugin = plugin;
		cfg = plugin.getConfig();
		
	}
	
	/**
	 * Get the database type, either flatfile, mysql
	 * @return
	 */
	public String getStorageType()
	{
		return cfg.getString("database.type");
	}

	/**
	 * If using mysql, get the url of the server
	 */
	public String getMysqlUrl()
	{
		return cfg.getString(mysqlHeader + ".address");
	}
	
	/**
	 * If using mysql, the username
	 * @return
	 */
	public String getMysqlUser()
	{
		return cfg.getString(mysqlHeader + ".user");
	}
	
	/**
	 * If using mysql, thhe password
	 * @return
	 */
	public String getMysqlPass()
	{
		return cfg.getString(mysqlHeader + ".password");
	}
	
	public int getMysqlPort()
	{
		return cfg.getInt(mysqlHeader + ".port");
	}
	
	public String getMysqlDb()
	{
		return cfg.getString(mysqlHeader + ".database");
	}
	
	// Load the protection stones from the database
	/*
	protected static void loadProtectionStones()
	{
		protectionStones = new ArrayList<RegionCreator>();
		int loaded = 0;
		for(ConfigurationSection configSection : getProtectionList())
		{
			Material mat = Material.valueOf(configSection.getString("block").toUpperCase());
			int data = configSection.contains("data") ? data = configSection.getInt("data") : 0;
			int radius = configSection.getInt("radius");
			
			RegionCreator protectionStone = new RegionCreator(configSection.getName(), mat, data, radius);
			
			if(configSection.contains("custom-height")) protectionStone.setCustomHeight(configSection.getInt("custom-height"));
			if(configSection.contains("prevent-place")) protectionStone.setAllowsPlace(configSection.getBoolean("prevent-place"));
			if(configSection.contains("prevent-break")) protectionStone.setAllowsBreak(configSection.getBoolean("prevent-break"));
			if(configSection.contains("meta.title")) protectionStone.setTitle(configSection.getString("meta.title"));
			if(configSection.contains("welcome-message")) protectionStone.setWelcomeMessage(configSection.getString("welcome-message"));
			if(configSection.contains("leave-message")) protectionStone.setLeaveMessage(configSection.getString("leave-message"));
			if(configSection.isList("meta.lore")) protectionStone.setLore(configSection.getStringList("meta.lore"));
			if(configSection.contains("prevent-tnt")) protectionStone.setAllowsTnt(configSection.getBoolean("prevent-tnt"));
			if(configSection.contains("prevent-pvp")) protectionStone.setAllowsPvp(configSection.getBoolean("prevent-pvp"));
			protectionStones.add(protectionStone);
			System.out.println("Loaded " + protectionStone.getTitle());
			loaded++;
		}
		
		Logger.Log(Level.INFO, loaded + " protection stones were loaded");
	}
	
	
	// Get the loaded protection stones
	public static List<RegionCreator> getProtectionStones()
	{
		return protectionStones;
	}
	*/
	/**
	 * Get the configuration for a protection stone in the config.
	 * 
	 * If the item stack has a meta display name then it is matched
	 * with the given config meta, if no meta is found in the configs version
	 * and the item has no display name then a true is returned
	 * @param item
	 * @return null if no known configuration for the item is found
	 */
	public ConfigurationSection getConfigurationSection(String node, ItemStack item)
	{
		for(String key : plugin.getConfig().getConfigurationSection(node).getValues(false).keySet())
		{
			ConfigurationSection stone = plugin.getConfig().getConfigurationSection(node + "." + key);
			// Check if they of same material type
			if(stone.getString("block").equalsIgnoreCase(item.getType().name()))
			{
				if(stone.contains("meta"))
				{
					if(!item.hasItemMeta()) return null;
					// Check if the item meta match the configuration meta
					if(matchesItemMeta(stone, item.getItemMeta())) return stone;

				} else
				{
					// Items can't be the same if it has an item meta
					if(item.hasItemMeta()) return null;
					// Return the configured stone otherwise
					return stone;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns whether an item correctly matches the item meta of a configuration section
	 * @param stone
	 * @param item
	 * @return
	 */
	private static boolean matchesItemMeta(ConfigurationSection stone, ItemMeta im)
	{
		/**
		 * First section does a title check
		 */
		if(stone.contains("meta.title"))
		{
			if(im.hasDisplayName() && !im.getDisplayName().equals(StringUtil.colorize(stone.getString("meta.title"))))
			{
				return false;
			} else if(!im.hasDisplayName()) return false;
		} else
		{
			if(im.hasDisplayName()) return false;
		}
		
		/**
		 * Second section checks if the lore is the same
		 *
		 */
		//TODO Check lore
		return true;
	}
	
	public int getInteractLimit()
	{
		return plugin.getConfig().getInt("interact-limit");
	}
}
