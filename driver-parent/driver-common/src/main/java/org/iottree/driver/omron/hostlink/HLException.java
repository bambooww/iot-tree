package org.iottree.driver.omron.hostlink;

@SuppressWarnings("serial")
public class HLException extends Exception
{
	public static final int ERR_TIMEOUT_NOR = 1 ;
	
	public static final int ERR_TIMEOUT_SERIOUS = 2 ;
	
	int errCode = -1 ;
	
	public HLException(int err_c,String msg)
	{
		super(msg) ;
		this.errCode = err_c ;
	}
	
	public int getErrCode()
	{
		return this.errCode ;
	}
}
