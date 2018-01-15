package com.hotmail.steven.biomeprotect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.biomeprotect.visualize.Visualization;

public class RegionVisualizer {

	/**
	 * Blocks waiting to be visualized
	 */
	private Queue<Visualization> visuals;
	private Queue<Block> blockQueue;
	private Queue<Block> removeQueue;
	
	public RegionVisualizer(BiomeProtect plugin)
	{
		visuals = new LinkedList<Visualization>();
		blockQueue = new LinkedList<Block>();
		removeQueue = new LinkedList<Block>();
		
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
                		System.out.println("Removing viauzliation");
                		removeQueue.addAll(visualization.getBlocks());
                		itr.remove();
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
				Block top = blockQueue.poll();
				if(top.getType() == Material.AIR)
				{
					top.setType(Material.GLASS);
				}		
			}
			// Same but for remove queue
			if(!removeQueue.isEmpty())
			{
				// Place a piece of glass at the spot
				Block top = removeQueue.poll();
				top.setType(Material.AIR);
			}
		}
	}
	
	/**
	 * Get blocks waiting to be visualized
	 * @return
	 */
	public Queue<Block> getQueue()
	{
		return blockQueue;
	}
	
	/**
	 * Get blocks waiting to be removed from the visualized blocks
	 * @return
	 */
	public Queue<Block> getRemoveQueue()
	{
		return removeQueue;
	}
}
