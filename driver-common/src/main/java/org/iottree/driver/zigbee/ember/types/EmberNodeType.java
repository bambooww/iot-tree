package org.iottree.driver.zigbee.ember.types;

public enum EmberNodeType
{
	/** The device is not joined. */
    UNKNOWN_DEVICE(0),  // = SL_ZIGBEE_DEVICE_TYPE_UNCHANGED
    /** Will relay messages and can act as a parent to other nodes. */
    COORDINATOR(1),
    /** Will relay messages and can act as a parent to other nodes. */
    ROUTER(2),
    /** Communicates only with its parent and will not relay messages. */
    END_DEVICE(3),
    /** An end device whose radio can be turned off to save power. The application must call ::emberPollForData() to receive messages. */
    SLEEPY_END_DEVICE(4),
    /** Sleepy end device which transmits with wake up frames (CSL). */
    S2S_INITIATOR_DEVICE(5),
    /** Sleepy end device which duty cycles the radio Rx (CSL). */
    S2S_TARGET_DEVICE(6);
    
    private final int val;

    EmberNodeType(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberNodeType valOfInt(int v)
	{
		for (EmberNodeType st : EmberNodeType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
