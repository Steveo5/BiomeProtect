package com.hotmail.steven.biomeprotect.commands;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;
import com.hotmail.steven.util.PlayerUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CmdShow extends BiomeProtectCommand {

	public CmdShow(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
		
		requiresPlayer(true);
	}
	
	@Override
	public boolean run(Player sender, String name, String[] args) throws Exception
	{
		// Players target block
		Block targetBlock = PlayerUtil.getTarget(sender, 10);
		if(targetBlock != null)
		{
			ProtectedRegion foundRegion = getPlugin().getRegionContainer().queryRegion(targetBlock.getLocation());
			if(foundRegion != null)
			{
				foundRegion.show();
				throw new Exception(foundRegion.toString());
			} else
			{
				throw new Exception(tl("regionDoesntExist"));
			}
		} else
		{
			throw new Exception(tl("regionDoesntExist"));
		}
	}

}
