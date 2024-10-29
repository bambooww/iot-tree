package org.iottree.core.util.cache;

/**
 * Cacher�����
 */

public class Shell
{
	 /**
	  * ʱ��α��棬����Ϊ��λ
	  */
	 //public static final short BASE_MONTH = 1 ;
	 /**
	  * ʱ��α��棬����Ϊ��λ
	  */
	 //public static final short BASE_WEEK = 2 ;
	 /**
	  * ʱ��α��棬����Ϊ��λ
	  */
	 //public static final short BASE_DAY = 3 ;
	 /**
	  *
	  */
	 Object key = null ;
	 /**
	  * ���Դ����ڴ��е�ʱ���
	  */
	 long lLiveTime = -1 ;
	 /**
	  * ����ʱ���
	  */
	 long lExpireTime = -1 ;
	 /**
	  * �Ƿ�ɱ����¡����������ʺ󣬶Թ���ʱ�����и���
	  */
	 boolean bRefresh = true ;
	 /**
	  * ��cache������
	  */
	 Object object = null ;



	 public Shell ()
	 {}

	 /**
	  * ���ñ���һ������
	  */
	 public Shell (Object key,Object ob)
	 {
		  this.key = key ;
		  object = ob ;
	 }
	 /**
	  * ����һ����ʱ�����޵Ķ���refresh=true
	  *@param ob ���������
	  *@param livetime ������ʱ��
	  */
	 public Shell (Object key,Object ob,long livetime)
	 {
		this (key,ob) ;
		this.lLiveTime = livetime ;
		if (livetime>0)
			 lExpireTime = System.currentTimeMillis () + livetime ;
	 }
	 /**
	  * ����һ����ʱ�����޵Ķ���
	  *@param ob ���������
	  *@param livetime ������ʱ��
	  *@param refresh �����ϣ������ʱ�䱻����=false
	  */
	 public Shell (Object key,Object ob,long livetime,boolean refresh)
	 {
		  this (key,ob,livetime) ;
		  bRefresh = refresh ;
	 }

	 public Object clone ()
	 {
		  return new Shell (this.key,this.object,this.lLiveTime,this.bRefresh) ;
	 }
	 /*
	 public Shell (Object ob, short base,long start,long end)
	 {
		object = ob ;
		if (lifetime>0)
			timeout = System.currentTimeMillis () + lifetime ;
		else
			timeout = -1 ; //cache it forever
	 }
	 */
	 public Object getKey ()
	 {
		  return key ;
	 }
	 /**
	  * ����ʱ���Ƿ�ɱ�����
	  */
	 public boolean isRefresh ()
	 {
		  return bRefresh ;
	 }

	 public void setRefresh (boolean brefresh)
	 {
		  bRefresh = brefresh ;
	 }
	 /**
	  * �ж��Ƿ����
	  */
	 public boolean isTimeOut ()
	 {
		  if (lExpireTime<0)
			   return false ;
		  else if (lExpireTime>System.currentTimeMillis())
			   return false ;
		  else
			   return true ;
	 }

	 public long getLiveTime ()
	 {
		  return lLiveTime ;
	 }
	 /**
	  * �鿴���ݣ����Թ���ʱ�����κ���
	  */
	 public Object peekContent ()
	 {
		  return object ;
	 }
	 /**
	  * �õ���cache�����ݣ����Ҫrefresh����Թ���ʱ����и���
	  */
	 public Object getContent ()
	 {
		if (lExpireTime>0&&bRefresh)
		{
			 lExpireTime = System.currentTimeMillis () + lLiveTime ;
		}

		return object ;
	 }
	 /**
	  * ֱ�����ù���ʱ��
	  */
	 public void setExpireTime (long exptime)
	 {
		  this.lExpireTime = exptime ;
	 }

	 public void setLiveTime (long livetime)
	 {
		  this.lLiveTime = livetime ;

		if (livetime>0)
			 lExpireTime = System.currentTimeMillis () + livetime ;
		else
			   lExpireTime = -1 ;
	 }
	 /**
	  * ���ñ���������
	  */
	 public void setContent (Object ob)
	 {
		  object = ob ;
	 }
	 
	 void closeContent()
	 {
		 if(object instanceof AutoCloseable)
		 {
			 try
			 {
				 ((AutoCloseable)object).close();
			 }
			 catch(Exception e)
			 {
				 e.printStackTrace();
			 }
		 }
	 }

	 public void clear ()
	 {
		  lLiveTime = -1 ;
		  lExpireTime = -1 ;
		  bRefresh = true ;
		  object = null ;
	 }

	 public String toString ()
	 {
		  return "("+key+"=" +object +"," + lLiveTime +","+lExpireTime +","+bRefresh+ ")" ;
	 }

	 Shell prev = null ;
	 Shell next = null ;

	 public Shell getPrev ()
	 {
		  return prev ;
	 }
	 public void setPrev (Shell sh)
	 {
		  prev = sh ;
	 }

	 public Shell getNext ()
	 {
		  return next ;
	 }
	 public void setNext (Shell sh)
	 {
		  next = sh ;
	 }
}