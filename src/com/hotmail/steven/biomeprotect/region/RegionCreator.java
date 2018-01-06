package com.hotmail.steven.biomeprotect.region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import com.hotmail.steven.util.StringUtil;

public class RegionCreator {

	private Material type;
	private int radius = 5;
	private int height;
	private String title = "";
	private List<String> lore;
	private String name;
	private int priority = 0;
	
	public RegionCreator(String name)
	{
		this.name = name;
		lore = new ArrayList<String>();
	}
	
	public String getName()
	{
		return name;
	}
	
	/**
	 * Set the material type for the central block
	 * @param type
	 * @return RegionCreator for method chaining
	 */
	public RegionCreator type(Material type)
	{
		this.type = type;
		return this;
	}
	
	/**
	 * Gets the material type for the central block
	 * @return
	 */
	public Material type()
	{
		return type;
	}
	
	/**
	 * Set the region priority to be generated. Higher priority
	 * means permission checks overrides smaller priorities
	 * @param priority
	 * @return RegionCreator for method chaining
	 */
	public RegionCreator priority(int priority)
	{
		this.priority = priority;
		return this;
	}
	
	/**
	 * Get the region priority
	 * @return
	 */
	public int priority()
	{
		return priority;
	}
	
	/**
	 * Set the regions radius in blocks
	 * @param radius
	 * @return RegionCreator for method chaining
	 */
	public RegionCreator radius(int radius)
	{
		this.radius = radius;
		return this;
	}
	
	/**
	 * Get the regions radius in blocks
	 * @return block size
	 */
	public int radius()
	{
		return radius;
	}
	
	/**
	 * Overrides the radius and sets a custom height. 
	 * A -1 value will set the height from bedrock to sky height
	 * @param height
	 * @return RegionCreator for method chaining
	 */
	public RegionCreator height(int height)
	{
		this.height = height;
		return this;
	}
	
	/**
	 * Gets the custom height if set
	 * @return
	 */
	public int height()
	{
		return height;
	}
	
	/**
	 * Set the title of this region, used for some messages but mainly
	 * for matching the item meta for stones that can't be obtained in-game
	 * @param title
	 * @return RegionCreator for method chaining
	 */
	public RegionCreator title(String title)
	{
		this.title = title;
		return this;
	}
	
	/**
	 * Gets the regions title
	 * @return
	 */
	public String title()
	{
		return title;
	}
	
	/**
	 * Set the lore for this region, used for matching the item meta when placing
	 * a new region
	 * @param lore
	 * @return
	 */
	public RegionCreator lore(List<String> lore)
	{
		for(String str : lore)
		{
			this.lore.add(StringUtil.colorize(str));
		}
		return this;
	}
	
	/**
	 * Gets the lore for this region
	 * @return
	 */
	public List<String> lore()
	{
		return lore;
	}
	
	/**
	 * Creates the new region with the given owner
	 * @param owner
	 * @return
	 */
	public ProtectedRegion createRegion(UUID owner, Location location, Material material)
	{
		// Generate new id for the region
		UUID id = UUID.randomUUID();
		// Create the region instance
		ProtectedRegion region = new ProtectedRegion(name, material, id, owner, location, radius(), height());
		return region;
	}
	
}
