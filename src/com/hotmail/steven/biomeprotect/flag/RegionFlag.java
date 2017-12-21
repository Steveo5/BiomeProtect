package com.hotmail.steven.biomeprotect.flag;

public abstract class RegionFlag<T> {

	private String name;
	protected T value;
	private String permission = "";
	
	public RegionFlag(String name)
	{
		this.name = name;
	}
	
	public RegionFlag(String name, String permission)
	{
		this(name);
		this.permission = permission;
	}
	
	/**
	 * Get the name of this flag
	 * @return
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the default value for the flag. For example
	 * an integer flag might have a default value of 10
	 * boolean false etc
	 * @return
	 */
	public T getDefaultValue()
	{
		return value;
	}
	
	public T getValue()
	{
		return value;
	}
	
	public void setValue(T value)
	{
		this.value = value;
	}
	
}
