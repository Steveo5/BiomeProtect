package com.hotmail.steven.biomeprotect.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.hotmail.steven.biomeprotect.BiomeProtect;

import static com.hotmail.steven.biomeprotect.Language.tl;

public abstract class BiomeProtectCommand {

	private BiomeProtect plugin;
	private boolean requiresPlayer;
	private String name, description, usage;
	
	public BiomeProtectCommand(BiomeProtect plugin, String name, String description, String usage)
	{
		this.plugin = plugin;
		this.name = name;
		this.description = description;
		this.usage = usage;
	}
	
	/**
	 * Get this command name
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getUsage()
	{
		return usage;
	}
	
	/**
	 * Get the main BiomeProtect plugin
	 * @return
	 */
	public BiomeProtect getPlugin()
	{
		return plugin;
	}
	
	/**
	 * Get the BiomeProtect FileConfiguration
	 * @return
	 */
	public FileConfiguration getConfig()
	{
		return plugin.getConfig();
	}
	
	/**
	 * Set whether this command requires a player sender. If
	 * so the run method that has a player is called
	 * @param require
	 * @return
	 */
	public BiomeProtectCommand requiresPlayer(boolean require)
	{
		this.requiresPlayer = require;
		return this;
	}
	
	/**
	 * Return whether this command requires a player sender
	 * @return
	 */
	public boolean requiresPlayer()
	{
		return requiresPlayer;
	}
	
	/**
	 * Base command, run first to test which run method to actually execute
	 * @param sender
	 * @param name
	 * @param label
	 * @param args
	 */
	public boolean run(CommandSender sender, String name, String label, String[] args) throws Exception
	{
		/**
		 * Check if the command requires a player
		 */
		if(requiresPlayer)
		{
			if(!(sender instanceof Player))
			{
				throw new Exception(tl("playerOnly"));
			} else
			{
				return run((Player)sender, name, args);
			}
		} else
		{
			return run(sender, name, args);
		}
		
		
	}
	
	/**
	 * Execute a generic BiomeProtectCommand
	 * @param sender
	 * @param name
	 * @param args
	 * @throws Exception
	 */
	public boolean run(CommandSender sender, String name, String[] args) throws Exception
	{
		return false;
	}
	
	/**
	 * Execute a generic BiomeProtectCommand as a specific player
	 * @param sender
	 * @param name
	 * @param args
	 * @throws Exception
	 */
	public boolean run(Player sender, String name, String[] args) throws Exception
	{
		return false;
	}
	
}
