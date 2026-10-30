package org.iottree.driver.zigbee.ember.types;

/** uint8_t */
public enum EmberGpAppId
{
	/** Source identifier. */
	SOURCE_ID(0x00),
	/** IEEE address. */
	IEEE_ADDRESS(0x02);

	private final int val;

	EmberGpAppId(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberGpAppId valOfInt(int v)
	{
		for (EmberGpAppId st : EmberGpAppId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
