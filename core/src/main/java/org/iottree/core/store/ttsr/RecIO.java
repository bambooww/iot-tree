package org.iottree.core.store.ttsr;

import java.util.HashMap;
import java.util.List;

import org.iottree.core.store.gdb.connpool.DBConnPool;

public abstract class RecIO
{
	private HashMap<String,Integer> tagsMap = null ;
	
	protected abstract boolean initIO(DBConnPool cp,StringBuilder failedr) ;
	
	public final HashMap<String,Integer> getBlockTagsMap()  throws Exception
	{
		if(tagsMap!=null)
			return tagsMap ;
		
		synchronized(this)
		{
			if(tagsMap!=null)
				return tagsMap ;
			
			tagsMap = this.loadBlockTagsMap() ;
			return tagsMap ;
		}
	}
	
	/**
	 * load all tag in adapters
	 * @param r
	 * @throws Exception
	 */
	protected abstract HashMap<String,Integer> loadBlockTagsMap() throws Exception ;
	
	
	
	public abstract boolean removeBlocksByTag(String tag) ;
	
	public abstract <T> List<RecBlock<T>> loadBlocksAt(String tag,long at_dt) ;
	
	//public abstract 
	
	public abstract boolean saveBlock(RecTag<?> t,RecBlock<?> r) throws Exception ;
	
	public final void saveRecorder(RecTag<?> r) throws Exception
	{
		int[] ft = r.getDirtyBlockIdxFromTo() ;
		if(ft==null)
			return ;
		for(int i = ft[0] ; i <= ft[1] ; i ++)
		{
			RecBlock<?> rb = r.getBlockByIdx(i) ;
			this.saveBlock(r,rb) ;
		}
	}
}
