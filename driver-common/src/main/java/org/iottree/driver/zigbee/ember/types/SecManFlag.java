package org.iottree.driver.zigbee.ember.types;

/**
 * Security Manager context flags. uint8_t
 * 
 * @author jason.zhu
 *
 */
public enum SecManFlag
{
	NONE(0x00),
	/**
	 * For export APIs, this flag indicates the key_index parameter is valid in
	 * the ::sl_zb_sec_man_context_t structure. This bit is set by the caller
	 * when intending to search for a key by key_index. This flag has no
	 * significance for import APIs.
	 */
	KEY_INDEX_IS_VALID(0x01),
	/**
	 * For export APIs, this flag indicates the eui64 parameter is valid in the
	 * ::sl_zb_sec_man_context_t structure. This bit is set by the caller when
	 * intending to search for a key by eui64. It is also set when searching by
	 * key_index and an entry is found. This flag has no significance for import
	 * APIs.
	 */
	EUI_IS_VALID(0x02),
	/**
	 * Internal use only. This indicates that the transient key being added is
	 * an unconfirmed, updated key. This bit is set when we add a transient key
	 * and the ::EmberTcLinkKeyRequestPolicy policy is
	 * ::EMBER_ALLOW_TC_LINK_KEY_REQUEST_AND_GENERATE_NEW_KEY, whose behavior
	 * dictates that we generate a new, unconfirmed key, send it to the
	 * requester, and await for a Verify Key Confirm message.
	 */
	UNCONFIRMED_TRANSIENT_KEY(0x04),
	/**
	 * Internal use only. This indicates that the key being added was derived
	 * via dynamic link key negotiation. This may be used in conjunction with
	 * the above ::UNCONFIRMED_TRANSIENT_KEY while the derived link key awaits
	 * confirmation
	 */
	AUTHENTICATED_DYNAMIC_LINK_KEY(0x08),
	/**
	 * Internal use only. This indicates that the "key" being added is instead
	 * the symmetric passphrase to be stored in the link key table. This flag
	 * will trigger the addition of the KEY_TABLE_SYMMETRIC_PASSPHRASE bitmask
	 * when storing the symmetric passphrase so that it can be differentiated
	 * from other keys with the same EUI64.
	 */
	SYMMETRIC_PASSPHRASE(0x10);

	private final int val;
	// private final String title;

	SecManFlag(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static SecManFlag valOfInt(int v)
	{
		for (SecManFlag st : SecManFlag.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
