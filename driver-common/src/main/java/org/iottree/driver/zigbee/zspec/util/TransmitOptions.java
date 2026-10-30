package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;

/**
 Transmit options bit mask according to the following defines from AF.h: bit 1:
				sets ‘Wildcard Profile ID’; bit 4: turns on/off ‘APS ACK’; bit 5 sets ‘discover
				route’; bit 6 sets ‘APS security’; bit 7 sets ‘skip routing’. (This doesn’t apply for
				an inter-pan message.)
				
 * @author jason.zhu
 *
 */
public class TransmitOptions
{
	private static String[] OPTS = new String[] {
			"Wildcard Profile ID", //bit 1
			"",
			"",
			"turns on/off ‘APS ACK’",
			"discover route",
			"APS security",
			"skip routing",
			""
	};
	
	private int opts ;
	
	public TransmitOptions(int v)
	{
		this.opts = v ;
	}

	public int getOptions()
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
		return this.opts +" "+Convert.combineWith(this.getOptionsList(), ',');
	}
}
