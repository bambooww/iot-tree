package org.iottree.core.store.record;

import java.util.List;

import org.iottree.core.store.tssdb.TSSValPt;
import org.iottree.core.store.tssdb.TSSValSeg;

/**
 * Record process, based on tssdb ,which process data to another data record,
 * 
 * every process can has its owner shower,which can show output data records.
 * 
 * it's structure can be used to process data and stat data
 * 
 * @author jason.zhu
 *
 */
public abstract class RecPro<T>
{
	public static interface IProInput
	{
		
	}
	
	public static interface IProOutput
	{
		
	}
	
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;
	
	public abstract List<String> getSupportedSavers() ;
	
	protected abstract boolean doPro(IProInput input,IProOutput output) ;
	
	
	public void proValSeg(TSSValSeg<T> vs)
	{
		
	}
	
	public void proValPt(TSSValPt<T> vp)
	{
		
	}
}
