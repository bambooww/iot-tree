package org.iottree.driver.zigbee.zspec.types;

import org.iottree.driver.zigbee.zspec.zdo_def.SelectedPreSharedSecret;

public enum ZSDeviceType
{
	Unknown(0),
	
	Coordinator(1),
	
	Router(2),

	EndDevice(3),
	
	GreenPower(4);
	
	private final int val;

	ZSDeviceType(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static ZSDeviceType valOfInt(int v)
	{
		for (ZSDeviceType st : ZSDeviceType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
