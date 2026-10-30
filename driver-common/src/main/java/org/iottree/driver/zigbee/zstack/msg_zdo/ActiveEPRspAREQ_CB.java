package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class ActiveEPRspAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0x85 ;
	
	
	public ActiveEPRspAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_ACTIVE_EP_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 6;
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

	/**
	 * The message’s source network address
	 * @return
	 */
	public int getSrcAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ; //0-1
	}
	
	//This field indicates either SUCCESS or FAILURE.
	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[2]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[2] & 0xFF;
	}
	
	/**
	 * Device’s short address that this response describes.
	 * @return
	 */
	public int getNwkAddr()
	{
		return DataUtil.getUInt16(this.data, 3) ; //3-4
	}
	
	public int getActiveEPCount()
	{
		return this.data[5] & 0xFF ;
	}
	
	public List<Integer> getActiveEPList()
	{
		int c = this.getActiveEPCount() ;
		if(c>77)
			return null ;
		ArrayList<Integer> rets = new ArrayList<>(c) ;
		for(int i = 0 ; i < c ; i ++)
		{
			rets.add(this.data[6+i] & 0xFF) ;
		}
		return rets ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.put("src_addr", this.getSrcAddr()).put("nwk_addr", this.getNwkAddr()).put("active_ep_count", this.getActiveEPCount())
				.putOpt("active_ep_list", this.getActiveEPList());
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus()+ " src_addr="+DataUtil.transHex(getSrcAddr())+" nwk_addr="+DataUtil.transHex(this.getNwkAddr())
//				+" ep_count="+this.getActiveEPCount()+" ep_list="+Convert.combineWith(this.getActiveEPList(), ',');
//	}
}
