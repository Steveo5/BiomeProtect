package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.hotmail.steven.util.LocationUtil;

public class ProtectedRegion extends ProtectionStone {

	private Location center, point1, point2;
	private UUID owner;
	private List<Block> shownBlocks;
	private HashSet<Chunk> chunks;
	private UUID id;
	private HashSet<Player> playersInRegion;
	private HashSet<UUID> members;

	public ProtectedRegion(ProtectionStone base, UUID owner, Location center)
	{
		super(base.getName(), base.getMaterial(), base.getData(), base.getRadius());
		shownBlocks = new ArrayList<Block>();
		playersInRegion = new HashSet<Player>();
		members = new HashSet<UUID>();
		this.owner = owner;
		this.center = center;
		Location p1 = this.center.clone().subtract(base.getRadius(), base.getCustomHeight() == -1 ? base.getRadius() : base.getCustomHeight(), base.getRadius());
		Location p2 = this.center.clone().add(base.getRadius(), base.getCustomHeight() == -1 ? base.getRadius() : base.getCustomHeight(), base.getRadius());
		// Set the smaller and larger points
		int p1X = p1.getBlockX() < p2.getBlockX() ? p1.getBlockX() : p2.getBlockX();
		int p1Y = p1.getBlockY() < p2.getBlockY() ? p1.getBlockY() : p2.getBlockY();
		int p1Z = p1.getBlockZ() < p2.getBlockZ() ? p1.getBlockZ() : p2.getBlockZ();
		
		int p2X = p1.getBlockX() > p2.getBlockX() ? p1.getBlockX() : p2.getBlockX();
		int p2Y = p1.getBlockY() > p2.getBlockY() ? p1.getBlockY() : p2.getBlockY();
		int p2Z = p1.getBlockZ() > p2.getBlockZ() ? p1.getBlockZ() : p2.getBlockZ();
		
		point1 = new Location(p1.getWorld(), p1X, p1Y, p1Z);
		point2 = new Location(p2.getWorld(), p2X, p2Y, p2Z);
		// Generate id based on 3d to 1d
		// Copy the flags over
		if(base.hasWelcomeMessage()) setWelcomeMessage(base.getWelcomeMessage());
		if(base.hasLeaveMessage()) setLeaveMessage(base.getLeaveMessage());
		setAllowsBreak(base.allowsBreak());
		setAllowsPlace(base.allowsPlace());
		if(base.getCustomHeight() != -1) this.setCustomHeight(base.getCustomHeight());
		setAllowsPvp(base.allowsPvp());
		setAllowsTnt(base.allowsTnt());
		// Get existing chunks this region resides
		chunks = LocationUtil.getAllChunks(point1, point2);
		Logger.Log(Level.INFO, Bukkit.getOfflinePlayer(owner).getName() + " placed a field at " + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());
		Logger.Log(Level.INFO, "Region intercepts " + chunks.size() + " chunks");
		Logger.Log(Level.INFO, "Min location " + point1.getBlockX() + " " + point1.getBlockY() + " " + point1.getBlockZ());
		Logger.Log(Level.INFO, "Max location " + point2.getBlockX() + " " + point2.getBlockY() + " " + point2.getBlockZ());
	}
	
	protected void setUUID(UUID id)
	{
		this.id = id;
	}
	
	/**
	 * Get all the chunks this region exists in
	 * @return
	 */
	public HashSet<Chunk> getExistingChunks()
	{
		return chunks;
	}
	
	/**
	 * Get all the players that are currently in this region
	 * @return
	 */
	protected HashSet<Player> getPlayers()
	{
		return playersInRegion;
	}
	
	protected boolean isPlayerPersistantInside(Player player)
	{
		return getPlayers().contains(player);
	}
	
	protected void addPlayerPersistant(Player player)
	{
		playersInRegion.add(player);
	}
	
	protected void removePlayerPersistant(Player player)
	{
		playersInRegion.remove(player);
	}
	
	/**
	 * Check if a player is inside this region
	 * @param uuid
	 * @return
	 */
	public boolean isPlayerInside(Player player)
	{
		return isLocationInside(player.getLocation());
	}
	
	public boolean isLocationInside(Location loc)
	{
		return LocationUtil.boxContains(getSmallerPoint(), getLargerPoint(), loc);
	}
	
	/**
	 * Checks if a protected region intercepts this region
	 * @param compare
	 * @return
	 */
	public boolean intercepts(ProtectedRegion compare)
	{
		compare.getSmallerPoint().toVector().
		return false;
	}
	
	public UUID getId()
	{
		return id;
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
	
	public UUID getOwner()
	{
		return owner;
	}
	
	public void remove()
	{
		BiomeProtect.removeProtectedRegion(this);
		if(BiomeProtect.getRegionCache().isCached(getId()))
			BiomeProtect.getRegionCache().getCache().remove(getId());
	}
	
	public void show()
	{
		int radius = this.getRadius();
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
	
	public boolean isOwner(UUID uuid)
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
				if(!region.hasPermission(uuid))
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
				if(!region.hasPermission(uuid))
				{
					return false;
				}
			}
		}	
		
		return true;		
	}
	
	/**
	 * Get all the members who have build, pvp etc permission in the region
	 * @return
	 */
	public HashSet<UUID> getMembers()
	{
		return members;
	}
	
	/**
	 * Add a member to this region
	 * @param member
	 */
	public void addMember(UUID member)
	{
		members.add(member);
	}
	
	/**
	 * Check if the region has a member
	 * @param member
	 * @return
	 */
	public boolean hasMember(UUID member)
	{
		return members.contains(member);
	}
	
	public void removeMember(UUID member)
	{
		members.remove(member);
	}
	
	@Override
	public String toString()
	{
		String minString = "Min x " + getSmallerPoint().getBlockX() + " Min y " + getSmallerPoint().getBlockY() + " Min z " + getSmallerPoint().getBlockZ();
		String maxString = "Max x " + getLargerPoint().getBlockX() + " Max y " + getLargerPoint().getBlockY() + " Max z " + getLargerPoint().getBlockZ();
	
		return minString + "\n" + maxString;
	}
	
}
