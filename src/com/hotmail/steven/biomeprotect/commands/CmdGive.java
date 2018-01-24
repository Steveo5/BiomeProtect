package com.hotmail.steven.biomeprotect.commands;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.util.StringUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CmdGive extends BiomeProtectCommand {

	public CmdGive(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
	}
	
	@Override
	public boolean run(CommandSender sender, String name, String[] args)
	{
		if(args.length > 1)
		{
			if(getPlugin().getConfig().isConfigurationSection("protection-stones." + args[1]))
			{
				Player recieve = null;
				ItemStack item = null;
				
				ConfigurationSection section = getPlugin().getConfig().getConfigurationSection("protection-stones." + args[1]);
				// Create the item
				item = new ItemStack(Material.valueOf(section.getString("block").toUpperCase()));
				// Set the item meta if applicable
				if(section.isConfigurationSection("meta"))
				{
					ItemMeta im = item.getItemMeta();
					// Set the title
					if(section.isString("meta.title"))
					{
						im.setDisplayName(StringUtil.colorize(section.getString("meta.title")));
					}
					// Set the lore
					if(section.isString("meta.lore"))
					{
						im.setLore(Arrays.asList(StringUtil.colorize(section.getString("meta.lore")).split("\\|")));
					}
					// Update meta
					item.setItemMeta(im);
				}
				// Check if the player is giving another player
				if(args.length > 2)
				{
					recieve = Bukkit.getPlayer(args[2]);
					if(recieve == null)
					{
						sender.sendMessage(tl("unknownPlayer"));
					}
				} else if(!(sender instanceof Player))
				{
					sender.sendMessage(tl("playerOnly"));
				} else
				{
					recieve = (Player)sender;
				}
					Logger.Log(Level.INFO, "Giving " + args[1]);
				// Give the reciever the protection stone
				if(recieve != null && item != null)
				{
					recieve.getInventory().addItem(item);
				}
			} else
			{
				sender.sendMessage(tl("unknownProtectionStone"));
			}
		} else
		{
			sender.sendMessage(tl("unknownProtectionStone"));
		}
		
		return true;
	}

}
