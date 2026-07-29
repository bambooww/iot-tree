package org.iottree.driver.aromat.serial;

@SuppressWarnings("serial")
public class AMException extends Exception
{
public static final int ERR_TIMEOUT_NOR = 1 ;
	
	public static final int ERR_TIMEOUT_SERIOUS = 2 ;
	
	int errCode = -1 ;
	
	public AMException(int err_c,String msg)
	{
		super(msg) ;
		this.errCode = err_c ;
	}
	
	public int getErrCode()
	{
		return this.errCode ;
	}
}
