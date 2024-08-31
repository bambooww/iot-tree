package org.iottree.core.util.logger;

public interface ILogable
{
	public default ILogger LOG()
	{
		return LoggerManager.getLogger(this.getClass()) ;
	}
	
	public default void LOG_debug(String msg)
	{
		ILogger log = LOG() ;
		if(log.isDebugEnabled())
			log.debug(msg);
	}
	
	public default void LOG_debug(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isDebugEnabled())
			log.debug(msg,t);
	}
	
	public default void LOG_trace(String msg)
	{
		ILogger log = LOG() ;
		if(log.isTraceEnabled())
			log.trace(msg);
	}
	
	public default void LOG_trace(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isTraceEnabled())
			log.trace(msg,t);
	}
	
	public default void LOG_info(String msg)
	{
		ILogger log = LOG() ;
		if(log.isInfoEnabled())
			log.info(msg);
	}
	
	public default void LOG_info(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isInfoEnabled())
			log.info(msg,t);
	}
	
	public default void LOG_warn(String msg)
	{
		ILogger log = LOG() ;
		if(log.isWarnEnabled())
			log.warn(msg);
	}
	
	public default void LOG_warn(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isWarnEnabled())
			log.warn(msg,t);
	}
	
	public default void LOG_warn_debug(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isDebugEnabled())
			log.debug(msg, t);
		else if(log.isWarnEnabled())
			log.warn(msg);
	}
	
	public default void LOG_error(String msg)
	{
		ILogger log = LOG() ;
		if(log.isErrorEnabled())
			log.error(msg);
	}
	
	public default void LOG_error(String msg,Throwable t)
	{
		ILogger log = LOG() ;
		if(log.isErrorEnabled())
			log.error(msg,t);
	}
}
