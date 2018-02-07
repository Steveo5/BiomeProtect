package com.hotmail.steven.biomeprotect.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.steven.biomeprotect.BiomeProtect;

public class SessionData {

	private BiomeProtect plugin;
	// The file storing temp data
	private String sessionFileName = "session.yml";
	// File created inside the plugin data folder with the above file name
	private File file;
	// Create from the file above
	private FileConfiguration cfg;
	
	public SessionData(BiomeProtect plugin)
	{
		this.plugin = plugin;
		
		file = new File(plugin.getDataFolder() + File.separator + sessionFileName);
		
		// Create and load the session file
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		cfg = YamlConfiguration.loadConfiguration(file);
		
	}
	
	/**
	 * Get a session list from the config
	 * @param node
	 * @return
	 */
	public List<String> getSession(String node)
	{
		if(cfg != null && cfg.isList(node))
		{
			return cfg.getStringList(node);
		} else
		{
			return new ArrayList<String>();
		}
	}
	
	/**
	 * Save some session data to the config
	 * @param node
	 * @param sessionData
	 */
	public void saveSession(String node, List<String> sessionData)
	{
		if(cfg == null) return;
		// Remove old session data
		if(cfg.isConfigurationSection(node)) cfg.set(node, null);
		cfg.createSection(node);
		cfg.set(node, sessionData);
		save();
	}
	
	private void save()
	{
		try {
			cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
