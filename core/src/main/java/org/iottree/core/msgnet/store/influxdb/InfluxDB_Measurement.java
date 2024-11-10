package org.iottree.core.msgnet.store.influxdb;

import java.util.List;

import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class InfluxDB_Measurement extends MNNodeRes
{
	String measurement = null ;

	@Override
	public String getTP()
	{
		return "influxdb_mt";
	}

	@Override
	public String getTPTitle()
	{
		return "Measourement";
	}

	@Override
	public String getColor()
	{
		return "#f3b484";
	}

	@Override
	public String getIcon()
	{
		return "PK_influxdb";
	}

	@Override
	public String getPmTitle()
	{
		return measurement;
	}
	
	public String getMeasurement()
	{
		return this.measurement ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(measurement))
		{
			failedr.append("no measurement name") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("mt", this.measurement) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("mt") ;
	}

	// rt lines
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
//		if(outTP==OutTP.msg_per_ln)
//		{
//			StringBuilder divsb = new StringBuilder() ;
//			divsb.append("<div class=\"rt_blk\">Read Line CC= "+LINE_CC) ;
//			divsb.append("</div>") ;
//			divblks.add(new DivBlk("file_r_line_cc",divsb.toString())) ;
//		}
		
		super.RT_renderDiv(divblks);
	}
	
}
