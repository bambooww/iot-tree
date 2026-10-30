package org.iottree.driver.zigbee.zcl.cmd_frm;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.json.JSONObject;

/**
 * WriteAttr Undivided (不可分割)
 * 
 * @author jason.zhu
 *
 */
public class WriteAttrUndiv extends ZCLFrame
{

	@Override
	public int getCmdID()
	{
		return 0x03;
	}
	
	@Override
	public String getCmdName()
	{
		return "write_attr_undiv" ;
	}

	@Override
	protected int getPayloadLen()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected JSONObject getPayloadJO()
	{
		JSONObject r = new JSONObject();
		return r;
	}

	@Override
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		return -1;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
