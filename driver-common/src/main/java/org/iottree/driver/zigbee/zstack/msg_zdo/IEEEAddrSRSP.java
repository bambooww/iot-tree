package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.zdo.AddrRsp;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class IEEEAddrSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x01 ;

	AddrRsp zspecRsp = null ;
	
	public IEEEAddrSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_IEEE_ADDR_REQ";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;//IEEEAddrRsp.PACK_MIN_LEN;
	}
	
//	public boolean isCluster()
//	{
//		return true ;
//	}
//
//	@Override
//	public byte[] getClusterData()
//	{
//		return this.zspecRsp.getClusterPack();
//	}
//
//	@Override
//	protected boolean parseClusterData(byte[] data, StringBuilder failedr)
//	{
//		this.zspecRsp = new IEEEAddrRsp() ;
//		return this.zspecRsp.fromClusterPack(data, failedr) ;
//	}
	
	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}

//	public IEEEAddrRsp getClusterRSP()
//	{//Status is either Success (0) or Failure (1).
//		return this.zspecRsp ;
//	}
	
	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[0]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus());
	}

//	@Override
//	public String toString()
//	{
//		return super.toString()+this.zspecRsp;
//	}

}
