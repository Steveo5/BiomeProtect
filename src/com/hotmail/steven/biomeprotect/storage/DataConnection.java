package com.hotmail.steven.biomeprotect.storage;

import java.util.UUID;

import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

public interface DataConnection {
	
	/**
	 * Save a particular region to the database
	 * @param region
	 */
	void saveRegion(ProtectedRegion region);
	/**
	 * Loads all regions on server start into memory
	 */
	void loadRegions();
	/**
	 * Remove a particular region from the database
	 * @param id
	 */
	void removeRegion(UUID id);
	
}
