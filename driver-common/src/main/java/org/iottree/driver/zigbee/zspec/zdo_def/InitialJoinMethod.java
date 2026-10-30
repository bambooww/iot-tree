package org.iottree.driver.zigbee.zspec.zdo_def;

public enum InitialJoinMethod
{
	ANONYMOUS(0x00), 
	
	INSTALL_CODE_KEY(0x01), 
	
	WELLKNOWN_PASSPHRASE(0x02), 
	
	INSTALL_CODE_PASSPHRASE(0x03);

	private final int val;

	InitialJoinMethod(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static InitialJoinMethod valOfInt(int v)
	{
		for (InitialJoinMethod st : InitialJoinMethod.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
