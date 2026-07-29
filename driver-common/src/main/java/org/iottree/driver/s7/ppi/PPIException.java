package org.iottree.driver.s7.ppi;

/**
 * 
 */
public class PPIException extends Exception
{
	private static final long serialVersionUID = 7350994271896884309L;

	public PPIException()
	{
		super();
	}

	public PPIException(String message)
	{
		super(message);
	}

	public PPIException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PPIException(Throwable cause)
	{
		super(cause);
	}

	protected PPIException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
