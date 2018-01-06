package com.hotmail.steven.biomeprotect.storage;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.flag.RegionFlag;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.mysql.jdbc.Connection;

public class MysqlConnection implements DataConnection {

	private String user, pass, host, db;
	private int port;
	private Connection connection;
	private BiomeProtect plugin;
	private String cuboidsTable = "CREATE TABLE IF NOT EXISTS cuboids (cuboid_id VARCHAR(60) PRIMARY KEY NOT NULL,"
			+ " owner VARCHAR(60) NOT NULL,"
			+ " x INT(6) NOT NULL,"
			+ " y INT (6) NOT NULL,"
			+ " z INT(6) NOT NULL,"
			+ " world VARCHAR(60) NOT NULL,"
			+ " name VARCHAR(60) NOT NULL,"
			+ " material VARCHAR(30) NOT NULL,"
			+ " data INT(6) NOT NULL,"
			+ " radius INT(6) NOT NULL"
			+ ")";
	private String flagsTable = "CREATE TABLE IF NOT EXISTS cuboid_flags (cuboid_id VARCHAR(60),"
			+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id),"
			+ " flag_name VARCHAR(36),"
			+ " PRIMARY KEY(cuboid_id, flag_name),"
			+ " value VARCHAR(60),"
			+ " enabled TINYINT(1))";
	private String whitelistTable = "CREATE TABLE IF NOT EXISTS cuboid_whitelist (whitelist_id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
			+ " uuid VARCHAR(40),"
			+ " cuboid_id VARCHAR(60),"
			+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id))";
	
	public MysqlConnection(BiomeProtect plugin, String user, String pass, String host, String db, int port)
	{
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.db = db;
		this.port = port;
		this.plugin = plugin;
		Logger.Log(Level.INFO, "Mysql connection type selected, sending connection shortly");
        try {    
            openConnection(); 
            Logger.Log(Level.INFO, "Database connection was succesful");
    
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            Logger.Log(Level.INFO, "Failed connection to database... disabling, please check your config and restart the server/plugin");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
        
        
        createDefaultTables();
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
	
	/**
	 * Create all the tables needed for the connection, will not create them
	 * if the table already exists. In order to update a table structure
	 * the table needs manually removing
	 */
	private void createDefaultTables()
	{
		try { 
			Statement stmt = connection.createStatement();
			ResultSet cuboidsTable = stmt.executeQuery("SHOW TABLES LIKE 'cuboids'");
			// Check if the cuboids table exists and if not create it
			if(!cuboidsTable.next())
			{
				Logger.Log(Level.INFO, "Creating table cuboids");
				stmt.execute(this.cuboidsTable);
				
				ResultSet flagsTable = stmt.executeQuery("SHOW TABLES LIKE 'cuboid_flags'");
				// Check if the cuboid_flags table exists and if not create its
				if(!flagsTable.next())
				{
					Logger.Log(Level.INFO, "Creating table cuboid_flags");
					stmt.execute(this.flagsTable);
				}
				
				ResultSet buildersTable = stmt.executeQuery("SHOW TABLES LIKE 'cuboid_whitelist'");
				// Check if the cuboid_whitelisted table exists and if not create it
				if(!buildersTable.next())
				{
					Logger.Log(Level.INFO, "Creating table cuboid_whitelist");
					stmt.execute(this.whitelistTable);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveRegion(final ProtectedRegion region) {
		
		Location center = region.getCenter();
		// Query to insert the region into the database
		final String insertRegion = "INSERT INTO cuboids (cuboid_id,owner,x,y,z,world,name,material,data,radius) VALUES "
			+ "('" + region.getId().toString() + "','" + region.getOwner().toString() + "'," + center.getBlockX()
			+ "," + center.getBlockY() + "," + center.getBlockZ() + ",'" + region.getWorld().getUID().toString() + "',"
			+ "'" + region.getName() + "','" + region.getMaterial().name().toLowerCase() + "',0," + region.getRadius() + ")";
		// Copy the array as to not cause access exception
		final HashSet<RegionFlag<?>> flags = new HashSet<RegionFlag<?>>(region.getFlags());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{

			@Override
			public void run() {
				
				try {
					Statement stmt = connection.createStatement();
					ResultSet existing = stmt.executeQuery("SELECT * FROM cuboids WHERE cuboid_id LIKE '" + region.getId().toString() + "'");
					// Check if there is an existing cuboid already
					if(!existing.next())
					{
						stmt.execute(insertRegion);
					}
					// Save the flags
					for(RegionFlag<?> flag : flags)
					{
						// Create the update/create flag query
						final String insertFlag = "INSERT INTO cuboid_flags (cuboid_id,flag_name,value,enabled) VALUES ("
								+ "'" + region.getId().toString() + "','" + flag.getName() + "','" + flag.getValue() + "',1)"
								+ " ON DUPLICATE KEY UPDATE value='" + flag.getValue() + "'";
						stmt.execute(insertFlag);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	
		});
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
