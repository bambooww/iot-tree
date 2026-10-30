package org.iottree.driver.zigbee.ember.types;

/** This is the Initial Security Bitmask that controls the use of various security features. */
public enum EmberInitSecBitmask
{
	/** Enables Distributed Trust Center Mode for the device forming the network. (Previously known as ::EMBER_NO_TRUST_CENTER_MODE) */
    DISTRIBUTED_TRUST_CENTER_MODE(0x0002),
    /** Enables a Global Link Key for the Trust Center. All nodes will share the same Trust Center Link Key. */
    TRUST_CENTER_GLOBAL_LINK_KEY(0x0004),
    /** Enables devices that perform MAC Association with a pre-configured Network Key to join the network. It is only set on the Trust Center. */
    PRECONFIGURED_NETWORK_KEY_MODE(0x0008),
    // Hidden field used internally.
    HAVE_TRUST_CENTER_UNKNOWN_KEY_TOKEN(0x0010),
    // Hidden field used internally.
    HAVE_TRUST_CENTER_LINK_KEY_TOKEN(0x0020),
    /**
     * This denotes that the ::EmberInitialSecurityState::preconfiguredTrustCenterEui64 has a value in it containing the trust center EUI64.
     * The device will only join a network and accept commands from a trust center with that EUI64.
     * Normally this bit is NOT set and the EUI64 of the trust center is learned during the join process.
     * When commissioning a device to join onto an existing network that is using a trust center and without sending any messages,
     * this bit must be set and the field ::EmberInitialSecurityState::preconfiguredTrustCenterEui64 must be populated with the appropriate EUI64.
     */
    HAVE_TRUST_CENTER_EUI64(0x0040),
    /**
     * This denotes that the ::EmberInitialSecurityState::preconfiguredKey is not the actual Link Key but a Root Key known only to the Trust Center.
     * It is hashed with the IEEE Address of the destination device to create the actual Link Key used in encryption.
     * This is bit is only used by the Trust Center. The joining device need not set this.
     */
    TRUST_CENTER_USES_HASHED_LINK_KEY(0x0084),
    /**
     * This denotes that the ::EmberInitialSecurityState::preconfiguredKey element has valid data that should be used to configure
     * the initial security state.
     */
    HAVE_PRECONFIGURED_KEY(0x0100),
    /**
     * This denotes that the ::EmberInitialSecurityState::networkKey element has valid data that should be used to configure
     * the initial security state.
     */
    HAVE_NETWORK_KEY(0x0200),
    /**
     * This denotes to a joining node that it should attempt to acquire a Trust Center Link Key during joining.
     * This is necessary if the device does not have a pre-configured key, or wants to obtain a new one
     * (since it may be using a well-known key during joining).
     */
    GET_LINK_KEY_WHEN_JOINING(0x0400),
    /**
     * This denotes that a joining device should only accept an encrypted network key from the Trust Center (using its pre-configured key).
     * A key sent in-the-clear by the Trust Center will be rejected and the join will fail.
     * This option is only valid when using a pre-configured key.
     */
    REQUIRE_ENCRYPTED_KEY(0x0800),
    /**
     * This denotes whether the device should NOT reset its outgoing frame counters (both NWK and APS) when
     * ::emberSetInitialSecurityState() is called.
     * Normally it is advised to reset the frame counter before joining a new network.
     * However, when a device is joining to the same network again (but not using ::emberRejoinNetwork()),
     * it should keep the NWK and APS frame counters stored in its tokens.
     *
     * NOTE: The application is allowed to dynamically change the behavior via EMBER_EXT_NO_FRAME_COUNTER_RESET field.
     */
    NO_FRAME_COUNTER_RESET(0x1000),
    /**
     * This denotes that the device should obtain its pre-configured key from an installation code stored in the manufacturing token.
     * The token contains a value that will be hashed to obtain the actual pre-configured key.
     * If that token is not valid, the call to ::emberSetInitialSecurityState() will fail.
     */
    GET_PRECONFIGURED_KEY_FROM_INSTALL_CODE(0x2000),
    // Internal data
    EM_SAVED_IN_TOKEN(0x4000);
    /* All other bits are reserved and must be zero. */

	private final int val;

	EmberInitSecBitmask(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberInitSecBitmask valOfInt(int v)
	{
		for (EmberInitSecBitmask st : EmberInitSecBitmask.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
