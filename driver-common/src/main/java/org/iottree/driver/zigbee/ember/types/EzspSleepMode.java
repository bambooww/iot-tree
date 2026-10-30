package org.iottree.driver.zigbee.ember.types;

public enum EzspSleepMode
{
	/** Processor idle. */
    IDLE(0x00),
    /** Wake on interrupt or timer. */
    DEEP_SLEEP(0x01),
    /** Wake on interrupt only. */
    POWER_DOWN(0x02),
    /** Reserved */
    RESERVED_SLEEP(0x03);
    
    private final int val;

    EzspSleepMode(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspSleepMode valOfInt(int v)
	{
		for (EzspSleepMode st : EzspSleepMode.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
