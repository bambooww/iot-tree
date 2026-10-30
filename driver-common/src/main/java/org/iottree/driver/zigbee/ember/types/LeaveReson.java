package org.iottree.driver.zigbee.ember.types;

public enum LeaveReson
{
	REASON_NONE(0), 
	DUE_TO_NWK_LEAVE_MESSAGE(1), 
	DUE_TO_APS_REMOVE_MESSAGE(2),
	// Currently, the stack does not process the ZDO leave message since it is
	// optional.
	DUE_TO_ZDO_LEAVE_MESSAGE(3), 
	DUE_TO_ZLL_TOUCHLINK(4),

	DUE_TO_APP_EVENT_1(0xff);

	private final int val;

	LeaveReson(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static LeaveReson valOfInt(int v)
	{
		for (LeaveReson st : LeaveReson.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
