package org.iottree.driver.zigbee.ember.types;

public enum EmberTransmitPriority
{
	HIGH(0),
	
	NORMAL(1),
	
	SCAN_OKAY(2);
	
	private final int val;

	EmberTransmitPriority(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberTransmitPriority valOfInt(int v)
	{
		for (EmberTransmitPriority st : EmberTransmitPriority.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
