package org.iottree.driver.zigbee.ember;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataBuffer;

/**
 * FrameType = DATA
 * Sent By=NCP,Host
 * Data Field Size: 3-128
 * 
 * @author jason.zhu
 *
 */
public class ASHFrmData extends ASHFrame
{

	public static ASHFrmData createFrmDataCmd(EmberManager emgr,EZSPCmd cmd)
	{
		ASHFrmData r = new ASHFrmData(emgr,0) ;
		r.dfFrame = cmd;
		return r ;
	}
	
	public static ASHFrmData createFrmDataRsp(EmberManager emgr,EZSPRsp rsp)
	{
		ASHFrmData r = new ASHFrmData(emgr,0) ;
		r.dfFrame = rsp;
		return r ;
	}
	
	
	long rt_senddt = System.currentTimeMillis() ;
	int rt_retry_c = 0 ;
	
//	private int dfLen = -1 ;
//	
//	private byte[] dfData = null ;
	
	private EZSPFrame dfFrame = null ;
	
	private int unpackedFrmID = -1 ;
	
	private EZSPBuffer unpackedFrmBuf = null ;
	
	private EmberManager emgr ;
	
	public ASHFrmData(EmberManager emgr,int ctrl_b)
	{
		super(ctrl_b) ;
		this.emgr = emgr ;
	}
	
	public EmberManager getOwner()
	{
		return this.emgr ;
	}
	
	/**
	 * DATA frame’s 3-bit sequence number bit:4-6
	 * @return
	 */
	public int getFrmNum()
	{
		return (this.ctrl >> 4) & 0b111 ;
	}
	
	/**
	 * 0-7-0
	 * @param seq
	 */
	public void setFrmNum(int seq)
	{
		if(seq<0 || seq>7)
			throw new IllegalArgumentException("seq must in [0-7]") ;
		seq = seq << 4 ;
		this.ctrl = this.ctrl & 0b10001111 ;
		this.ctrl = this.ctrl | seq ;
	}
	
	/**
	 * reTx - set to 1 in a retransmitted DATA frame; 0 otherwise
	 * @return
	 */
	public boolean isReTx()
	{
		return (this.ctrl & 0b1000)>0 ;
	}
	
	/**
	 * 设置re transmitting  mark
	 * @param b
	 */
	public void setReTx(boolean b)
	{
		if(b)
			this.ctrl |= 0b1000 ;
		else
			this.ctrl &= 0b11110111 ;
	}
	/**
	 * ackNum - acknowledges receipt of DATA frames up to, but not including, ackNum
	 * @return
	 */
	public int getAckNum()
	{
		return this.ctrl & 0b111 ;
	}
	
	/**
	 * 0-7-0
	 * @param seq
	 */
	public void setAckNum(int ack_n)
	{
		if(ack_n<0 || ack_n>7)
			throw new IllegalArgumentException("ack_n must in [0-7]") ;
		this.ctrl = this.ctrl & 0b11111000 ;
		this.ctrl = this.ctrl | ack_n ;
	}

	@Override
	public int getDataFieldLen()
	{
		if(this.dfFrame==null)
			return -1 ;
		return dfFrame.getEZSPFrameLen(this.emgr.isLegacy());
	}
	
//	public byte[] getDataField()
//	{
//		return this.dfData ;
//	}
	
	public EZSPFrame getEZSPFrame()
	{
		return this.dfFrame ;
	}

	@Override
	protected boolean packDataField(byte[] data, int offset,int dflen)
	{
		StringBuilder failedr = new StringBuilder() ;
		if(this.dfFrame==null)
			return false;
		if(this.emgr.isLegacy())
			return this.dfFrame.packLegacy(data, offset, dflen,failedr) ;
		else
			return this.dfFrame.pack(data, offset, dflen,failedr) ;
	}

	@Override
	protected boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr)
	{
//		dfFrame = EZSPFrame.unpackLegacy(emgr,data,offset,dflen,failedr) ;
		
		//System.out.println("df="+Convert.byteArray2HexStr(data, offset,dflen,"-")) ;
		int[] ret_frmid = new int[1] ;
		if(this.emgr.isLegacy())
			dfFrame = EZSPFrame.unpackLegacy(emgr,data,offset,dflen,ret_frmid,failedr) ;
		else
			dfFrame = EZSPFrame.unpack(emgr,data,offset,dflen,ret_frmid,failedr) ;
		this.unpackedFrmID = ret_frmid[0] ;
		this.unpackedFrmBuf = new EZSPBuffer(data,offset,dflen) ;
		return dfFrame!=null;
	}
	
	public int getEZSPFrmIDUnpacked()
	{
		return this.unpackedFrmID;
	}
	
	
	public EZSPBuffer getEZSPFrmBufUnpacked()
	{
		return this.unpackedFrmBuf ;
	}
	
	@Override
	public String toShortString()
	{
		String r = "DATA dflen="+this.getDataFieldLen()+" ezsp_frm=";
		if(dfFrame!=null)
			r += dfFrame.toShortString() ;
		return r ;
	}
	
	public String toString()
	{
		return "DATA frm_n="+this.getFrmNum()+" reTx="+this.isReTx()+" ack_n="+this.getAckNum()+
				" rt_senddt="+rt_senddt+" rt_retry_c="+rt_retry_c+" dflen="+this.getDataFieldLen()+" ezsp_frm="+dfFrame;
	}

	transient int rt_sentNum = 0 ; //运行过程中发送次数
	
	transient long rt_sentDT = -1 ; //运行过程中发送时间
	
	/**
	 * 0=unknown
	 * 1=ReTx
	 * -1 = timeout ignore
	 * @return
	 */
	int RT_checkReTx()
	{
		return 0;
	}
}
