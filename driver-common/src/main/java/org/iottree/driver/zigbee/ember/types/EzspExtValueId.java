package org.iottree.driver.zigbee.ember.types;

/**
 * Identifies a value based on specified characteristics. Each set of
 * characteristics is unique to that value and is specified during the call to
 * get the extended value.
 *
 * uint8_t
 */
public enum EzspExtValueId
{
	/**
	 * The flags field associated with the specified endpoint. Value is uint16_t
	 */
	ENDPOINT_FLAGS(0x00),
	/**
	 * This is the reason for the node to leave the network as well as the
	 * device that told it to leave. The leave reason is the 1st byte of the
	 * value while the node ID is the 2nd and 3rd byte. If the leave was caused
	 * due to an API call rather than an over the air message, the node ID will
	 * be EMBER_UNKNOWN_NODE_ID (0xFFFD).
	 */
	LAST_LEAVE_REASON(0x01),
	/**
	 * This number of bytes of overhead required in the network frame for source
	 * routing to a particular destination.
	 */
	GET_SOURCE_ROUTE_OVERHEAD(0x02),
	/**
	 * v17+ These values are current or boot-time metrics gathered by the memory
	 * manager/buffer manager.
	 */
	MEMORY_USAGE_DATA(0x03);

	private final int val;

	EzspExtValueId(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspExtValueId valOfInt(int v)
	{
		for (EzspExtValueId st : EzspExtValueId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
