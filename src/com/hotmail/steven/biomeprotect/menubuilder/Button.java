package com.hotmail.steven.biomeprotect.menubuilder;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Button {

	private int position;
	private ItemStack item;
	private ClickListener clickListener;
	
	public Button(int position, ItemStack icon)
	{
		this.position = position;
		this.item = icon;
	}
	
	public int getPosition()
	{
		return position;
	}
	
	public ItemStack getIcon()
	{
		return item;
	}
	
	public boolean hasClickListener()
	{
		return clickListener != null;
	}
	
	public ClickListener getClickListener()
	{
		return clickListener;
	}
	
	public void setClickListener(ClickListener listener)
	{
		clickListener = listener;
	}
	
}
