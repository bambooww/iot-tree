package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;

public class ServerMask
{
	private static String[] OPTS = new String[] {
			"Primary Trust Center", //bit 0
			"Backup Trust Center", 
			"Primary Binding Table Cache",
			"Backup Binding Table Cache",
			"Primary Discovery Cache",
			"Backup Discovery Cache" //bit 5
	};
	
	private int opts ;
	
	public ServerMask(int v)
	{
		this.opts = v ;
	}

	public int getMaskVal()
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
	
	public String toString()
	{
		return Convert.combineWith(this.getOptionsList(), ',');
	}
}
