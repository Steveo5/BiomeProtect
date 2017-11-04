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
	 * If using mysql, the username
	 * @return
	 */
	public static String getMysqlUser()
	{
		return "steve";
	}
	
	/**
	 * If using mysql, thhe password
	 * @return
	 */
	public static String getMysqlPass()
	{
		return "C0nnect.2u";
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
