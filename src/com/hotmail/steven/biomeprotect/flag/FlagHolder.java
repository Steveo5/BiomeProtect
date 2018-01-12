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
		String[] states = {"allow", "deny", "whitelist"};
		/**
		 * Add all the default flags to our loaded list.
		 * 
		 * Here is where you could add your own custom flag types
		 * by extending RegionFlag, StringFlag etc
		 */
		add(new StateFlag("pvp", states));
		add(new StateFlag("tnt", states));
		add(new StateFlag("break", states));
		add(new StateFlag("place", states));
		
		add(new StringFlag("welcome-message"));
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
		System.out.println("Getting flag " + name);
		for(RegionFlag<?> flag : this)
		{
			System.out.println("Flag " + flag.getName());
			if(flag.getName().equalsIgnoreCase(name))
			{
				System.out.println("Returning flag " + name);
				if(flag instanceof BooleanFlag) return new BooleanFlag(name);
				if(flag instanceof StateFlag) return new StateFlag(name);
				if(flag instanceof StringFlag) return new StringFlag(name);
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
