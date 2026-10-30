package org.iottree.driver.zigbee.ember.types;

/** Identifies a policy decision. */
public enum EzspDecisionId
{
	/**
     * BINDING_MODIFICATION_POLICY default decision.
     *
     * Do not allow the local binding table to be changed by remote nodes.
     */
    DISALLOW_BINDING_MODIFICATION(0x10),
    /**
     * BINDING_MODIFICATION_POLICY decision.
     *
     * Allow remote nodes to change the local binding table.
     */
    ALLOW_BINDING_MODIFICATION(0x11),
    /**
     * BINDING_MODIFICATION_POLICY decision.
     *
     * Allows remote nodes to set local binding entries only if the entries correspond to endpoints
     * defined on the device, and for output clusters bound to those endpoints.
     */
    CHECK_BINDING_MODIFICATIONS_ARE_VALID_ENDPOINT_CLUSTERS(0x12),
    /**
     * UNICAST_REPLIES_POLICY default decision.
     *
     * The NCP will automatically send an empty reply (containing no payload) for every unicast received.
     * */
    HOST_WILL_NOT_SUPPLY_REPLY(0x20),
    /**
     * UNICAST_REPLIES_POLICY decision.
     *
     * The NCP will only send a reply if it receives a sendReply command from the Host.
     */
    HOST_WILL_SUPPLY_REPLY(0x21),
    /**
     * POLL_HANDLER_POLICY default decision.
     *
     * Do not inform the Host when a child polls.
     */
    POLL_HANDLER_IGNORE(0x30),
    /**
     * POLL_HANDLER_POLICY decision.
     *
     * Generate a pollHandler callback when a child polls.
     */
    POLL_HANDLER_CALLBACK(0x31),
    /**
     * MESSAGE_CONTENTS_IN_CALLBACK_POLICY default decision.
     *
     * Include only the message tag in the messageSentHandler callback.
     */
    MESSAGE_TAG_ONLY_IN_CALLBACK(0x40),
    /**
     * MESSAGE_CONTENTS_IN_CALLBACK_POLICY decision.
     *
     * Include both the message tag and the message contents in the messageSentHandler callback.
     */
    MESSAGE_TAG_AND_CONTENTS_IN_CALLBACK(0x41),
    /**
     * TC_KEY_REQUEST_POLICY decision.
     *
     * When the Trust Center receives a request for a Trust Center link key, it will be ignored.
     */
    DENY_TC_KEY_REQUESTS(0x50),
    /**
     * TC_KEY_REQUEST_POLICY decision.
     *
     * When the Trust Center receives a request for a Trust Center link key, it will reply to it with the corresponding key.
     */
    ALLOW_TC_KEY_REQUESTS_AND_SEND_CURRENT_KEY(0x51),
    /**
     * TC_KEY_REQUEST_POLICY decision.
     *
     * When the Trust Center receives a request for a Trust Center link key, it will generate a key to send to the joiner.
     * After generation, the key will be added to the transient key tabe and After verification, this key will be added into the link key table.
     */
    ALLOW_TC_KEY_REQUEST_AND_GENERATE_NEW_KEY(0x52),
    /**
     * APP_KEY_REQUEST_POLICY decision.
     * When the Trust Center receives a request for an application link key, it will be ignored.
     * */
    DENY_APP_KEY_REQUESTS(0x60),
    /**
     * APP_KEY_REQUEST_POLICY decision.
     *
     * When the Trust Center receives a request for an application link key, it will randomly generate a key and send it to both partners.
     */
    ALLOW_APP_KEY_REQUESTS(0x61),
    /** Indicates that packet validate library checks are enabled on the NCP. */
    PACKET_VALIDATE_LIBRARY_CHECKS_ENABLED(0x62),
    /** Indicates that packet validate library checks are NOT enabled on the NCP. */
    PACKET_VALIDATE_LIBRARY_CHECKS_DISABLED(0x63);

	private final int val;

	EzspDecisionId(int val)
	{
		this.val= val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspDecisionId valOfInt(int v)
	{
		for (EzspDecisionId st : EzspDecisionId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
