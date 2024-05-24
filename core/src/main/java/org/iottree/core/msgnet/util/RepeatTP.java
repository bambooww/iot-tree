package org.iottree.core.msgnet.util;

import org.iottree.core.util.Lan;

public enum RepeatTP
{
	none(0), intv(1), intv_bt(2);

	private final int val;

	RepeatTP(int v)
	{
		val = v;
	}

	public int getInt()
	{
		return val;
	}

	public String getTitle()
	{
		Lan lan = Lan.getLangInPk(RepeatTP.class);
		return lan.g("reptp_" + this.name());
	}

	public static RepeatTP valOfInt(int i)
	{
		switch (i)
		{
		case 0:
			return none;
		case 1:
			return intv;
		case 2:
			return intv_bt;
		default:
			return null;
		}
	}

}
