package com.hotmail.steven.biomeprotect.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.menu.ActionButton;
import com.hotmail.steven.biomeprotect.menu.BiomeMenu;
import com.hotmail.steven.biomeprotect.menu.InputButton;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.util.StringUtil;

public class RegionProtectionListener extends BiomeProtectListener {

	private BiomeMenu menu;
	private HashMap<UUID, ProtectedRegion> editingRegions;
	
	public RegionProtectionListener(BiomeProtect plugin) {
		super(plugin);
		
		editingRegions = new HashMap<UUID, ProtectedRegion>();
		menu = new BiomeMenu(plugin, "m1", "&c&lManage protection", 9);	

		// Show and hide the area of a region
		ActionButton btn1 = new ActionButton("0", 0, new ItemStack(Material.GLASS), "&aShow area", "&9Shows the area of this region")
		{
			@Override
			public void onClick(Player player)
			{
				player.closeInventory();
				editingRegions.get(player.getUniqueId()).show();
			}
		};
		
		String welcome = region.hasFlag("welcome-message") ? ((StringFlag)region.getFlag("welcome-message")).getValue() : "";
		InputButton btn2 = new InputButton("0", 1, new ItemStack(Material.PAPER), "&aWelcome message", welcome)
		{
			@Override
			public void onInput(Player player, String message)
			{
				StringFlag strFlag = new StringFlag("welcome-message");
				strFlag.setValue(message);
				menu.open(player, editingRegions.get(player.getUniqueId()));
			}
		};
		menu.addButtons(btn1, btn2);
	}

	@EventHandler
	public void onBlockClick(PlayerInteractEvent evt)
	{
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			// Check if the player is clicking a protected region
			ProtectedRegion region = getPlugin().getRegionContainer().queryRegion(evt.getClickedBlock().getLocation());
			if(region != null)
			{
				menu.open(evt.getPlayer(), region);
				editingRegions.put(evt.getPlayer().getUniqueId(), region);
			}
		}
	}
	
}
