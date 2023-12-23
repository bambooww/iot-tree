package org.slf4j.impl;

import java.io.PrintStream;
import java.util.Date;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public class IOTTLogger extends MarkerIgnoringBase implements ILogger
{
		
    // The OFF level can only be used in configuration files to disable logging.
    // It has
    // no printing method associated with it in o.s.Logger interface.
    protected static final int LOG_LEVEL_OFF = LOG_LEVEL_ERROR + 10;
    
	//ILogger ilog = null ;
	
	String name = null ;
	
	private String shortLogName = null ;
	
	static LogConfig config = null;
	
	private static boolean INITIALIZED = false;

    static void lazyInit() {
        if (INITIALIZED) {
            return;
        }
        INITIALIZED = true;
        config = new LogConfig() ;
		config.init();
    }
	
    
    static
    {
    	lazyInit();
    }

	
	public IOTTLogger(String name)
	{
//		this.ilog = log ;
		this.name = name ;
		shortLogName = this.computeShortName();
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	

	@Override
	public String getLoggerId()
	{
		return name;
	}

	
	private String getFormattedDate() {
        return Convert.toFullYMDHMS(new Date()) ;
    }
	
	 private String computeShortName() {
	        return name.substring(name.lastIndexOf(".") + 1);
	    }
	 
	 protected String renderLevel(int level) {
	        switch (level) {
	        case LOG_LEVEL_TRACE:
	            return "TRACE";
	        case LOG_LEVEL_DEBUG:
	            return ("DEBUG");
	        case LOG_LEVEL_INFO:
	            return "INFO";
	        case LOG_LEVEL_WARN:
	            return "WARN";
	        case LOG_LEVEL_ERROR:
	            return "ERROR";
	        }
	        throw new IllegalStateException("Unrecognized level [" + level + "]");
	    }

	private void log(int level, String message, Throwable t) {
        if (!isLevelEnabled(level)) {
            return;
        }

        StringBuilder buf = new StringBuilder(32);

        // Append date-time if so configured
        buf.append(getFormattedDate());
        buf.append(' ');
       

        // Append current thread name if so configured
        if (false)
        {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
        }

        //if (CONFIG_PARAMS.levelInBrackets)
            buf.append('[');

        // Append a readable representation of the log level
        String levelStr = renderLevel(level);
        buf.append(levelStr);
        //if (CONFIG_PARAMS.levelInBrackets)
        buf.append(']');
        buf.append(' ');

        // Append the name of the log instance if so configured
        buf.append(String.valueOf(shortLogName)).append(" - ");
       
        // Append the message
        buf.append(message);

        write(buf, t);

    }
	
	 protected void writeThrowable(Throwable t, PrintStream targetStream) {
	        if (t != null) {
	            t.printStackTrace(targetStream);
	        }
	    }
	
	void write(StringBuilder buf, Throwable t) {
        PrintStream targetStream = config.outputChoice.getTargetPrintStream();

        targetStream.println(buf.toString());
        writeThrowable(t, targetStream);
        targetStream.flush();
        
        //ilog.
    }
	
    private void formatAndLog(int level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }
    
	
    /**
     * A simple implementation which logs messages of level TRACE according to
     * the format outlined above.
     */
    public void trace(String msg) {
        log(LOG_LEVEL_TRACE, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     */
    public void trace(String format, Object param1) {
        formatAndLog(LOG_LEVEL_TRACE, format, param1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     */
    public void trace(String format, Object param1, Object param2) {
        formatAndLog(LOG_LEVEL_TRACE, format, param1, param2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * TRACE according to the format outlined above.
     */
    public void trace(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_TRACE, format, argArray);
    }

    /** Log a message of level TRACE, including an exception. */
    public void trace(String msg, Throwable t) {
        log(LOG_LEVEL_TRACE, msg, t);
    }

    /** Are {@code debug} messages currently enabled? */
//    public boolean isDebugEnabled() {
//        return isLevelEnabled(LOG_LEVEL_DEBUG);
//    }

    /**
     * A simple implementation which logs messages of level DEBUG according to
     * the format outlined above.
     */
    public void debug(String msg) {
        log(LOG_LEVEL_DEBUG, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     */
    public void debug(String format, Object param1) {
        formatAndLog(LOG_LEVEL_DEBUG, format, param1, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     */
    public void debug(String format, Object param1, Object param2) {
        formatAndLog(LOG_LEVEL_DEBUG, format, param1, param2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * DEBUG according to the format outlined above.
     */
    public void debug(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
    }

    /** Log a message of level DEBUG, including an exception. */
    public void debug(String msg, Throwable t) {
        log(LOG_LEVEL_DEBUG, msg, t);
    }

    /**
     * A simple implementation which logs messages of level INFO according to
     * the format outlined above.
     */
    public void info(String msg) {
        log(LOG_LEVEL_INFO, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     */
    public void info(String format, Object arg) {
        formatAndLog(LOG_LEVEL_INFO, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     */
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * INFO according to the format outlined above.
     */
    public void info(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_INFO, format, argArray);
    }

    /** Log a message of level INFO, including an exception. */
    public void info(String msg, Throwable t) {
        log(LOG_LEVEL_INFO, msg, t);
    }

    
    /**
     * A simple implementation which always logs messages of level WARN
     * according to the format outlined above.
     */
    public void warn(String msg) {
        log(LOG_LEVEL_WARN, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    public void warn(String format, Object arg) {
        formatAndLog(LOG_LEVEL_WARN, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * WARN according to the format outlined above.
     */
    public void warn(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_WARN, format, argArray);
    }

    /** Log a message of level WARN, including an exception. */
    public void warn(String msg, Throwable t) {
        log(LOG_LEVEL_WARN, msg, t);
    }

    /** Are {@code error} messages currently enabled? */
   
    /**
     * A simple implementation which always logs messages of level ERROR
     * according to the format outlined above.
     */
    public void error(String msg) {
        log(LOG_LEVEL_ERROR, msg, null);
    }

    /**
     * Perform single parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     */
    public void error(String format, Object arg) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     */
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
    }

    /**
     * Perform double parameter substitution before logging the message of level
     * ERROR according to the format outlined above.
     */
    public void error(String format, Object... argArray) {
        formatAndLog(LOG_LEVEL_ERROR, format, argArray);
    }

    /** Log a message of level ERROR, including an exception. */
    public void error(String msg, Throwable t) {
        log(LOG_LEVEL_ERROR, msg, t);
    }


//	@Override
//	public boolean isStackTrace()
//	{
//		return false;
//	}
//
//	@Override
//	public void setStackTrace(boolean b)
//	{
//		
//	}

	
//	@Override
//	public void error(String msg,Throwable t)
//	{
//		
//	}

}
