package org.iottree.driver.zigbee.zspec.util;

public enum RoutingTableStatus
{
	ACTIVE(0x0),

	DISCOVERY_UNDERWAY(0x1),

	DISCOVERY_FAILED(0x2),

	INACTIVE(0x3),

	VALIDATION_UNDERWAY(0x4),

	RESERVED1(0x5), RESERVED2(0x6), RESERVED3(0x7);

	public final int val;

	RoutingTableStatus(int val)
	{
		this.val = val;
	}

	public static RoutingTableStatus valOfInt(int v)
	{
		for (RoutingTableStatus st : RoutingTableStatus.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
