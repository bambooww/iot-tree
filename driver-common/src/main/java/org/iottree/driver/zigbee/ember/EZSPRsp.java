package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.ember.util.EZSPCallbackType;

/**
 * response from NCP
 * 
 * @author jason.zhu
 */
public abstract class EZSPRsp extends EZSPFrame
{
	public EZSPRsp(EmberManager emgr)
	{
		super(emgr) ;
	}

	/**
	 * 0 - No memory shortage since the previous response.
	 * 1 - The NCP ran out of memory since the previous response.
	 * @return
	 */
	public boolean isOverflow()
	{
		return (this.frmCtrlLow & 0x01)>0 ;
	}
	
	/**
	 * 0-The current response was not truncated.
	 * 1-The NCP truncated the current response to avoid exceeding the maximum EZSP frame length.
	 * @return
	 */
	public boolean isTruncated()
	{
		return (this.frmCtrlLow & 0b10 )>0 ;
	}
	
	/**
	 * 1=A callback is pending on the NCP. If this response is a callback, at least one more callback is available.
		0=All callbacks have been delivered to the host.
	 * @return
	 */
	public boolean isCallbackPending()
	{
		return (this.frmCtrlLow & 0b100 )>0 ;
	}
	
	public EZSPCallbackType getCallbackType()
	{
		return new EZSPCallbackType((this.frmCtrlLow>>3)&0b11) ; //bit 3-4
	}
	
//	@Override
//	protected void unpackParams(byte[] bs, int offset, int len)
//	{
//		EZSPParams r = EZSPParams.createRspByFrmID(this.getID()) ;
//		if(r==null)
//			return ;
//		if(!r.unpackParams(bs, offset, len))
//			return ;
//		this.params = r ;
//	}
	
	public String toString()
	{
		String r ="RSP*"+ super.toString() ;
		r += ";overflow:"+this.isOverflow()+";truncated:"+this.isTruncated()+";callback_pend:"+this.isCallbackPending()
			+";callback_tp:"+this.getCallbackType() ;
		return r ;
	}

}
