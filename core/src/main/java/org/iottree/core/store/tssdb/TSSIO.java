package org.iottree.core.store.tssdb;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.store.gdb.connpool.DBConnPool;

public abstract class TSSIO
{

	private HashMap<String,Integer> tagsMap = null ;
	
	protected TSSAdapter belongTo ;
	
	protected TSSIO(TSSAdapter adp)
	{
		this.belongTo = adp ;
	}
	
	public abstract boolean initIO(DBConnPool cp,StringBuilder failedr) ;
	
	protected abstract Integer getOrAddTagIdx(String tag) throws Exception ;
	
	public final HashMap<String,Integer> getTagsMap()  throws Exception
	{
		if(tagsMap!=null)
			return tagsMap ;
		
		synchronized(this)
		{
			if(tagsMap!=null)
				return tagsMap ;
			
			tagsMap = this.readTag2IdxMap() ;
			return tagsMap ;
		}
	}
	
	
	
//	public abstract boolean removeBlocksByTag(String tag) ;
	
//	public abstract <T> List<RecBlock<T>> loadBlocksAt(String tag,long at_dt) ;
	
	//public abstract 
	
//	public abstract boolean saveValSeg(TSSTagSegs<?> t,TSSValSeg<?> r) throws Exception;
	
	
	
	protected abstract int saveTagSegsPKS(List<TSSSavePK> pks) throws Exception;
	
	private transient long lastSaveDT = -1 ;
	private transient long lastSaveCost = -1 ;
	private transient int lastSaveSegNum = - 1 ;
	
	final List<TSSSavePK> saveTagSegs(List<TSSTagSegs<?>> ts) throws Exception
	{
		ArrayList<TSSSavePK> pks=  new ArrayList<>() ;
		for(TSSTagSegs<?> t:ts)
		{
			int segn = t.memSegs.size() ;
			boolean bdirty = t.bLastSegDirty ;
			if(segn<=0 && !bdirty)
				continue ;
			
			long fromdt ,todt ;
			if(segn>0)
				fromdt = t.memSegs.get(0).startDT ;
			else
				fromdt = t.lastSeg.startDT ;
			
			long lastdt ;
			if(bdirty)
				lastdt = todt = t.lastSeg.getEndDT() ;
			else
			{
				lastdt = -1 ;
				todt = t.memSegs.get(segn-1).getEndDT() ;
			}
				
			//long lastdt = bdirty?:-1 ;
			TSSSavePK pk = new TSSSavePK(t,segn,fromdt,todt,lastdt) ;
			pks.add(pk) ;
		}
		
		if(pks.size()<=0)
			return null;
		
		long st = System.currentTimeMillis() ;
		lastSaveSegNum = saveTagSegsPKS(pks) ;
		lastSaveDT = System.currentTimeMillis() ;
		lastSaveCost = lastSaveDT -st ;
		//System.out.println(" save pks row num ["+rown+"] cost ms="+(System.currentTimeMillis()-st)) ;
		
		for(TSSSavePK pk:pks)
		{
			if(pk.mem_seg_num>0)
			{
				pk.segs.cleanMemSegsAfterSave(pk.mem_seg_num);
			}
			if(pk.last_seg_enddt>0)
			{
				pk.segs.cleanLastSegAfterSave(pk.last_seg_enddt) ;
			}
		}
		
		return pks ;
	}
	
	
	public long RT_getLastSaveDT()
	{
		return this.lastSaveDT ;
	}
	
	public long RT_getLastSaveCost()
	{
		return this.lastSaveCost ;
	}
	
	public int RT_getLastSaveSegNum()
	{
		return this.lastSaveSegNum ;
	}
	
	// read methods
	
	/**
	 * load all tag in adapters
	 * @param r
	 * @throws Exception
	 */
	public abstract HashMap<String,Integer> readTag2IdxMap() throws Exception ;
	
	public abstract <T> HashMap<Integer,TSSValSeg<T>> readTagIdx2MaxValSeg(Class<T> c) throws Exception;
	
	public abstract <T> HashMap<Integer,TSSValSeg<T>> readTagIdx2MinSeg(Class<T> c) throws Exception;
	
	
	public final <T> HashMap<Integer,TSSValSegFT<T>> readTagIdx2MinMaxSeg(Class<T> c) throws Exception
	{
		HashMap<Integer,TSSValSeg<T>> max = readTagIdx2MaxValSeg(c) ; 
		HashMap<Integer,TSSValSeg<T>> min = readTagIdx2MinSeg(c) ;
		
		HashMap<Integer,TSSValSegFT<T>> ret = new HashMap<Integer,TSSValSegFT<T>>() ;
		for(Map.Entry<Integer,TSSValSeg<T>> idx2vs:max.entrySet())
		{
			Integer idx = idx2vs.getKey() ;
			TSSValSeg<T> maxv = idx2vs.getValue() ;
			TSSValSeg<T> minv = min.get(idx) ;
			ret.put(idx, new TSSValSegFT<>(minv,maxv)) ;
		}
		return ret ;
	}
	
	/**
	 * 此方法要限制返回数据长度
	 * 
	 * @param <T>
	 * @param ts
	 * @param from_dt
	 * @param to_dt
	 * @return
	 * @throws Exception
	 */
	public abstract <T> List<TSSValSeg<T>> readValSegs(TSSTagSegs<T> ts, long from_dt, long to_dt,boolean b_desc,int limit_num) throws Exception;
	
	public abstract <T> TSSValSeg<T> readValSegAt(TSSTagSegs<T> ts, long at_dt) throws Exception ;
	
	public abstract <T> TSSValSegHit<T> readValSegAt(TSSTagSegs<T> ts,long at_dt,boolean b_prev,boolean b_next) throws Exception ;
	
	public abstract <T> List<TSSValSeg<T>> readValSegAt2(TSSTagSegs<T> ts, long at_dt1,long at_dt2) throws Exception ;
	
	public final <T> TSSValPt<T> readValPt(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		TSSValSeg<T> vs = readValSegAt( ts, at_dt) ;
		if(vs==null)
			return null ;
		return new TSSValPt<>(at_dt,vs.bvalid,vs.val) ;
	}
	
	public <T> TSSValSegHitNext<T> readValSegAtAndNext(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		TSSValSegHit<T> hit = readValSegAt( ts,at_dt,false,true) ;
		if(hit==null)
			return null ;
		return new TSSValSegHitNext<>(hit.hitSeg,hit.nextSeg) ;
	}
	
	public <T> TSSValSegHitPrev<T> readValSegAtAndPrev(TSSTagSegs<T> ts, long at_dt) throws Exception
	{
		TSSValSegHit<T> hit = readValSegAt( ts,at_dt,true,false) ;
		if(hit==null)
			return null ;
		return new TSSValSegHitPrev<>(hit.hitSeg,hit.prevSeg) ;
	}
	
	public abstract <T> TSSValSeg<T> readValSegNext(TSSTagSegs<T> ts,TSSValSeg<T> vs) throws Exception;
	
	public abstract <T> TSSValSeg<T> readValSegAtOrNext(TSSTagSegs<T> ts,long at_dt) throws Exception;
	
	
	public abstract <T> void iterValSegsFrom(TSSTagSegs<T> ts, long from_dt,IValSegSelectCB<T> cb) throws Exception ;
	
	public abstract <T> int clearValSegsAll(TSSTagSegs<T> ts) throws Exception ;
}
