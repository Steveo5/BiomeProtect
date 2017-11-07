package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class ProtectedRegion {

	private Location center, point1, point2;
	private int radius, height;
	private UUID owner;
	private List<Block> shownBlocks;
	
	public ProtectedRegion(UUID owner, Location center, int height, int radius)
	{
		shownBlocks = new ArrayList<Block>();
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
	
	public void show()
	{
		int p1X = point1.getBlockX() < point2.getBlockX() ? point1.getBlockX() : point2.getBlockX();
		int p1Y = point1.getBlockY() < point2.getBlockY() ? point1.getBlockY() : point2.getBlockY();
		int p1Z = point1.getBlockZ() < point2.getBlockZ() ? point1.getBlockZ() : point2.getBlockZ();
		
		int p2X = point1.getBlockX() > point2.getBlockX() ? point1.getBlockX() : point2.getBlockX();
		int p2Y = point1.getBlockY() > point2.getBlockY() ? point1.getBlockY() : point2.getBlockY();
		int p2Z = point1.getBlockZ() > point2.getBlockZ() ? point1.getBlockZ() : point2.getBlockZ();
		int diameter = radius * 2;
		// Get all blocks in the square
		for(int i=0;i<radius * 2;i++)
		{
			Block bottom1X = point1.getWorld().getBlockAt(p1X + i,  p1Y, p1Z);
			shownBlocks.add(bottom1X);
			
			Block top1X = point1.getWorld().getBlockAt(p1X + i,  p1Y + diameter, p1Z);
			shownBlocks.add(top1X);
			
			Block firstY = point1.getWorld().getBlockAt(p1X, p1Y + i, p1Z);
			shownBlocks.add(firstY);
			
			Block bottom1Z = point1.getWorld().getBlockAt(p1X, p1Y, p1Z + i);
			shownBlocks.add(bottom1Z);
			
			Block top1Z = point1.getWorld().getBlockAt(p1X, p1Y + diameter, p1Z + i);
			shownBlocks.add(top1Z);
			
			Block top2X = point1.getWorld().getBlockAt(p2X - i,  p2Y, p2Z);
			shownBlocks.add(top2X);
			
			Block bottom2X = point1.getWorld().getBlockAt(p2X - i,  p2Y - diameter, p2Z);
			shownBlocks.add(bottom2X);
			
			Block thirdY = point1.getWorld().getBlockAt(p2X, p2Y - i, p2Z);
			shownBlocks.add(thirdY);
			
			Block top2Z = point1.getWorld().getBlockAt(p2X, p2Y, p2Z - i);
			shownBlocks.add(top2Z);
			
			Block bottom2Z = point1.getWorld().getBlockAt(p2X, p2Y - diameter, p2Z - i);
			shownBlocks.add(bottom2Z);
			
			Block secondY = point1.getWorld().getBlockAt(p1X + diameter, p2Y - i, p1Z);
			shownBlocks.add(secondY);
			
			Block fourthY = point1.getWorld().getBlockAt(p2X - diameter, p2Y - i, p2Z);
			shownBlocks.add(fourthY);
		}
		
		for(Block b : shownBlocks)
		{
			if(b.getType() == Material.AIR)
			{
				b.setType(Material.GLASS);
			}
		}
		
		new BlockCleanupTask(shownBlocks).runTaskLater(BiomeProtect.instance(), 20L * 5);
	}
	
	/**
	 * Checks if a uuid has access to this region
	 * @param uuid
	 * @return
	 */
	public boolean hasPermission(UUID uuid)
	{
		return uuid.equals(owner);
	}
	
	/**
	 * Checks if a user can break blocks at the location
	 * @param uuid
	 * @param loc
	 * @return
	 */
	public static boolean hasBreakPermission(UUID uuid, Location loc)
	{
		List<ProtectedRegion> foundRegions = BiomeProtect.findRegions(loc);
		
		if(foundRegions.size() > 0)
		{
			for(ProtectedRegion region : foundRegions)
			{
				// Check ownership of the region
				if(!region.getOwner().equals(uuid))
				{
					return false;
				}
			}
		}	
		
		return true;	
	}
	
	public static boolean hasPlacePermission(UUID uuid, Location loc)
	{
		List<ProtectedRegion> foundRegions = BiomeProtect.findRegions(loc);
		
		if(foundRegions.size() > 0)
		{
			for(ProtectedRegion region : foundRegions)
			{
				// Check ownership of the region
				if(!region.getOwner().equals(uuid))
				{
					return false;
				}
			}
		}	
		
		return true;		
	}
	
	@Override
	public String toString()
	{
		String minString = "Min x " + getSmallerPoint().getBlockX() + " Min y " + getSmallerPoint().getBlockY() + " Min z " + getSmallerPoint().getBlockZ();
		String maxString = "Max x " + getLargerPoint().getBlockX() + " Max y " + getLargerPoint().getBlockY() + " Max z " + getLargerPoint().getBlockZ();
	
		return minString + "\n" + maxString;
	}
	
}
