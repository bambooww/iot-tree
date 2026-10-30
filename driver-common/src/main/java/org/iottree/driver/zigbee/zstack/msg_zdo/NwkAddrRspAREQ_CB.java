package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class NwkAddrRspAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0x80 ;
	
	public NwkAddrRspAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_NWK_ADDR_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 13;
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
	{//Status is either Success (0) or Failure (1).
		return this.data[0]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}

	
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(data, 1) ; //1-8
	}
	/**
	 * Specifies Device’s short address that this response describes.
	 * @return
	 */
	public ShortAddr getNwkAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 9)) ; //9-10
	}
	
	public int getStartIdx()
	{
		return this.data[11] & 0xFF ;
	}
	
	/**
	 * Specifies the length of the simple descriptor
	 * @return
	 */
	public int getNumAssocDev()
	{
		return this.data[12] & 0xFF ;
	}
	
	public List<Integer> getAssocDevList()
	{
		int n = getNumAssocDev() ;
		ArrayList<Integer> rets = new ArrayList<>(n) ;
		if(n<=0)
			return rets ;
		for(int i = 0 ; i < n ; i ++)
		{
			rets.add(DataUtil.getUInt16(this.data, 13+i*2)) ;
		}
		return rets ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.putOpt("ieee_addr", this.getIEEEAddr()).put("nwk_addr", this.getNwkAddr().getAddrVal())
				.put("assoc_dev_num", this.getNumAssocDev()).put("start_idx", this.getStartIdx())
				.putOpt("assoc_devs", this.getAssocDevList())
				.put("data_hex",Convert.byteArray2HexStr(this.data));
	}
	
	

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus()+" ieee_addr="+this.getIEEEAddr()
//			+" nwk_addr="+this.getNwkAddr()+" start_idx="+this.getStartIdx()+" assoc_dev_n="+this.getNumAssocDev()
//			+"\r\n\tdev_list="+Convert.combineWith(this.getAssocDevList(), ',');
//	}
}
