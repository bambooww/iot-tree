package org.iottree.core.store.tssdb;

public class TSSValSegHitPrev<T>
{
	/**
	 * may hit seg at some date point
	 */
	public TSSValSeg<T> hitSeg = null ;
	
	/**
	 * prev seg order by startdt
	 */
	public TSSValSeg<T> prevSeg = null ;
	
	TSSValSegHitPrev(TSSValSeg<T> hit_seg,TSSValSeg<T> prev_seg)
	{
		this.hitSeg = hit_seg ;
		this.prevSeg = prev_seg;
	}
}
