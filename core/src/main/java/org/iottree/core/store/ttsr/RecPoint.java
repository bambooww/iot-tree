package org.iottree.core.store.ttsr;

/**
 * 一次相同值的起始发现和结束时间点。
 * 值在有效的时间范围是 ( 大于等于startDT && 小于 endDT)
 * @return
 */
public class RecPoint<T>
{
	long startDT ;
	
	boolean bvalid ;
	
	T val ;
	
	long endDT ;
	
	public RecPoint(long dt,boolean bvalid,T val)
	{
		this.startDT = this.endDT = dt ;
		this.bvalid = bvalid ;
		this.val = val ;
	}
	
	public RecPoint(long sdt,long edt,boolean bvalid,T val)
	{
		this.startDT = sdt;
		this.endDT = edt ;
		this.bvalid = bvalid ;
		this.val = val ;
	}
	
	public long getStartDT()
	{
		return startDT ;
	}
	
	public boolean isValid()
	{
		return bvalid ;
	}
	
	public T getVal()
	{
		return val ;
	}
	
	public long getEndDT()
	{
		return this.endDT ;
	}
	
	/**
	 * 数据更新发现值没有变化，则可以触发此函数进行结束时间延长
	 * @param dt
	 */
	public void setEndDT(long dt)
	{
		if(dt<=this.endDT)
			return ;
		this.endDT = dt ;//
	}
	
	public String toFormatStr()
	{
		return startDT+","+(bvalid?"1":"0")+","+(val==null?"":val.toString())+","+endDT ;
	}
}
