package org.iottree.core.util.logger.provider;

import java.io.*;
import java.util.*;
import javax.swing.*;

import org.iottree.core.util.logger.ILogDo;

/**
 * ����־��Ϣ�����������̨����־�ṩ��
 * @author Jason Zhu
 */
public class TextAreaLogProvider
	//implements ILogProvider
{
	JTextArea textArea = null ;
	int maxLineNum = 100 ;
	
	private HashMap loggers = new HashMap();
	
	public TextAreaLogProvider(JTextArea ta,int maxlinenum)
	{
		textArea = ta ;
		maxLineNum = maxlinenum ;
		if(maxLineNum<=0)
			maxLineNum = 100 ;
		textArea.setRows(maxlinenum);
	}

//	public synchronized ILogger getLogger(String id)
//	{
//		ILogger logger = (ILogger) loggers.get(id);
//		if (logger == null)
//		{
//			logger = new LoggerImpl(id);
//			loggers.put(id, logger);
//		}
//		return logger;
//	}

	/**
	 * ʵ��Logger�ӿڵ�Logger����
	 * <p>Copyright: Copyright (c) 2004</p>
	 * <p>Company: </p>
	 * @author Jason Zhu
	 * @version 2.0
	 */
	class LoggerImpl
		implements ILogDo
	{
		private String id;

		public LoggerImpl(String id)
		{
			this.id = id;
		}

		public String getLoggerId()
		{
			return id;
		}
		
		public void print(String msg)
		{
			printMessage("",msg) ;
		}
		
		public void printException(Exception ex)
		{
			printError("",ex) ;
		}

		public void fatal(String message)
		{
			printMessage("FATAL", message);
		}
		
		public void fatal(Throwable t)
		{
			printError("FATAL",t);
		}
		
		public void fatal(String msg,Throwable t)
		{
			printError("FATAL "+msg,t);
		}

		public void error(String message)
		{
			printMessage("ERROR", message);
		}
		
		public void error(Throwable t)
		{
			printError("ERROR",t);
		}
		
		public void error(String msg,Throwable t)
		{
			printError("ERROR "+msg,t);
		}

		public void warn(String message)
		{
			printMessage("WARN", message);
		}
		
		public void warn(String msg,Throwable t)
		{
			printError("WARN "+msg,t);
		}

		public void info(String message)
		{
			printMessage("INFO", message);
		}
		
		public void info(String msg,Throwable t)
		{
			printError("INFO "+msg,t);
		}

		public void trace(String msg)
		{
			printMessage("TRACE", msg);
		}
		
		public void trace(String msg,Throwable t)
		{
			printError("TRACE",t);
		}

		public boolean isTraceEnabled()
		{
			return true ;
		}
		
		public boolean isInfoEnabled()
		{
			return true ;
		}
		
		public boolean isWarnEnabled()
		{
			return true ;
		}
		
		public boolean isErrorEnabled()
		{
			return true ;
		}
		
		public boolean isFatalEnabled()
		{
			return true ;
		}
		/**
		 * ��ӡdebug�������Ϣ
		 * @param message
		 */
		public void debug(String message)
		{
			printMessage("DEBUG", message);
		}
		
		public void debug(String msg,Throwable t)
		{
			printError("DEBUG "+msg,t);
		}
		
		public boolean isDebugEnabled()
		{
			return true ;
		}

		private void printError(String level,Throwable t)
		{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.println(level);
			t.printStackTrace(pw);
			textArea.append(sw.getBuffer().toString());
		}
		/**	Pring a message at the specified level.
		 @param level The log level
		 @param message The message
		 */

		private void printMessage(String level, String message)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(id);
			buffer.append(" [");
			buffer.append(level);
			buffer.append("]: ");
			buffer.append(message);

			//System.out.println(buffer.toString());
			
			textArea.append(buffer.toString());
		}
	}
}
