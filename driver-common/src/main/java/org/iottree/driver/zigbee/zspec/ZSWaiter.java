package org.iottree.driver.zigbee.zspec;

/**
 * 异步消息等待器
 * 
 * @author jason.zhu
 */
public abstract class ZSWaiter<M,R> //ASHFrmData fd,EZSPRsp rsp
{
	private long toMs;
	
	private R waitRsp = null ; //EZSPRsp waitRsp = null ;
	
	private long waitStartDT = System.currentTimeMillis() ;
	private long waitCost = -1 ;
	private long waitEndDT = -1 ;
	
	public ZSWaiter(long to_ms)
	{
		if(to_ms<=0)
			throw new IllegalArgumentException("time out msg must >0") ;
		this.toMs = to_ms ;
	}
	
	public final void startWait()
	{
		this.waitStartDT = System.currentTimeMillis() ;
	}
	
	public final int RT_checkEnd()
	{
		boolean bget_recv = (this.waitRsp!=null) ;
		if(bget_recv)
			return 1 ;
		if(System.currentTimeMillis()-waitStartDT>this.toMs)
		{
			//this.errInf = "time out" ;
			return -1 ; //error
		}
		return 0 ; // keep waiting
	}
	
	public final boolean RT_onRspRecv(M m,R rsp)
	{
		if(!RT_chkRsp(m,rsp))
		{
			return false;
		}
		waitRsp = rsp ;
		this.waitEndDT = System.currentTimeMillis() ;
		waitCost = this.waitEndDT - this.waitStartDT ;
		return true;
	}
	

	public long getWaitCost()
	{
		return this.waitCost ;
	}
	
	public R getWaitRsp()
	{
		return this.waitRsp ;
	}
	
	public abstract boolean RT_chkRsp(M fd,R rsp) ;
}
