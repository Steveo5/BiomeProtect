package com.hotmail.steven.util;

import java.util.HashSet;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.hotmail.steven.biomeprotect.Logger;

public class LocationUtil {

    /**
     * Checks if a location exists between a small
     * and larger point
     * @param smaller
     * @param larger
     * @param search
     * @return
     */
    public static boolean boxContains(Location smaller, Location larger, Location search)
    {
    	if(search.getBlockX() > smaller.getBlockX() && search.getBlockY() > smaller.getBlockY() && search.getBlockZ() > smaller.getBlockZ())
    	{
    		if(search.getBlockX() < larger.getBlockX() && search.getBlockY() < larger.getBlockY() && search.getBlockZ() < larger.getBlockZ())
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Find all chunks in an area
     * @param smaller
     * @param larger
     */
    public static HashSet<Chunk> getAllChunks(Location p1, Location p2)
    {
    	HashSet<Chunk> chunks = new HashSet<Chunk>();
    	
    	World w = p1.getWorld();

        for (int x = p1.getBlockX(); x <= p2.getBlockX(); x+=5) {
       
            for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); z+=5) {
            	
            	Chunk add = w.getBlockAt(x, 64, z).getChunk();
                chunks.add(add);
            }
           
        }
        
        return chunks;
    }
	
}
