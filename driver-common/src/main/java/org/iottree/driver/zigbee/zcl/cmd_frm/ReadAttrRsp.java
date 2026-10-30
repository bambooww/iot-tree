package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;
import java.util.HashMap;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrReadStatusVal;
import org.iottree.driver.zigbee.zcl.util.DataVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class ReadAttrRsp extends ZCLFrame
{
	ArrayList<AttrReadStatusVal> rsVals = new ArrayList<>() ;
	
	@Override
	public int getCmdID()
	{
		return 0x01;
	}
	
	@Override
	public String getCmdName()
	{
		return "read_attr_rsp" ;
	}
	
	public ArrayList<AttrReadStatusVal> getReadStatusVals()
	{
		return this.rsVals ;
	}
	
	public HashMap<Integer,DataVal> getReadAttrId2DataVal()
	{
		HashMap<Integer,DataVal> ret = new HashMap<>() ;
		for(AttrReadStatusVal rsv:this.rsVals)
		{
			if(!rsv.isStatusOk())
				continue ;
			int attrid = rsv.getAttrId() ;
			DataVal dv = rsv.getDataVal() ;
			if(dv==null)
				continue ;
			ret.put(attrid,dv) ;
		}
		return ret ;
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
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		int len = getPayloadLen() ;
		int sidx = offset ;
		for(AttrReadStatusVal rv: rsVals)
		{
			int rlen = rv.packTo(data, sidx) ;
			if(rlen<0)
				return -1 ;
			sidx += rlen ;
		}
		return len;
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
	protected JSONObject getPayloadJO()
	{
		JSONObject r = new JSONObject();
		JSONArray jarr = new JSONArray() ;
		r.put("st_vals",jarr) ;
		for(AttrReadStatusVal rsv:rsVals)
		{
			jarr.put(rsv.toJO()) ;
		}
		return r ;
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
