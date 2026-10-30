package org.iottree.driver.zigbee.zspec.types;


public enum BroadcastAddr
{
	// Reserved(0xfff8,
    // Reserved(0xfff9,
    // Reserved(0xfffa,
    /** Low power routers only */
    LOW_POWER_ROUTERS(0xfffb),
    /** All routers and coordinator */
    DEFAULT(0xfffc),
    /** macRxOnWhenIdle(TRUE (all non-sleepy devices) */
    RX_ON_WHEN_IDLE(0xfffd),
    // Reserved(0xFFFE,
    /** All devices in PAN (including sleepy end devices) */
    SLEEPY(0xffff);
    
    public final int val;
    BroadcastAddr(int val){this.val =val;}
    public static BroadcastAddr valOfInt(int v)
	{
		for (BroadcastAddr st : BroadcastAddr.values())
			if (st.val == v)
				return st;
		return null;
	}
}
