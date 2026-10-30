package org.iottree.driver.zigbee.ember.types;

/** Defines binding types. uint8_t */
public enum BindingType
{
	/** A binding that is currently not in use. */
    UNUSED_BINDING(0),
    /** A unicast binding whose 64-bit identifier is the destination EUI64. */
    UNICAST_BINDING(1),
    /**
     * A unicast binding whose 64-bit identifier is the many-to-one destination EUI64.
     * Route discovery should be disabled when sending unicasts via many-to-one bindings.
     */
    MANY_TO_ONE_BINDING(2),
    /**
     * A multicast binding whose 64-bit identifier is the group address.
     * This binding can be used to send messages to the group and to receive messages sent to the group.
     */
    MULTICAST_BINDING (3);

	private final int val;
	// private final String title;

	BindingType(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static BindingType valOfInt(int v)
	{
		for (BindingType st : BindingType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
