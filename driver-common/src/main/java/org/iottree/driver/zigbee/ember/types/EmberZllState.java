package org.iottree.driver.zigbee.ember.types;

/** A bitmask indicating the state of the ZLL device. 
 * This maps directly to the ZLL information field in the scan response. uint16_t */
public enum EmberZllState
{
	/** No state. */
    NONE(0x0000),
    /** The device is factory new. */
    FACTORY_NEW(0x0001),
    /** The device is capable of assigning addresses to other devices. */
    ADDRESS_ASSIGNMENT_CAPABLE(0x0002),
    /** The device is initiating a link operation. */
    LINK_INITIATOR(0x0010),
    /** The device is requesting link priority. */
    LINK_PRIORITY_REQUEST(0x0020),
    /** The device is a Zigbee 3.0 device. */
    PROFILE_INTEROP(0x0080),
    /** The device is on a non-ZLL network. */
    NON_ZLL_NETWORK(0x0100),
    /** Internal use: the ZLL token's key values point to a PSA key identifier */
    TOKEN_POINTS_TO_PSA_ID(0x0200);

	private final int val;

	EmberZllState(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberZllState valOfInt(int v)
	{
		for (EmberZllState st : EmberZllState.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
