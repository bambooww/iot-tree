package org.iottree.driver.zigbee.zcl.cmd_frm;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.json.JSONObject;

/**
 * 0x0b=11 Default Response
 * @author jason.zhu
 *
 */
public class DefaultRsp extends ZCLFrame
{
	public static final int CMD_ID = 0x0b ;
	
	int recvCmdId =0;
	
	int statusCode ;
	
	@Override
	public int getCmdID()
	{
		return CMD_ID; // 11
	}
	
	@Override
	public String getCmdName()
	{
		return "default_rsp" ;
	}

	@Override
	protected int getPayloadLen()
	{
		return 2;
	}

	@Override
	protected int packPayloadDataTo(byte[] data, int offset)
	{
		data[offset] = (byte)recvCmdId ;
		data[offset+1] = (byte)statusCode ;
		return 2;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		this.recvCmdId = data[offset] & 0xFF ;
		this.statusCode = data[offset+1] & 0xFF ;
		return true;
	}

	@Override
	protected JSONObject getPayloadJO()
	{
		return new JSONObject().put("recv_cmd_id",this.recvCmdId).put("status_code",this.statusCode);
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		ret += " recv_cmd_id="+this.recvCmdId+" status_code="+this.statusCode ;
		return ret ;
	}
}
