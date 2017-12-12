package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

import com.hotmail.steven.util.LocationUtil;

public class ProtectedRegionList<E> {
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;
    private Object elements[];

    public ProtectedRegionList() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public void add(E e) {
        if (size == elements.length) {
            ensureCapa();
        }
        elements[size++] = e;
    }
    
    public void remove(E e)
    {
    	for(int i=0;i<elements.length;i++)
    	{
    		if(elements[i] != null && elements[i].equals(e)) elements[i] = null;
    	}
    }
    
    /**
     * Get every region, outside of the cache which
     * intercepts usually uses
     * @return
     */
    public List<ProtectedRegion> getAll()
    {
    	List<ProtectedRegion> regions = new ArrayList<ProtectedRegion>();
    	
    	for(int i=0;i<elements.length;i++)
    	{
    		if(elements[i] == null || !(elements[i] instanceof ProtectedRegion)) continue;
    		regions.add((ProtectedRegion)elements[i]);
    	}
    	
    	return regions;
    }
    
    /**
     * Get all protected regions intercepting a block
     * @param loc
     * @return
     */
    public HashSet<ProtectedRegion> intercepts(Location loc)
    {
    	HashSet<ProtectedRegion> intercepting = new HashSet<ProtectedRegion>();
    	RegionCache cache = BiomeProtect.getRegionCache();
    	
    	// Brute force for now
    	for(ProtectedRegion region : cache.getCache().values())
    	{
    		// Do a distance calculation
    		// TODO Check height
    		if(LocationUtil.boxContains(region.getSmallerPoint(), region.getLargerPoint(), loc))
    		{

    			intercepting.add(region);
    		}
    	}
    	return intercepting;
    }
    
    /**
     * Gets the highest priority region in a list of regions
     * @param regionList
     * @return
     */
    public ProtectedRegion getHighestPriority(HashSet<ProtectedRegion> regionList)
    {
    	if(regionList.size() < 1) return null;
    	
    	ProtectedRegion highestPriority = null;
    	for(ProtectedRegion region : regionList)
    	{
    		if(highestPriority == null || region.getPriority() > highestPriority.getPriority()) highestPriority = region;
    	}
    	
    	return highestPriority;
    }
    

    private void ensureCapa() {
        int newSize = elements.length * 2;
        elements = Arrays.copyOf(elements, newSize);
    }

    @SuppressWarnings("unchecked")
    public E get(int i) {
        if (i>= size || i <0) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size " + i );
        }
        return (E) elements[i];
    }
}