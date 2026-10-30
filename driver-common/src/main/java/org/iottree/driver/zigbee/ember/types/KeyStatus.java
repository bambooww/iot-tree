package org.iottree.driver.zigbee.ember.types;

/** This denotes the status of an attempt to establish a key with another device. */
public enum KeyStatus
{
	STATUS_NONE(0x00),
    APP_LINK_KEY_ESTABLISHED(0x01),
    TRUST_CENTER_LINK_KEY_ESTABLISHED(0x03),

    ESTABLISHMENT_TIMEOUT(0x04),
    TABLE_FULL(0x05),

    // These are success status values applying only to the Trust Center answering key requests.
    TC_RESPONDED_TO_KEY_REQUEST(0x06),
    TC_APP_KEY_SENT_TO_REQUESTER(0x07),

    // These are failure status values applying only to the
    // Trust Center answering key requests.
    TC_RESPONSE_TO_KEY_REQUEST_FAILED(0x08),
    TC_REQUEST_KEY_TYPE_NOT_SUPPORTED(0x09),
    TC_NO_LINK_KEY_FOR_REQUESTER(0x0a),
    TC_REQUESTER_EUI64_UNKNOWN(0x0b),
    TC_RECEIVED_FIRST_APP_KEY_REQUEST(0x0c),
    TC_TIMEOUT_WAITING_FOR_SECOND_APP_KEY_REQUEST(0x0d),
    TC_NON_MATCHING_APP_KEY_REQUEST_RECEIVED(0x0e),
    TC_FAILED_TO_SEND_APP_KEYS(0x0f),
    TC_FAILED_TO_STORE_APP_KEY_REQUEST(0x10),
    TC_REJECTED_APP_KEY_REQUEST(0x11),
    TC_FAILED_TO_GENERATE_NEW_KEY(0x12),
    TC_FAILED_TO_SEND_TC_KEY(0x13),

    // These are generic status values for a key requester.
    TRUST_CENTER_IS_PRE_R21(0x1e),

    // These are status values applying only to the Trust Center verifying link keys.
    TC_REQUESTER_VERIFY_KEY_TIMEOUT(0x32),
    TC_REQUESTER_VERIFY_KEY_FAILURE(0x33),
    TC_REQUESTER_VERIFY_KEY_SUCCESS(0x34),

    // These are status values applying only to the key requester
    // verifying link keys.
    VERIFY_LINK_KEY_FAILURE(0x64),
    VERIFY_LINK_KEY_SUCCESS(0x65);

	private final int val;

	KeyStatus(int val)
	{
		this.val= val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static KeyStatus valOfInt(int v)
	{
		for (KeyStatus st : KeyStatus.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
