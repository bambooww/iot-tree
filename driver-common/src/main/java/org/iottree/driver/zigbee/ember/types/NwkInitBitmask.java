package org.iottree.driver.zigbee.ember.types;

/** uint16 */
public enum NwkInitBitmask
{
	NO_OPTIONS(0x0000),
	/**
	 * The Parent Node ID and EUI64 are stored in a token. This prevents the
	 * need to perform an Orphan scan on startup.
	 */
	PARENT_INFO_IN_TOKEN(0x0001),
	/**
	 * Z3 compliant end devices on a network must send a rejoin request on
	 * reboot.
	 */
	END_DEVICE_REJOIN_ON_REBOOT(0x0002);

	private final int val;
	// private final String title;

	NwkInitBitmask(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static NwkInitBitmask valOfInt(int v)
	{
		for (NwkInitBitmask st : NwkInitBitmask.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
