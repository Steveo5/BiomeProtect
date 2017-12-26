package com.hotmail.steven.biomeprotect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger {

	// Hold the actual path to the log file
	private static String filePath;
	// Appended to the front of every log message
	private static String header = "[BiomeProtect]";
	// Whether to log to file
	private static boolean loggingEnabled = true;
	// Format the date/time string
	private static DateFormat dateFormat;
	
	/**
	 * Initializes the logger and allows logging to file
	 * @param plugin
	 */
	public static void enable(BiomeProtect plugin)
	{
		filePath = plugin.getDataFolder() + File.separator + "log.txt";		
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Logger.Log(Level.INFO, "Enabling the logger");
		// Load from config whether we will enable logging to file
		if(plugin.getConfig().isBoolean("logging.enabled"))
		{
			loggingEnabled = plugin.getConfig().getBoolean("logging.enabled");
		}
	}
	
	/**
	 * Log something to console/file
	 * @param level
	 * @param message
	 */
	public static void Log(Level level, String message)
	{
		System.out.println(header + " " + message);
		if(loggingEnabled)
		{
			writeToFile(message);
		}
	}
	
	/**
	 * Writes a message to the log file
	 * header is appended to the front with the current date/time
	 * @param message
	 */
	private static void writeToFile(String message)
	{
		PrintWriter printer = null;
		try {
			// Create the file writer
			FileWriter writer = new FileWriter(filePath, true);
			// Initialize our printer
			printer = new PrintWriter(writer);
			Date date = new Date();
			// Write the log message
			printer.println(dateFormat.format(date) + " " + header + " " + message);
		} catch (IOException e) {
			System.out.println(header + " failed to generate log.txt file. Disabling logging");
		}
		finally
		{
			if(printer != null) printer.close();
		}
	}
	
}
