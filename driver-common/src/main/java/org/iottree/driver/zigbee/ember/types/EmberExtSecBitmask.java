package org.iottree.driver.zigbee.ember.types;

/** This is the Extended Security Bitmask that controls the use of various extended security features. */
public enum EmberExtSecBitmask
{
	/**
     * If this bit is set, the 'key token data' field is set in the Initial Security Bitmask to 0 (No Preconfig Key token).
     * Otherwise, the field is left as is.
     */
    PRECONFIG_KEY_NOT_VALID(0x0001),
    // bits 2-3 are unused.
    /**
     * This denotes that the network key update can only happen if the network key update request is unicast and encrypted
     * i.e. broadcast network key update requests will not be processed if bit 1 is set
     */
    SECURE_NETWORK_KEY_ROTATION(0x0002),
    /** This denotes whether a joiner node (router or end-device) uses a Global Link Key or a Unique Link Key. */
    JOINER_GLOBAL_LINK_KEY(0x0010),
    /**
     * This denotes whether the device's outgoing frame counter is allowed to be reset during forming or joining.
     * If the flag is set, the outgoing frame counter is not allowed to be reset.
     * If the flag is not set, the frame counter is allowed to be reset.
     */
    EXT_NO_FRAME_COUNTER_RESET(0x0020),
    /** This denotes whether a device should discard or accept network leave without rejoin commands. */
    NWK_LEAVE_WITHOUT_REJOIN_NOT_ALLOWED(0x0040),
    // Bit 7 reserved for future use (stored in TOKEN).
    /** This denotes whether a router node should discard or accept network Leave Commands. */
    NWK_LEAVE_REQUEST_NOT_ALLOWED(0x0100),
    /**
     * This denotes whether a node is running the latest stack specification or is emulating R18 specs behavior.
     * If this flag is enabled, a router node should only send encrypted Update Device messages while the TC
     * should only accept encrypted Updated Device messages.
     */
    R18_STACK_BEHAVIOR(0x0200),
    // Bit 10 is reserved for future use (stored in TOKEN).
    // Bit 11 is reserved for future use(stored in RAM).
    // Bit 12 - This denotes whether an end device should discard or accept ZDO Leave
    // from a network node other than its parent.
    ZDO_LEAVE_FROM_NON_PARENT_NOT_ALLOWED(0x1000);
    // Bits 13-15 are unused.

	private final int val;

	EmberExtSecBitmask(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberExtSecBitmask valOfInt(int v)
	{
		for (EmberExtSecBitmask st : EmberExtSecBitmask.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
