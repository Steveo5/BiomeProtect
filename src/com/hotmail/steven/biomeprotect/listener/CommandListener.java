package com.hotmail.steven.biomeprotect.listener;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.commands.BiomeProtectCommand;
import com.hotmail.steven.util.StringUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CommandListener implements CommandExecutor {

	private BiomeProtect plugin;
	
	public CommandListener(BiomeProtect plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(plugin.getCommandHandler().isCommand(cmd.getName()))
		{
			try {
				
				BiomeProtectCommand command = plugin.getCommandHandler().getCommand(cmd.getName());
				if(!command.run(sender, cmd.getName(), label, args))
				{
					sender.sendMessage(command.getUsage());
				}
			} catch (Exception e) {
				sender.sendMessage(StringUtil.colorize(e.getMessage()));
				return true;
			}
		}
		
		sender.sendMessage(tl("unknownCommand"));
		/*
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("show"))
			{
				if(sender instanceof Player)
				{
					Player player = (Player)sender;

				} else
				{
					sender.sendMessage(tl("playerOnly"));
				}
			} else if(args[0].equalsIgnoreCase("flags"))
			{
				sender.sendMessage(StringUtil.paginateArray(plugin.getFlagHolder().getNames().toArray(), 10, 0));
			} else if(args[0].equalsIgnoreCase("stones"))
			{
				List<String> pStones = new ArrayList<String>(plugin.getConfig().getConfigurationSection("protection-stones").getKeys(false));
				sender.sendMessage(StringUtil.paginateArray(pStones.toArray(), 10, 0));
			} else if(args[0].equalsIgnoreCase("give"))
			{
				if(args.length > 1)
				{
					if(plugin.getConfig().isConfigurationSection("protection-stones." + args[1]))
					{
						Player recieve = null;
						ItemStack item = null;
						
						ConfigurationSection section = plugin.getConfig().getConfigurationSection("protection-stones." + args[1]);
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
			}
			return true;
		}*/
		return true;
	}

}
