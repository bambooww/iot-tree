package org.iottree.core.util.logger;

import java.io.*;
import java.util.*;

import org.iottree.core.util.Convert;

/**
 * һЩӦ����Ҫר����������־���Ա��ں����ļ��׷�ݡ�һ�����������ʹ���ļ���¼�ķ�ʽ����
 *  ��¼ʱ�䣬�����ʲô�µȵ�
 * @author Jason Zhu
 */
public class FileLogger
{
	String filePath = null ;
	
	public FileLogger(String fp)
	{
		filePath = fp ;
	}
	
	/**
	 * ��¼�ض���ʱ�䣬�û���������
	 * ϵͳ�Զ������ļ����е��ַ�������
	 * @param logdt
	 * @param username
	 * @param str
	 */
	public void logLine(Date logdt,String username,String str)
		throws IOException
	{
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(filePath,true) ;
			StringBuilder sb = new StringBuilder() ;
			sb.append(Convert.toFullYMDHMS(new Date())) ;
			sb.append("|") ;
			if(logdt!=null)
				sb.append(Convert.toFullYMDHMS(logdt)) ;
			sb.append("|") ;
			if(username!=null)
				sb.append(username) ;
			sb.append("|") ;
			if(str!=null)
				sb.append(str) ;
			sb.append("\r\n") ;
			
			fw.write(sb.toString());
		}
		finally
		{
			fw.close() ;
		}
	}
	
	public void logLine(String username,String str)
	throws IOException
	{
		logLine(null, username, str) ;
	}
}
