package com.hotmail.steven.biomeprotect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.biomeprotect.visualize.Visualization;
import com.hotmail.steven.util.StringUtil;

public class RegionVisualizer {

	/**
	 * Blocks waiting to be visualized
	 */
	private Queue<Visualization> visuals;
	// Queue to be shown
	private Queue<Location> blockQueue;
	// Queue to be hidden
	private Queue<Location> removeQueue;
	private BiomeProtect plugin;
	
	/**
	 * Initialize the RegionVisualizer
	 * @param plugin
	 * @param sessionRemoveQueue blocks left in queue that still need to be removed
	 */
	public RegionVisualizer(BiomeProtect plugin, LinkedList<Location> removeQueue, LinkedList<Location> blockQueue)
	{
		visuals = new LinkedList<Visualization>();
		this.blockQueue = blockQueue;
		this.removeQueue = removeQueue;
		this.plugin = plugin;
		
        // Create the task anonymously and schedule to run it once, after 20 ticks
        new BukkitRunnable() {
        
            @Override
            public void run() {
                proccessQueue(10);
                
                Iterator<Visualization> itr = visuals.iterator();
                while(itr.hasNext())
                {
                	Visualization visualization = itr.next();
                	// Visualization needs to be removed
                	if(System.currentTimeMillis() > (visualization.getStartTime() + (visualization.getLength() * 1000)))
                	{
                		if(visualization.isVisible())
                		{
	                		Logger.Log(Level.INFO, "Removing viauzliation");
	                		removeQueue.addAll(visualization.getBlocks());
	                		visualization.setVisible(false);
                		}
                	}
                }
            }
            
        }.runTaskTimer(plugin, 20, 20);
	}
	
	/**
	 * Gets all the blocks that would visualize a region
	 * @param region
	 * @return
	 */
	public Visualization getVisualization(ProtectedRegion region)
	{
		Set<Block> shownBlocks = new HashSet<Block>();
		int radius = region.getRadius();
		BlockVector point1 = region.getSmallerPoint();
		BlockVector point2 = region.getLargerPoint();
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
			Block bottom1X = region.getWorld().getBlockAt(p1X + i,  p1Y, p1Z);
			shownBlocks.add(bottom1X);
			
			Block top1X = region.getWorld().getBlockAt(p1X + i,  p1Y + diameter, p1Z);
			shownBlocks.add(top1X);
			
			Block firstY = region.getWorld().getBlockAt(p1X, p1Y + i, p1Z);
			shownBlocks.add(firstY);
			
			Block bottom1Z = region.getWorld().getBlockAt(p1X, p1Y, p1Z + i);
			shownBlocks.add(bottom1Z);
			
			Block top1Z = region.getWorld().getBlockAt(p1X, p1Y + diameter, p1Z + i);
			shownBlocks.add(top1Z);
			
			Block top2X = region.getWorld().getBlockAt(p2X - i,  p2Y, p2Z);
			shownBlocks.add(top2X);
			
			Block bottom2X = region.getWorld().getBlockAt(p2X - i,  p2Y - diameter, p2Z);
			shownBlocks.add(bottom2X);
			
			Block thirdY = region.getWorld().getBlockAt(p2X, p2Y - i, p2Z);
			shownBlocks.add(thirdY);
			
			Block top2Z = region.getWorld().getBlockAt(p2X, p2Y, p2Z - i);
			shownBlocks.add(top2Z);
			
			Block bottom2Z = region.getWorld().getBlockAt(p2X, p2Y - diameter, p2Z - i);
			shownBlocks.add(bottom2Z);
			
			Block secondY = region.getWorld().getBlockAt(p1X + diameter, p2Y - i, p1Z);
			shownBlocks.add(secondY);
			
			Block fourthY = region.getWorld().getBlockAt(p2X - diameter, p2Y - i, p2Z);
			shownBlocks.add(fourthY);
		}
		
		Set<Block> blocks = new HashSet<Block>(shownBlocks);
		for(Block b : blocks)
		{
			if(b.getType() != Material.AIR)
			{
				shownBlocks.remove(b);
			}
		}
		return new Visualization(shownBlocks, System.currentTimeMillis(), 40);
	}
	
	/**
	 * Shows a visualization in game
	 * @param visualization
	 */
	public void show(Visualization visualization)
	{
		blockQueue.addAll(visualization.getBlocks());
		visuals.add(visualization);
		visualization.setVisible(true);
	}
	
	/**
	 * Process a certain amount of blocks in the queue
	 * @param amount
	 */
	private void proccessQueue(int amount)
	{
		for(int i=0;i<amount;i++)
		{
			if(!blockQueue.isEmpty())
			{
				// Place a piece of glass at the spot
				Location top = blockQueue.peek();
				if(top != null && top.getChunk().isLoaded())
				{
					if(top.getBlock().getType() == Material.AIR)
					{
						top.getBlock().setType(Material.GLASS);
					}
					// Remove head
					blockQueue.poll();
				} else
				{
					continue;
				}
			}
		}
		
		for(int i=0;i<amount;i++)
		{		
			// Same but for remove queue
			if(!removeQueue.isEmpty())
			{
				Location top = removeQueue.peek();
				if(top != null && top.getChunk().isLoaded())
				{
					// Place a piece of air at the spot
					Block topBlock = removeQueue.poll().getBlock();
					topBlock.setType(Material.AIR);
				} else
				{
					continue;
				}
			}
		}
	}	
	
	/**
	 * Get blocks waiting to be visualized
	 * @return
	 */
	public Queue<Location> getQueue()
	{
		return blockQueue;
	}
	
	/**
	 * Get blocks waiting to be removed from the visualized blocks
	 * @return
	 */
	public Queue<Location> getRemoveQueue()
	{
		return removeQueue;
	}
	
	/**
	 * Essentially serializes a queue into a string queue
	 * @param queue
	 * @return
	 */
	public List<String> createSession(Queue<Location> queue)
	{
		// Save the current session in case of crash or restart
		List<String> strQueue = new ArrayList<String>();
		for(Location loc : queue)
		{
			// Essentially serialize
			String strLoc = loc.getWorld().getUID().toString() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
			strQueue.add(strLoc);
			System.out.println("adding " + strLoc);
		}
		return strQueue;
	}
}
