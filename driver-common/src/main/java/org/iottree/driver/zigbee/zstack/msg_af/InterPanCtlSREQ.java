package org.iottree.driver.zigbee.zstack.msg_af;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.AddrMode;
import org.iottree.driver.zigbee.zstack.MsgData;

public class InterPanCtlSREQ extends MsgData
{	
	public InterPanCtlSREQ()
	{
		super(0x24,0x10) ;
	}
	
	/**
	 * cmd                 data len      desc
	 * 0: InterPanClr    0            Proxy call to StubAPS_SetIntraPanChannel() to switch channel back to the NIB-specified channel.
	   1: InterPanSet    1            Proxy call to StubAPS_SetInterPanChannel() with the 1-byte channel specified.
	   2: InterPanReg   1           If the 1-byte Endpoint specified by the data argument is found by invoking afFindEndPointDesc(), then proxy a call to
			                                StubAPS_AppRegister () with the pointer to the endPointDesc_t found
			                                  (i.e. the Endpoint must already be registered with AF).
	   3: InterPanChk   3            Proxy a call to StubAPS_InterPan() with the 2-byte PanId (LSB:MSB) and 1-byte EndPoint data.
	   
	 * @param cmd
	 * @param data
	 */
	public InterPanCtlSREQ(int cmd,byte[] dd)
	{
		super(0x24,0x10) ;
		
		int dlen = data!=null?data.length:0 ;
		data = new byte[1+dlen] ;
		data[0] = (byte)cmd ;
		if(dd!=null && dd.length>0)
			System.arraycopy(dd, 0, data, 1, dlen);
	}
	
	@Override
	public String getTpName()
	{
		return "AF_INTER_PAN_CTL";
	}
	@Override
	public int getDataLenMin()
	{
		return 1+(this.data!=null?this.data.length:0);
	}
	
	public int getInterPanCmd()
	{
		return this.data[0] & 0xFF ;
	}
	
	public byte[] getInterPanData()
	{
		byte[] r = null ;
		if(this.data==null || this.data.length<=1)
			return r ;
		r = new byte[this.data.length-1] ;
		System.arraycopy(this.data, 1, r, 0, r.length);
		return r ;
	}
	
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		ret += " inter_pan_cmd="+this.getInterPanCmd()+" inter_pan_data="+Convert.byteArray2HexStr(getInterPanData(), "-") ;
		return ret ;
	}
}
