package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrStatus;
import org.json.JSONObject;

public class ConfigRepReadRsp extends ZCLFrame
{
	ArrayList<AttrStatus> attr_status = new ArrayList<>() ;
	
	@Override
	public int getCmdID()
	{
		return 0x09; // Read Reporting Configuration Response
	}
	
	@Override
	public String getCmdName()
	{
		return "config_report_read_rsp" ;
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
		return r ;
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
