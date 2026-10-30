package org.iottree.driver.zigbee.zcl.cmd_frm;

import java.util.ArrayList;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrStatus;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The Configure Reporting Response command is used to respond to a Configure Reporting command.
 * 
 * @author jason.zhu
 */
public class ConfigReportRsp extends ZCLFrame
{
	ArrayList<AttrStatus> attr_statuss = new ArrayList<>() ;
	
	@Override
	public int getCmdID()
	{
		return 0x07; // Configure Reporting Response
	}
	
	@Override
	public String getCmdName()
	{
		return "config_report_rsp" ;
	}

	@Override
	protected int getPayloadLen()
	{
		return attr_statuss.size()*4;
	}
	
	@Override
	protected JSONObject getPayloadJO()
	{
		JSONObject r = new JSONObject();
		JSONArray jarr = new JSONArray() ;
		r.put("attr_statuss",jarr) ;
		for(AttrStatus rsv:attr_statuss)
		{
			jarr.put(rsv.toJO()) ;
		}
		return r ;
	}

	@Override
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		int pldlen = getPayloadLen() ;
		if(data.length-offset<pldlen)
			return -1 ;
		int sidx = offset ;
		for(int i = 0 ; i < attr_statuss.size() ; i ++)
		{
			AttrStatus ast = attr_statuss.get(i) ;
			ast.packTo(data, sidx);
			sidx += 4 ;
		}
		return pldlen;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		int leftn = data.length - offset ;
		if(leftn%4!=0)
		{
			failedr.append("pld len must N*4") ;
			return false;
		}
		int n = leftn / 4 ;
		int sidx = offset ;
		for(int i = 0 ; i < n ; i ++)
		{
			AttrStatus ast = new AttrStatus() ;
			int rlen = ast.parseFrom(data, sidx) ;
			if(rlen<0)
			{
				failedr.append("parse AttrStatus failed") ;
				return false;
			}
			this.attr_statuss.add(ast) ;
			sidx += 4 ;
		}
		return true ;
	}
}
