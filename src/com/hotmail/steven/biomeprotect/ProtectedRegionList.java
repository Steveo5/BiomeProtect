package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;

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
    
    /**
     * Get all protected regions intercepting a block
     * @param loc
     * @return
     */
    public List<ProtectedRegion> intercepts(Location loc)
    {
    	List<ProtectedRegion> intercepting = new ArrayList<ProtectedRegion>();
    	
    	// Brute force for now

    	for(int i=0;i<elements.length;i++)
    	{
    		if(elements[i] == null || !(elements[i] instanceof ProtectedRegion)) continue;
    		ProtectedRegion region = (ProtectedRegion)elements[i];
    		System.out.println("Region  minx " + region.getSmallerPoint().getBlockX() + " miny " + region.getSmallerPoint().getBlockY() + " minz " + region.getSmallerPoint().getBlockZ() 
    				+ " maxx " + region.getLargerPoint().getBlockX() + " maxy " + region.getLargerPoint().getBlockY() + " maxz " + region.getLargerPoint().getBlockZ());
    		System.out.println("Dist min " + region.getSmallerPoint().distance(loc) + " max " +  region.getLargerPoint().distance(loc));
    		if(region.getSmallerPoint().distance(loc) < 20 && region.getLargerPoint().distance(loc) < 20)
    		{
    			intercepting.add(region);
    		}
    	}
    	return intercepting;
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