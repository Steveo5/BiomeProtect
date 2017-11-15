package com.hotmail.steven.biomeprotect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.hotmail.steven.util.StringUtil;

public class ProtectionStone {

	private Material type;
	private int radius = 5;
	private int data;
	private int customHeight = -1;
	private boolean preventPlace = true;
	private boolean preventBreak = true;
	private String welcomeMessage = "";
	private String leaveMessage = "";
	private String title = "";
	private List<String> lore;
	private String name;
	private boolean preventPvp = false;
	private boolean preventTnt = false;
	private int priority = 0;
	
	public ProtectionStone(String name, Material type, int data, int radius)
	{
		this.name = name;
		this.type = type;
		this.radius = radius;
		this.data = data;
		lore = new ArrayList<String>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setAllowsPvp(boolean allow)
	{
		this.preventPvp = allow;
	}
	
	public void setAllowsBreak(boolean allow)
	{
		this.preventBreak = allow;
	}
	
	public void setAllowsPlace(boolean allow)
	{
		this.preventPlace = allow;
	}
	
	public void setCustomHeight(int height)
	{
		this.customHeight = height;
	}
	
	public void setWelcomeMessage(String message)
	{
		this.welcomeMessage = StringUtil.colorize(message);
	}
	
	public void setLeaveMessage(String message)
	{
		this.leaveMessage = StringUtil.colorize(message);
	}
	
	protected void setTitle(String title)
	{
		this.title = StringUtil.colorize(title);
	}
	
	protected void setLore(List<String> lore)
	{
		this.lore = lore;
	}
	
	public void setAllowsTnt(boolean allow)
	{
		this.preventTnt = allow;
	}
	
	public void setPriority(int priority)
	{
		this.priority = priority;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public boolean allowsBreak()
	{
		return preventBreak;
	}
	
	public boolean allowsPlace()
	{
		return preventPlace;
	}
	
	public boolean allowsTnt()
	{
		return preventTnt;
	}
	
	public boolean allowsPvp()
	{
		return preventPvp;
	}
	
	public int getCustomHeight()
	{
		return customHeight;
	}
	
	public int getRadius()
	{
		return radius;
	}
	
	public int getData()
	{
		return data;
	}
	
	public Material getMaterial()
	{
		return type;
	}
	
	public String getWelcomeMessage()
	{
		return welcomeMessage;
	}
	
	public String getLeaveMessage()
	{
		return leaveMessage;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public List<String> getLore()
	{
		return lore;
	}
	
	public boolean hasTitle()
	{
		return !title.equals("");
	}
	
	public boolean hasWelcomeMessage()
	{
		return !welcomeMessage.equals("");
	}
	
	public boolean hasLeaveMessage()
	{
		return !leaveMessage.equals("");
	}
	
}
