package org.iottree.core.util.logger;

import java.util.*;

import org.iottree.core.util.logger.provider.ConsoleLogDo;


/**
 * logmanager which support switch logs at runtime
 * @author Jason Zhu
 *
 */
public class LoggerManager
{
	static boolean Is_Debug = true;
	static boolean Is_Info = true;
	static boolean Is_Trace=true;
	static boolean Is_Error=true;
	static boolean Is_Fatal=true;
	static boolean Is_Warn=true ;

	static
	{
//		providerMap.put("console",
//						"org.iottree.core.wfengine.log.provider.ConsoleLogProvider");
//		providerMap.put("null",
//						"org.iottree.core.wfengine.log.provider.NullLogProvider");
//		providerMap.put("null",
//			"org.iottree.core.wfengine.log.provider.Log4jProvider");
		
		
		
		//
		if(false)//if(AppConfig.isDebug())
		{
			
		}
		else
		{
			Is_Debug = false;
			Is_Info = false;
			Is_Trace = false;
			Is_Warn = false;
		}
	}
	

	static final ThreadLocal<Boolean> thLogOpen = new ThreadLocal<Boolean>()
	{
        @Override
        protected Boolean initialValue()
        {
        	return false;
        }
    };
    
    
	/**
	 * ���õ�ǰ��־���߳���������ʱ��
	 * �˷���һ���������Թ����У���log����ʱ�򿪺͹ر�
	 * ����������������Ϲر�
	 * @param bopen
	 */
	public static void setThreadLogOpen(boolean bopen)
	{
		thLogOpen.set(bopen) ;
	}


	private static HashMap<String,ILogger> id2log = new HashMap<String,ILogger>() ;
	
	private static boolean bInCtrl = false;
	
	private static HashSet<String> ctrlEnableIds = null ;
	/**
	 * ���ݵ�ǰLog�ṩ�ߣ���ȡָ��id����־����<br>
	 * �÷���Ӧ��Ϊ����ģ���ṩΨһ��ȡLogger�������ڡ�<br>
	 * ����ʹ�õ���־�Ĵ��룬�������Լ�����Logger���󡪡�ÿ��ʹ����־ʱͨ�����´��룺<br>
	 * <pre>
	 *     LoggerManager.getLogger(myid).info("xxxx") ;
	 * </pre>
	 * @param id ��־����id
	 * @return ��־����
	 */
	public static ILogger getLogger(String id)
	{
		ILogger logger = id2log.get(id);
		if(logger!=null)
			return logger ;
		
		synchronized(id2log)
		{
			if(logger!=null)
				return logger ;
			
			logger = new LoggerObj(id);
			id2log.put(id, logger);
			
			if(bInCtrl&&ctrlEnableIds!=null)
			{
				if(ctrlEnableIds.contains(id))
				{
					logger.setCtrl(ILogger.CTRL_ENABLE) ;
				}
				else
				{
					logger.setCtrl(ILogger.CTRL_DISABLE) ;
				}
			}
			return logger;
		}
	}
	
	/**
	 * ����
	 * @param c
	 * @return
	 */
	public static ILogger getLogger(Class c)
	{
		//return provider.getLogger(c.getCanonicalName());
		return getLogger(c.getCanonicalName());
		
	}
	
	
	/**
	 * �õ��ļ�Log
	 * @param logn
	 * @return
	 */
	public static ILogger getFileLogger(String fn)
	{
		return null;
	}
	/**
	 * ��õ�ǰ���е�Logger
	 * @return
	 */
	public static ILogger[] getAllLoggers()
	{
		ILogger[] rets = new ILogger[id2log.size()] ;
		id2log.values().toArray(rets) ;
		return rets ;
	}
	
	/**
	 * ����id���ö�Ӧ��logǿ�ƿ��ƴ򿪣��Ͳ��ֵ�ǿ�ƿ��ƹر�
	 * �÷����ṩ������ʱʹ�ã������������й����У��ı�log�Ŀ���״̬
	 * @param logid
	 */
	public static void setupLoggerInCtrl(HashSet<String> enableids)
	{
		setupLoggerInCtrl(enableids,false);
	}
	
	public static void setupLoggerInCtrl(HashSet<String> enableids,boolean btrace)
	{
		bInCtrl = true ;
		ctrlEnableIds = enableids ;
		
		for(ILogger l:getAllLoggers())
		{
			String id = l.getLoggerId() ;
			if(ctrlEnableIds!=null&&ctrlEnableIds.contains(id))
			{
				l.setCtrl(ILogger.CTRL_ENABLE) ;
				l.setStackTrace(btrace) ;
			}
			else
			{
				l.setCtrl(ILogger.CTRL_DISABLE) ;
				l.setStackTrace(false) ;
			}
		}
	}
	
	/**
	 * �������е�log��ȱʡ״̬
	 * �÷����ṩ�����Խ�����ʹ�ã������������й����У��ָ�log��ȱʡ����״̬
	 */
	public static void setupLoggerDefault()
	{
		bInCtrl = false ;
		
		for(ILogger l:getAllLoggers())
		{
			l.setCtrl(ILogger.CTRL_DEFAULT) ;
			l.setStackTrace(false) ;
		}
	}
	
	
	public static boolean isInCtrl()
	{
		return bInCtrl ;
	}
	
	public static HashSet<String> getInCtrlEnableIds()
	{
		if(bInCtrl)
			return ctrlEnableIds ;
		
		return null ;
	}
	
	private static ILogDo DEFAULT_LOGDO = new ConsoleLogDo() ;
	
	static ILogDo logDo = DEFAULT_LOGDO ;
	
	/**
	 * ��ÿ��LoggerObj����ʹ�õĻ�ȡ��������־�Ķ����Ķ���
	 * @return
	 */
	static ILogDo getLogDo()
	{
		return logDo ;
	}
	
	/**
	 * ���þ������־��������
	 * ������Service����״̬�£���Ҫʹ����ʱ�����Ӳ鿴��־��Ϣ
	 * @param ld
	 */
	public static void setLogDo(ILogDo ld)
	{
		if(ld==null)
		{
			logDo = DEFAULT_LOGDO ;
			return ;
		}
		
		logDo = ld ;
	}
}
