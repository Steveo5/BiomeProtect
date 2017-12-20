package com.hotmail.steven.biomeprotect.region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.BlockCleanupTask;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.flag.RegionFlag;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.menubuilder.Button;
import com.hotmail.steven.biomeprotect.menubuilder.ButtonListener;
import com.hotmail.steven.biomeprotect.menubuilder.InputListener;
import com.hotmail.steven.biomeprotect.menubuilder.MenuBuilder;
import com.hotmail.steven.util.ItemUtil;
import com.hotmail.steven.util.LocationUtil;

public class ProtectedRegion extends Region {

	private UUID owner;
	/**
	 * Hold the blocks shown when displaying the area
	 * in the physical world
	 */
	private List<Block> shownBlocks;
	private HashSet<Chunk> chunks;
	private UUID id;
	private HashSet<Player> playersInRegion;
	private HashSet<UUID> members;
	private HashSet<RegionFlag<?>> flags;
	private int priority;
	private String name;
	private MenuBuilder builder;

	protected ProtectedRegion(String name, UUID id, UUID owner, Location center, int radius, int height)
	{
		super(center, radius, height);
		this.name = name;
		shownBlocks = new ArrayList<Block>();
		playersInRegion = new HashSet<Player>();
		members = new HashSet<UUID>();
		this.owner = owner;
		this.id = id;
		// Get existing chunks this region resides
		chunks = LocationUtil.getAllChunks(getWorld(), getSmallerPoint(), getLargerPoint());
		Logger.Log(Level.INFO, Bukkit.getOfflinePlayer(owner).getName() + " placed a field at " + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());
		Logger.Log(Level.INFO, "Region intercepts " + chunks.size() + " chunks");
		
		flags = new HashSet<RegionFlag<?>>();
	}
	
	protected void setUUID(UUID id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
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
	 * Get all the flags this protected region has.
	 * Returns a hash set so only one of each type
	 * should be able to exist
	 * @return
	 */
	public HashSet<RegionFlag<?>> getFlags()
	{
		return flags;
	}
	
	/**
	 * Check if a flag exists based on the string name
	 * @param name
	 * @return
	 */
	public boolean hasFlag(String name)
	{
		for(RegionFlag<?> flag : flags)
		{
			if(flag.getName().equalsIgnoreCase(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * Add a new flag to this protected regions flag list
	 * @param flag
	 */
	public void setFlag(RegionFlag<?> flag)
	{
		flags.add(flag);
	}
	
	/**
	 * Get a region flag from its string name
	 * @param name
	 * @return
	 */
	public RegionFlag<?> getFlag(String name)
	{
		Iterator<RegionFlag<?>> itrFlag = flags.iterator();
		// Loop over the existing flags
		while(itrFlag.hasNext())
		{
			RegionFlag<?> next = itrFlag.next();
			// Remove the flag from this region if it exists
			if(next.getName().equalsIgnoreCase(name)) return next;
		}
		
		return null;
	}
	
	/**
	 * Remove a flag from this protected region, causing it to
	 * no longer have effect
	 * @param name
	 */
	public void removeFlag(String name)
	{
		Iterator<RegionFlag<?>> itrFlag = flags.iterator();
		// Loop over the existing flags
		while(itrFlag.hasNext())
		{
			RegionFlag<?> next = itrFlag.next();
			// Remove the flag from this region if it exists
			if(next.getName().equalsIgnoreCase(name)) itrFlag.remove();
		}
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
	
	public World getWorld()
	{
		return getCenter().getWorld();
	}
	
	/**
	 * Gets this regions priority
	 * @return
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * Set the priority for this region, if more then one region
	 * is touching/existing in this region then all priorities should be
	 * different.
	 * 
	 * Higher priorities will mean that block place checking, pvp flags etc
	 * will be prioritized in this region instead of the parent region
	 * @param priority
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
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
	
	public UUID getId()
	{
		return id;
	}
	
	public UUID getOwner()
	{
		return owner;
	}
	
	public void show()
	{
		int radius = getRadius();
		BlockVector point1 = getSmallerPoint();
		BlockVector point2 = getLargerPoint();
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
			Block bottom1X = getWorld().getBlockAt(p1X + i,  p1Y, p1Z);
			shownBlocks.add(bottom1X);
			
			Block top1X = getWorld().getBlockAt(p1X + i,  p1Y + diameter, p1Z);
			shownBlocks.add(top1X);
			
			Block firstY = getWorld().getBlockAt(p1X, p1Y + i, p1Z);
			shownBlocks.add(firstY);
			
			Block bottom1Z = getWorld().getBlockAt(p1X, p1Y, p1Z + i);
			shownBlocks.add(bottom1Z);
			
			Block top1Z = getWorld().getBlockAt(p1X, p1Y + diameter, p1Z + i);
			shownBlocks.add(top1Z);
			
			Block top2X = getWorld().getBlockAt(p2X - i,  p2Y, p2Z);
			shownBlocks.add(top2X);
			
			Block bottom2X = getWorld().getBlockAt(p2X - i,  p2Y - diameter, p2Z);
			shownBlocks.add(bottom2X);
			
			Block thirdY = getWorld().getBlockAt(p2X, p2Y - i, p2Z);
			shownBlocks.add(thirdY);
			
			Block top2Z = getWorld().getBlockAt(p2X, p2Y, p2Z - i);
			shownBlocks.add(top2Z);
			
			Block bottom2Z = getWorld().getBlockAt(p2X, p2Y - diameter, p2Z - i);
			shownBlocks.add(bottom2Z);
			
			Block secondY = getWorld().getBlockAt(p1X + diameter, p2Y - i, p1Z);
			shownBlocks.add(secondY);
			
			Block fourthY = getWorld().getBlockAt(p2X - diameter, p2Y - i, p2Z);
			shownBlocks.add(fourthY);
		}
		
		for(Block b : shownBlocks)
		{
			if(b.getType() == Material.AIR)
			{
				b.setType(Material.GLASS);
			}
		}
		
		new BlockCleanupTask(shownBlocks).runTaskLater(BiomeProtect.instance(), 20L * 40);
	}
	
	/**
	 * Show the editing menu for this protected region
	 * @param player
	 */
	public void showMenu(Player p)
	{
		if(builder == null)
		{
			builder = new MenuBuilder();
			builder.size(9).title("Manage protection");
			builder.button(0, new Button(0, ItemUtil.item(Material.GLASS, 1, "&aShow area")), new ButtonListener()
			{
				@Override
				public void onClick(Button button, Player player)
				{
					show();
				}
			});
			
			builder.button(1, new Button(1, ItemUtil.item(Material.PAPER, 1, "&7Entry message", "&3none")), new InputListener()
			{
				@Override
				public void onEnable(Button button, Player player)
				{
					if(hasFlag("welcome-message"))
					{
						StringFlag welcomeFlag = (StringFlag)getFlag("welcome-message");
						String welcomeMessage = welcomeFlag.getValue();
						button.lore(welcomeMessage);
						builder.update();
					}
				}
				
				@Override
				public void onInput(Player player, String message)
				{
					// Create our welcome flag and set the message value
					StringFlag welcomeFlag = new StringFlag("welcome-message");
					welcomeFlag.setValue(message);
					setFlag(welcomeFlag);
					player.sendMessage("Welcome message updated");
				}
				
				@Override
				public void onClick(Button button, Player player)
				{
					player.sendMessage("Enter a new welcome message:");
				}
			});
		}
		
		builder.show(p);
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
	 * Get all the members of this region
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
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ProtectedRegion)
		{
			ProtectedRegion region = (ProtectedRegion)obj;
			if(region.getId().equals(getId())) return true;
		}
		return false;
	}
	
}
