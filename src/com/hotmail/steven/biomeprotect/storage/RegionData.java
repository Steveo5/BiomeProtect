package com.hotmail.steven.biomeprotect.storage;

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

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.biomeprotect.region.RegionCreator;
import com.mysql.jdbc.Connection;

public class RegionData {
	
	// Hold our connection
	private DataConnection connection = null;
	private BiomeProtect plugin;
	
	public RegionData(BiomeProtect plugin)
	{
		if(plugin.getRegionConfig().getStorageType().equals("mysql"))
		{
			String host = plugin.getRegionConfig().getMysqlUrl();
			String username = plugin.getRegionConfig().getMysqlUser();
			String database = plugin.getRegionConfig().getMysqlDb();
			String password = plugin.getRegionConfig().getMysqlPass();
			int port = plugin.getRegionConfig().getMysqlPort();
			
			connection = new MysqlConnection(plugin, username, password, host, database, port);
		}
		
		// Load all regions
		if(connection != null)
		{
			connection.loadRegions();
		}
	}
	
	/**
	 * Get the physical connection to the data. Common method include
	 * saving, loading and removing regions
	 * @return instance of DataConnection
	 */
	public DataConnection getConnection()
	{
		return connection;
	}
	
	/*
	public void loadRegions()
	{
		int regionsLoaded = 0;
		if(RegionConfig.getStorageType().equals("mysql"))
		{
			try
			{
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM cuboids");
				while(rs.next())
				{
					UUID id = UUID.fromString(rs.getString(1));
					Logger.Log(Level.INFO, "Loading cuboid " + id);
					UUID owner = UUID.fromString(rs.getString(2));
					int x = rs.getInt(3);
					int y = rs.getInt(4);
					int z = rs.getInt(5);
					World w = Bukkit.getWorld(UUID.fromString(rs.getString(6)));
					if(w == null)
					{
						Logger.Log(Level.WARNING, "Failed to load cuboid " + id + " as no world was found");
						continue;
					}
					String name = rs.getString(7);
					Material mat = Material.valueOf(rs.getString(8));
					int data = rs.getInt(9);
					int radius = rs.getInt(10);
					
					RegionCreator settings = new RegionCreator(name, mat, data, radius);
					Statement flagStatement = connection.createStatement();
					for(int i=1;i<6;i++)
					{
						ResultSet flag = flagStatement.executeQuery("SELECT * FROM cuboid_flags WHERE (cuboid_id,flag_id)=('" + id.toString() + "'," + i + ")");
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
					
					ProtectedRegion region = BiomeProtect.defineRegion(settings, owner, new Location(w, x, y, z));
					region.setUUID(id);
					regionsLoaded++;

				}
			} catch(SQLException e)
			{
				e.printStackTrace();
			}
		}
		Logger.Log(Level.INFO, regionsLoaded + " regions have been loaded");
	}
	
	/**
	 * Saves a region to the database
	 * @param region
	 * @param create - specify if this is the first time
	 * this protection stone is being entered into the db
	 
	public void saveRegion(ProtectedRegion region, boolean create)
	{
		UUID owner = region.getOwner();
		int x = region.getCenter().getBlockX();
		int y = region.getCenter().getBlockY();
		int z = region.getCenter().getBlockZ();
		UUID world = region.getWorld().getUID();
		UUID id = region.getId();
	}
	*/
		/*
		String name = region.getName();
		String mat = region.getMaterial().name();
		int data = region.getData();
		int radius = region.getRadius();
		try
		{
			Statement stmt = connection.createStatement();
			System.out.println("Inserting region data for " + region.getId());
			if(create)
			{
				stmt.execute("REPLACE INTO cuboids (cuboid_id,owner,x,y,z,world,name,material,data,radius) "
						+ "VALUES('" + id.toString() + "','" + owner.toString() + "'," + x + "," + y + "," + z
						+ ",'" + world.toString() + "','" + name + "','" + mat + "'," + data + "," + radius + ")");
			} else
			{
				//TODO update code
			}
			
			// Flags don't break the database rules, insert or replace them anyway
			if(region.hasWelcomeMessage())
			{
				stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES('" + region.getId()
				+ "',2,'" + region.getWelcomeMessage() + "',1)");
			}
			
			if(region.hasLeaveMessage())
			{
				stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES('" + region.getId()
				+ "',3,'" + region.getLeaveMessage() + "',1)");
			}
			
			stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES('" + region.getId()
			+ "',5,'" + region.allowsBreak() + "',1)");
			
			stmt.execute("REPLACE INTO cuboid_flags(cuboid_id,flag_id,value,enabled) VALUES('" + region.getId()
			+ "',6,'" + region.allowsPlace() + "',1)");
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
	//public void removeRegion(UUID id)
	//{
		/*
		try
		{
			Statement stmt = connection.createStatement();
			// Remove the flags
			for(int i=1;i<7;i++)
			{
				stmt.execute("DELETE FROM cuboid_flags WHERE (cuboid_id,flag_id) = ('" + id.toString() + "'," + i + ")");
			}
			
			stmt.execute("DELETE FROM cuboids WHERE cuboid_id='" + id + "'");
			stmt.close();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		*/
	//}
	
}
