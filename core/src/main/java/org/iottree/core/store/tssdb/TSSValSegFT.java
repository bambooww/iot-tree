package org.iottree.core.store.tssdb;

public class TSSValSegFT<T>
{
	/**
	 * may hit seg at some date point
	 */
	public TSSValSeg<T> from = null ;
	
	/**
	 * next seg order by startdt
	 */
	public TSSValSeg<T> to = null ;
	
	TSSValSegFT(TSSValSeg<T> f,TSSValSeg<T> t)
	{
		this.from = f ;
		this.to = t;
	}
}
