package org.iottree.core.util.logger;

/**
 * ��־����
 * @author Jason Zhu
 */
public interface ILogger
{
	/**
	 * no enable
	 */
	public static final int CTRL_DEFAULT = 0 ;
	
	/**
	 * enable
	 */
	public static final int CTRL_ENABLE = 1 ;
	
	/**
	 * ctrl disable
	 */
	public static final int CTRL_DISABLE = -1 ;
	
	static final public int LOG_LEVEL_TRACE = 00;
    static final public int LOG_LEVEL_DEBUG = 10;
    static final public int LOG_LEVEL_INFO = 20;
    static final public int LOG_LEVEL_WARN = 30;
    static final public int LOG_LEVEL_ERROR = 40;
    
    static final public int[] LEVELS = {LOG_LEVEL_TRACE,LOG_LEVEL_DEBUG,LOG_LEVEL_INFO,LOG_LEVEL_WARN,LOG_LEVEL_ERROR};
    static final public String[] LEVELS_TT = {"trace","debug","info","warn","error"};
  
	
	public String getLoggerId() ;
	
	/**
	 * 
	 * @return
	 */
	public int getCtrl();
	
	public String getCtrlTitle();
	/**
	 * 
	 * @param c
	 */
	public void setCtrl(int c) ;
	
	public void setCurrentLogLevel(int loglvl);
    
    public int getCurrentLogLevel();
	
//	public boolean isStackTrace();
//	
//	public void setStackTrace(boolean b) ;
	
//	/**
//	 * ����System.out.println���������ܿ�������
//	 * @param msg
//	 */
//	public void print(String msg) ;
//	
//	public void println(String msg) ;
//	
//	public void printException(Exception ex) ;

//	public void fatal(String msg);
//	
//	public void fatal(Throwable t);

	public void error(String msg);
	
	public void error(String msg,Throwable t);
	
	public default void error(Throwable t)
	{
		error("",t);
	}

	public void warn(String msg);
	
	public void warn(String msg,Throwable t);
	
	public default void warn(Throwable t)
	{
		warn("",t);
	}

	public void info(String msg);
	
	public void info(String msg,Throwable t);
	
	public default void info(Throwable t)
	{
		info("",t);
	}

	public void debug(String msg);
	
	public void debug(String msg,Throwable t);
	
	public default void debug(Throwable t)
	{
		debug("",t);
	}
	
	public default void warn_debug(String msg,Throwable t)
	{
		if(isDebugEnabled())
			debug(msg, t);
		else if(isWarnEnabled())
			warn(msg);
	}
	
	public void trace(String msg);
	
	public void trace(String msg,Throwable t);
	
	public default void trace(Throwable t)
	{
		trace("",t);
	}

	public boolean isTraceEnabled() ;
	
	public boolean isDebugEnabled() ;
	
	public boolean isInfoEnabled();
	
	public boolean isWarnEnabled() ;
	
	public boolean isErrorEnabled();
	
	public void println(String s) ;
	
	public void print(String s) ;
	
//	public void setTraceEnabled(boolean b) ;
//	
//	public void setDebugEnabled(boolean b) ;
//	
//	public void setInfoEnabled(boolean b) ;
//	
//	public void setWarnEnabled(boolean b) ;
//	
//	public void setErrorEnabled(boolean b) ;
//	
//	public void setFatalEnabled(boolean b) ;
}
