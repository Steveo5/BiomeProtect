package com.hotmail.steven.biomeprotect;

import java.util.UUID;

import org.bukkit.Location;

public class ProtectedRegion {

	private Location center, point1, point2;
	private int radius, height;
	private UUID owner;
	
	public ProtectedRegion(UUID owner, Location center, int height, int radius)
	{
		this.owner = owner;
		this.center = center;
		this.radius = radius;
		point1 = this.center.clone().subtract(radius, height, radius);
		point2 = this.center.clone().add(radius, height, radius);
	}
	
	public Location getSmallerPoint()
	{
		return point1;
	}
	
	public Location getLargerPoint()
	{
		return point2;
	}
	
	public Location getCenter()
	{
		return center;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public  int getRadius()
	{
		return radius;
	}
	
	public UUID getOwner()
	{
		return owner;
	}
	
	public void remove()
	{
		BiomeProtect.removeProtectedRegion(this);
	}
	
}
