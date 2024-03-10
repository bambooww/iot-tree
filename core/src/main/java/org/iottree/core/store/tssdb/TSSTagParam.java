package org.iottree.core.store.tssdb;

import org.iottree.core.UAVal;
import org.iottree.core.util.xmldata.XmlVal;

public class TSSTagParam
{
	String tag ;
	
	UAVal.ValTP valTp ;
	
	long gatherIntv ;
	
	long minRecordGap  = -1;
	
	/**
	 * for cal gap
	 */
	transient long lastAddDT = -1 ;
	
	transient boolean lastValid = false;
	
	public TSSTagParam(String tag,UAVal.ValTP valtp,long gather_intv,long min_record_gap)
	{
		this.tag = tag ;
		this.valTp = valtp ;
		this.gatherIntv = gather_intv ;
		this.minRecordGap = min_record_gap ;
	}
	
	public long getBreakGapIntv()
	{
		if(gatherIntv<=0)
			return -1 ;
		
		return gatherIntv * 3 ; //采样间隔的3倍，认为中间有中断数据获取间隔
	}
	
	public long getMinRecordGap()
	{
		return this.minRecordGap ;
	}
	
	public boolean isValBool()
	{
		return this.valTp==UAVal.ValTP.vt_bool ;
	}
	
	public boolean isValInt()
	{
		if(!this.valTp.isNumberVT())
			return false;
		return !this.valTp.isNumberFloat() ;
	}
	
	public boolean isValFloat()
	{
		return this.valTp.isNumberFloat() ;
	}
	
	public String getTableName(String prefix)
	{
		if(isValBool())
			return prefix+"_bool" ;
		if(isValInt())
			return prefix+"_int" ;
		if(isValFloat())
			return prefix+"_float" ;
		
		throw new IllegalArgumentException("unknown tag param ,no table found") ;
	}
	
	public static String calTableName(String prefix,XmlVal.XmlValType vt)
	{
		switch(vt)
		{
		case vt_bool:
			return prefix+"_bool" ;
		case vt_int64:
			return prefix+"_int" ;
		case vt_double:
			return prefix+"_float" ;
		default:
			throw new IllegalArgumentException("unknown val type ,no table found") ;
		}
	}
}
