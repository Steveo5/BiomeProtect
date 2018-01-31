package com.hotmail.steven.biomeprotect.visualize;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Visualization {

	private Set<Location> blocks;
	private long started;
	private int length;
	private boolean shown;
	
	/**
	 * Creates a new visualization of blocks for the allocated start time and length
	 * @param blocks
	 * @param started - time this visualizatin was initialized
	 * @param length of time in seconds
	 */
	public Visualization(Set<Block> blocks, long started, int length)
	{
		this.blocks = new HashSet<Location>();
		for(Block b : blocks)
		{
			this.blocks.add(b.getLocation());
		}
		this.started = started;
		this.length = length;
	}
	
	public boolean isVisible()
	{
		return shown;
	}
	
	public void setVisible(boolean shown)
	{
		this.shown = shown;
	}
	
	public Set<Location> getBlocks()
	{
		return blocks;
	}
	
	public long getStartTime()
	{
		return started;
	}
	
	public int getLength()
	{
		return length;
	}
	
}
