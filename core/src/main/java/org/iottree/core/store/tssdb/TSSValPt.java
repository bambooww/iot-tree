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
}
