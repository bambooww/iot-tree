package org.iottree.driver.zigbee.zcl.cmd_frm;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

/**
 * config attribute report
 * 
 * @author jason.zhu
 *
 */
public class ConfigReport extends ZCLFrame
{
	ArrayList<AttrReportConfig> attr_reports = new ArrayList<>() ;
	
	@Override
	public int getCmdID()
	{
		return 0x06; // Configure Reporting
	}
	
	@Override
	public String getCmdName()
	{
		return "config_report" ;
	}
	
	@Override
	public int getCmdIDResp()
	{
		return 0x07 ;
	}
	
	public void setAttrReportConfig(AttrReportConfig rc)
	{
		this.attr_reports.add(rc) ;
	}
	
	public void setAttrReportConfigs(List<AttrReportConfig> rcs)
	{
		this.attr_reports.addAll(rcs) ;
	}

	@Override
	protected int getPayloadLen()
	{
		int r = 0 ;
		for(AttrReportConfig rc:this.attr_reports)
			r += rc.calcPackedLen() ;
		return r;
	}
	
	@Override
	protected JSONObject getPayloadJO()
	{
		JSONObject r = new JSONObject();
		r.put("report_config_num", this.attr_reports.size()) ;
		JSONArray jarr = new JSONArray() ;
		r.put("report_configs", jarr) ;
		for(AttrReportConfig rc:this.attr_reports)
			jarr.put(rc.toJO()) ;
		return r ;
	}

	@Override
	protected int packPayloadDataTo(byte[] data,int offset)
	{
		int r = 0 ;
		for(AttrReportConfig rc:this.attr_reports)
		{
			//r += rc.calcPackedLen() ;
			byte[] bs = rc.packTo() ;
			System.arraycopy(bs, 0, data, offset, bs.length);
			r += bs.length ;
		}
		return r;
	}

	@Override
	protected boolean unpackPayloadData(byte[] data, int offset, StringBuilder failedr)
	{
		int dlen = data.length - offset ;
		int sidx = offset ;
		while(dlen>0)
		{
			AttrReportConfig rc = new AttrReportConfig() ;
			int len = rc.parseFrom(data, sidx) ;
			if(len<0)
				return false;
			this.attr_reports.add(rc) ;
			sidx += len ;
			dlen -= len ;
		}
		return true;
	}

}
