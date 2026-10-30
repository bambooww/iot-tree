package org.iottree.driver.zigbee.ember.types;

public enum EmberInterpanMsgTp
{
	UNICAST(0x00),

	BROADCAST(0x08),

	MULTICAST(0x0c);

	private final int val;

	EmberInterpanMsgTp(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberInterpanMsgTp valOfInt(int v)
	{
		for (EmberInterpanMsgTp st : EmberInterpanMsgTp.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
