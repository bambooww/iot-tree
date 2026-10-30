package org.iottree.driver.zigbee.zstack.util;

public enum ZnpVersion
{
	ZStack12(0), ZStack3x0(1), ZStack30x(2);

	public final int val;

	ZnpVersion(int val)
	{
		this.val = val;
	}
	public static ZnpVersion valOfInt(int v)
	{
		for (ZnpVersion st : ZnpVersion.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
