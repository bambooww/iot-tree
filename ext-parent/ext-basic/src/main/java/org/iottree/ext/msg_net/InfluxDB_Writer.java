package org.iottree.ext.msg_net;

import java.time.Instant;
import java.util.ArrayList;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDB_Writer extends MNNodeEnd
{
	public static class WDef
	{
		String id ;
		
		String name ;
		
		MNCxtValSty valSty = MNCxtValSty.vt_str ;
		
		String valSubN ;
		
		public WDef()
		{
		}
		
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("id", this.id) ;
			jo.putOpt("n",this.name) ;
			jo.putOpt("val_sty", this.valSty.name()) ;
			jo.putOpt("val_subn", this.valSubN) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.id = jo.getString("id") ;
			this.name = jo.optString("n","") ;
			this.valSty = MNCxtValSty.valueOf(jo.optString("val_sty","vt_str")) ;
			this.valSubN = jo.optString("val_subn","") ;
		}
	}
	
	String measurement = null ;
	
	MNCxtValSty tsSty = MNCxtValSty.timestamp ;
	
	String tsSubN ;
	
	ArrayList<WDef> tagWDefs = new ArrayList<>() ;
	
	ArrayList<WDef> fieldWDefs = new ArrayList<>() ;
	
	@Override
	public String getTP()
	{
		return "influxdb_w";
	}

	@Override
	public String getTPTitle()
	{
		return "InfluxDB Writer";
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
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(measurement))
//		{
//			failedr.append("no measurement set") ;
//			return false;
//		}
//		if(tagWDefs==null&&tagWDefs.size()<=0)
//		{
//			failedr.append("no tag set") ;
//			return false;
//		}
//		if(fieldWDefs==null&&fieldWDefs.size()<=0)
//		{
//			failedr.append("no field set") ;
//			return false;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("measurement", this.measurement) ;
		jo.putOpt("ts_sty", this.tsSty.name()) ;
		jo.putOpt("ts_subn", this.tsSubN) ;

		JSONArray jarr = new JSONArray() ;
		for(WDef wd:this.tagWDefs)
		{
			jarr.put(wd.toJO()) ;
		}
		jo.put("tags",jarr) ;
		
		jarr = new JSONArray() ;
		for(WDef wd:this.fieldWDefs)
		{
			jarr.put(wd.toJO()) ;
		}
		jo.put("fields",jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("measurement","") ;

		this.tsSty = MNCxtValSty.valueOf(jo.optString("ts_sty","timestamp")) ;
		if(this.tsSty==null)
			this.tsSty = MNCxtValSty.timestamp ;
		this.tsSubN = jo.optString("ts_subn","") ;
		
		JSONArray jarr = jo.optJSONArray("tags") ;
		ArrayList<WDef> defs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				WDef wd = new WDef() ;
				wd.fromJO(tmpjo);
				defs.add(wd) ;
			}
		}
		this.tagWDefs = defs ;
		
		jarr = jo.optJSONArray("fields") ;
		defs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				WDef wd = new WDef() ;
				wd.fromJO(tmpjo);
				defs.add(wd) ;
			}
		}
		this.fieldWDefs = defs ;
	}
	
	
	private Point calPoint(MNMsg msg)
	{
		Point point = Point.measurement(this.measurement);
		
		Long ts = (Long)this.tsSty.RT_getValInCxt(this.tsSubN, this.getBelongTo(), this, msg) ;
		point.time(ts,WritePrecision.MS);
		
		for(WDef tag:this.tagWDefs)
		{
			String tagn = tag.name ;
			String tagv = (String)tag.valSty.RT_getValInCxt(tag.valSubN, this.getBelongTo(), this, msg) ;
			point.addTag(tagn, tagv) ;
		}
		for(WDef f:this.fieldWDefs)
		{
			String n = f.name ;
			Object v = f.valSty.RT_getValInCxt(f.valSubN, this.getBelongTo(), this, msg) ;
			if(v instanceof Number)
				point.addField(n,(Number)v) ;
			else if(v instanceof String)
				point.addField(n,(String)v) ;
			else if(v instanceof Boolean)
				point.addField(n,(Boolean)v) ;
		}
		
		//		.addField("value", 55D)
		//		.time(Instant.now(), WritePrecision.MS);
				
		//		JSONObject jo,
		return point ;
	}
	
	private Point calPointFmt(MNMsg msg)
	{
		JSONObject  pld = msg.getPayloadJO(null) ;
		if(pld==null)
			return null ;
		
		String m = pld.optString("measurement",this.measurement) ;
		if(Convert.isNullOrEmpty(m))
			return null ;
		Point point = Point.measurement(m);
		long ts = pld.optLong("ts",-1) ;
		if(ts<=0)
			return null ;
		
		point.time(ts,WritePrecision.MS);
		
		JSONArray tagsjarr = pld.optJSONArray("tags") ;
		if(tagsjarr!=null)
		{
			int n = tagsjarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = tagsjarr.getJSONObject(i) ;
				String tagn = tmpjo.optString("n") ;
				if(Convert.isNullOrEmpty(tagn))
					continue ;
				String v = tmpjo.optString("v") ;
				if(Convert.isNullOrEmpty(v))
					continue ;
				point.addTag(tagn, v) ;
			}
		}
		
		JSONObject tagob = pld.optJSONObject("tagob") ;
		if(tagob!=null)
		{
			for(String n:tagob.keySet())
			{
				String v = tagob.optString(n) ;
				if(Convert.isNullOrEmpty(v))
					continue ;
				point.addTag(n, v) ;
			}
		}
		
		JSONArray fjarr = pld.optJSONArray("fields") ;
		if(fjarr!=null)
		{
			int n = fjarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = fjarr.getJSONObject(i) ;
				String fn = tmpjo.optString("n") ;
				if(Convert.isNullOrEmpty(fn))
					continue ;
				Object v = tmpjo.opt("v") ;
				if(v==null)
					continue ;
				
				if(v instanceof Number)
					point.addField(fn,(Number)v) ;
				else if(v instanceof String)
					point.addField(fn,(String)v) ;
				else if(v instanceof Boolean)
					point.addField(fn,(Boolean)v) ;
				else // if(v==null)
					point.addField(fn, (Number)null) ;
			}
		}
		
		JSONObject fieldob = pld.optJSONObject("fieldob") ;
		if(fieldob!=null)
		{
			for(String fn:fieldob.keySet())
			{
				Object v = fieldob.opt(fn) ;
				if(v==null)
					continue ;
				
				if(v instanceof Number)
					point.addField(fn,(Number)v) ;
				else if(v instanceof String)
					point.addField(fn,(String)v) ;
				else if(v instanceof Boolean)
					point.addField(fn,(Boolean)v) ;
				else // if(v==null)
					point.addField(fn, (Number)null) ;
			}
		}
		
		return point ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Point pt = calPointFmt(msg);
		if(pt==null)
			return null;
		
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		InfluxDBClient client = dbm.RT_getClient() ;
		WriteApiBlocking wapi = client.getWriteApiBlocking() ;
		wapi.writePoint(pt);
		return null;
	}
	
	private static String inTitle = "<pre>"
			+ "{\r\n" + 
			"   \"measurement\":\"m1\",\r\n" + 
			"   \"ts\":1213423423,\r\n" + 
			"   \"tags\":[\r\n" + 
			"        {\"n\":\"tag1\",\"v\":\"ttt1\"},\r\n" + 
			"        {\"n\":\"tag2\",\"v\":\"ttt2\"}\r\n" + 
			"    ],\r\n" + 
			"   \"tagob\":{\r\n" +
			"		\"tag1\":\"ttt1\",\"tag2\":\"ttt2\"" +
			"    },\r\n" + 
			"   \"fields\":[\r\n" + 
			"         {\"n\":\"f1\",\"v\",true},\r\n" + 
			"         {\"n\":\"ff2.xx\",\"v\":3.14},\r\n" + 
			"         {\"n\":\"ff2.yy\",valid:false}\r\n" + 
			"    ],\r\n" +
			"   \"fieldob\":{\r\n" +
			"		\"f1\":\"ffff1\",\"f2\":3.14" +
			"    },\r\n" + 
			"}"
			+ "</pre>" ; 

	@Override
	public String RT_getInTitle()
	{
		return inTitle ;
	}
}
