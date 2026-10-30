package org.iottree.driver.zigbee.ember;

/**
 * 异步消息等待器
 * 
 * @author jason.zhu
 */
public abstract class EZSPWaiter
{
	private long toMs;
	
	private EZSPRsp waitRsp = null ;
	
	private long waitStartDT = System.currentTimeMillis() ;
	private long waitCost = -1 ;
	private long waitEndDT = -1 ;
	
	public EZSPWaiter(long to_ms)
	{
		if(to_ms<=0)
			throw new IllegalArgumentException("time out msg must >0") ;
		this.toMs = to_ms ;
	}
	
	void startWait()
	{
		this.waitStartDT = System.currentTimeMillis() ;
	}
	
	int RT_checkEnd()
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
	
	boolean RT_onRspRecv(ASHFrmData fd,EZSPRsp m_recv)
	{
		if(!RT_chkRsp(fd,m_recv))
		{
			return false;
		}
		waitRsp = m_recv ;
		this.waitEndDT = System.currentTimeMillis() ;
		waitCost = this.waitEndDT - this.waitStartDT ;
		return true;
	}
	

	public long getWaitCost()
	{
		return this.waitCost ;
	}
	
	public EZSPRsp getWaitRsp()
	{
		return this.waitRsp ;
	}
	
	public abstract boolean RT_chkRsp(ASHFrmData fd,EZSPRsp rsp) ;
}
