package org.iottree.core.store.ttsr;

import java.util.ArrayList;

/**
 * 一次连续记录的数据块,每次记录时都以这个为单位进行永久写入存储
 * 
 * @author jason.zhu
 */
public class RecBlock<T>
{
	//long startDT ;
	
	//long endDT ;
	
	ArrayList<RecPoint<T>> points = new ArrayList<>() ;
	
	/**
	 * 与永久存储不一致时 dirty=true
	 */
	boolean bDirty = true;
	
	/**
	 * 未作存储
	 */
	boolean bNew = false;
	
	public RecBlock(RecPoint<T> first_rp,boolean bdirty,boolean bnew)
	{
		//this.startDT = first_rp.startDT ;
		points.add(first_rp) ;
		this.bDirty = bdirty ;
		this.bNew = bnew ;
	}
	
	public RecBlock(long dt,boolean bvalid,T val,boolean bdirty,boolean bnew)
	{
		RecPoint<T> first_rp = new RecPoint<>(dt,bvalid,val) ;
		//this.startDT = dt ;
		points.add(first_rp) ;
		this.bDirty = bdirty ;
		this.bNew = bnew ;
	}
	
//	public static <K> RecBlock<K> createByFirstInvalid(long dt)
//	{
//		return new RecBlock<K>(new RecPoint<K>(dt,false,null)) ;
//	}
	
	public boolean isDirty()
	{
		return this.bDirty ;
	}
	
	public boolean isNew()
	{
		return this.bNew ;
	}
	
	void setSavedOk()
	{
		this.bDirty = false;
		this.bNew = false;
	}
	
	public long getStartDT()
	{
		return points.get(0).startDT ;
	}
	
	public long getEndDT()
	{
		return points.get(points.size()-1).endDT ;
	}
	
	public RecPoint<T> getPointAt(long at_dt)
	{
		if(at_dt<this.getStartDT() || at_dt>this.getEndDT())
			return null ;
		
		int n = this.points.size() ;
		for(int i = 0 ; i < n ; i ++)
		{
			RecPoint<T> rp = this.points.get(i) ;
			if(rp.startDT<=at_dt && at_dt<rp.endDT)
			{
				return rp ;
			}
		}
		return null ;
	}
	
	public T getValAt(long at_dt)
	{
		RecPoint<T> rp = getPointAt(at_dt) ;
		if(rp==null)
			return null ;
		return rp.val ;
	}
	
	synchronized int tryAddPointNotChged(long dt,boolean bvalid,T val,long break_gap_dt)
	{
		long enddt = this.getEndDT() ;
		if(dt<enddt)
			return -1 ;//非常特殊的异常情况
		
		if(dt-enddt>=break_gap_dt)
		{//新数据
			return 0; //break gap,不管值是否相同，都需要插入一个无效结束点
		}
		
		RecPoint<T> lastp = points.get(points.size()-1) ;
		if(lastp.isValid() && lastp.val.equals(val))
		{
			lastp.setEndDT(dt);
			this.bDirty = true;
			return 1;
		}
		
		return -1;
	}
	
	public synchronized void addPointValid(long dt,T val)
	{
		try
		{
			RecPoint<T> lastp = points.get(points.size()-1) ;
			if(lastp.isValid() && lastp.val.equals(val))
			{
				lastp.setEndDT(dt);
				return ;
			}
			
			//changed
			lastp.setEndDT(dt);
			RecPoint<T> rp = new RecPoint<>(dt,true,val) ;
			points.add(rp) ;
		}
		finally
		{
			this.bDirty = true ;
		}
	}
	
	public synchronized void addPointInvalid(long dt,boolean b_end)
	{
		try
		{
			RecPoint<T> lastp = points.get(points.size()-1) ;
			if(!lastp.isValid())
			{
				lastp.setEndDT(dt);
				return ;
			}
			
			//changed
			if(b_end)
			{//前一个有效点结束时间不变，并且成为新无效点的起始。当前时间是结束时间
				RecPoint<T> rp = new RecPoint<>(lastp.getEndDT(),dt,false,null) ;
				points.add(rp) ;
			}
			else
			{
				lastp.setEndDT(dt);
				RecPoint<T> rp = new RecPoint<>(dt,false,null) ;
				points.add(rp) ;
			}
		}
		finally
		{
			this.bDirty = true ;
		}
	}
	
	public String toFormatStr()
	{
		StringBuilder sb = new StringBuilder() ;
		int n = points.size() ;
		sb.append(points.get(0).toFormatStr()) ;
		for(int i = 1 ; i < n ; i ++)
			sb.append("|").append(points.get(i).toFormatStr()) ;
		return sb.toString() ;
	}
}
