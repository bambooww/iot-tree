package org.iottree.core.store.tssdb;

import java.util.LinkedList;
import java.util.List;

/**
 * 针对单个Tag的记录器对象支持
 * 
 * @author jason.zhu
 *
 */
public class TSSTagSegs<T>
{
	private TSSAdapter belongTo = null ;
	/**
	 * unique name
	 */
	String tag = null ;
	
	TSSTagParam param = null ;
	
	/**
	 * 两次数据之间的时间超过一定的值，就认为中间有获取数据中断，
	 * 后面的数据是新数据，不能与前一个数据合并或形成时间连续
	 */
	private long breakGapDT ;
	
	/**
	 * not saved mem cache segs
	 */
	transient LinkedList<TSSValSeg<T>> memSegs = new LinkedList<>() ;
	
	transient TSSValSeg<T> lastSeg ;
	
	transient boolean bLastSegDirty ;
	/**
	 * 
	 * @param belongto
	 * @param tag
	 * @param break_gap_dt
	 * @param lastseg 如果是新建，则内部new=true，如果是从存储装载，则new=false
	 */
	TSSTagSegs(TSSAdapter belongto,String tag,TSSTagParam pm,TSSValSeg<T> lastseg,boolean b_lastseg_dirty)
	{
		if(lastseg==null)
			throw new IllegalArgumentException("last seg cannot be null") ;
//		if(break_gap_dt<=0)
//			throw new IllegalArgumentException("invalid break_gap_dt") ;
		
		this.belongTo = belongto ;
		this.tag = tag ;
		this.param = pm ;
		this.breakGapDT = pm.getBreakGapIntv(); 
		this.lastSeg = lastseg ;
		this.bLastSegDirty = b_lastseg_dirty ;
	}
	
	public TSSAdapter getBelongTo()
	{
		return this.belongTo ;
	}
	
	public String getTag()
	{
		return this.tag ;
	}
	
	synchronized void cleanMemSegsAfterSave(int num)
	{
		if(memSegs.size()<=0)
			return ;
		for(int i = 0 ; i < num ; i ++)
			memSegs.removeFirst() ;
	}
	
	/**
	 * called after save
	 * @param save_enddt
	 */
	synchronized void cleanLastSegAfterSave(long save_enddt)
	{
		lastSeg.bNew = false;
		
		if(save_enddt!=lastSeg.endDT)
			return ;//not clean
		
		bLastSegDirty = false; //clean ok
	}
	
	public synchronized void addPointValid(long dt,T val)
	{
		long last_edt = lastSeg.getEndDT() ;
		if(dt<=last_edt)
		{//特殊的异常情况，为了保证数据完整性，直接丢弃此数据
			System.out.println("Warn:new data time is old then existed,discard this data!") ;
			return;
		}
		
		try
		{
			if(this.breakGapDT>0 && (dt-last_edt>=this.breakGapDT))
			{//两次采集时间间隔超时gap，说明两者之间有异常，
				if(!lastSeg.isValid())
				{
					lastSeg.setEndDT(last_edt);
				}
				else
				{
					memSegs.addLast(lastSeg) ;
					//此时应该插入一个无效数据,使数据完整
					memSegs.addLast(new TSSValSeg<T>(lastSeg.getEndDT(),dt,false,null,true)) ;
					lastSeg = new TSSValSeg<T>(dt,true,val,true) ;
				}
				return ;
			}
			
			if(lastSeg.isValid() && lastSeg.val.equals(val))
			{
				lastSeg.setEndDT(dt);
				return ;
			}
				
			//changed
			lastSeg.setEndDT(dt);
			memSegs.addLast(lastSeg) ;
			lastSeg = new TSSValSeg<>(dt,true,val,true) ;
		}
		finally
		{
			bLastSegDirty=true;
		}
	}
	
	public synchronized void addPointInvalid(long dt,boolean b_end)
	{
		try
		{
			if(!lastSeg.isValid())
			{
				lastSeg.setEndDT(dt);
				return ;
			}
			
			//changed
			if(b_end)
			{//前一个有效点结束时间不变，并且成为新无效点的起始。当前时间是结束时间
				memSegs.addLast(lastSeg) ;
				lastSeg = new TSSValSeg<>(lastSeg.getEndDT(),dt,false,null,true) ;
			}
			else
			{
				lastSeg.setEndDT(dt);
				memSegs.addLast(lastSeg) ;
				lastSeg = new TSSValSeg<>(dt,false,null,true) ;
			}
		}
		finally
		{
			bLastSegDirty=true;
		}
	}
	
//	public synchronized void addPoint(long dt,boolean bvalid,T val)
//	{
//		RecBlock<T> lastrb = null;
//		if(blockList.size()<=0)
//		{
//			lastrb = new RecBlock<>(dt,bvalid,val,true,true) ;
//			blockList.add(lastrb) ;
//			startdt2block.put(dt, lastrb) ;
//			return ;
//		}
//			
//		lastrb = blockList.getLast();
//		int ares = lastrb.tryAddPointNotChged(dt, bvalid,val, this.breakGapDT) ;
//		if(ares>0)
//			return ;
//		
//		if(ares==0)
//		{//gap timeout,current block add a invalid end pt.
//			lastrb.addPointInvalid(dt,true);
//		}
//		
//		if(lastrb.points.size()>=maxBlockPointNum)
//		{
//			lastrb = new RecBlock<>(dt,bvalid,val,true,true) ;
//			blockList.add(lastrb) ;
//			startdt2block.put(dt, lastrb) ;
//			
////			if(blockList.size()>maxBlockNum)
////			{//remove first
////				RecBlock<T> frb = blockList.removeFirst() ;
////				long sdt = frb.getStartDT() ;
////				if(frb.isDirty())
////				{//需要做存储
////					
////				}
////				startdt2block.remove(sdt) ;
////			}
//			
//			
//			return ;
//		}
//		
//		if(bvalid)
//			lastrb.addPointValid(dt, val);
//		else
//			lastrb.addPointInvalid(dt,false);
//	}
	
	
	public void addPoints(List<TSSValPt<T>> rvs)
	{
		for(TSSValPt<T> rv:rvs)
		{
			if(rv.bvalid)
				addPointValid(rv.dt,rv.val) ;
			else
				addPointInvalid(rv.dt,false) ;
		}
	}
	
	
}
