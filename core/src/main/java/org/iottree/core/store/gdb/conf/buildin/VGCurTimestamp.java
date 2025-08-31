package org.iottree.core.store.gdb.conf.buildin;

import java.sql.Timestamp;

import org.iottree.core.store.gdb.conf.BuildInValGenerator;

public class VGCurTimestamp extends BuildInValGenerator
{
	public String getName()
	{
		return "CURRENT_TIMESTAMP";
	}

	public Object getVal(String[] parms)
	{
		return new Timestamp(System.currentTimeMillis());
	}

	public String getDesc()
	{
		return "当前时间戳";
	}
}
