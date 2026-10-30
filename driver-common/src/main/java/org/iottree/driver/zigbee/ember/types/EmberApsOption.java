package org.iottree.driver.zigbee.ember.types;

public enum EmberApsOption
{
	/** No options. */
	NONE(0x0000), 
	
	ENCRYPT_WITH_TRANSIENT_KEY(0x0001), 
	
	USE_ALIAS_SEQUENCE_NUMBER(0x0002),
	/**
	 * This signs the application layer message body (APS Frame not included)
	 * and appends the ECDSA signature to the end of the message, which is
	 * needed by Smart Energy applications and requires the CBKE and ECC
	 * libraries. The ::emberDsaSignHandler() function is called after DSA
	 * signing is complete but before the message has been sent by the APS
	 * layer. Note that when passing a buffer to the stack for DSA signing, the
	 * final byte in the buffer has a special significance as an indicator of
	 * how many leading bytes should be ignored for signature purposes. See the
	 * API documentation of emberDsaSign() or the dsaSign EZSP command for more
	 * details about this requirement.
	 */
	DSA_SIGN(0x0010),
	/**
	 * Send the message using APS Encryption using the Link Key shared with the
	 * destination node to encrypt the data at the APS Level.
	 */
	ENCRYPTION(0x0020),
	/**
	 * Resend the message using the APS retry mechanism. This option and the
	 * enable route discovery option must be enabled for an existing route to be
	 * repaired automatically.
	 */
	RETRY(0x0040),
	/**
	 * Send the message with the NWK 'enable route discovery' flag, which causes
	 * a route discovery to be initiated if no route to the destination is
	 * known. Note that in the mesh stack, this option and the APS retry option
	 * must be enabled an existing route to be repaired automatically.
	 */
	ENABLE_ROUTE_DISCOVERY(0x0100),
	/**
	 * Send the message with the NWK 'force route discovery' flag, which causes
	 * a route discovery to be initiated even if one is known.
	 */
	FORCE_ROUTE_DISCOVERY(0x0200),
	/** Include the source EUI64 in the network frame. */
	SOURCE_EUI64(0x0400),
	/** Include the destination EUI64 in the network frame. */
	DESTINATION_EUI64(0x0800),
	/**
	 * Send a ZDO request to discover the node ID of the destination if it is
	 * not already known.
	 */
	ENABLE_ADDRESS_DISCOVERY(0x1000),
	/**
	 * This message is being sent in response to a call to ::emberPollHandler().
	 * It causes the message to be sent immediately instead of being queued up
	 * until the next poll from the (end device) destination.
	 */
	POLL_RESPONSE(0x2000),
	/**
	 * This incoming message is a valid ZDO request and the application is
	 * responsible for sending a ZDO response. This flag is used only within
	 * emberIncomingMessageHandler() when
	 * EMBER_APPLICATION_RECEIVES_UNSUPPORTED_ZDO_REQUESTS is defined.
	 */
	ZDO_RESPONSE_REQUIRED(0x4000),
	/**
	 * This message is part of a fragmented message. This option may only be set
	 * for unicasts. The groupId field gives the index of this fragment in the
	 * low-order byte. If the low-order byte is zero this is the first fragment
	 * and the high-order byte contains the number of fragments in the message.
	 */
	FRAGMENT(0x8000); // SIGNED_ENUM 0x8000

	private final int val;

	EmberApsOption(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberApsOption valOfInt(int v)
	{
		for (EmberApsOption st : EmberApsOption.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
