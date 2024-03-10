package org.iottree.core.store.tssdb;

public class TSSValSegHitNext<T>
{
	/**
	 * may hit seg at some date point
	 */
	public TSSValSeg<T> hitSeg = null ;
	
	/**
	 * next seg order by startdt
	 */
	public TSSValSeg<T> nextSeg = null ;
	
	TSSValSegHitNext(TSSValSeg<T> hit_seg,TSSValSeg<T> next_seg)
	{
		this.hitSeg = hit_seg ;
		this.nextSeg = next_seg;
	}
}
