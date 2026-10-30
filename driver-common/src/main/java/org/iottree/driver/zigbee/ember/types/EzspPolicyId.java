package org.iottree.driver.zigbee.ember.types;

/** Identifies a policy. */
public enum EzspPolicyId
{
	/** Controls trust center behavior. */
    TRUST_CENTER_POLICY(0x00),
    /** Controls how external binding modification requests are handled. */
    BINDING_MODIFICATION_POLICY(0x01),
    /** Controls whether the Host supplies unicast replies. */
    UNICAST_REPLIES_POLICY(0x02),
    /** Controls whether pollHandler callbacks are generated. */
    POLL_HANDLER_POLICY(0x03),
    /** Controls whether the message contents are included in the messageSentHandler callback. */
    MESSAGE_CONTENTS_IN_CALLBACK_POLICY(0x04),
    /** Controls whether the Trust Center will respond to Trust Center link key requests. */
    TC_KEY_REQUEST_POLICY(0x05),
    /** Controls whether the Trust Center will respond to application link key requests. */
    APP_KEY_REQUEST_POLICY(0x06),
    /**
     * Controls whether Zigbee packets that appear invalid are automatically dropped by the stack.
     * A counter will be incremented when this occurs.
     */
    PACKET_VALIDATE_LIBRARY_POLICY(0x07),
    /** Controls whether the stack will process ZLL messages. */
    ZLL_POLICY(0x08),
    /**
     * Controls whether Trust Center (insecure) rejoins for devices using the well-known link key are accepted.
     * If rejoining using the well-known key is allowed,
     * it is disabled again after sli_zigbee_allow_tc_rejoins_using_well_known_key_timeout_sec seconds.
     */
    TC_REJOINS_USING_WELL_KNOWN_KEY_POLICY(0x09);
    
    private final int val;

    EzspPolicyId(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspPolicyId valOfInt(int v)
	{
		for (EzspPolicyId st : EzspPolicyId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
