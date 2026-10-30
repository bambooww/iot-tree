package org.iottree.driver.zigbee.ember.types;

/** Flags associated with the endpoint data configured on the NCP. */
public enum EzspEndpointFlag
{
	/** Indicates that the endpoint is disabled and NOT discoverable via ZDO. */
    DISABLED(0x00),
    /** Indicates that the endpoint is enabled and discoverable via ZDO. */
    ENABLED(0x01);
    
	private final int val;

	EzspEndpointFlag(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspEndpointFlag valOfInt(int v)
	{
		for (EzspEndpointFlag st : EzspEndpointFlag.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
