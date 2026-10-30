package org.iottree.driver.zigbee.ember;

/**
 * 纯确认帧
 * @author jason.zhu
 *
 */
public class ASHFrmAck extends ASHFrame
{
	public ASHFrmAck(int ctrlb)
	{
		super(ctrlb) ;
	}
	
	public static ASHFrmAck createAck(boolean nrdy,int ack_n)
	{
		int ctrlb = 0b10000000;
		if(nrdy)
			ctrlb = ctrlb | 0b00001000 ;
		ctrlb |= (ack_n & 0b111) ;
		return new ASHFrmAck(ctrlb) ;
	}
	/**
	 * nRdy – host sets to 1 if to inhibit the NCP from sending callbacks frames to the host 
	 * (always 0 in frames sent by the NCP)
	 * 
	 * nRdy 是 ASH DATA 帧 Control Byte 里的一个流控位：Host 把它置 1，表示“我现在忙，NCP 先别发回调帧给我”；NCP 发出的帧里这个位永远是 0。
	 *      发送方	        nRdy = 0	                                        nRdy = 1
		Host → NCP	正常状态，NCP 可以发送回调/事件帧	    暂时抑制 NCP 发送回调帧
		NCP → Host	固定为 0，不使用该位	                        不会出现
		
		nRdy 是 Host 对 NCP 的“暂停回调”信号，不是双向协商位。
		
		它抑制的是 NCP 主动发给 Host 的 callback / event 帧，比如设备入网、属性上报、ZCL 事件等。
		
	 * @return
	 */
	public boolean isNRdy()
	{
		return (this.ctrl & 0b1000)>0 ;
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
	void setAckNum(int ack_n)
	{
		if(ack_n<0 || ack_n>7)
			throw new IllegalArgumentException("ack_n must in [0-7]") ;
		this.ctrl = this.ctrl & 0b11111000 ;
		this.ctrl = this.ctrl | ack_n ;
	}
	
	@Override
	public int getDataFieldLen()
	{
		return 0;
	}

	@Override
	protected boolean packDataField(byte[] data, int offset,int dflen)
	{
		return true;
	}
	@Override
	protected boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr)
	{
		return true;
	}

	@Override
	public String toShortString()
	{
		return "ACK ack_n="+this.getAckNum();
	}

	public String toString()
	{
		return "ACK nRdy="+this.isNRdy() +" ack_n="+this.getAckNum();
	}

}
