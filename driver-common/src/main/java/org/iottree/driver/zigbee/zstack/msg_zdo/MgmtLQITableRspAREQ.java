package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.zdo.LQITableRsp;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class MgmtLQITableRspAREQ extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xB1 ;
	
	LQITableRsp tableRsp = new LQITableRsp() ;
	
	public MgmtLQITableRspAREQ()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "LQI_TABLE_RESPONSE";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 4;//tableRsp.getClusterPackMinLen();
	}
	
	public boolean isCluster()
	{
		return true ;
	}

	@Override
	public byte[] getClusterData()
	{
		return this.tableRsp.getClusterPack();
	}

	@Override
	protected boolean parseClusterData(byte[] data, StringBuilder failedr)
	{
		this.tableRsp.unpackFrom(new ZDOBuffer(data,0,data.length)) ;
		//return this.tableRsp.fromClusterPack(data, failedr) ;
		return true;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return this.tableRsp.toJO() ;
	}

//	@Override
//	public String toString()
//	{
//		String r = super.toString() ;
//		return r += this.tableRsp.toString() ;
//	}
}
