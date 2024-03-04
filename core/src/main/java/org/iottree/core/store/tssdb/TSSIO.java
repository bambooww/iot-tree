package org.iottree.core.store.tssdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.store.gdb.connpool.DBConnPool;

public abstract class TSSIO
{
	private HashMap<String,Integer> tagsMap = null ;
	
	protected abstract boolean initIO(DBConnPool cp,StringBuilder failedr) ;
	
	public final HashMap<String,Integer> getTagsMap()  throws Exception
	{
		if(tagsMap!=null)
			return tagsMap ;
		
		synchronized(this)
		{
			if(tagsMap!=null)
				return tagsMap ;
			
			tagsMap = this.loadTagsMap() ;
			return tagsMap ;
		}
	}
	
	/**
	 * load all tag in adapters
	 * @param r
	 * @throws Exception
	 */
	protected abstract HashMap<String,Integer> loadTagsMap() throws Exception ;
	
//	public abstract HashMap<Integer,TSSValSeg<Boolean>> loadLastTagsSegBool() throws Exception;
//	
//	public abstract HashMap<Integer,TSSValSeg<Long>> loadLastTagsSegInt64() throws Exception;
//	
//	public abstract HashMap<Integer,TSSValSeg<Double>> loadLastTagsSegDouble() throws Exception;
	
	public abstract <T> HashMap<Integer,TSSValSeg<T>> loadLastTagsSeg(Class<T> c) throws Exception;
	
//	public abstract boolean removeBlocksByTag(String tag) ;
	
//	public abstract <T> List<RecBlock<T>> loadBlocksAt(String tag,long at_dt) ;
	
	//public abstract 
	
	public abstract boolean saveValSeg(TSSTagSegs<?> t,TSSValSeg<?> r) throws Exception;
	
	public static class SavePK
	{
		TSSTagSegs<?> segs ;
		
		int mem_seg_num ; //本次保存数量
		
		long last_seg_enddt ; //本次保存最后一个
		
		SavePK(TSSTagSegs<?> segs,int mem_segn,long last_seg_dt)
		{
			this.segs = segs;
			this.mem_seg_num = mem_segn ;
			this.last_seg_enddt = last_seg_dt ;
		}
		
		public String getTag()
		{
			return segs.getTag() ;
		}
		
		public int getAffectRowNum()
		{
			return (mem_seg_num>0?mem_seg_num:0) + (last_seg_enddt>0?1:0) ;
		}
	}
	
	protected abstract int saveTagSegsPKS(List<SavePK> pks) throws Exception;
	
	public final void saveTagSegs(List<TSSTagSegs<?>> ts) throws Exception
	{
		ArrayList<SavePK> pks=  new ArrayList<>() ;
		for(TSSTagSegs<?> t:ts)
		{
			int segn = t.memSegs.size() ;
			boolean bdirty = t.bLastSegDirty ;
			if(segn<=0 && !bdirty)
				continue ;
			
			SavePK pk = new SavePK(t,segn,bdirty?t.lastSeg.getEndDT():-1) ;
			pks.add(pk) ;
		}
		
		if(pks.size()<=0)
			return ;
		
		long st = System.currentTimeMillis() ;
		int rown = saveTagSegsPKS(pks) ;
		System.out.println(" save pks row num ["+rown+"] cost ms="+(System.currentTimeMillis()-st)) ;
		
		for(SavePK pk:pks)
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
		
	}
}
