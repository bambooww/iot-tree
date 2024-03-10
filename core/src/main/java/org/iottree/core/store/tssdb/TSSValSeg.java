package org.iottree.core.store.tssdb;

/**
 * 一次相同值的起始发现和结束时间点。
 * 值在有效的时间范围是 ( 大于等于startDT && 小于 endDT)
 * @return
 */
public class TSSValSeg<T>
{
	long startDT ;
	
	boolean bvalid ;
	
	T val ;
	
	long endDT ;
	
	
	/**
	 * 未作存储
	 */
	transient boolean bNew ;
	
	public TSSValSeg(long dt,boolean bvalid,T val,boolean b_new)
	{
		this.startDT = this.endDT = dt ;
		this.bvalid = bvalid ;
		this.val = val ;
		//this.bDirty = b_dirty ;
		this.bNew = b_new ;
	}
	
	public TSSValSeg(long sdt,long edt,boolean bvalid,T val,boolean b_new)
	{
		this.startDT = sdt;
		this.endDT = edt ;
		this.bvalid = bvalid ;
		this.val = val ;
		
		//this.bDirty = b_dirty ;
		this.bNew = b_new ;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof TSSValSeg))
			return false;
		TSSValSeg<?> ovs = (TSSValSeg<?>)obj ;
		if(startDT!=ovs.startDT)
			return false;
		if(bvalid!=ovs.bvalid)
			return false;
		if(endDT!=ovs.endDT)
			return false;
		if(!bvalid)
			return true ;
		
		return val.equals(ovs.val) ;
	}
	
//	public boolean isDirty()
//	{
//		return this.bDirty ;
//	}
	
	public boolean isNew()
	{
		return this.bNew ;
	}
	
	void setSavedOk()
	{
//		this.bDirty = false;
		this.bNew = false;
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
	
	public boolean containsDT(long dt)
	{
		if(dt<startDT) return false;
		if(dt>endDT) return false;
		if(dt<endDT) return true ;
		
		//dt==endDT
		return startDT==endDT ;
	}
	
	public boolean hitFromToDT(long from_dt,long to_dt)
	{
		if(startDT==endDT)
			return from_dt<=startDT && startDT<to_dt ;
		
		if(to_dt<startDT) return false;
		if(from_dt>=endDT) return false;
		
		return true;
	}
	
	public String toString()
	{
		return toFormatStr() ;
	}
}
