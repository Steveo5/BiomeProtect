package com.hotmail.steven.biomeprotect.region;

import java.util.HashSet;
import java.util.logging.Level;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;

public class RegionSaveTask implements Runnable {

	private BiomeProtect plugin;
	private Long lastRun = System.currentTimeMillis();
	
	/**
	 * Initialize the region save task. Runs on a set timer
	 * saving the region data to the configured database
	 * @param plugin
	 */
	public RegionSaveTask(BiomeProtect plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		Logger.Log(Level.ALL, "Saving regions to database");
		HashSet<ProtectedRegion> regions = new HashSet<ProtectedRegion>(plugin.getRegionContainer().getRegions());
		regions.addAll(plugin.getRegionContainer().getCache().getAll());
		// Loop over each region in the container
		for(ProtectedRegion region : regions)
		{
			// Save the region
			plugin.getRegionData().getConnection().saveRegion(region);
		}
		lastRun = System.currentTimeMillis();
	}

}
