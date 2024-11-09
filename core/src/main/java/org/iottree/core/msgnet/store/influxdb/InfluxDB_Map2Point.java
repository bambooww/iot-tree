package org.iottree.core.msgnet.store.influxdb;

import java.util.ArrayList;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class InfluxDB_Map2Point extends MNNodeMid
{
	static class TagDef
	{
		String name ;
		
		String value ;
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("n", this.name) ;
			jo.putOpt("v", this.value) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.name = jo.optString("n","") ;
			this.value = jo.optString("v","") ;
		}
		
		public boolean isValid()
		{
			return Convert.isNotNullEmpty(this.name) &&
					Convert.isNotNullEmpty(this.value) ;
		}
	}
	
	
	String measurement = null ;
	
	TagDef tag1 = null ;
	
	TagDef tag2 = null ;
	
	TagDef tag3 = null ;
	
	ArrayList<String> filterTags = new ArrayList<>() ;
	
	/**
	 * 如果某个标签的更新时间，超过此值，则认为对于的标签值无效
	 */
	long invalidTimeout = 3000 ;
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "influxdb_map2pt";
	}

	@Override
	public String getTPTitle()
	{
		return "Transfer Map To Points";
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
		if(Convert.isNullOrEmpty(measurement))
		{
			failedr.append("no measurement set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("measurement", this.measurement) ;
		if(tag1!=null)
			jo.put("tag1", tag1.toJO()) ;
		if(tag2!=null)
			jo.put("tag2", tag2.toJO()) ;
		if(tag3!=null)
			jo.put("tag3", tag3.toJO()) ;
		if(filterTags!=null)
			jo.put("filter_tags", this.filterTags) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("measurement","") ;
		JSONObject tmpjo = jo.optJSONObject("tag1") ;
		if(tmpjo!=null)
		{
			tag1 = new TagDef() ;
			tag1.fromJO(tmpjo);
		}
		tmpjo = jo.optJSONObject("tag2") ;
		if(tmpjo!=null)
		{
			tag2 = new TagDef() ;
			tag2.fromJO(tmpjo);
		}
		tmpjo = jo.optJSONObject("tag3") ;
		if(tmpjo!=null)
		{
			tag3 = new TagDef() ;
			tag3.fromJO(tmpjo);
		}
		
		JSONArray jarr = jo.optJSONArray("filter_tags") ;
		ArrayList<String> fts = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ; 
			for(int i = 0 ; i < n ; i ++)
			{
				String tmps = jarr.getString(i) ;
				fts.add(tmps) ;
			}
		}
		this.filterTags = fts ;
	}
	
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		JSONArray fjarr = new JSONArray() ;
		long ts = -1 ;
		
		JSONArray pld_jarr = msg.getPayloadJArr(null) ;
		JSONObject pld_jo = msg.getPayloadJO(null) ;
		if(pld_jarr!=null)
		{
			ts = transTagsFieldsJArr(pld_jarr,fjarr) ;
		}
		else if(pld_jo!=null)
		{
			ts = transTagsFieldsJO(pld_jo,fjarr) ;
		}

		if(ts<=0 || fjarr.length()<=0)
			return null; 
		
		JSONObject retjo = new JSONObject() ;
		retjo.put("measurement",this.measurement) ;
		JSONArray tags_jo = new JSONArray() ;
		if(tag1!=null && tag1.isValid())
		{
			tags_jo.put(tag1.toJO()) ;
		}
		if(tag2!=null && tag2.isValid())
		{
			tags_jo.put(tag2.toJO()) ;
		}
		if(tag3!=null && tag3.isValid())
		{
			tags_jo.put(tag3.toJO()) ;
		}
		retjo.put("tags", tags_jo) ;
		retjo.put("fields", fjarr) ;
		retjo.put("ts", ts) ;
		return RTOut.createOutAll(new MNMsg().asPayload(retjo)) ;
	}

	private long transTagsFieldsJO(JSONObject jo,JSONArray retjarr)
	{
		return -1 ;
	}
	
	private long transTagsFieldsJArr(JSONArray jarr,JSONArray retjarr)
	{
		if(jarr==null)
			return -1 ;
		
		int n = jarr.length() ;
		
		long ts = -1 ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject  tmpjo = jarr.getJSONObject(i) ;
			String tag = tmpjo.optString("tag") ;
			if(Convert.isNullOrEmpty(tag))
				continue ;
			if(filterTags.size()>0 && !this.filterTags.contains(tag))
				continue ;
			
			long updt = tmpjo.optLong("up_dt", -1) ;
			if(updt<=0)
				continue ;
			
			if(updt>ts)
				ts = updt ;
			
			JSONObject fjo = new JSONObject() ;
			fjo.put("n", tag) ;
			boolean bvalid = tmpjo.optBoolean("valid",false) ;
			Object ov = tmpjo.opt("v") ;
			if(!bvalid)
			{
				fjo.put("valid", false) ;
			}
			else
			{
				fjo.put("v", ov) ;
			}
			retjarr.put(fjo) ;
		}
		return ts ;
	}
}
