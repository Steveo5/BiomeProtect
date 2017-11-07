package com.hotmail.steven.biomeprotect;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockCleanupTask extends BukkitRunnable {

	private List<Block> toRemove;
	
	public BlockCleanupTask(List<Block> toRemove)
	{
		this.toRemove = toRemove;
	}
	
	@Override
	public void run() {
		
		for(Block b : toRemove)
		{
			b.setType(Material.AIR);
		}
		
		toRemove.clear();
		
	}

}
