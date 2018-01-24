package com.hotmail.steven.biomeprotect.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.util.StringUtil;

public class CmdList extends BiomeProtectCommand {

	public CmdList(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
	}
	
	@Override
	public boolean run(CommandSender sender, String name, String[] args)
	{
		List<String> pStones = new ArrayList<String>(getPlugin().getConfig().getConfigurationSection("protection-stones").getKeys(false));
		sender.sendMessage(StringUtil.paginateArray(pStones.toArray(), false, 10, 0));
		return true;
	}

}
