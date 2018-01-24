package com.hotmail.steven.biomeprotect.commands;

import org.bukkit.command.CommandSender;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.util.StringUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CmdHelp extends BiomeProtectCommand {

	public CmdHelp(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
	}
	
	@Override
	public boolean run(CommandSender sender, String name, String[] args) throws Exception
	{
		int page = 0;
		// Get the page
		if(args.length > 0)
		{
			try
			{
				page = Integer.parseInt(args[0]);
			} catch(NumberFormatException e)
			{
				throw new Exception(tl("invalidNumber"));
			}
		}
		
		String paginated = StringUtil.paginateArray(getPlugin().getCommandHandler().getRegisteredCommands().toArray(), true, 10, page);
		sender.sendMessage(paginated);
		return true;
	}

}
