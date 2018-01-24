package com.hotmail.steven.biomeprotect.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.ProtectedRegionList;
import com.hotmail.steven.biomeprotect.region.ProtectedRegion;

import static com.hotmail.steven.biomeprotect.Language.tl;

public class CmdNear extends BiomeProtectCommand {

	public CmdNear(BiomeProtect plugin, String name, String description, String usage) {
		super(plugin, name, description, usage);
		
		requiresPlayer(true);
	}
	
	@Override
	public boolean run(Player player, String name, String[] args) throws Exception
	{
		int radius = 20;
		Location playerLoc = player.getLocation();
		// Check if the user specified a radius
		if(args.length > 0)
		{
			try
			{
				radius = Integer.parseInt(args[0]);
			} catch(NumberFormatException e)
			{
				throw new Exception(tl("invalidRadius"));
			}
		}
		
		// Generate box boundaries
		Location point1 = playerLoc.subtract(radius, 0, radius);
		Location point2 = playerLoc.add(radius, 0, radius);
		
		ProtectedRegionList regions = getPlugin().getRegionContainer().queryRegions(point1, point2);
		player.sendMessage(tl("regionsFound", new String[] {"%regions%", String.valueOf(regions.size())}));
		for(ProtectedRegion region : regions)
		{
			player.sendMessage(region.toString());
		}
		return true;
	}
	

}
