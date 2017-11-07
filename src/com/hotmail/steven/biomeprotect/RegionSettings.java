package com.hotmail.steven.biomeprotect;

public class RegionSettings {

	/**
	 * Get the database type, either flatfile, mysql
	 * @return
	 */
	public static String getStorageType()
	{
		return "mysql";
	}

	/**
	 * If using mysql, get the url of the server
	 */
	public static String getMysqlUrl()
	{
		return "localhost";
	}
	
	/**
	 * If using mysql, the username
	 * @return
	 */
	public static String getMysqlUser()
	{
		return "root";
	}
	
	/**
	 * If using mysql, thhe password
	 * @return
	 */
	public static String getMysqlPass()
	{
		return "";
	}
	
	public static int getMysqlPort()
	{
		return 3306;
	}
	
	public static String getMysqlDb()
	{
		return "regions";
	}
	
}
