package com.hotmail.steven.biomeprotect;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hotmail.steven.util.PlayerUtil;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("show"))
			{
				if(sender instanceof Player)
				{
					Player player = (Player)sender;
					// Players target block
					Block targetBlock = PlayerUtil.getTarget(player, 10);
					if(targetBlock != null)
					{
						ProtectedRegion foundRegion = BiomeProtect.findRegionExact(targetBlock);
						if(foundRegion != null)
						{
							player.sendMessage(foundRegion.toString());
							foundRegion.show();
						} else
						{
							tl(player, "regionDoesntExist");
						}
					} else
					{
						tl(player, "regionDoesntExist");
					}
				} else
				{
					sender.sendMessage(tl("playerOnly"));
				}
			}
			return true;
		}
		return false;
	}

}
