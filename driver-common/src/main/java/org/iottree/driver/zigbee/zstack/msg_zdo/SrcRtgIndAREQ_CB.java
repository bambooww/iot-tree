package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This message is an indication to inform host device the receipt of a source 
 * route to a given device.
 * 
 * @author jason.zhu
 *
 */
public class SrcRtgIndAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xC4 ;
	
	public SrcRtgIndAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_SRC_RTG_IND";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 3;
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
	
	public ShortAddr getDstAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ; //0-1
	}
	
	public int getRelayCount()
	{
		return this.data[2] & 0xFF ;
	}
	
	public ArrayList<ShortAddr> getRelayList()
	{
		int c = getRelayCount() ;
		ArrayList<ShortAddr> rets = new ArrayList<>(c); 
		if(c>0)
		{
			for(int i = 0 ; i < c ; i ++)
				rets.add(new ShortAddr(DataUtil.getUInt16(this.data, 3+i*2)));
		}
		return rets ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("dst_addr",this.getDstAddr()).put("relay_count", this.getRelayCount())
				.putOpt("relay_list", Convert.combineWith(this.getRelayList(), ','));
	}

//	public String toString()
//	{
//		return super.toString() +" dst_addr="+this.getDstAddr()+" relay_count="+this.getRelayCount()
//		+" relay_addrs="+Convert.combineWith(this.getRelayList(), ',');
//	}
}
