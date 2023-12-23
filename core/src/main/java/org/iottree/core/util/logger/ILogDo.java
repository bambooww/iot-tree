package org.iottree.core.util.logger;

public interface ILogDo
{
	/**
	 * ����System.out.println���������ܿ�������
	 * @param msg
	 */
	public void print(String msg) ;
	
	public void printException(Exception ex) ;
	
	public void fatal(String msg);
	
	public void fatal(Throwable t);

	public void error(String msg);
	
	public void error(String msg,Throwable t);

	public void warn(String msg);
	
	public void warn(String msg,Throwable t);

	public void info(String msg);
	
	public void info(String msg,Throwable t);

	public void debug(String msg);
	
	public void debug(String msg,Throwable t);
	
	public void trace(String msg);
	
	public void trace(String msg,Throwable t);
}
