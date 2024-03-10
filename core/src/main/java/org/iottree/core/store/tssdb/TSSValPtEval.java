package org.iottree.core.store.tssdb;

/**
 * Evaluation value
 * 
 * @author jason.zhu
 *
 */
public class TSSValPtEval<T>
{
	long dt ;
	
	T val ;
	
	//0-100
	short evalRatio ;
	
	public TSSValPtEval(long dt,T val,short eval)
	{
		this.dt = dt ;
		this.val = val ;
		this.evalRatio = eval ;
	}
	
	public long getDT()
	{
		return dt ;
	}
	
	public int getEvalRatio()
	{
		return this.evalRatio ;
	}
	
	public T getVal()
	{
		return val ;
	}
	
	public double getValDouble()
	{
		return ((Number)val).doubleValue() ;
	}
}
