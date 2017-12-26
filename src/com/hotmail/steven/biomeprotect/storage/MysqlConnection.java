package com.hotmail.steven.biomeprotect.storage;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.mysql.jdbc.Connection;

public class MysqlConnection implements IConnection {

	private String user, pass, host, db;
	private int port;
	private Connection connection;
	
	public MysqlConnection(BiomeProtect plugin, String user, String pass, String host, String db, int port)
	{
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.db = db;
		this.port = port;
		
        try {    
            openConnection(); 
            Logger.Log(Level.INFO, "Database connection was succesful");
    
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            Logger.Log(Level.INFO, "Failed connection to database... disabling, please check your config and restart the server/plugin");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
	}
	
	/**
	 * Opens the connection to the mysql database
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = (Connection) DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.db, this.user, this.pass);
	    }
	}
	
	private void createDefaultTables()
	{
		try { 
			Statement stmt = connection.createStatement();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveRegion(ProtectedRegion region) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadRegions() {
		try {
			if(connection != null && !connection.isClosed())
			{
				Logger.Log(Level.INFO, "Loading regions shortly...");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void removeRegion(UUID id) {
		// TODO Auto-generated method stub
		
	}
	
}
