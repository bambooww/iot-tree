package org.iottree.driver.zigbee.ember.types;

/**
 * This is the policy decision bitmask that controls the trust center decision strategies.
 * The bitmask is modified and extracted from the EzspDecisionId for supporting bitmask operations.
 * uint16_t
 */
public enum EzspDecisionBitmask
{
	/** Disallow joins and rejoins. */
	DEFAULT_CONFIGURATION(0x0000),
    /** Send the network key to all joining devices. */
	ALLOW_JOINS(0x0001),
    /** Send the network key to all rejoining devices. */
	ALLOW_UNSECURED_REJOINS(0x0002),
    /** Send the network key in the clear. */
	SEND_KEY_IN_CLEAR(0x0004),
    /** Do nothing for unsecured rejoins. */
	IGNORE_UNSECURED_REJOINS(0x0008),
    /** Allow joins if there is an entry in the transient key table. */
	JOINS_USE_INSTALL_CODE_KEY(0x0010),
    /** Delay sending the network key to a new joining device. */
	DEFER_JOINS(0x0020);
	
    private final int val;

    EzspDecisionBitmask(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspDecisionBitmask valOfInt(int v)
	{
		for (EzspDecisionBitmask st : EzspDecisionBitmask.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
