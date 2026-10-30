package org.iottree.driver.zigbee.ember.types;

/**
 * uint8_t
 * 
 * @author jason.zhu
 */
public enum OutgoingMsgType
{
	/** Unicast sent directly to an EmberNodeId. */
	DIRECT(0),
	/** Unicast sent using an entry in the address table. */
	VIA_ADDRESS_TABLE(1),
	/** Unicast sent using an entry in the binding table. */
	VIA_BINDING(2),
	/**
	 * Multicast message. This value is passed to emberMessageSentHandler()
	 * only. It may not be passed to emberSendUnicast().
	 */
	MULTICAST(3),
	/**
	 * An aliased multicast message. This value is passed to
	 * emberMessageSentHandler() only. It may not be passed to
	 * emberSendUnicast().
	 */
	MULTICAST_WITH_ALIAS(4),
	/**
	 * An aliased Broadcast message. This value is passed to
	 * emberMessageSentHandler() only. It may not be passed to
	 * emberSendUnicast().
	 */
	BROADCAST_WITH_ALIAS(5),
	/**
	 * A broadcast message. This value is passed to emberMessageSentHandler()
	 * only. It may not be passed to emberSendUnicast().
	 */
	BROADCAST(6);

	private final int val;
	// private final String title;

	OutgoingMsgType(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static OutgoingMsgType valOfInt(int v)
	{
		for (OutgoingMsgType st : OutgoingMsgType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
