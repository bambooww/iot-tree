package org.iottree.core.store.tssdb;

public class TSSValSegHit<T>
{
	public TSSValSeg<T> prevSeg = null ;
	
	/**
	 * may hit seg at some date point
	 */
	public TSSValSeg<T> hitSeg = null ;
	
	/**
	 * next seg order by startdt
	 */
	public TSSValSeg<T> nextSeg = null ;
	

	
	TSSValSegHit(TSSValSeg<T> prev_seg,TSSValSeg<T> hit_seg,TSSValSeg<T> next_seg)
	{
		this.hitSeg = hit_seg ;
		this.prevSeg = prev_seg;
		this.nextSeg = next_seg ;
	}
}
