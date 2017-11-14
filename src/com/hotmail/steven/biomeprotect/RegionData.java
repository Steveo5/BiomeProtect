package com.hotmail.steven.biomeprotect;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
			host = RegionSettings.getMysqlUrl();
			username = RegionSettings.getMysqlUser();
			database = RegionSettings.getMysqlDb();
			password = RegionSettings.getMysqlPass();
			port = RegionSettings.getMysqlPort();
			
	        try {    
	            openConnection(); 
	            Logger.Log(Level.INFO, "Database connection was succesful");
	            createDefaultTables();
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
	
	private void createDefaultTables()
	{
		try { 
			Statement stmt = connection.createStatement();
			createRegionTable(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createRegionTable(Statement stmt)
	{
		try {
			stmt.execute("CREATE TABLE IF NOT EXISTS cuboids (cuboid_id INT(6) PRIMARY KEY NOT NULL,"
					+ " owner VARCHAR(60) NOT NULL,"
					+ " x INT(6) NOT NULL,"
					+ " y INT (6) NOT NULL,"
					+ " z INT(6) NOT NULL,"
					+ " world VARCHAR(60) NOT NULL,"
					+ " name VARCHAR(60) NOT NULL,"
					+ " material VARCHAR(30) NOT NULL,"
					+ " data INT(6) NOT NULL,"
					+ " radius INT(6) NOT NULL"
					+ ")");
			createFlagTable(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createFlagTable(Statement stmt)
	{
		try
		{
			stmt.execute("CREATE TABLE IF NOT EXISTS flags (flag_id INT(6) PRIMARY KEY,"
					+ "flag_name VARCHAR(30))");
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM flags");
			if(!rs.next())
			{
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (1, 'pvp')");
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (2, 'entry_message')");
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (3, 'exit_message')");
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (4, 'tnt')");
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (5, 'break')");
				stmt.execute("INSERT INTO flags (flag_id, flag_name) VALUES (6, 'place')");
			}
			createRegionFlagTable(stmt);
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void createRegionFlagTable(Statement stmt)
	{
		try
		{
			stmt.execute("CREATE TABLE IF NOT EXISTS cuboid_flags (cuboid_id INT(6),"
					+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id),"
					+ " flag_id INT(6),"
					+ " FOREIGN KEY (flag_id) REFERENCES flags(flag_id),"
					+ " PRIMARY KEY(cuboid_id, flag_id),"
					+ " value VARCHAR(60),"
					+ " enabled TINYINT(1))");
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		createBuildersTable(stmt);
	}
	
	private void createBuildersTable(Statement stmt)
	{
		try
		{
			stmt.execute("CREATE TABLE IF NOT EXISTS players (builder_id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
					+ " uuid VARCHAR(40),"
					+ " cuboid_id INT(6),"
					+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id))");
			stmt.close();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadRegions()
	{
		if(RegionSettings.getStorageType().equals("mysql"))
		{
			try
			{
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM cuboids");
				while(rs.next())
				{
					int id = rs.getInt(1);
					Logger.Log(Level.INFO, "Loading cuboid " + id);
					UUID owner = UUID.fromString(rs.getString(2));
					int x = rs.getInt(3);
					int y = rs.getInt(4);
					int z = rs.getInt(5);
					World w = Bukkit.getWorld(UUID.fromString(rs.getString(6)));
					
					String name = rs.getString(7);
					Material mat = Material.valueOf(rs.getString(8));
					int data = rs.getInt(9);
					int radius = rs.getInt(10);
					
					ProtectionStone settings = new ProtectionStone(name, mat, data, radius);
					Statement flagStatement = connection.createStatement();
					for(int i=1;i<6;i++)
					{
						ResultSet flag = flagStatement.executeQuery("SELECT * FROM cuboid_flags WHERE (cuboid_id,flag_id)=(" + id + "," + i + ")");
						if(flag.next())
						{
							switch(i)
							{
								case 1:
									break;
								case 2:
									settings.setWelcomeMessage(flag.getString(3));
									break;
								case 3:
									settings.setLeaveMessage(flag.getString(3));
									break;
								case 4:
									break;
								case 5:
									settings.setAllowsBreak(flag.getString(3).equals("true"));
									break;
								case 6:
									settings.setAllowsPlace(flag.getString(3).equals("true"));
									break;
							}
						}
					}
					
					BiomeProtect.defineRegion(settings, owner, new Location(w, x, y, z));

				}
			} catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Check if a region exists in the database
	 * @param id
	 * @return
	 */
	public boolean hasRegion(int id)
	{
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM cuboids WHERE cuboid_id=" + id);
			return rs.next();
		} catch(SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			if(stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return false;
	}
	
	/**
	 * Saves a region to the database
	 * @param region
	 */
	public void saveRegion(ProtectedRegion region)
	{
		UUID owner = region.getOwner();
		int x = region.getCenter().getBlockX();
		int y = region.getCenter().getBlockY();
		int z = region.getCenter().getBlockZ();
		UUID world = region.getSmallerPoint().getWorld().getUID();
		int id = region.getId();
		
		String name = region.getName();
		String mat = region.getMaterial().name();
		int data = region.getData();
		int radius = region.getRadius();
		try
		{
			Statement stmt = connection.createStatement();
			stmt.execute("REPLACE INTO cuboids (cuboid_id,owner,x,y,z,world,name,material,data,radius) "
					+ "VALUES(" + id + ",'" + owner.toString() + "'," + x + "," + y + "," + z
					+ ",'" + world.toString() + "','" + name + "','" + mat + "'," + data + "," + radius + ")");
			
			if(region.hasWelcomeMessage())
			{
				stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES(" + region.getId()
				+ ",2,'" + region.getWelcomeMessage() + "',1)");
			}
			
			if(region.hasLeaveMessage())
			{
				stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES(" + region.getId()
				+ ",3,'" + region.getLeaveMessage() + "',1)");
			}
			
			stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES(" + region.getId()
			+ ",5,'" + region.allowsBreak() + "',1)");
			
			stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES(" + region.getId()
			+ ",6,'" + region.allowsPlace() + "',1)");
			//stmt.execute("REPLACE INTO cuboid_flags(flag_id,value,enabled) VALUES("")
			stmt.close();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}		
	}
	
	/**
	 * Remove a region from the database
	 * @param id
	 */
	public void removeRegion(int id)
	{
		try
		{
			Statement stmt = connection.createStatement();
			// Remove the flags
			for(int i=1;i<7;i++)
			{
				stmt.execute("DELETE FROM cuboid_flags WHERE (cuboid_id,flag_id) = (" + id + "," + i + ")");
			}
			
			stmt.execute("DELETE FROM cuboids WHERE cuboid_id=" + id);
			stmt.close();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
}
