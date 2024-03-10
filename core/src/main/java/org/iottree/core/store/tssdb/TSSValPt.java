package org.iottree.core.store.tssdb;

public class TSSValPt<T>
{
	long dt ;
	
	boolean bvalid ;
	
	T val ;
	
	public TSSValPt(long dt,boolean bvalid,T val)
	{
		this.dt = dt ;
		this.bvalid = bvalid ;
		this.val = val ;
	}
	
	public long getDT()
	{
		return dt ;
	}
	
	public boolean isValid()
	{
		return this.bvalid ;
	}
	
	public T getVal()
	{
		return val ;
	}
	
	public Long getValInt64()
	{
		if(val==null || !bvalid)
			return null ;
		
		if(val instanceof Number)
			return ((Number)val).longValue() ;
		
		throw new RuntimeException("") ;
	}
	
	public String toString()
	{
		return "("+dt+","+bvalid+","+val+")" ;
	}
}
