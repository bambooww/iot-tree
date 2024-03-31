package org.iottree.driver.omron.hostlink.cmode;

import org.iottree.core.util.ILang;

public class CMCmd implements ILang 
{
	public static String ENDCODE_NOR = "00" ;
	
	public static String ENDCODE_FCS_ERR = "13" ;
	
	
	private String getEndCodeTitle(String endc)
	{
		return g("encode_"+endc,"") ;
	}
}
