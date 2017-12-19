package com.hotmail.steven.biomeprotect.listener;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.storage.RegionConfig;

public class BiomeProtectListener implements Listener {

	private final BiomeProtect plugin;
	
	/**
	 * Helper class for other listeners to extend
	 * @param plugin
	 */
	public BiomeProtectListener(BiomeProtect plugin)
	{
		this.plugin = plugin;
	}
	
	/**
	 * Get the main plugin
	 * @return
	 */
	public BiomeProtect getPlugin()
	{
		return plugin;
	}
	
	/**
	 * Gets the file configuration for protection stones, settings etc
	 * @return
	 */
	public RegionConfig getRegionConfig()
	{
		return plugin.getRegionConfig();
	}
	
	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}
}
