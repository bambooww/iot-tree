package org.iottree.core.util;

import java.io.File;
import java.util.Date;

import org.iottree.core.Config;

public class IdCreator
{
public static final int MAX_ID_LEN =  20 ;
	
	private static String serverId; 
	static
	{
		serverId = Config.getServerId() ;
		if(Convert.isNullOrEmpty(serverId))
			serverId="0" ;
	}
	/**
	 * 从一个输入的uuid计算获得
	 * @param uuid
	 * @return
	 */
	public static String transFromUUID(String uuid)
	{
		StringBuilder sb = new StringBuilder() ;
		String[] ss = uuid.split("-") ;
		for(String s:ss)
		{
			long l = Long.parseLong(s, 16);
			sb.append(long2id36(l)) ;
		}
		
		return sb.toString() ;
	}
	
	//public static 
	/**
	 * long值转换成36进制的表示
	 * @param l
	 * @return
	 */
	private static String long2id36(long l)
	{
		return long2id36(l,0);
	}
	
	private static String long2id36(long l,int minlen)
	{
		String ret = "" ;
		long tmpl;
		do
		{
			tmpl = l % 36 ;
			if(tmpl<10)
				ret = ((char)('0'+tmpl))+ret ;
			else
				ret = ((char)('A'+tmpl-10))+ret ;
			l /= 36 ;
		}while(l!=0);
		
		int bl = minlen-ret.length() ;
		for(int i = 0 ; i < bl ; i ++)
			ret = '0'+ret ;
		return ret ;
	}
	
	static long ID36_V[] = new long[10] ;
	
	static
	{
		ID36_V[0] = 1 ;
		for(int k = 1 ; k < 10 ; k ++)
			ID36_V[k] = ID36_V[k-1]*36 ;
	}
	
	private static long id36_to_long(String strid36)
	{
		long rl = 0 ;
		int s = strid36.length() ;
		for(int i = 0 ; i < s ; i ++)
		{
			char c = strid36.charAt(s-i-1) ;
			int k;
			if(c>='0'&&c<='9')
				k = c - '0' ;
			else if(c>='A'&&c<='Z')
				k = c - 'A'+10 ;
			else
				throw new IllegalArgumentException("invalid id 36="+strid36) ;
			
			rl += ID36_V[i]*k ;
		}
		
		return rl ;
	}
	/**
	 * 从一个36进制表示的id转换成对应的UUID
	 * @param id36
	 * @return
	 */
	public static String transToUUID(String id36)
	{
		throw new RuntimeException("not impl") ;
	}
	
	//36^10有11万年的跨度
	private static int DT_MINLEN = 10 ;
	
	static Object locker = new Object() ;
	static long LAST_CT = -1 ;
	static long SEQ_COUNT = 1 ;
	static long MAX_SEQ_COUNT = 36*36*36*36 ;
	/**
	 * 获得一个新id  10char时间 + 4subcount + ServerID
	 * @return
	 */
	public static String newSeqId()
	{
		//return transFromUUID(UUID.randomUUID().toString()) ;
		long ct = System.currentTimeMillis() ;
		synchronized(locker)
		{
			if(ct==LAST_CT)
			{
				SEQ_COUNT++ ;
				if(SEQ_COUNT==MAX_SEQ_COUNT)
				{
					SEQ_COUNT = 1 ;
					while((ct=System.currentTimeMillis())==LAST_CT)
					{//force ct to next ms
						try
						{
							Thread.sleep(0, 100000);
						}
						catch(Exception e) {}
					}
				}
			}
			else
			{
				LAST_CT = ct ;
				SEQ_COUNT = 1;
			}
		}
		
		StringBuilder sb = new StringBuilder() ;
		sb.append(long2id36(ct,DT_MINLEN))
			.append(long2id36(SEQ_COUNT,4)).append(serverId) ;

		return sb.toString() ;
	}
	
	/**
	 * 从顺序号里面提取时间的毫秒数
	 * 该方法用来支持通过seqid定位一些相关的时间
	 * @param seqid
	 * @return
	 */
	public static long extractTimeInMillInSeqId(String seqid)
	{
		if(seqid==null)
			return -1 ;
		
		if(seqid.length()<=DT_MINLEN)
			return -1 ;
		
		//read 10 char
		String c10 = seqid.substring(0,DT_MINLEN) ;
		
		return id36_to_long(c10) ;
	}
	
	
}
