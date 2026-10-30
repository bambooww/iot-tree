package org.iottree.driver.zigbee.ember.types;

/** Green Power Security Level. uint8_t */
public enum EmberGpSecurityLevel
{
	/** No Security  */
    NONE(0x00),
    /** Reserved  */
    RESERVED(0x01),
    /** 4 Byte Frame Counter and 4 Byte MIC */
    FC_MIC(0x02),
    /** 4 Byte Frame Counter and 4 Byte MIC with encryption */
    FC_MIC_ENCRYPTED(0x03);

	private final int val;

	EmberGpSecurityLevel(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberGpSecurityLevel valOfInt(int v)
	{
		for (EmberGpSecurityLevel st : EmberGpSecurityLevel.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
