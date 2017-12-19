package com.hotmail.steven.biomeprotect.region;

import org.bukkit.Location;
import org.bukkit.util.BlockVector;

public class Region {

	private Location center;
	private BlockVector point2;
	private BlockVector point1;
	private int radius, height;
	
	public Region(Location center, int radius, int height)
	{
		this.radius = radius;
		this.height = height;
		this.center = center;
		Location p1 = this.center.clone().subtract(radius, height, radius);
		Location p2 = this.center.clone().add(radius, height, radius);
		// Set the smaller and larger points
		int p1X = p1.getBlockX() < p2.getBlockX() ? p1.getBlockX() : p2.getBlockX();
		int p1Y = p1.getBlockY() < p2.getBlockY() ? p1.getBlockY() : p2.getBlockY();
		int p1Z = p1.getBlockZ() < p2.getBlockZ() ? p1.getBlockZ() : p2.getBlockZ();
		
		int p2X = p1.getBlockX() > p2.getBlockX() ? p1.getBlockX() : p2.getBlockX();
		int p2Y = p1.getBlockY() > p2.getBlockY() ? p1.getBlockY() : p2.getBlockY();
		int p2Z = p1.getBlockZ() > p2.getBlockZ() ? p1.getBlockZ() : p2.getBlockZ();
		
		point1 = new BlockVector(p1X, p1Y, p1Z);
		point2 = new BlockVector(p2X, p2Y, p2Z);
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public BlockVector getSmallerPoint()
	{
		return point1;
	}
	
	public BlockVector getLargerPoint()
	{
		return point2;
	}
	
	public Location getCenter()
	{
		return center;
	}
	
	/**
	 * Checks if a region intercepts this region
	 * @param compare
	 * @return
	 */
	public boolean interceptBoundingBox(Region compare)
	{
        BlockVector rMaxPoint = compare.getLargerPoint();
        BlockVector min = getSmallerPoint();

        if (rMaxPoint.getBlockX() < min.getBlockX()) return false;
        if (rMaxPoint.getBlockY() < min.getBlockY()) return false;
        if (rMaxPoint.getBlockZ() < min.getBlockZ()) return false;

        BlockVector rMinPoint = compare.getSmallerPoint();
        BlockVector max = getLargerPoint();

        if (rMinPoint.getBlockX() > max.getBlockX()) return false;
        if (rMinPoint.getBlockY() > max.getBlockY()) return false;
        if (rMinPoint.getBlockZ() > max.getBlockZ()) return false;

        return true;
	}
	
}
