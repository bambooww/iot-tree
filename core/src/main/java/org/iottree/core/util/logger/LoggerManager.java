package org.iottree.core.util.logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.provider.ConsoleLogDo;
import org.slf4j.impl.IOTTLogger;


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
	


	private static HashMap<String,ILogger> id2log = new HashMap<String,ILogger>() ;
	
	private static boolean bInCtrl = false;
	
	private static HashSet<String> ctrlEnableIds = null ;
	
	static int Default_LVL = ILogger.LOG_LEVEL_WARN ;

	static HashMap<String,Integer> configLog2Lvl = null ;
	
	static
	{

	}
	
	public static void saveLogConfig() throws FileNotFoundException, IOException
	{
		String odir = Config.getDataOthersDir() ;
		File dirf = new File(odir);
		if(!dirf.exists())
			dirf.mkdirs();
		
		File logf = new File(dirf,"log.txt") ;
		try(FileOutputStream fos = new FileOutputStream(logf);)
		{
			int deflvl = getDefaultLogLevel() ;
			fos.write((deflvl+"\r\n").getBytes());
			
			for(ILogger log:getAllLoggers())
			{
				int lvl = log.getCurrentLogLevel() ;
				if(lvl==deflvl)
					continue ;
				fos.write((log.getLoggerId()+"="+lvl+"\r\n").getBytes());
			}
		}
	}
	
	public static void loadLogConfig() throws FileNotFoundException, IOException
	{
		String odir = Config.getDataOthersDir() ;
		File dirf = new File(odir);
		if(!dirf.exists())
			return ;
		
		File logf = new File(dirf,"log.txt") ;
		if(!logf.exists())
			return ;
		
		try(FileInputStream fis = new FileInputStream(logf);)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(fis)) ;
			//load default
			String ln = br.readLine() ;
			if(Convert.isNullOrEmpty(ln))
				return ;
		
			int defv = Integer.parseInt(ln) ;
			setDefaultLogLevel(defv) ;
			HashMap<String,Integer> log2lvl = new HashMap<>() ; 
			while((ln=br.readLine())!=null)
			{
				ln=  ln.trim() ;
				if(Convert.isNullOrEmpty(ln)||ln.startsWith("#"))
					continue ;
				int k = ln.indexOf("=") ;
				if(k<=0)
					continue ;
				String logid = ln.substring(0,k).trim() ;
				String strlvl = ln.substring(k+1).trim() ;
				if(Convert.isNullOrEmpty(strlvl))
					continue ;
				int lvl = Integer.parseInt(strlvl) ;
				log2lvl.put(logid, lvl) ;
			}
			configLog2Lvl = log2lvl ;
		}
	}
	
	public static int getDefaultLogLevel()
	{
		return Default_LVL;
	}
	
	public static boolean setDefaultLogLevel(int lvl)
	{
		switch(lvl)
		{
		case ILogger.LOG_LEVEL_TRACE:
		case ILogger.LOG_LEVEL_DEBUG:
		case ILogger.LOG_LEVEL_INFO:
		case ILogger.LOG_LEVEL_WARN:
		case ILogger.LOG_LEVEL_ERROR:
			break ;
		default:
			return false;
		}
		Default_LVL = lvl ;
		return true;
	}
	
	static
	{
		try
		{
			loadLogConfig() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(configLog2Lvl!=null)
		{
			for(Map.Entry<String, Integer> log2lvl:configLog2Lvl.entrySet())
			{
				getLogger(log2lvl.getKey()).setCurrentLogLevel(log2lvl.getValue());
			}
		}
	}
	
	
	public static void setLogToDefaultAll()
	{
		int deflvl = getDefaultLogLevel() ;
		for(ILogger log:getAllLoggers())
		{
			log.setCurrentLogLevel(deflvl);
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
	 * @param bopen
	 */
	public static void setThreadLogOpen(boolean bopen)
	{
		thLogOpen.set(bopen) ;
	}


	public static ILogger getLogger(String id)
	{
		ILogger logger = id2log.get(id);
		if(logger!=null)
			return logger ;
		
		synchronized(id2log)
		{
			if(logger!=null)
				return logger ;
			
			logger = new IOTTLogger(id);//LoggerObj(id);
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
	 * @param c
	 * @return
	 */
	public static ILogger getLogger(Class c)
	{
		//return provider.getLogger(c.getCanonicalName());
		return getLogger(c.getCanonicalName());
		
	}
	
	public static ILogger getLoggerExisted(String id)
	{
		return id2log.get(id);
	}
	/**
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
	
	
//	public static void setupLoggerInCtrl(HashSet<String> enableids)
//	{
//		setupLoggerInCtrl(enableids,false);
//	}
//	
//	public static void setupLoggerInCtrl(HashSet<String> enableids,boolean btrace)
//	{
//		bInCtrl = true ;
//		ctrlEnableIds = enableids ;
//		
//		for(ILogger l:getAllLoggers())
//		{
//			String id = l.getLoggerId() ;
//			if(ctrlEnableIds!=null&&ctrlEnableIds.contains(id))
//			{
//				l.setCtrl(ILogger.CTRL_ENABLE) ;
//				l.setStackTrace(btrace) ;
//			}
//			else
//			{
//				l.setCtrl(ILogger.CTRL_DISABLE) ;
//				l.setStackTrace(false) ;
//			}
//		}
//	}
	
	
	public static boolean setupLogger(String logid,int ctrl,int log_level)
	{
		ILogger log = getLogger(logid) ;
		if(log==null)
			return false;
		
		log.setCtrl(ctrl);
		log.setCurrentLogLevel(log_level);
		return true ;
	}
	
	public static void setupLoggerDefault()
	{
		bInCtrl = false ;
		
		for(ILogger l:getAllLoggers())
		{
			l.setCtrl(ILogger.CTRL_DEFAULT) ;
			//l.setStackTrace(false) ;
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
