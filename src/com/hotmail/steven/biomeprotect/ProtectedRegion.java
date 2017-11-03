package com.hotmail.steven.biomeprotect;

import java.util.UUID;

import org.bukkit.Location;

public class ProtectedRegion {

	private Location point1, point2;
	private UUID owner;
	
	public ProtectedRegion(UUID owner, Location point1, Location point2)
	{
		this.owner = owner;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public Location getSmallerPoint()
	{
		return point1;
	}
	
	public Location getLargerPoint()
	{
		return point2;
	}
	
}
