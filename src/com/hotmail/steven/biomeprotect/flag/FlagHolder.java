package com.hotmail.steven.biomeprotect.flag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class FlagHolder extends ArrayList<RegionFlag<?>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Hold all of the available flags able to be used
	 */
	public FlagHolder()
	{
		/**
		 * Add all the default flags to our loaded list.
		 * 
		 * Here is where you could add your own custom flag types
		 * by extending RegionFlag, StringFlag etc
		 */
		add(new BooleanFlag("pvp"));
		add(new BooleanFlag("tnt"));
		add(new BooleanFlag("break"));
		add(new BooleanFlag("place"));
		
		add(new StringFlag("entry-message"));
		add(new StringFlag("leave-message"));
	}
	
	/**
	 * Get a flag from the loaded list of flags.
	 * Name type doesn't have to be exact case
	 * @param name
	 * @return a null value if no flag is found
	 */
	public RegionFlag<?> get(String name)
	{
		for(RegionFlag<?> flag : this)
		{
			if(flag.getName().equalsIgnoreCase(name))
			{
				return flag;
			}
		}
		
		return null;
	}
	
	/**
	 * List the names of all the loaded flags. Returns a
	 * generic array list
	 * @return
	 */
	public Collection<String> getNames()
	{
		List<String> strList = new ArrayList<String>();
		// Loop the known flags
		for(RegionFlag<?> flag : this)
		{
			strList.add(flag.getName());
		}
		return strList;
	}
	
}
