package org.iottree.core.util.logger;

/**
 * ��־����
 * @author Jason Zhu
 */
public interface ILogger
{
	/**
	 * ��Ծ����log����ֵ��ȱʡ����¼̳�ȫ��
	 */
	public static final int CTRL_DEFAULT = 0 ;
	
	/**
	 * ��Ծ����log����ֵ���Զ�������´�
	 */
	public static final int CTRL_ENABLE = 1 ;
	
	/**
	 * ��Ծ����log����ֵ���Զ�������¹ر�
	 */
	public static final int CTRL_DISABLE = -1 ;
	
	
	public String getLoggerId() ;
	
	/**
	 * ��õ�ǰ�Ŀ���
	 * @return
	 */
	public int getCtrl();
	
	/**
	 * ���õ�ǰ�Ŀ���
	 * @param c
	 */
	public void setCtrl(int c) ;
	
	public boolean isStackTrace();
	
	public void setStackTrace(boolean b) ;
	
	/**
	 * ����System.out.println���������ܿ�������
	 * @param msg
	 */
	public void print(String msg) ;
	
	public void println(String msg) ;
	
	public void printException(Exception ex) ;

	public void fatal(String msg);
	
	public void fatal(Throwable t);

	public void error(String msg);
	
	public void error(Throwable t);

	public void warn(String msg);
	
	public void warn(String msg,Throwable t);

	public void info(String msg);
	
	public void info(String msg,Throwable t);

	public void debug(String msg);
	
	public void debug(String msg,Throwable t);
	
	public void trace(String msg);
	
	public void trace(String msg,Throwable t);

	public boolean isTraceEnabled() ;
	
	public boolean isDebugEnabled() ;
	
	public boolean isInfoEnabled();
	
	public boolean isWarnEnabled() ;
	
	public boolean isErrorEnabled();
	
	public boolean isFatalEnabled() ;
	//public boolean is
	
	
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
