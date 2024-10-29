package org.iottree.core.util.cache;

import java.io.* ;
import java.util.* ;
/**
 * Cacher�Ļ�����
 * �û�����ʹ��Hashtable&������ʹ�ü����Կ��ٵĻ������
 * �ֿ��Կ��ƻ������ĳ���,ͬʱʵ��LRU�㷨�Ծ����ݽ����ų�
 */

public class CacherBuffer
{
	 int iMaxBufferLen = Integer.MAX_VALUE ;
	 /**
	  * ����ָ��Shell˫���������βָ�룬������Ҳ��һ��Shell
	  * ����prevָ��β����nextָ������ͷ
	  */
	 Shell shHead = new Shell () ;
	 Hashtable<Object,Shell> htBuffer = new Hashtable<> () ;
	 
	 Cacher<?,?> belongTo = null ;

	 public CacherBuffer (Cacher<?,?> bt)
	 {
		 this.belongTo = bt ;
		  //create a empty list
		  shHead.setPrev (shHead) ;
		  shHead.setNext (shHead) ;
	 }

	 public CacherBuffer (Cacher<?,?> bt,int len)
	 {
		  this (bt) ;
		  if (len<=0)
			   throw new RuntimeException ("Error:CacherBuffer cannot be set to <=0!") ;
		  iMaxBufferLen = len ;
	 }

	 /**
	  * ��һ���µ�shell�ӵ������ͷ��
	  */
	 private void addToListHead (Shell newsh)
	 {
		  if (newsh.getPrev()!=null||newsh.getNext()!=null)
			   throw new RuntimeException ("Shell cannot be added because of existing in buffer!") ;
		  newsh.setPrev (shHead) ;
		  newsh.setNext (shHead.getNext()) ;
		  shHead.getNext().setPrev (newsh) ;
		  shHead.setNext (newsh) ;
	 }


	 private Shell removeListTail ()
	 {
		  Shell tmpsh = getListTail () ;
		  if (tmpsh!=null)
			   removeFromList (tmpsh,true) ;
		  return tmpsh ;
	 }
	 /**
	  *
	  */
	 private void removeFromList (Shell sh,boolean bclose)
	 {
		  if (sh.getPrev()==null||sh.getNext()==null)
			   throw new RuntimeException ("Shell cannot remove from list because of not existing in buffer!") ;
		  if (sh==shHead)
			   throw new RuntimeException ("Cannot remove head!") ;
		  sh.getPrev().setNext (sh.getNext()) ;
		  sh.getNext().setPrev (sh.getPrev()) ;
		  sh.setPrev (null) ;
		  sh.setNext (null) ;
		  
		  if(bclose && belongTo.bAutoClose)
			  sh.closeContent();
	 }
	 /**
	  * ���Ѵ��ڵ�shellһ��ͷ����ʵ��LRU����
	  */
	 private void transferToListHead (Shell sh)
	 {
		  removeFromList (sh,false) ;
		  addToListHead (sh) ;
	 }

	 private Shell getListHead ()
	 {
		  Shell tmpsh = shHead.getNext () ;
		  if (tmpsh==shHead)
			   return null ;
		  else
			   return tmpsh ;
	 }

	 private Shell getListTail ()
	 {
		  Shell tmpsh = shHead.getPrev () ;
		  if (tmpsh==shHead)
			   return null ;
		  else
			   return tmpsh ;
	 }



	 synchronized public void addShell (Shell sh)
	 {
		  if (htBuffer.size()<iMaxBufferLen)
		  {
			   addToListHead (sh) ;
		  }
		  else
		  {//full and remove one shell USING LRU
			   Shell tmpsh = removeTailShell () ;
			   if (tmpsh==null)
					throw new RuntimeException ("Some Error:no shell in list while buffer is full??") ;
			   addToListHead (sh) ;
		  }

		  htBuffer.put (sh.getKey(),sh) ;
	 }
	 /**
	  * ��ȡĳ��shell���������ŵ������ǰ��
	  */
	 synchronized public Shell accessShell (Object key)
	 {
		  Shell tmpsh = (Shell)htBuffer.get (key) ;
		  if (tmpsh==null)
			   return null ;
		  transferToListHead (tmpsh) ;
		  return tmpsh ;
	 }
	 /**
	  * ��ȡĳ��shell
	  */
	 public Shell getShell (Object key)
	 {
		  return (Shell)htBuffer.get (key) ;
	 }
	 synchronized public Shell removeShell (Object key)
	 {
		  Shell tmpsh = (Shell)htBuffer.get (key) ;
		  if (tmpsh==null)
			   return null ;
		  removeFromList (tmpsh,true) ;
		  htBuffer.remove (key) ;
		  return tmpsh ;
	 }

	 synchronized public void removeShell (Shell sh)
	 {
		  removeFromList (sh,true) ;
		  htBuffer.remove (sh.getKey()) ;
	 }

	 synchronized public Shell removeTailShell ()
	 {
		  Shell tmpsh = removeListTail () ;
		  if (tmpsh==null)
			   return null ;
		  htBuffer.remove (tmpsh.getKey()) ;
		  return tmpsh ;
	 }

	 public void emptyBuffer ()
	 {
		 Hashtable<Object,Shell> tmpht = null;
		 synchronized(this)
		 {
			  shHead.setPrev (shHead) ;
			  shHead.setNext (shHead) ;
			  
			  tmpht = htBuffer ;
			  htBuffer = new Hashtable<>();//.clear () ;
		 }
		 
		 if(belongTo.bAutoClose)
		 {
			 for(Shell sh:tmpht.values())
			 {
				 sh.closeContent();
			 }
		 }
	 }

	 public boolean isEmpty()
	 {
		  return htBuffer.isEmpty() ;
	 }

	 public int size()
	 {
		  return htBuffer.size() ;
	 }

	 public int getMaxBufferLen ()
	 {
		  return iMaxBufferLen ;
	 }

	 synchronized public void setMaxBufferLen (int len)
	 {
		  if (len<=0)
			   throw new RuntimeException ("Error:CacherBuffer cannot be set to <=0!") ;
		  int curlen = getBufferLen () ;
		  for (int i = len ; i < curlen ; i ++)
			   removeTailShell () ;
		  iMaxBufferLen = len ;
	 }

	 public int getBufferLen ()
	 {
		  return htBuffer.size () ;
	 }

	 synchronized public Shell[] getAllShell ()
	 {
		  int s = htBuffer.size () ;
		  Shell[] rets = new Shell [s] ;
		  Shell tmpsh = shHead ;
		  for (int i = 0 ; i < s ; i ++)
		  {
			   rets[i] = tmpsh.getNext () ;
			   tmpsh = rets[i] ;
		  }
		  return rets ;
	 }


	 synchronized public Enumeration getAllKeys()
	 {
		  return htBuffer.keys() ;
	 }


	 synchronized public void list ()
	 {
		  System.out.println ("-----In list----------------") ;
		Shell[] shs = getAllShell () ;
		for (int i = 0 ; i < shs.length ; i ++)
			System.out.println (shs[i].toString()) ;
		System.out.println ("---------------------------") ;
		System.out.println ("-----In hash----------------") ;
		for (Enumeration en = htBuffer.keys () ; en.hasMoreElements () ;)
		{
			 String tmpkey = (String)en.nextElement () ;
			 System.out.println (tmpkey+"="+htBuffer.get(tmpkey)) ;
		}
		System.out.println ("---------------------------") ;
	 }
}