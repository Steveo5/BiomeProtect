package com.hotmail.steven.biomeprotect.flag;

public class StateFlag extends RegionFlag<String> {

	private String[] states;
	
	/**
	 * Create a state flag with values that you predefine and
	 * are the ones only allowed to be used
	 * @param name
	 */
	public StateFlag(String name) {
		super(name);
	}
	
	/**
	 * Set the allowed states for this flag
	 * @param states
	 */
	public void states(String...states)
	{
		value = states[0];
		this.states = states;
	}
	
	/**
	 * Get the allowed states for this flag
	 * @return
	 */
	public String[] states()
	{
		return states;
	}
	
	/**
	 * Get the next state for this flag, will return state + 1
	 * or state at index 0 if we are at the end of the array
	 * @return
	 */
	public String next()
	{
		String nextStr = states[0];
		// Loop over the known states
		for(int i=0;i<states.length;i++)
		{
			// Check if we have the same state
			if(states[i].equalsIgnoreCase(value))
			{
				// Next state is i+1
				int next = i+1;
				// Check if its bigger then the array size
				if(next > states.length - 1)
				{
					next = 0;
				}
				// Set the next string
				nextStr = states[next];
				break;
				
			}
		}
		return nextStr;
	}
	
	/**
	 * Set the value for this state flag
	 */
	@Override
	public void setValue(String value)
	{
		for(String state : states)
		{
			if(state.equalsIgnoreCase(value)) this.value = value;
		}
	}

}
