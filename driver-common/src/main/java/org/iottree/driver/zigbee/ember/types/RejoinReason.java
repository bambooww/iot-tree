package org.iottree.driver.zigbee.ember.types;

public enum RejoinReason
{
	REASON_NONE(0),
    DUE_TO_NWK_KEY_UPDATE(1),
    DUE_TO_LEAVE_MESSAGE(2),
    DUE_TO_NO_PARENT(3),
    DUE_TO_ZLL_TOUCHLINK(4),
    DUE_TO_END_DEVICE_REBOOT(5),

    // App. Framework events
    // 0xA0 - 0xE0
    // See af.h for a subset of defined rejoin reasons

    // Customer-defined Events
    //   These are numbered down from 0xFF so their assigned values
    //   need not change if more application events are needed.
    DUE_TO_APP_EVENT_5(0xfb),
    DUE_TO_APP_EVENT_4(0xfc),
    DUE_TO_APP_EVENT_3(0xfd),
    DUE_TO_APP_EVENT_2(0xfe),
    DUE_TO_APP_EVENT_1(0xff);
    
    private final int val;
	// private final String title;

	RejoinReason(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static RejoinReason valOfInt(int v)
	{
		for (RejoinReason st : RejoinReason.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
