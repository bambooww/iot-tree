package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;

public class PowerSrcAvailable
{
	private static String[] OPTS = new String[] {
			"Constant (Mains) Power", //bit 0 - 市电/永久供电
			"Rechargeable Battery",  // 可充电电池
			"Disposable Battery", //一次性电池
			"" //bit 3
	};
	
	private int opts ;
	
	public PowerSrcAvailable(int v)
	{
		this.opts = v ;
	}

	public int getOptionVal()
	{
		return this.opts ;
	}
	
	public ArrayList<String> getOptionsList()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		for(int i = 0 ; i < OPTS.length ; i ++)
		{
			int v = 1<<i ;
			if((opts & v)>0)
				rets.add(OPTS[i]) ;
		}
		return rets ;
	}
	
	public String getOptionCur()
	{
		ArrayList<String> opts = getOptionsList() ;
		if(opts==null||opts.size()<=0)
			return null ;
		return opts.get(0) ;
	}
	
	public String toString()
	{
		return Convert.combineWith(this.getOptionsList(), ',');
	}
}
