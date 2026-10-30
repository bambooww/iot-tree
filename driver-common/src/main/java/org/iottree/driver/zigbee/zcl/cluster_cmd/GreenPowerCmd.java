package org.iottree.driver.zigbee.zcl.cluster_cmd;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.json.JSONObject;

public class GreenPowerCmd extends ZCLFrmClusterCmd
{
	byte[] pld_bs ;
	
	public GreenPowerCmd(int cmd_id,boolean dir_s2c,byte[] pldbs)
	{
		super(cmd_id,dir_s2c);
		this.pld_bs = pldbs ;
	}
	
	public static GreenPowerCmd createGPPermitJoin(boolean permit_or_not)
	{
		byte[] plds = new byte[3] ;
		if(permit_or_not)
		{
			plds[0] = 0x0B ; //option
			DataUtil.setUInt16(0xFE, plds, 1); // second 254
		}
		else
		{
			plds[0] = 0x0A ; //option
			DataUtil.setUInt16(0, plds, 1); // second 0
		}
		return new GreenPowerCmd(2,true,plds) ; //s2c=true
	}
	
	@Override
	protected int getPayloadLen()
	{
		if(pld_bs==null)
			return 0;
		return pld_bs.length ;
	}

	@Override
	protected int packPayloadDataTo(byte[] data, int offset)
	{
		if(pld_bs==null||pld_bs.length<=0)
			return 0 ;
		System.arraycopy(this.pld_bs, 0, data, offset, this.pld_bs.length);
		return pld_bs.length;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected JSONObject getPayloadJO()
	{
		return null;//new JSONObject().put("cluster_cmd_id",this.cmdId);
	}

}
