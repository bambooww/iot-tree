package org.iottree.driver.zigbee.ember.types;

/** Defines the possible incoming message types. uint8_t */
public enum IncomeMsgTp
{
	/** Unicast. */
    UNICAST (0),
    /** Unicast reply. */
    UNICAST_REPLY (1),
    /** Multicast. */
    MULTICAST (2),
    /** Multicast sent by the local device. */
    MULTICAST_LOOPBACK (3),
    /** Broadcast. */
    BROADCAST (4),
    /** Broadcast sent by the local device. */
    BROADCAST_LOOPBACK (5);

	private final int val;

	IncomeMsgTp(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static IncomeMsgTp valOfInt(int v)
	{
		for (IncomeMsgTp st : IncomeMsgTp.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
