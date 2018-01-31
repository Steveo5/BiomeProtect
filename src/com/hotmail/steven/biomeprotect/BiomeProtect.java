package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.hotmail.steven.biomeprotect.commands.CmdFlags;
import com.hotmail.steven.biomeprotect.commands.CmdGive;
import com.hotmail.steven.biomeprotect.commands.CmdHelp;
import com.hotmail.steven.biomeprotect.commands.CmdList;
import com.hotmail.steven.biomeprotect.commands.CmdNear;
import com.hotmail.steven.biomeprotect.commands.CmdShow;
import com.hotmail.steven.biomeprotect.flag.FlagHolder;
import com.hotmail.steven.biomeprotect.listener.BiomeProtectListener;
import com.hotmail.steven.biomeprotect.listener.CommandListener;
import com.hotmail.steven.biomeprotect.listener.RegionCacheListener;
import com.hotmail.steven.biomeprotect.listener.RegionCreateListener;
import com.hotmail.steven.biomeprotect.listener.RegionFlagsListener;
import com.hotmail.steven.biomeprotect.listener.RegionProtectionListener;
import com.hotmail.steven.biomeprotect.listener.RegionVisualizationListener;
import com.hotmail.steven.biomeprotect.manager.CommandHandler;
import com.hotmail.steven.biomeprotect.manager.RegionContainer;
import com.hotmail.steven.biomeprotect.manager.RegionVisualizer;
import com.hotmail.steven.biomeprotect.menubuilder.MenuBuilderListener;
import com.hotmail.steven.biomeprotect.region.RegionSaveTask;
import com.hotmail.steven.biomeprotect.storage.RegionConfig;
import com.hotmail.steven.biomeprotect.storage.RegionData;
import com.hotmail.steven.biomeprotect.storage.SessionData;

public class BiomeProtect extends JavaPlugin {
	
	private RegionData regionData;
	private SessionData sessionData;
	private static BiomeProtect plugin;
	private static RegionMenu menu;
	private FlagHolder flagHolder;
	private RegionConfig regionConfig;
	private RegionContainer regionContainer;
	private List<BiomeProtectListener> listeners;
	private RegionSaveTask regionSaveTask;
	private static RegionVisualizer visualizer;
	private CommandHandler commandHandler;
	
	@Override
	public void onEnable()
	{
		// Start the logger
		Logger.enable(this);
		// Enable the language file
		Language.enable(this);
		regionConfig = new RegionConfig(this);
		listeners = new ArrayList<BiomeProtectListener>();
		menu = new RegionMenu();
		// Save default config if needed
		this.saveDefaultConfig();
		// Register player listener
		getServer().getPluginManager().registerEvents(menu, this);
		commandHandler = new CommandHandler(this);
		this.getCommand("biomeprotect").setExecutor(new CommandListener(this));
		
		plugin = this;
		
		regionContainer = new RegionContainer(this);
		
		//regionData.loadRegions();
		flagHolder = new FlagHolder();
		// Initialize the listeners
		RegionCacheListener cacheListener = new RegionCacheListener(this);
		RegionCreateListener createListener = new RegionCreateListener(this);
		RegionFlagsListener flagListener = new RegionFlagsListener(this);
		RegionProtectionListener protectionListener = new RegionProtectionListener(this);
		RegionVisualizationListener regionVisualizationListener = new RegionVisualizationListener(this);
		// Add them to the loaded list
		listeners.add(cacheListener);
		listeners.add(createListener);
		listeners.add(flagListener);
		listeners.add(protectionListener);
		listeners.add(regionVisualizationListener);
		
		for(BiomeProtectListener listener : listeners)
		{
			getServer().getPluginManager().registerEvents(listener, this);
		}
		
		// Handle events for the menu builders
		new MenuBuilderListener(this);
		
		sessionData = new SessionData(this);
	
		/**
		 * Initiate the region data (loads all regions into memory)
		 */
		regionData = new RegionData(this);
		regionSaveTask = new RegionSaveTask(this);
		Bukkit.getScheduler().runTaskTimer(this, regionSaveTask, 20L * 10L, 20L * getConfig().getInt("database.interval.seconds"));
		
		LinkedList<Location> sessionRemoveQueue = new LinkedList<Location>();
		Logger.Log(Level.ALL, "Loading previous visualization session queue");
		// get all locations in the queue
		for(String strLocation : sessionData.getSession("remove-queue"))
		{
			String[] locData = strLocation.split(",");
			World w = Bukkit.getWorld(UUID.fromString(locData[0]));
			int x = Integer.parseInt(locData[1]);
			int y = Integer.parseInt(locData[2]);
			int z = Integer.parseInt(locData[3]);
			Location loc = new Location(w, x, y, z);
			sessionRemoveQueue.add(loc);
		}
		visualizer = new RegionVisualizer(this, sessionRemoveQueue);
		
		/**
		 * Register BiomeProtectCommands
		 */
		getCommandHandler().registerCommand(new CmdShow(this, "show", "Show the physical boundaries of a region", "Usage: /bp show"));
		getCommandHandler().registerCommand(new CmdFlags(this, "flags", "List all of the flags that are possible (even those not shown in the gui", "Usage: /bp flags [page]"));
		getCommandHandler().registerCommand(new CmdList(this, "list", "List all the configured protection stones", "Usage: /bp list"));
		getCommandHandler().registerCommand(new CmdHelp(this, "help", "Show all available commands", "Usage: /help [page]"));
		getCommandHandler().registerCommand(new CmdGive(this, "give", "Give yourself or another player a protection stone", "Usage: /bp give <pstone> [player]"));
		getCommandHandler().registerCommand(new CmdNear(this, "near", "Show nearby protected regions", "Usage: /bp near [radius]"));
	}
	
	@Override
	public void onDisable()
	{
		//Logger.Log(Level.INFO, "Saving all regions to the database");
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
	
	public SessionData getSessionData()
	{
		return sessionData;
	}
	
	public static RegionMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Gets the command handler
	 * @return
	 */
	public CommandHandler getCommandHandler()
	{
		return commandHandler;
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
	
	public static RegionVisualizer getVisualizer()
	{
		return visualizer;
	}
	
}
