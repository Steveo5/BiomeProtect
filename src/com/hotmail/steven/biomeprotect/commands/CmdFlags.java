package com.hotmail.steven.biomeprotect.commands;

import org.bukkit.command.CommandSender;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.util.StringUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CmdFlags extends BiomeProtectCommand {

	public CmdFlags(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
	}
	
	@Override
	public boolean run(CommandSender sender, String name, String[] args) throws Exception
	{
		int page = 0;
		if(args.length > 0)
		{
			try
			{
				page = Integer.parseInt(args[0]);
			} catch(NumberFormatException e)
			{
				throw new Exception(tl("invalidPage"));
			}
		}
		sender.sendMessage(StringUtil.paginateArray(getPlugin().getFlagHolder().getNames().toArray(), false, 10, page));
		return true;
	}
}
