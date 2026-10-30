package org.iottree.driver.zigbee.ember.types;

public enum NwkStatus
{
	/** The node is not associated with a network in any way. */
    NO_NETWORK(0),
    /** The node is currently attempting to join a network. */
    JOINING_NETWORK(1),
    /** The node is joined to a network. */
    JOINED_NETWORK(2),
    /** The node is an end device joined to a network but its parent is not responding. */
    JOINED_NETWORK_NO_PARENT(3),
    /** The node is a Sleepy-to-Sleepy initiator */
    JOINED_NETWORK_S2S_INITIATOR(4),
    /** The node is a Sleepy-to-Sleepy target */
    JOINED_NETWORK_S2S_TARGET(5),
    /** The node is in the process of leaving its current network. */
    LEAVING_NETWORK(6);

	private final int val;
	// private final String title;

	NwkStatus(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static NwkStatus valOfInt(int v)
	{
		for (NwkStatus st : NwkStatus.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
