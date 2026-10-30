package org.iottree.driver.zigbee.zspec.zdo_def;

public enum GlobalTLV
{
	/** Minimum Length 2-byte */
	MANUFACTURER_SPECIFIC(64),
	/** Minimum Length 2-byte */
	SUPPORTED_KEY_NEGOTIATION_METHODS(65),
	/**
	 * Minimum Length 4-byte XXX: spec min doesn't make sense, this is one pan
	 * id => 2-byte???
	 */
	PAN_ID_CONFLICT_REPORT(66),
	/** Minimum Length 2-byte */
	NEXT_PAN_ID_CHANGE(67),
	/** Minimum Length 4-byte */
	NEXT_CHANNEL_CHANGE(68),
	/** Minimum Length 16-byte */
	SYMMETRIC_PASSPHRASE(69),
	/** Minimum Length 2-byte */
	ROUTER_INFORMATION(70),
	/** Minimum Length 2-byte */
	FRAGMENTATION_PARAMETERS(71),

	JOINER_ENCAPSULATION(72),

	BEACON_APPENDIX_ENCAPSULATION(73),
	// Reserved(74),
	/** Minimum Length 2-byte XXX: min not in spec??? */
	CONFIGURATION_PARAMETERS(75),
	/** Refer to the Zigbee Direct specification for more details. */
	DEVICE_CAPABILITY_EXTENSION(76);
	// Reserved(77-255

	private final int val;

	GlobalTLV(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static GlobalTLV valOfInt(int v)
	{
		for (GlobalTLV st : GlobalTLV.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
