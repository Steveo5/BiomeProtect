package com.hotmail.steven.biomeprotect;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;

import com.mysql.jdbc.Connection;

public class RegionData {

	private File cfgFile;
	private FileConfiguration cfg;
	// Hold mysql connection
	private Connection connection;
	// Hold mysql database details
	private String host, database, username, password;
	private int port;
	
	public RegionData(BiomeProtect plugin)
	{
		if(RegionSettings.getStorageType().equals("mysql"))
		{
	        try {    
	            openConnection();        
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	
	private void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	    }
	}
	
}
