package org.iottree.driver.zigbee.ember.types;

/**
 * Interpan Message type: unicast, broadcast, or multicast.
 * uint8_t
 */
public enum InterpanMsgType
{
	UNICAST(0x00),
	BROADCAST(0x08),
	MULTICAST(0x0c);

	private final int val;

	InterpanMsgType(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static InterpanMsgType valOfInt(int v)
	{
		for (InterpanMsgType st : InterpanMsgType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
