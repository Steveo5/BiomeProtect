package com.hotmail.steven.biomeprotect.storage;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.flag.BooleanFlag;
import com.hotmail.steven.biomeprotect.flag.RegionFlag;
import com.hotmail.steven.biomeprotect.flag.StateFlag;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.biomeprotect.region.RegionCreator;
import com.hotmail.steven.util.StringUtil;
import com.mysql.jdbc.Connection;

public class MysqlConnection implements DataConnection {

	private String user, pass, host, db;
	private int port;
	private Connection connection;
	private BiomeProtect plugin;
	
	/**
	 * Queries that create the tables
	 */
	private final String cuboidsTable = "CREATE TABLE IF NOT EXISTS cuboids (cuboid_id VARCHAR(60) PRIMARY KEY NOT NULL,"
			+ " owner VARCHAR(60) NOT NULL,"
			+ " x INT(6) NOT NULL,"
			+ " y INT (6) NOT NULL,"
			+ " z INT(6) NOT NULL,"
			+ " world VARCHAR(60) NOT NULL,"
			+ " name VARCHAR(60) NOT NULL,"
			+ " material VARCHAR(30) NOT NULL,"
			+ " data INT(6) NOT NULL,"
			+ " radius INT(6) NOT NULL,"
			+ " priority INT(6) NOT NULL,"
			+ " title varchar(60),"
			+ " lore varchar(120)"
			+ ")";
	private final String flagsTable = "CREATE TABLE IF NOT EXISTS cuboid_flags (cuboid_id VARCHAR(60),"
			+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id),"
			+ " flag_name VARCHAR(36) NOT NULL,"
			+ " PRIMARY KEY(cuboid_id, flag_name),"
			+ " value VARCHAR(60) NOT NULL,"
			+ " enabled TINYINT(1))";
	private final String whitelistTable = "CREATE TABLE IF NOT EXISTS cuboid_whitelist ("
			+ " uuid VARCHAR(40),"
			+ " cuboid_id VARCHAR(60),"
			+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id),"
			+ " PRIMARY KEY (uuid, cuboid_id))";
	
	/**
	 * Queries that retrieve information
	 */
	private final String showCuboidsTable = "SHOW TABLES LIKE 'cuboids'";
	private final String showFlagsTable = "SHOW TABLES LIKE 'cuboid_flags'";
	private final String showWhitelistedTable = "SHOW TABLES LIKE 'cuboid_whitelist'";
	private final String existingCuboidsId = "SELECT * FROM cuboids WHERE cuboid_id LIKE ''{0}''";
	private final String existingCuboids = "SELECT * FROM cuboids";
	private final String existingFlags = "SELECT * FROM cuboid_flags WHERE cuboid_id = ''{0}''";
	private final String existingWhitelist = "SELECT * FROM cuboid_whitelist WHERE cuboid_id = ''{0}''";
	
	/**
	 * Queries that insert or update information
	 */
	// Create the update/create flag query
	private final String insertFlag = "INSERT INTO cuboid_flags (cuboid_id,flag_name,value,enabled) VALUES ("
			+ "''{0}'',''{1}'',''{2}'',1)"
			+ " ON DUPLICATE KEY UPDATE value = VALUES(value)";
	private final String insertRegion = "INSERT INTO cuboids (cuboid_id,owner,x,y,z,world,name,material,data,radius,priority,title,lore) VALUES "
			+ "(''{0}'', ''{1}'', {2}, {3}, {4}, ''{5}'', ''{6}'', ''{7}'', 0, {8}, {9}, ''{10}'', ''{11}'')";
	private final String insertWhitelist = "INSERT INTO cuboid_whitelist (uuid, cuboid_id) VALUES (''{0}'', ''{1}'')"
			+ " ON DUPLICATE KEY UPDATE uuid = uuid, cuboid_id = cuboid_id";
	
	/**
	 * Queries that remove information
	 */
	private final String removeRegion = "DELETE FROM cuboids WHERE cuboid_id LIKE ''{0}''";
	private final String removeFlags = "DELETE FROM cuboid_flags WHERE cuboid_id = ''{0}''";
	private final String removeMembers = "DELETE FROM cuboid_whitelist WHERE cuboid_id = ''{0}''";
	
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
			ResultSet cuboidsTable = stmt.executeQuery(showCuboidsTable);
			// Check if the cuboids table exists and if not create it
			if(!cuboidsTable.next())
			{
				Logger.Log(Level.INFO, "Creating table cuboids");
				stmt.execute(this.cuboidsTable);
				
				ResultSet flagsTable = stmt.executeQuery(showFlagsTable);
				// Check if the cuboid_flags table exists and if not create its
				if(!flagsTable.next())
				{
					Logger.Log(Level.INFO, "Creating table cuboid_flags");
					stmt.execute(this.flagsTable);
				}
				
				ResultSet buildersTable = stmt.executeQuery(showWhitelistedTable);
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
		final String insertRegionQuery = MessageFormat.format(this.insertRegion, region.getId().toString(), region.getOwner().toString(), 
				String.valueOf(center.getBlockX()), String.valueOf(center.getBlockY()), String.valueOf(center.getBlockZ()), region.getWorld().getUID().toString(), region.getName(), 
				region.getMaterial().name().toLowerCase(), region.getRadius(), String.valueOf(region.getPriority()),
				region.getTitle(), StringUtil.listToString(region.getLore()));
		System.out.println(insertRegionQuery);
		// Copy the array as to not cause access exception
		final HashSet<RegionFlag<?>> flags = new HashSet<RegionFlag<?>>(region.getFlags());
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
		{

			@Override
			public void run() {
				
				try {
					Statement stmt = connection.createStatement();
					ResultSet existing = stmt.executeQuery(MessageFormat.format(existingCuboidsId, region.getId().toString()));
					// Check if there is an existing cuboid already
					if(!existing.next())
					{
						stmt.execute(insertRegionQuery);
					}
					// Clear flags
					stmt.execute(MessageFormat.format(removeFlags, region.getId().toString()));
					// Save the flags
					for(RegionFlag<?> flag : flags)
					{
						stmt.execute(MessageFormat.format(insertFlag, region.getId().toString(), flag.getName(), flag.getValue()));
					}
					// Clear whitelist
					stmt.execute(MessageFormat.format(removeMembers, region.getId().toString()));
					// Save the whitelisted players
					for(UUID uuid : region.getMembers())
					{
						stmt.execute(MessageFormat.format(insertWhitelist, uuid.toString(), region.getId().toString()));
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
		Statement stmt = null, stmtFlags = null, stmtWhitelist = null;
		try {
			if(connection != null && !connection.isClosed())
			{
				Logger.Log(Level.INFO, "Loading regions shortly...");
				stmt = connection.createStatement();
				stmtFlags = connection.createStatement();
				stmtWhitelist = connection.createStatement();
				// Get all the cuboids
				ResultSet cuboids = stmt.executeQuery(existingCuboids);
				int counter = 0;
				int loaded = 0;
				// Loop over every cuboid, retrieving flags, whitelist, etc
				while(cuboids.next())
				{
					// The cuboid settings
					UUID cuboidId = UUID.fromString(cuboids.getString(1));
					UUID owner = UUID.fromString(cuboids.getString(2));
					int x = cuboids.getInt(3);
					int y = cuboids.getInt(4);
					int z = cuboids.getInt(5);
					UUID worldId = UUID.fromString(cuboids.getString(6));
					String name = cuboids.getString(7);
					Material material = Material.valueOf(cuboids.getString(8).toUpperCase());
					int radius = cuboids.getInt(10);
					int priority = cuboids.getInt(11);
					String title = cuboids.getString(12);
					String lore = cuboids.getString(13);
					World w = Bukkit.getWorld(worldId);
					if(w != null)
					{
						// Create the location
						Location loc = new Location(w, x, y, z);
						// Initialize the region creator
						RegionCreator creator = new RegionCreator(name);
						// Set basic settings
						creator.height(radius).radius(radius).type(material).priority(priority);
						if(!title.isEmpty()) creator.title(title);
						if(!lore.isEmpty()) creator.lore(StringUtil.stringToList(lore));
						// Create the region
						ProtectedRegion region = creator.createRegion(cuboidId, owner, loc, material);
						ResultSet flags = stmtFlags.executeQuery(MessageFormat.format(existingFlags, cuboidId.toString()));
						ResultSet whitelist = stmtWhitelist.executeQuery(MessageFormat.format(existingWhitelist, cuboidId.toString()));
						
						System.out.println("whitelist query " + MessageFormat.format(existingWhitelist, cuboidId.toString()));
						while(flags.next())
						{
							String flagName = flags.getString(2);
							String value = flags.getString(3);
							RegionFlag<?> flag = plugin.getFlagHolder().get(flagName);
							System.out.println("Gettinf flag " + flag.getName());
							
							if(flag instanceof BooleanFlag) ((BooleanFlag)flag).setValue(Boolean.valueOf(value));
							if(flag instanceof StringFlag) ((StringFlag)flag).setValue(value);
							if(flag instanceof StateFlag) ((StateFlag)flag).setValue(value);
							System.out.println("Setting flag " + flag.getName() + " " + flag.getValue());
							region.setFlag(flag);
						}
						while(whitelist.next())
						{
							String uuid = whitelist.getString(1);
							System.out.println("Recieved " + uuid);
							region.addMember(UUID.fromString(uuid));
						}
						// Add the region to the loaded list
						plugin.getRegionContainer().addRegion(region);
						loaded++;
					}
					counter++;
				}
				Logger.Log(Level.INFO, "Total " + loaded + " / " + counter + " regions loaded");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			try {
				if(stmt != null) stmt.close();
				if(stmtFlags != null) stmtFlags.close();
				if(stmtWhitelist != null) stmtWhitelist.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void removeRegion(UUID id) {
		
		try {
			Statement stmt = connection.createStatement();
			stmt.execute(MessageFormat.format(removeMembers, id));
			stmt.execute(MessageFormat.format(removeFlags, id));
			stmt.execute(MessageFormat.format(removeRegion, id));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
