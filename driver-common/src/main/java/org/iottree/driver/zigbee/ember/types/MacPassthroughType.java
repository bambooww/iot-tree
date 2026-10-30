package org.iottree.driver.zigbee.ember.types;

/** The types of MAC passthrough messages that an application may receive. This is a bitmask. */
public enum MacPassthroughType
{
	/** No MAC passthrough messages. */
    NONE(0x00),
    /** SE InterPAN messages. */
    SE_INTERPAN(0x01),
    /** EmberNet and first generation (v1) standalone bootloader messages. */
    EMBERNET(0x02),
    /** EmberNet messages filtered by their source address. */
    EMBERNET_SOURCE(0x04),
    /** Application-specific passthrough messages. */
    APPLICATION(0x08),
    /** Custom inter-pan filter. */
    CUSTOM(0x10),

    /** Internal Stack passthrough. */
    INTERNAL_ZLL(0x80),
    INTERNAL_GP(0x40);

	private final int val;
	// private final String title;

	MacPassthroughType(int val) // , String title)
	{
		this.val= val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static MacPassthroughType valOfInt(int v)
	{
		for (MacPassthroughType st : MacPassthroughType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
