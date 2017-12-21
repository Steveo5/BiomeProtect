package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.steven.biomeprotect.flag.FlagHolder;
import com.hotmail.steven.biomeprotect.listener.BiomeProtectListener;
import com.hotmail.steven.biomeprotect.listener.RegionCacheListener;
import com.hotmail.steven.biomeprotect.listener.RegionCreateListener;
import com.hotmail.steven.biomeprotect.listener.RegionFlagsListener;
import com.hotmail.steven.biomeprotect.listener.RegionProtectionListener;
import com.hotmail.steven.biomeprotect.manager.RegionContainer;
import com.hotmail.steven.biomeprotect.menubuilder.MenuBuilderListener;
import com.hotmail.steven.biomeprotect.storage.RegionConfig;
import com.hotmail.steven.biomeprotect.storage.RegionData;

public class BiomeProtect extends JavaPlugin {
	
	private RegionData regionData;
	private static BiomeProtect plugin;
	private static RegionMenu menu;
	private FlagHolder flagHolder;
	private RegionConfig regionConfig;
	private RegionContainer regionContainer;
	private List<BiomeProtectListener> listeners;
	
	@Override
	public void onEnable()
	{
		// Start the logger
		Logger.enable(this);
		regionData = new RegionData(this);
		regionConfig = new RegionConfig(this);
		listeners = new ArrayList<BiomeProtectListener>();
		menu = new RegionMenu();
		// Save default config if needed
		this.saveDefaultConfig();
		// Register player listener
		getServer().getPluginManager().registerEvents(menu, this);
		this.getCommand("biomeprotect").setExecutor(new CommandHandler(this));
		
		plugin = this;
		
		regionContainer = new RegionContainer(this);
		
		Logger.Log(Level.INFO, "Loading regions shortly...");
		//regionData.loadRegions();
		flagHolder = new FlagHolder();
		// Initialize the listeners
		RegionCacheListener cacheListener = new RegionCacheListener(this);
		RegionCreateListener createListener = new RegionCreateListener(this);
		RegionFlagsListener flagListener = new RegionFlagsListener(this);
		RegionProtectionListener protectionListener = new RegionProtectionListener(this);
		// Add them to the loaded list
		listeners.add(cacheListener);
		listeners.add(createListener);
		listeners.add(flagListener);
		listeners.add(protectionListener);
		
		for(BiomeProtectListener listener : listeners)
		{
			getServer().getPluginManager().registerEvents(listener, this);
		}
		
		// Handle events for the menu builders
		new MenuBuilderListener(this);
	}
	
	@Override
	public void onDisable()
	{
		Logger.Log(Level.INFO, "Saving all regions to the database");
		//TODO save regions to database
	}
	
	public static BiomeProtect instance()
	{
		return plugin;
	}
	
	/**
	 * Get all the listeners the plugin is listening on
	 * @return
	 */
	public List<BiomeProtectListener> getListeners()
	{
		return listeners;
	}
	
	/**
	 * Holds the database/flatfile methods to store
	 * and retrieve protection stones
	 * @return
	 */
	public RegionData getRegionData()
	{
		return regionData;
	}
	
	public static RegionMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Get the main config.yml
	 * @return
	 */
	public RegionConfig getRegionConfig()
	{
		return regionConfig;
	}
	
	/**
	 * Get the region container/manager
	 * @return
	 */
	public RegionContainer getRegionContainer()
	{
		return regionContainer;
	}
	
	public FlagHolder getFlagHolder()
	{
		return flagHolder;
		
	}
	
}
