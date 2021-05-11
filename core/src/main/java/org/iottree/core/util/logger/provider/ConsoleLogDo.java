package org.iottree.core.util.logger.provider;

import java.util.*;

import org.iottree.core.util.logger.ILogDo;

/**
 * ����־��Ϣ�����������̨����־�ṩ��
 * @author Jason Zhu
 */
public class ConsoleLogDo implements ILogDo
{

		public ConsoleLogDo()
		{
			
		}
		
		public void print(String msg)
		{
			System.out.print(msg) ;
		}
		
		public void printException(Exception ex)
		{
			ex.printStackTrace() ;
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

		
		
		public boolean isWarnEnabled()
		{
			return true;//AppConfig.isDebug() ;
		}
		
		public boolean isErrorEnabled()
		{
			return true;
		}
		
		public boolean isFatalEnabled()
		{
			return true;
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

		public void trace(String msg)
		{
			printMessage("TRACE", msg);
		}
		
		public void trace(String msg,Throwable t)
		{
			printMessage("TRACE", msg);
			t.printStackTrace();
		}

		
		private void printError(String level,Throwable t)
		{
			System.out.println(level);
			t.printStackTrace(System.out);
		}
		/**	Pring a message at the specified level.
		 @param level The log level
		 @param message The message
		 */

		private void printMessage(String level, String message)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(" [");
			buffer.append(level);
			buffer.append("]: ");
			buffer.append(message);

			System.out.println(buffer.toString());
		}
}
