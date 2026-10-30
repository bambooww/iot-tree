package org.iottree.driver.zigbee.ember.types;

public enum NwkLeaveOption
{
	/** Leave with no option. */
    WITH_NO_OPTION(0x00),
    /** Leave with option rejoin. */
    WITH_OPTION_REJOIN(0x20),
    /** Leave is requested. */
    IS_REQUESTED(0x40);

	private final int val;
	// private final String title;

	NwkLeaveOption(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static NwkLeaveOption valOfInt(int v)
	{
		for (NwkLeaveOption st : NwkLeaveOption.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
