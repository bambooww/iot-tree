package org.iottree.driver.aromat.serial;

import org.iottree.core.util.Lan;

/**
 * Read contact area
 * @author jason.zhu
 *
 */
public abstract class AMMsgReqRC extends AMMsgReq
{
	public static final char CONTACT_CODE_X = 'X' ;
	public static final char CONTACT_CODE_Y = 'Y' ;
	public static final char CONTACT_CODE_R = 'R' ;
	public static final char CONTACT_CODE_L = 'L' ;
	public static final char CONTACT_CODE_T = 'T' ;
	public static final char CONTACT_CODE_C = 'C' ;
	
	public static String getContactCodeTitle(char c)
	{
		Lan lan = Lan.getLangInPk(AMMsgReqRC.class) ;
		return lan.g("contact_"+c) ;
	}
	
	
	
	@Override
	public String getCmdCode()
	{
		return "RC";
	}

}
