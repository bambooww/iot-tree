package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;

public class BDBCommRemainModes
{
	/**
	 "BDB_COMMISSIONING_MODE_INITIATOR_TL",
			"BDB_COMMISSIONING_MODE_NWK_STEERING",
			"BDB_COMMISSIONING_MODE_NWK_FORMATION",
			"BDB_COMMISSIONING_MODE_FINDING_BINDING",
			"BDB_COMMISSIONING_MODE_INITIALIZATION",
			"BDB_COMMISSIONING_MODE_PARENT_LOST"
	 */
	private static String[] MODES=new String[] {
			"INITIATOR_TL",
			"NWK_STEERING",
			"NWK_FORMATION",
			"FINDING_BINDING",
			"INITIALIZATION",
			"PARENT_LOST"
	};
	
	private int modes ;
	
	public BDBCommRemainModes(int v)
	{
		this.modes = v ;
	}

	public int getModes()
	{
		return this.modes ;
	}
	
	public ArrayList<String> getModesList()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		for(int i = 0 ; i < MODES.length ; i ++)
		{
			int v = 1<<i ;
			if((modes & v)>0)
				rets.add(MODES[i]) ;
		}
		return rets ;
	}
	
	public String toString()
	{
		return Convert.combineWith(this.getModesList(), ',');
	}
}
