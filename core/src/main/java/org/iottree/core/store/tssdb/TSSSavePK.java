package org.iottree.core.store.tssdb;

public class TSSSavePK
{
	TSSTagSegs<?> segs ;
	
	int mem_seg_num ; //本次保存数量
	
	long fromdt ; //本次保存起始时间
	
	long todt ;
	
	long last_seg_enddt ; //本次保存最后一个
	
	TSSSavePK(TSSTagSegs<?> segs,int mem_segn,long from_dt,long to_dt, 
			long last_seg_dt)
	{
		this.segs = segs;
		this.mem_seg_num = mem_segn ;
		this.last_seg_enddt = last_seg_dt ;
		this.fromdt = from_dt ;
		this.todt = to_dt ;
	}
	
	public TSSTagSegs<?> getTagSegs()
	{
		return segs ;
	}
	
	/**
	 * this save cached seg num - not include last one
	 * @return
	 */
	public int getMemSegNum()
	{
		return mem_seg_num ;
	}
	
	/**
	 * this save last seg enddt
	 * @return
	 */
	public long getLastSegEndDT()
	{
		return last_seg_enddt ;
	}
	
	public String getTag()
	{
		return segs.getTag() ;
	}
	
	public int getAffectRowNum()
	{
		return (mem_seg_num>0?mem_seg_num:0) + (last_seg_enddt>0?1:0) ;
	}
	
	public long getFromDT()
	{
		return this.fromdt ;
	}
	
	public long getToDT()
	{
		return this.todt ;
	}
}