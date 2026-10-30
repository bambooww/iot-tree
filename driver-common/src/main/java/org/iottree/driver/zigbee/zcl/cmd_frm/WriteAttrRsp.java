package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrReadStatusVal;
import org.json.JSONObject;

public class WriteAttrRsp extends ZCLFrame
{
//	int attrId ;
//	int status ;
	
	ArrayList<AttrReadStatusVal> rsVals = new ArrayList<>() ;
	
	@Override
	public int getCmdID()
	{
		return 0x04;
	}
	
	@Override
	public String getCmdName()
	{
		return "write_attr_rsp" ;
	}
	
//	public int getAttrId()
//	{
//		return this.attrId ;
//	}
//	
//	public int getStatus()
//	{
//		return this.status ;
//	}
	
//	public boolean isStatusSucc()
//	{
//		return this.status == 0 ;
//	}
	
	public ArrayList<AttrReadStatusVal> getReadStatusVals()
	{
		return this.rsVals ;
	}

	@Override
	protected int getPayloadLen()
	{
		int r = 0 ;
		for(AttrReadStatusVal rv: rsVals)
		{
			r += rv.getPackLen() ;
		}
		return r;
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
		return -1 ;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		if(data==null)
			return false;
		
		int sidx = offset;
		while(data.length-sidx>=3)
		{
			AttrReadStatusVal sv =AttrReadStatusVal.parseFrom(data,sidx) ;
			if(sv==null)
				return false;
			this.rsVals.add(sv) ;
			sidx += sv.getPackLen() ;
		}
		return true ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		for(AttrReadStatusVal rv: rsVals)
		{
			ret += "\r\n   "+rv;
		}
		
		return ret ;
	}
}
