package org.iottree.core.store.ttsr;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 针对单个Tag的记录器对象支持
 * 
 * @author jason.zhu
 *
 */
public class RecTag<T>
{
	private RecAdapter belongTo = null ;
	/**
	 * unique name
	 */
	String tagName = null ;
	
	int maxBlockNum = 100 ;
	
	int maxBlockPointNum = 5 ;
	
	/**
	 * 两次数据之间的时间超过一定的值，就认为中间有获取数据中断，
	 * 后面的数据是新数据，不能与前一个数据合并或形成时间连续
	 */
	long breakGapDT ;
	
	private LinkedList<RecBlock<T>> blockList = new LinkedList<>();
	//LinkedList<>
	private HashMap<Long,RecBlock<T>> startdt2block = new HashMap<>() ;
	
	RecTag(RecAdapter belongto,String tag,int max_block_num,int max_block_pt_num,
			long break_gap_dt)
	{
		if(max_block_num<=1)
			throw new IllegalArgumentException("invalid max_block_num") ;
		
		if(max_block_pt_num<=1)
			throw new IllegalArgumentException("invalid max_block_pt_num") ;
		
		if(break_gap_dt<=0)
			throw new IllegalArgumentException("invalid break_gap_dt") ;
		
		this.belongTo = belongto ;
		this.tagName = tag ;
		this.maxBlockNum = max_block_num;
		this.maxBlockPointNum = max_block_pt_num ;
		this.breakGapDT = break_gap_dt; 
	}
	
	public RecAdapter getBelongTo()
	{
		return this.belongTo ;
	}
	
	public String getName()
	{
		return this.tagName ;
	}
	
	public synchronized void addPoint(long dt,boolean bvalid,T val)
	{
		RecBlock<T> lastrb = null;
		if(blockList.size()<=0)
		{
			lastrb = new RecBlock<>(dt,bvalid,val,true,true) ;
			blockList.add(lastrb) ;
			startdt2block.put(dt, lastrb) ;
			return ;
		}
			
		lastrb = blockList.getLast();
		int ares = lastrb.tryAddPointNotChged(dt, bvalid,val, this.breakGapDT) ;
		if(ares>0)
			return ;
		
		if(ares==0)
		{//gap timeout,current block add a invalid end pt.
			lastrb.addPointInvalid(dt,true);
		}
		
		if(lastrb.points.size()>=maxBlockPointNum)
		{
			lastrb = new RecBlock<>(dt,bvalid,val,true,true) ;
			blockList.add(lastrb) ;
			startdt2block.put(dt, lastrb) ;
			
//			if(blockList.size()>maxBlockNum)
//			{//remove first
//				RecBlock<T> frb = blockList.removeFirst() ;
//				long sdt = frb.getStartDT() ;
//				if(frb.isDirty())
//				{//需要做存储
//					
//				}
//				startdt2block.remove(sdt) ;
//			}
			
			
			return ;
		}
		
		if(bvalid)
			lastrb.addPointValid(dt, val);
		else
			lastrb.addPointInvalid(dt,false);
	}
	
	
	public void addPoints(List<RecValue<T>> rvs)
	{
		for(RecValue<T> rv:rvs)
			addPoint(rv.dt,rv.bvalid,rv.val) ;
	}
	
	public int[] getDirtyBlockIdxFromTo()
	{
		int n = this.blockList.size() ;
		int from=-1,to = -1 ;
		for(int i = n-1 ; i >=0 ; i --)
		{
			RecBlock<T> rb = this.blockList.get(i) ;
			if(!rb.isDirty())
				break ;
			
			if(to<0)
			{
				from = to = i ;
			}
			else
			{
				from = i ;
			}
		}
		
		if(to<0)
			return null ;
		return new int[] {from,to} ;
	}
	
	public RecBlock<T> getBlockByIdx(int idx)
	{
		if(idx<0||idx>=this.blockList.size())
			return null ;
		return blockList.get(idx) ;
	}
	
	public RecBlock<T> getBlockByStartDT(long startdt)
	{
		return this.startdt2block.get(startdt) ;
	}
	
	public void forEachDirtyBlock(Consumer<RecBlock<T>> action)
	{
		int n = this.blockList.size() ;
		for(int i = n-1 ; i >=0 ; i --)
		{
			RecBlock<T> rb = this.blockList.get(i) ;
			if(!rb.isDirty())
				break ;
			action.accept(rb);
		}
	}
}
