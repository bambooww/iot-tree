package org.iottree.core.store.tssdb;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;

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
	 * 一些数据采集速度很快（频度很高）并且每次都有变化，但这个数据从记录上不需要记录这么精细
	 * 如一些累计采集数据，一般可能按照天统计。但我们和设备连接采集时可能是秒级甚至毫秒级。
	 * 因此，通过此参数，用来减少记录条目数
	 */
	private long minRecordGap = -1 ;
	
	/**
	 * not saved mem cache segs
	 */
	transient LinkedList<TSSValSeg<T>> memSegs = new LinkedList<>() ;
	
	transient TSSValSeg<T> minSeg ;
	
	transient TSSValSeg<T> lastSeg ;
	
	transient boolean bLastSegDirty ;
	
	transient Integer tagIdx = null ;
	/**
	 * 
	 * @param belongto
	 * @param tag
	 * @param break_gap_dt
	 * @param lastseg 如果是新建，则内部new=true，如果是从存储装载，则new=false
	 */
	TSSTagSegs(TSSAdapter belongto,String tag,Integer tagidx,TSSTagParam pm,TSSValSeg<T> minseg,TSSValSeg<T> lastseg,boolean b_lastseg_dirty)
	{
		if(lastseg==null)
			throw new IllegalArgumentException("last seg cannot be null") ;
//		if(break_gap_dt<=0)
//			throw new IllegalArgumentException("invalid break_gap_dt") ;
		
		this.belongTo = belongto ;
		this.tag = tag ;
		this.tagIdx = tagidx ;
		this.param = pm ;
		this.breakGapDT = pm.getBreakGapIntv(); 
		this.minRecordGap = pm.getMinRecordGap() ;
		this.minSeg = minseg ;
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
	
	public Integer getTagIdx()
	{
		if(tagIdx==null)
			throw new RuntimeException("no saved tagidx to be set") ;
		return tagIdx ;
	}
	
	public ValTP getValTP()
	{
		return this.param.valTp ;
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
			System.out.println(" Warn:new data time is old then existed,discard this data!") ;
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
			
			if(lastSeg.isValid() && lastSeg.val.equals(val) )
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
	
	public int getUnsavedSegsNum()
	{
		return memSegs.size()+(this.bLastSegDirty?1:0) ;
	}
	
	// read funcs
	public List<TSSValSeg<T>> readValSegs(long from_dt, long to_dt) throws Exception
	{
		return this.getBelongTo().readValSegs(this, from_dt, to_dt) ;
	}
	
	public TSSValSeg<T> readValSegAt(long at_dt) throws Exception
	{
		return this.getBelongTo().readValSegAt(this, at_dt) ;
	}
	
	public List<TSSValSeg<T>> readValSegAt2(long at_dt1,long at_dt2) throws Exception
	{
		return this.getBelongTo().readValSegAt2(this, at_dt1, at_dt2) ;
	}
	
	public TSSValPt<T> readValPt(long at_dt) throws Exception
	{
		return this.getBelongTo().readValPt(this, at_dt) ;
	}
	
	public TSSValSeg<T> readValSegNext(TSSValSeg<T> vs) throws Exception
	{
		return this.getBelongTo().readValSegNext(this, vs) ;
	}
	
	public TSSValSeg<T> readValSegAtOrNext(long at_dt) throws Exception
	{
		return this.getBelongTo().readValSegAtOrNext(this, at_dt) ;
	}
	
	public final TSSValSegHit<T> readValSegAt(long at_dt,boolean b_prev,boolean b_next) throws Exception
	{
		return this.getBelongTo().readValSegAt(this, at_dt,b_prev,b_next) ;
	}
	
	/**
	 * 在指定位置获取或通过前后有效值进行线性计算对应值
	 * 前后值不能超过limit_gap
	 * 如果没有值，则返回null
	 * @param at_dt
	 * @return
	 */
	public final TSSValPtEval<T> readOrCalValAt(long at_dt) throws Exception
	{
		TSSValSegHit<T> hit = readValSegAt(at_dt,true,true) ;
		if(hit==null)
			return null ;
		if(hit.hitSeg.bvalid)
			return new TSSValPtEval<>(at_dt,hit.hitSeg.val,(short)100) ;
		
		//中间invalid，看两边计算
		T prev_v = (hit.prevSeg!=null && hit.prevSeg.bvalid)?hit.prevSeg.val:null ;
		T next_v = (hit.nextSeg!=null && hit.nextSeg.bvalid)?hit.nextSeg.val:null ;
		if(prev_v==null && next_v==null)
			return null ;
		
		if(prev_v!=null && next_v==null)
			return new TSSValPtEval<>(at_dt,prev_v,(short)50) ;
		
		if(prev_v==null && next_v!=null)
			return new TSSValPtEval<>(at_dt,next_v,(short)50) ;
		
		double pv = ((Number)prev_v).doubleValue() ;
		double nv = ((Number)next_v).doubleValue() ;
		
		long s = hit.hitSeg.startDT ;
		long e = hit.hitSeg.endDT ;
		T retv ;
		if(s==e) //中间是一个点
		{
			retv = (T)transValToT((pv+nv)/2) ;
			return new TSSValPtEval<>(at_dt,retv,(short)50) ;
		}
		
		double k = (nv-pv)/(e-s) ;
		double d = pv-k*s ;
		
		retv = (T)transValToT(k*at_dt+d) ;
		return new TSSValPtEval<>(at_dt,retv,(short)50) ;
	}
	
	private final Number transValToT(double v)
	{
		Number n = (Number)v ;
		switch(this.param.valTp)
		{
		case vt_double:
			return v ;
		case vt_int64:
			return n.longValue() ;
		case vt_int32:
			return n.intValue() ;
		case vt_int16:
			return n.shortValue() ;
		
		case vt_byte:
			return n.byteValue() ;
		case vt_float:
			return n.floatValue() ;
		
		default:
			return v ;
		}
	}
}
