package org.iottree.driver.zigbee.zstack.types;

public enum AddressMode
{
	ADDR_NOT_PRESENT(0),

	ADDR_GROUP(1),

	ADDR_16BIT(2),

	ADDR_64BIT(3),

	ADDR_BROADCAST(15);

	public final int val;

	AddressMode(int val)
	{
		this.val = val;
	}

	public static AddressMode valOfInt(int v)
	{
		for (AddressMode st : AddressMode.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
