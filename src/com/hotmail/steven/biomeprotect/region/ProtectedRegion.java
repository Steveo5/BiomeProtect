package com.hotmail.steven.biomeprotect.region;

import static com.hotmail.steven.biomeprotect.Language.tl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.BlockVector;

import com.hotmail.steven.biomeprotect.BiomeProtect;
import com.hotmail.steven.biomeprotect.Logger;
import com.hotmail.steven.biomeprotect.flag.RegionFlag;
import com.hotmail.steven.biomeprotect.flag.StateFlag;
import com.hotmail.steven.biomeprotect.flag.StringFlag;
import com.hotmail.steven.biomeprotect.menubuilder.Button;
import com.hotmail.steven.biomeprotect.menubuilder.ButtonListener;
import com.hotmail.steven.biomeprotect.menubuilder.InputListener;
import com.hotmail.steven.biomeprotect.menubuilder.MenuBuilder;
import com.hotmail.steven.biomeprotect.visualize.BlockCleanupTask;
import com.hotmail.steven.util.ItemUtil;
import com.hotmail.steven.util.LocationUtil;
import com.hotmail.steven.util.StringUtil;
import com.mojang.authlib.GameProfile;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.TileEntitySkull;

public class ProtectedRegion extends Region {

	private UUID owner;
	/**
	 * Hold the blocks shown when displaying the area
	 * in the physical world
	 */
	private List<Block> shownBlocks;
	private HashSet<Chunk> chunks;
	private UUID id;
	private HashSet<Player> playersInRegion;
	private HashSet<UUID> members;
	private HashSet<RegionFlag<?>> flags;
	private int priority;
	private String name;
	private Material material;
	private MenuBuilder mainMenu, flagsMenu, whitelistMenu;
	private String title = "";
	private List<String> lore;

	protected ProtectedRegion(String name, Material material, UUID id, UUID owner, Location center, int radius, int height)
	{
		super(center, radius, height);
		this.name = name;
		lore = new ArrayList<String>();
		shownBlocks = new ArrayList<Block>();
		playersInRegion = new HashSet<Player>();
		members = new HashSet<UUID>();
		this.owner = owner;
		this.id = id;
		this.material = material;
		// Get existing chunks this region resides
		chunks = LocationUtil.getAllChunks(getWorld(), getSmallerPoint(), getLargerPoint());
		Logger.Log(Level.INFO, Bukkit.getOfflinePlayer(owner).getName() + " placed a field at " + center.getBlockX() + " " + center.getBlockY() + " " + center.getBlockZ());
		Logger.Log(Level.INFO, "Region intercepts " + chunks.size() + " chunks");
		
		flags = new HashSet<RegionFlag<?>>();
		
		if(mainMenu == null)
		{
			mainMenu = new MenuBuilder();
			mainMenu.size(9).title("Manage protection");
			mainMenu.button(0, new Button(0, ItemUtil.item(Material.GLASS, 1, "&aShow area")), new ButtonListener()
			{
				@Override
				public void onClick(Button button, Player player)
				{
					show();
				}
			});
			
			mainMenu.button(1, new Button(1, ItemUtil.item(Material.PAPER, 1, "&aEdit flags")), new ButtonListener()
			{
				@Override
				public void onClick(Button button, Player player)
				{
					flagsMenu.show(player);
				}
			});
			
			mainMenu.button(2, new Button(2, ItemUtil.item(Material.PAPER, 1, "&aManage whitelist")), new ButtonListener()
			{
				@Override
				public void onClick(Button button, Player player)
				{
					whitelistMenu.show(player);
				}
			});
		}
		
		flagsMenu = new MenuBuilder();
		flagsMenu.size(9).title("&c&lManage flags");
		flagsMenu.button(0, new Button(0, ItemUtil.item(Material.PAPER, 1, "&7Entry message", "&3none")), new InputListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("welcome-message"))
				{
					StringFlag welcomeFlag = (StringFlag)getFlag("welcome-message");
					String welcomeMessage = welcomeFlag.getValue();
					button.lore(welcomeMessage);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onInput(Player player, String message)
			{
				// Create our welcome flag and set the message value
				StringFlag welcomeFlag = hasFlag("welcome-message") ? (StringFlag)getFlag("welcome-message") : new StringFlag("welcome-message");
				welcomeFlag.setValue(message);
				setFlag(welcomeFlag);
				player.sendMessage("Welcome message updated");
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				player.sendMessage("Enter a new welcome message:");
			}
		});
		
		flagsMenu.button(1, new Button(1, ItemUtil.item(Material.PAPER, 1, "&7Leave message", "&3none")), new InputListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("leave-message"))
				{
					StringFlag welcomeFlag = (StringFlag)getFlag("leave-message");
					String welcomeMessage = welcomeFlag.getValue();
					button.lore(welcomeMessage);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onInput(Player player, String message)
			{
				// Create our welcome flag and set the message value
				StringFlag welcomeFlag = hasFlag("leave-message") ? (StringFlag)getFlag("leave-message") : new StringFlag("leave-message");
				welcomeFlag.setValue(message);
				setFlag(welcomeFlag);
				player.sendMessage("Leave message updated");
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				player.sendMessage("Enter a new leave message:");
			}
		});
		
		flagsMenu.button(2, new Button(2, ItemUtil.item(Material.PAPER, 1, "&7Pvp", "&eallow")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("pvp"))
				{
					StateFlag pvpFlag = (StateFlag)getFlag("pvp");
					String state = pvpFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag pvpFlag = null;
				if(hasFlag("pvp"))
				{
					pvpFlag = (StateFlag)getFlag("pvp");
				} else
				{
					pvpFlag = new StateFlag("pvp", "allow", "deny", "whitelist");
					setFlag(pvpFlag);
				}
				String state = pvpFlag.next();
				pvpFlag.setValue(state);
				button.lore("&e" + pvpFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(3, new Button(3, ItemUtil.item(Material.PAPER, 1, "&7Tnt", "&eallow")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("tnt"))
				{
					StateFlag pvpFlag = (StateFlag)getFlag("tnt");
					String state = pvpFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					mainMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag pvpFlag = null;
				if(hasFlag("tnt"))
				{
					pvpFlag = (StateFlag)getFlag("tnt");
				} else
				{
					pvpFlag = new StateFlag("tnt", "allow", "deny", "whitelist");
					setFlag(pvpFlag);
				}
				String state = pvpFlag.next();
				pvpFlag.setValue(state);
				button.lore("&e" + pvpFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(4, new Button(4, ItemUtil.item(Material.PAPER, 1, "&7Break", "&edeny")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("break"))
				{
					StateFlag pvpFlag = (StateFlag)getFlag("break");
					String state = pvpFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag pvpFlag = null;
				if(hasFlag("break"))
				{
					pvpFlag = (StateFlag)getFlag("break");
				} else
				{
					pvpFlag = new StateFlag("break", "allow", "deny", "whitelist");
					setFlag(pvpFlag);
				}
				String state = pvpFlag.next();
				pvpFlag.setValue(state);
				button.lore("&e" + pvpFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(5, new Button(5, ItemUtil.item(Material.PAPER, 1, "&7Place", "&edeny")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("place"))
				{
					StateFlag pvpFlag = (StateFlag)getFlag("place");
					String state = pvpFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag pvpFlag = null;
				if(hasFlag("place"))
				{
					pvpFlag = (StateFlag)getFlag("place");
				} else
				{
					pvpFlag = new StateFlag("place", "allow", "deny", "whitelist");
					setFlag(pvpFlag);
				}
				String state = pvpFlag.next();
				pvpFlag.setValue(state);
				button.lore("&e" + pvpFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(6, new Button(6, ItemUtil.item(Material.PAPER, 1, "&7Doors", "&eallow")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("doors"))
				{
					StateFlag doorsFlag = (StateFlag)getFlag("doors");
					String state = doorsFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag doorsFlag = null;
				if(hasFlag("doors"))
				{
					doorsFlag = (StateFlag)getFlag("doors");
				} else
				{
					doorsFlag = new StateFlag("doors", "allow", "deny", "whitelist");
					setFlag(doorsFlag);
				}
				String state = doorsFlag.next();
				doorsFlag.setValue(state);
				button.lore("&e" + doorsFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(7, new Button(7, ItemUtil.item(Material.PAPER, 1, "&7Chests", "&eallow")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("chests"))
				{
					StateFlag chestsFlag = (StateFlag)getFlag("chests");
					String state = chestsFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag chestsFlag = null;
				if(hasFlag("chests"))
				{
					chestsFlag = (StateFlag)getFlag("chests");
				} else
				{
					chestsFlag = new StateFlag("chests", "allow", "deny", "whitelist");
					setFlag(chestsFlag);
				}
				String state = chestsFlag.next();
				chestsFlag.setValue(state);
				button.lore("&e" + chestsFlag.getValue());
				flagsMenu.update();
			}
		});
		
		flagsMenu.button(8, new Button(8, ItemUtil.item(Material.PAPER, 1, "&7Other", "&eallow")), new ButtonListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				if(hasFlag("other"))
				{
					StateFlag otherFlag = (StateFlag)getFlag("other");
					String state = otherFlag.getValue();
					button.lore(ChatColor.YELLOW + state);
					flagsMenu.update();
				}
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				StateFlag otherFlag = null;
				if(hasFlag("other"))
				{
					otherFlag = (StateFlag)getFlag("other");
				} else
				{
					otherFlag = new StateFlag("other", "allow", "deny", "whitelist");
					setFlag(otherFlag);
				}
				String state = otherFlag.next();
				otherFlag.setValue(state);
				button.lore("&e" + otherFlag.getValue());
				flagsMenu.update();
			}
		});
		
		whitelistMenu = new MenuBuilder();
		whitelistMenu.size(45).title("&c&lManage whitelist");
		whitelistMenu.button(44, new Button(44, ItemUtil.item(Material.PAPER, 1, "&aNext page")), new ButtonListener()
		{
			@Override
			public void onClick(Button button, Player player)
			{
				
			}
		});
		
		whitelistMenu.button(36, new Button(36, ItemUtil.item(Material.PAPER, 1, "&aPrevious page")), new ButtonListener()
		{
			@Override
			public void onClick(Button button, Player player)
			{
				
			}
		});
		
		whitelistMenu.button(40, new Button(40, ItemUtil.item(Material.PAPER, 1, "&aWhitelist player", "&eClick to add a player|&eto the whitelist")), new InputListener()
		{
			@Override
			public void onEnable(Button button, Player player)
			{
				// Get the whitelist iterator
				List<UUID> members = new ArrayList<UUID>(getMembers());
				// Update the menu, removing any old skulls
				whitelistMenu.update();
				for(int i=0;i<members.size();i++)
				{
					// Get our current player
					OfflinePlayer next = Bukkit.getOfflinePlayer(members.get(i));
					// Create a button for the player so they can click and remove them from the whitelist
					whitelistMenu.button(i, new Button(i, ItemUtil.item(Material.APPLE, 1, "&a" + next.getName(), "&eClick to remove")), new ButtonListener(next.getUniqueId())
					{
						@Override
						public void onClick(Button button, Player player)
						{
							removeMember((UUID)getData()[0]);
							whitelistMenu.removeButton(button.getPosition());
						}
					});
					i++;
				}
				whitelistMenu.update();
			}
			
			@Override
			public void onClick(Button button, Player player)
			{
				player.sendMessage("Enter a player to whitelist:");
			}
			
			@Override
			public void onInput(Player player, String message)
			{
				OfflinePlayer whitelist = Bukkit.getPlayer(message);
				if(whitelist != null)
				{
					addMember(whitelist.getUniqueId());
					player.sendMessage("Player whitelisted successfully");
				} else
				{
					player.sendMessage("You have entered an unknown player");
				}
			}
		});
	}
	
	protected void setLore(List<String> lore)
	{
		for(String str : lore)
		{
			this.lore.add(StringUtil.colorize(str));
		}
	}
	
	public boolean hasLore()
	{
		return !lore.isEmpty();
	}
	
	public List<String> getLore()
	{
		return lore;
	}
	
	protected void setTitle(String title)
	{
		this.title = title;
	}
	
	public boolean hasTitle()
	{
		return !title.isEmpty();
	}
	
	public String getTitle()
	{
		return title;
	}
	
	protected void setUUID(UUID id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get all the chunks this region exists in
	 * @return
	 */
	public HashSet<Chunk> getExistingChunks()
	{
		return chunks;
	}
	
	/**
	 * Get all the flags this protected region has.
	 * Returns a hash set so only one of each type
	 * should be able to exist
	 * @return
	 */
	public HashSet<RegionFlag<?>> getFlags()
	{
		return flags;
	}
	
	/**
	 * Check if a flag exists based on the string name
	 * @param name
	 * @return
	 */
	public boolean hasFlag(String name)
	{
		for(RegionFlag<?> flag : flags)
		{
			if(flag.getName().equalsIgnoreCase(name)) return true;
		}
		
		return false;
	}
	
	/**
	 * Add a new flag to this protected regions flag list
	 * @param flag
	 */
	public void setFlag(RegionFlag<?> flag)
	{
		flags.add(flag);
	}
	
	/**
	 * Get a region flag from its string name
	 * @param name
	 * @return
	 */
	public RegionFlag<?> getFlag(String name)
	{
		Iterator<RegionFlag<?>> itrFlag = flags.iterator();
		// Loop over the existing flags
		while(itrFlag.hasNext())
		{
			RegionFlag<?> next = itrFlag.next();
			// Remove the flag from this region if it exists
			if(next.getName().equalsIgnoreCase(name)) return next;
		}
		
		return null;
	}
	
	/**
	 * Remove a flag from this protected region, causing it to
	 * no longer have effect
	 * @param name
	 */
	public void removeFlag(String name)
	{
		Iterator<RegionFlag<?>> itrFlag = flags.iterator();
		// Loop over the existing flags
		while(itrFlag.hasNext())
		{
			RegionFlag<?> next = itrFlag.next();
			// Remove the flag from this region if it exists
			if(next.getName().equalsIgnoreCase(name)) itrFlag.remove();
		}
	}
	
	/**
	 * Gets the protection stone item this protected region
	 * represents, 'the block' that was placed
	 * @return
	 */
	public ItemStack getItem()
	{
		ItemStack item = new ItemStack(getMaterial());
		// Set the item lore and title
		if(hasTitle() || hasLore())
		{
			ItemMeta im = item.getItemMeta();
			if(hasTitle()) im.setDisplayName(StringUtil.colorize(getTitle()));
			if(hasLore()) im.setLore(getLore());
			item.setItemMeta(im);
		}
		return item;
	}
	
	/**
	 * Get all the players that are currently in this region
	 * @return
	 */
	protected HashSet<Player> getPlayers()
	{
		return playersInRegion;
	}
	
	protected boolean isPlayerPersistantInside(Player player)
	{
		return getPlayers().contains(player);
	}
	
	protected void addPlayerPersistant(Player player)
	{
		playersInRegion.add(player);
	}
	
	protected void removePlayerPersistant(Player player)
	{
		playersInRegion.remove(player);
	}
	
	public World getWorld()
	{
		return getCenter().getWorld();
	}
	
	public Material getMaterial()
	{
		return material;
	}
	
	/**
	 * Gets this regions priority
	 * @return
	 */
	public int getPriority()
	{
		return priority;
	}
	
	/**
	 * Set the priority for this region, if more then one region
	 * is touching/existing in this region then all priorities should be
	 * different.
	 * 
	 * Higher priorities will mean that block place checking, pvp flags etc
	 * will be prioritized in this region instead of the parent region
	 * @param priority
	 */
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	/**
	 * Check if a player is inside this region
	 * @param uuid
	 * @return
	 */
	public boolean isPlayerInside(Player player)
	{
		return isLocationInside(player.getLocation());
	}
	
	public boolean isLocationInside(Location loc)
	{
		return LocationUtil.boxContains(getSmallerPoint(), getLargerPoint(), loc);
	}
	
	public UUID getId()
	{
		return id;
	}
	
	public UUID getOwner()
	{
		return owner;
	}
	
	public void show()
	{
		BiomeProtect.getVisualizer().show(BiomeProtect.getVisualizer().getVisualization(this));
	}
	
	/**
	 * Show the editing menu for this protected region
	 * @param player
	 */
	public void showMenu(Player p)
	{
		
		mainMenu.show(p);
	}
	
	/**
	 * Checks if a uuid has access to this region
	 * @param uuid
	 * @return
	 */
	public boolean hasPermission(UUID uuid)
	{
		return uuid.equals(owner);
	}
	
	/**
	 * Checks if a UUID owns this region
	 * @param uuid
	 * @return
	 */
	public boolean isOwner(UUID uuid)
	{
		return uuid.equals(owner);
	}
	
	/**
	 * Checks if a player owns this region
	 * @param player
	 * @return
	 */
	public boolean isOwner(Player player)
	{
		return player.getUniqueId().equals(owner);
	}
	
	/**
	 * Get all the members of this region
	 * @return
	 */
	public HashSet<UUID> getMembers()
	{
		return members;
	}
	
	/**
	 * Add a member to this region
	 * @param member
	 */
	public void addMember(UUID member)
	{
		members.add(member);
	}
	
	/**
	 * Add a member to this region
	 * @param player
	 */
	public void addMember(Player player)
	{
		members.add(player.getUniqueId());
	}
	
	/**
	 * Check if the region has a member
	 * @param member
	 * @return
	 */
	public boolean hasMember(UUID member)
	{
		return members.contains(member);
	}
	
	/**
	 * Check if the region has a member/is whitelisted
	 * @param player
	 * @return
	 */
	public boolean hasMember(Player player)
	{
		return members.contains(player.getUniqueId());
	}
	
	public void removeMember(UUID member)
	{
		members.remove(member);
	}
	
	/**
	 * Check if a player has permission based on the StateFlag
	 * @param p
	 * @param flag
	 * @return
	 */
	public boolean hasPermission(Player p, StateFlag flag)
	{
		// Build flag is set to deny or player is not on the whitelist and the flag is whitelist
		if(flag.getValue().equals("deny") || flag.getValue().equals("whitelist"))
		{
			// Owner should be able to do everything
			if(flag.getValue().equals("deny") && isOwner(p)) return true;
			// Check if the player is white listed
			if(flag.getValue().equals("whitelist") && (hasMember(p) || isOwner(p)))
			{
				return true;
			}
			if(p.hasPermission("biomeprotect.bypass.break"))
			{
				return true;
			}
			
			return false;
		}
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder strRegion = new StringBuilder();
		strRegion.append(Bukkit.getPlayer(getOwner()).getName() + "'s " + getTitle());
		strRegion.append("\nCenter: " + getCenter().getBlockX() + " " + getCenter().getBlockY() + " " + getCenter().getBlockZ());
		strRegion.append("\nMin x " + getSmallerPoint().getBlockX() + " Min y " + getSmallerPoint().getBlockY() + " Min z " + getSmallerPoint().getBlockZ());
		strRegion.append("\nMax x " + getLargerPoint().getBlockX() + " Max y " + getLargerPoint().getBlockY() + " Max z " + getLargerPoint().getBlockZ());
	
		return strRegion.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ProtectedRegion)
		{
			ProtectedRegion region = (ProtectedRegion)obj;
			if(region.getId().equals(getId())) return true;
		}
		return false;
	}
	
}
