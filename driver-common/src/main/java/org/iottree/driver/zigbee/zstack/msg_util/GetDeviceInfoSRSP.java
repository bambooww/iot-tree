package org.iottree.driver.zigbee.zstack.msg_util;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.Capabilities;
import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.DeviceType;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class GetDeviceInfoSRSP extends MsgRecv
{
	public static final int CMD0 = 0x67 ;
	public static final int CMD1 = 0x00 ;
	
	public GetDeviceInfoSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "UTIL_GET_DEVICE_INFO";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 14;
	}

	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}

	public boolean getStatus()
	{
		return this.data[0]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}
	
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data,1) ; //1-8
	}
	
	public int getShortAddr() // node id 2byte,Nwk addr
	{
		return DataUtil.getUInt16(this.data,9) ; //9-10
	}
	
	public boolean isShortAddrValid()
	{
		return this.getShortAddr()<0xFFFE ;
	}
	
	public DeviceType getDeviceType()
	{
		return new DeviceType(this.data[11]&0xFF) ;
	}
	
	public DeviceState getDeviceState()
	{
		return new DeviceState(this.data[12] & 0xFF) ;
	}
	
	/**
	 * Specifies the number of Reduce Function Devices being associated to the target device
	 * @return
	 */
	public int getNumAssocDevices()
	{
		return this.data[13] & 0xFF;
	}
	
	/**
	 * Array of 16-bits of network addresses of Reduce Function Devices associated to the local device
	 * @return
	 */
	public List<Integer> listAssocDeviceNwkAddr()
	{
		int c = getNumAssocDevices() ;
		ArrayList<Integer> rets = new ArrayList<>() ;
		if(c<=0||c>64)
			return null ;
		for(int i = 0 ; i < c ; i ++)
		{
			int addr = DataUtil.getUInt16(this.data, 14+i*2) ;
			rets.add(addr) ;
		}
		return rets ;
	}
	
	
	
	@Override
	public JSONObject toRecvJO()
	{
		DeviceType dtp = this.getDeviceType() ;
		DeviceState dst = this.getDeviceState() ;
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.putOpt("ieee_addr", this.getIEEEAddr()).putOpt("short_addr", this.getShortAddr())
				.putOpt("short_addr_ok", this.isShortAddrValid())
				.putOpt("dev_tp",dtp.getTypeVal()).putOpt("dev_tpt", dtp.getTypeTitle())
				.putOpt("dev_st", dst.getStateV()).putOpt("dev_st_t", dst.getStateT())
				.putOpt("assoc_dev_num", this.getNumAssocDevices()).putOpt("assoc_nwk_addrs", this.listAssocDeviceNwkAddr())
				.put("data_hex", Convert.byteArray2HexStr(this.data));
	}
	
	
	
//	@Override
//	public String toString()
//	{
//		return super.toString()+" status="+this.getStatus() +" IEEEAddr="+this.getIEEEAddr()
//			+" ShortAddr=0x"+String.format("%04X", this.getShortAddr())
//			+" DeviceType="+this.getDeviceType()
//			+" DeviceState="+this.getDeviceState()
//			+" AssocDeviceNum="+this.getNumAssocDevices()
//			+"    \r\n"+Convert.combineWith(this.listAssocDeviceNwkAddr(), ',');
//			
//		
//	}
}
