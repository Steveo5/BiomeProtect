package com.hotmail.steven.biomeprotect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.hotmail.steven.util.StringUtil;

public class Language {

	// Hold the actual path to the log file
	private static String filePath;
	private static File file;
	private static boolean ready = false;

	/**
	 * Initializes the logger and allows logging to file
	 * @param plugin
	 */
	public static void enable(BiomeProtect plugin)
	{
		filePath = plugin.getDataFolder() + File.separator + "messages.txt";
		file = new File(filePath);
		Logger.Log(Level.INFO, "Enabling language files");
		if(!file.isFile()) createDefaultFile(plugin.getResource("messages.txt"));
	}
	
	/**
	 * Writes an input stream to the messages.txt file
	 * @param resource
	 */
	private static void createDefaultFile(InputStream resource)
	{
		Logger.Log(Level.INFO, "No messages.txt file found, creating now...");
		try {
			// Create the byte buffer
			byte[] buffer = new byte[resource.available()];
			resource.read(buffer);
			
			// Create the messages.txt
			file.createNewFile();
			OutputStream fos = new FileOutputStream(file);
			// Add defaults from our local resource
			fos.write(buffer);
			fos.close();
			ready = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String tl(String obj, String... replacers)
	{
		String message = "";
		if(ready)
		{
			try {
				// Start the reader for our file
				BufferedReader reader = new BufferedReader(new FileReader(file));
		        String str;
		        // Read line by line until its empty
		        while((str = reader.readLine()) != null)
		        {
		        	// Check if the string starts with the compare string
		        	if(str.startsWith(obj + "="))
		        	{
		        		// Trim the string and set our message
		        		message = str.replaceFirst(obj + "=", "");
		        		break;
		        	}
		        }
		        reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
		{
			if(obj.equals("noBuildPermission"))
			{
				message = "&cYou cannot build here";
			} else if(obj.equals("regionRemoved"))
			{
				message = "&cYou have removed the protected region";
			} else if(obj.equals("noPlacePermission"))
			{
				message = "You do not have permission to place blocks here";
			} else if(obj.equals("playerOnly"))
			{
				message = "This command can only be executed as a player";
			} else if(obj.equals("regionDoesntExist"))
			{
				message = "No protected regions exist here";
			} else if(obj.equals("noPermission"))
			{
				message = "You do not have permission to perform that action";
			} else if(obj.equals("regionPlaced"))
			{
				message =  "&6You have placed a new region at &cx %x% y %y% z %z%";
			} else if(obj.equals("noPvp"))
			{
				message = "&cYou cannot attack other players here";
			}
		}
		return replacers(StringUtil.colorize(message), replacers);
	}
	
	public static void tl(Player player, String obj)
	{
		player.sendMessage(tl(obj));
	}
	
	private static String replacers(String str, String...replacers)
	{
		for(int i=0;i<replacers.length;i+=2)
		{
			String replacer = replacers[i];
			String replacement = replacers[i+1];
			if(replacer != null && replacement != null)
			{
				str = str.replaceAll(replacer, replacement);
			}
		}
		return str;
	}
	
}
