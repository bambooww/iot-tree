package org.iottree.core.util.queue;


import java.util.LinkedList;

public class QueItem<T> implements Comparable<QueItem<T>>
{
	T objV = null ;
	/**
	 * 已经尝试次数
	 */
	int retryTime = 0 ;
	/**
	 * 开始处理时间
	 */
	long startProcT = -1 ;
	
	transient LinkedList<QueItem<T>> belongToLL = null ;
	
	//是否真正处理中，
	// 0 = 排队中
	// 1 = 处理中，则不允许进行替换和
	// -1 = 处理完毕，不在队列
	private int proST = 0;
	
	
	IQueObjTracer objTracer = null ;
	
	QueItem(T ov)
	{
		objV = ov ;
	}
	
	
	synchronized void setProST(int p)
	{
		proST = p ;
	}
	
	//在没有被处理之前，进行替换
	synchronized boolean replaceObj(T o)
	{
		if(proST!=0)
			return false;//替换失败
		objV = o ;
		return true;
	}
	
	public String getObjId()
	{
		if(objV==null)
			return null ;
		
		if(!(objV instanceof IQueObjId))
			return null ;
		
		return ((IQueObjId)objV).getQueObjId() ;
	}
	
	void removeFromList()
	{
		belongToLL.remove(this) ;
	}

	public int compareTo(QueItem<T> o)
	{
		long v = startProcT-o.startProcT ;
		if (v>0)
			return 1 ;
		if(v<0)
			return -1 ;
		return 0 ;
	}
}
