package com.hotmail.steven.biomeprotect.menubuilder;

import org.bukkit.entity.Player;

public abstract class ButtonListener {
	
	private Object[] customData;
	
	/**
	 * Specifies a new instance of the button listener. You can pass through
	 * any data to the constructor here
	 * @param customData
	 */
	public ButtonListener(Object...customData)
	{
		this.customData = customData;
	}
	
	public Object[] getData()
	{
		return customData;
	}
	
	public void onEnable(Button button, Player player) {}
	public void onClick(Button button, Player player) {}
	
}
