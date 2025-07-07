package org.iottree.driver.omron.fins;

@SuppressWarnings("serial")
public class FinsException extends Exception
{
	public static final int ERR_TIMEOUT_NOR = 1 ;
	
	public static final int ERR_TIMEOUT_SERIOUS = 2 ;
	
	int errCode = -1 ;
	
	public FinsException(int err_c,String msg)
	{
		super(msg) ;
		this.errCode = err_c ;
	}
	
	public int getErrCode()
	{
		return this.errCode ;
	}
}