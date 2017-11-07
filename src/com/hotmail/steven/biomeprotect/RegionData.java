package com.hotmail.steven.biomeprotect;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

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
			stmt.execute("CREATE TABLE IF NOT EXISTS cuboids (cuboid_id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
					+ " owner VARCHAR(40) NOT NULL,"
					+ " x INT(6) NOT NULL,"
					+ " y INT (6) NOT NULL,"
					+ " z INT(6) NOT NULL,"
					+ " minX INT(6) NOT NULL,"
					+ " minY INT(6) NOT NULL,"
					+ " minZ INT(6) NOT NULL,"
					+ " maxX INT(6) NOT NULL,"
					+ " maxY INT(6) NOT NULL,"
					+ " maxZ INT(6) NOT NULL,"
					+ " world VARCHAR(20) NOT NULL)");
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
			stmt.execute("CREATE TABLE IF NOT EXISTS flags (flag_id INT(6) UNSIGNED PRIMARY KEY,"
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
			stmt.execute("CREATE TABLE IF NOT EXISTS cuboid_flags (flag_id INT(6) UNSIGNED PRIMARY KEY,"
					+ " FOREIGN KEY (flag_id) REFERENCES flags(flag_id),"
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
					+ " cuboid_id INT(6) UNSIGNED,"
					+ " FOREIGN KEY (cuboid_id) REFERENCES cuboids(cuboid_id))");
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void saveRegion(ProtectedRegion region)
	{
		
	}
	
}
