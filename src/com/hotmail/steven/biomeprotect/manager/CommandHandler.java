package com.hotmail.steven.biomeprotect.manager;

import java.util.HashSet;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.commands.BiomeProtectCommand;

public class CommandHandler {

	private BiomeProtect plugin;
	private HashSet<BiomeProtectCommand> commands;
	
	public CommandHandler(BiomeProtect plugin)
	{
		commands = new HashSet<BiomeProtectCommand>();
		this.plugin = plugin;
	}
	
	/**
	 * Get all the registered BiomeProtect commands
	 * @return
	 */
	public HashSet<BiomeProtectCommand> getRegisteredCommands()
	{
		return commands;
	}
	
	/**
	 * Register a new BiomeProtectCommand
	 * @param command
	 */
	public void registerCommand(BiomeProtectCommand command)
	{
		commands.add(command);
	}
	
	/**
	 * Check if a command with a base name is registered
	 * @param name
	 * @return
	 */
	public boolean isCommand(String name)
	{
		for(BiomeProtectCommand command : commands)
		{
			if(command.getName().equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	/**
	 * Gets a BiomeProtectCommand from its name
	 * @param name
	 * @return null if no known command is found
	 */
	public BiomeProtectCommand getCommand(String name)
	{
		for(BiomeProtectCommand command : commands)
		{
			if(command.getName().equalsIgnoreCase(name)) return command;
		}
		return null;
	}
	
}
