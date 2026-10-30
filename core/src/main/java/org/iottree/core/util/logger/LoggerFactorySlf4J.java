package org.iottree.core.util.logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class LoggerFactorySlf4J implements ILoggerFactory
{
	ConcurrentMap<String, Logger> loggerMap;

	public LoggerFactorySlf4J()
	{
		loggerMap = new ConcurrentHashMap<String, Logger>();

	}

	@Override
	public Logger getLogger(String name)
	{

		Logger slf4jLogger = loggerMap.get(name);
		if (slf4jLogger != null)
		{
			return null;
		}

		return null;
	}

}

class MyLogger implements Logger
{
	String name = null;

	public MyLogger(String n)
	{
		this.name = n;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isTraceEnabled()
	{
		return false;
	}

	@Override
	public void trace(String msg)
	{

	}

	@Override
	public void trace(String format, Object arg)
	{

	}

	@Override
	public void trace(String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void trace(String format, Object... arguments)
	{

	}

	@Override
	public void trace(String msg, Throwable t)
	{

	}

	@Override
	public boolean isTraceEnabled(Marker marker)
	{
		return false;
	}

	@Override
	public void trace(Marker marker, String msg)
	{

	}

	@Override
	public void trace(Marker marker, String format, Object arg)
	{

	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void trace(Marker marker, String format, Object... argArray)
	{

	}

	@Override
	public void trace(Marker marker, String msg, Throwable t)
	{

	}

	@Override
	public boolean isDebugEnabled()
	{
		return false;
	}

	@Override
	public void debug(String msg)
	{

	}

	@Override
	public void debug(String format, Object arg)
	{

	}

	@Override
	public void debug(String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void debug(String format, Object... arguments)
	{

	}

	@Override
	public void debug(String msg, Throwable t)
	{

	}

	@Override
	public boolean isDebugEnabled(Marker marker)
	{
		return false;
	}

	@Override
	public void debug(Marker marker, String msg)
	{

	}

	@Override
	public void debug(Marker marker, String format, Object arg)
	{

	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void debug(Marker marker, String format, Object... arguments)
	{

	}

	@Override
	public void debug(Marker marker, String msg, Throwable t)
	{

	}

	@Override
	public boolean isInfoEnabled()
	{
		return false;
	}

	@Override
	public void info(String msg)
	{

	}

	@Override
	public void info(String format, Object arg)
	{

	}

	@Override
	public void info(String format, Object arg1, Object arg2)
	{
	}

	@Override
	public void info(String format, Object... arguments)
	{

	}

	@Override
	public void info(String msg, Throwable t)
	{

	}

	@Override
	public boolean isInfoEnabled(Marker marker)
	{
		return false;
	}

	@Override
	public void info(Marker marker, String msg)
	{

	}

	@Override
	public void info(Marker marker, String format, Object arg)
	{

	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void info(Marker marker, String format, Object... arguments)
	{

	}

	@Override
	public void info(Marker marker, String msg, Throwable t)
	{

	}

	@Override
	public boolean isWarnEnabled()
	{
		return false;
	}

	@Override
	public void warn(String msg)
	{

	}

	@Override
	public void warn(String format, Object arg)
	{

	}

	@Override
	public void warn(String format, Object... arguments)
	{

	}

	@Override
	public void warn(String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void warn(String msg, Throwable t)
	{

	}

	@Override
	public boolean isWarnEnabled(Marker marker)
	{
		return false;
	}

	@Override
	public void warn(Marker marker, String msg)
	{

	}

	@Override
	public void warn(Marker marker, String format, Object arg)
	{

	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void warn(Marker marker, String format, Object... arguments)
	{

	}

	@Override
	public void warn(Marker marker, String msg, Throwable t)
	{

	}

	@Override
	public boolean isErrorEnabled()
	{
		return false;
	}

	@Override
	public void error(String msg)
	{

	}

	@Override
	public void error(String format, Object arg)
	{

	}

	@Override
	public void error(String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void error(String format, Object... arguments)
	{

	}

	@Override
	public void error(String msg, Throwable t)
	{

	}

	@Override
	public boolean isErrorEnabled(Marker marker)
	{
		return false;
	}

	@Override
	public void error(Marker marker, String msg)
	{

	}

	@Override
	public void error(Marker marker, String format, Object arg)
	{

	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2)
	{

	}

	@Override
	public void error(Marker marker, String format, Object... arguments)
	{

	}

	@Override
	public void error(Marker marker, String msg, Throwable t)
	{

	}

}
