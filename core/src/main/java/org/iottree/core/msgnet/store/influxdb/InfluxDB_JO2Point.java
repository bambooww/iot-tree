package org.iottree.core.msgnet.store.influxdb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class InfluxDB_JO2Point  extends MNNodeMid
{
	public static enum ValTp
	{
		vt_int(1), vt_float(2),string(3),bool(4) ;

		private final int val;

		ValTp(int v)
		{
			val = v;
		}

		public int getValue()
		{
			return val;
		}

		public Object transStrValToObj(String strv)
		{
			switch(val)
			{
			case 1:
				if(Convert.isNullOrEmpty(strv))
					return null ;
				return Long.parseLong(strv) ;
			case 2:
				if(Convert.isNullOrEmpty(strv))
					return null ;
				return Double.parseDouble(strv) ;
			case 4:
				if(Convert.isNullOrEmpty(strv))
					return null ;
				return "true".equalsIgnoreCase(strv) ;
			default:
				return strv ;
			}
		}
		
		public Object tranObjVal(Object objv)
		{
			if(objv==null)
				return objv ;
			switch(val)
			{
			case 1: //number
				if(objv instanceof Number)
					return ((Number) objv).longValue() ;
				if(objv instanceof Boolean)
					return ((Boolean)objv)?1:0 ;
				String strv = objv.toString() ;
				//int k = strv.indexOf('.');
				return Long.parseLong(strv) ;
			case 2:
				if(objv instanceof Number)
					return ((Number) objv).doubleValue() ;
				//if(objv instanceof Boolean)
				//	return ((Boolean)objv)?1.0:0.0 ;
				strv = objv.toString() ;
				//k = strv.indexOf('.');
				//if(k>=0)
				return Double.parseDouble(strv) ;
			case 4: //bool
				if(objv instanceof Boolean)
					return objv ;
				if(objv instanceof Number)
					return ((Number)objv).intValue()>0 ;
					
				return "true".equalsIgnoreCase(objv.toString()) ;
			default:
				return objv.toString() ;
			}
		}
		
		public static ValTp valueOfInt(int i)
		{
			switch (i)
			{
			case 1:
				return ValTp.vt_int;
			case 2:
				return ValTp.vt_float;
			case 4:
				return ValTp.bool;
			default:
				return ValTp.string;
			}
		}
	}

	//static ILogger log = LoggerManager.getLogger(InfluxDB_JO2Point)
	public static class TagDef
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
	
	public static class FieldDef
	{
		boolean chked = false;
		/**
		 * member in JSONObject key name
		 */
		String joMembName ;
		
		/**
		 * rename to influxdb field name,null will be same as joMember
		 */
		String fieldName = null ;
		
		boolean bNotNull = false ;
		
		ValTp valTP = ValTp.string ;
		
		/**
		 * true will be not save
		 * @return
		 */
		public boolean isDefVal()
		{
			if(chked==true) return false;
			if(Convert.isNotNullEmpty(fieldName)) return false;
			if(bNotNull!=false) return false;
			if(valTP!=ValTp.string) return false;
			return true ;
		}
		
		private String getUsingFieldName()
		{
			if(Convert.isNotNullEmpty(fieldName))
				return fieldName ;
			return this.joMembName ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("chked", this.chked) ;
			jo.put("n", this.joMembName) ;
			jo.putOpt("fn", this.fieldName) ;
			jo.put("not_null", false) ;
			jo.put("vtp", valTP.getValue()) ;
			return jo ;
		}
		
		public static FieldDef fromJO(JSONObject jo)
		{
			String n = jo.optString("n") ;
			if(Convert.isNullOrEmpty(n))
				return null ;
			FieldDef fd = new FieldDef() ;
			fd.chked = jo.optBoolean("chked",false) ;
			fd.joMembName = n ;
			fd.fieldName = jo.optString("fn") ;
			fd.bNotNull = jo.optBoolean("not_null", false) ;
			int vtpv = jo.optInt("vtp", -1) ;
			fd.valTP = ValTp.valueOfInt(vtpv) ;
			return fd ;
		}
	}
	
	String measurement = null ;
	
	/**
	 * timestamp jo member
	 */
	String tsMemN = null ;
	
	/**
	 * timestamp string format
	 */
	String tsFmt = null ;
	
	
	TagDef tag1 = null ;
	
	TagDef tag2 = null ;
	
	TagDef tag3 = null ;
	
	ArrayList<FieldDef> fields = new ArrayList<>() ;
	
	/**
	 * 支持转换之后FieldName的取值映射使用TagList，因此需要储存一个分类TagListCat
	 */
	String tagListCat = null ;
	
	/**
	 * 如果某个标签的更新时间，超过此值，则认为对于的标签值无效
	 */
	long invalidTimeout = 3000 ;
	
	private transient JSONObject _lastMsgJO = null ; 
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "influxdb_jo2pt";
	}

	@Override
	public String getTPTitle()
	{
		return "JSON To Points";
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
		if(Convert.isNullOrEmpty(tsMemN))
		{
			failedr.append("no timestamp member set") ;
			return false;
		}
		if(fields==null||fields.size()<=0)
		{
			failedr.append("no fields set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("measurement", this.measurement) ;
		jo.putOpt("ts_n", this.tsMemN) ;
		jo.putOpt("ts_fmt", this.tsFmt) ;
		if(tag1!=null)
			jo.put("tag1", tag1.toJO()) ;
		if(tag2!=null)
			jo.put("tag2", tag2.toJO()) ;
		if(tag3!=null)
			jo.put("tag3", tag3.toJO()) ;
		if(fields!=null)
		{
			JSONArray jarr = new JSONArray() ;
			for(FieldDef fd:this.fields)
			{
				jarr.put(fd.toJO()) ;
			}
			jo.put("fields", jarr) ;
		}
		jo.putOpt("taglist_cat", tagListCat) ;
		jo.putOpt("last_msg_jo", _lastMsgJO) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("measurement","") ;
		this.tsMemN = jo.optString("ts_n") ;
		this.tsFmt = jo.optString("ts_fmt") ;
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
		
		JSONArray jarr = jo.optJSONArray("fields") ;
		ArrayList<FieldDef> fts = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ; 
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject fjo = jarr.getJSONObject(i) ;
				FieldDef fd = FieldDef.fromJO(fjo) ;
				if(fd==null || fd.isDefVal())
					continue ;
				fts.add(fd) ;
			}
		}
		this.fields = fts ;
		
		this.tagListCat = jo.optString("taglist_cat") ;
		_lastMsgJO = jo.optJSONObject("last_msg_jo") ;
	}
	
	
	
	public JSONObject RT_getLastMsgJO()
	{
		return _lastMsgJO ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		JSONArray pld_jarr = msg.getPayloadJArr(null) ;
		JSONObject pld_jo = msg.getPayloadJO(null) ;
		if(pld_jarr!=null)
		{
			int n = pld_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = pld_jarr.getJSONObject(i) ;
				if(tmpjo!=null)
					_lastMsgJO = tmpjo ;
				
				StringBuilder failedr = new StringBuilder() ;
				JSONObject retjo = transJO(tmpjo,failedr) ;
				if(retjo!=null)
				{
					RTOut rto = RTOut.createOutAll(new MNMsg().asPayload(retjo)) ;
					if(rto==null)
						continue ;
					
					this.RT_sendMsgOut(rto);
				}
				else
				{
					throw new Exception(failedr.toString()) ;
				}
			}
		}
		else if(pld_jo!=null)
		{
			_lastMsgJO = pld_jo ;
			StringBuilder failedr = new StringBuilder() ;
			JSONObject retjo = transJO(pld_jo,failedr) ;
			if(retjo!=null)
			{
				return RTOut.createOutAll(new MNMsg().asPayload(retjo)) ;
			}
			else
			{
				throw new Exception(failedr.toString()) ;
			}
		}
		
		return null ;
	}

	private JSONObject transJO(JSONObject jo,StringBuilder failedr) throws Exception
	{
		JSONObject retjo = new JSONObject() ;
		Long ts = getOrCalTS(jo,failedr) ;
		if(ts==null)
			return null ;
		retjo.put("measurement",this.measurement) ;
		JSONObject tag_jo = new JSONObject() ;
		if(tag1!=null && tag1.isValid())
		{
			tag_jo.put(tag1.name,tag1.value) ;
		}
		if(tag2!=null && tag2.isValid())
		{
			tag_jo.put(tag2.name,tag2.value) ;
		}
		if(tag3!=null && tag3.isValid())
		{
			tag_jo.put(tag3.name,tag3.value) ;
		}
		
		JSONObject fob = new JSONObject() ;
		for(FieldDef df:this.fields)
		{
			if(!df.chked)
				continue ;
			
			Object obv = jo.opt(df.joMembName) ;
			if(obv==null)
			{
				if(df.bNotNull)
				{
					failedr.append(df.joMembName+" in jo cannot be null") ;
					return null ;
				}
				continue ;
			}
			obv = df.valTP.tranObjVal(obv) ;
			fob.put(df.getUsingFieldName(),obv) ;
		}
		retjo.put("tagob", tag_jo) ;
		retjo.put("fieldob", fob) ;
		retjo.put("ts", ts) ;
		return retjo ;
	}
	
	private Pattern intPat = Pattern.compile("^[0-9]*[1-9][0-9]*$") ;
	
	private Long getOrCalTS(JSONObject jo,StringBuilder failedr) throws Exception
	{
		Object tsob = jo.opt(this.tsMemN) ;
		if(tsob==null)
		{
			failedr.append("no "+this.tsMemN+" in jo") ;
			return null ;
		}
		if(tsob instanceof Long)
			return (Long)tsob ;
		
		String tsstr = tsob.toString() ;
		if(intPat.matcher(tsstr).matches())
		{
			return Long.parseLong(tsstr) ;
		}
		
		if(Convert.isNullOrEmpty(this.tsFmt))
		{
			failedr.append("no timestamp format set") ;
			return null ;
		}
		
			SimpleDateFormat sdf = new SimpleDateFormat(this.tsFmt);
			Date dt = sdf.parse(tsstr);
		return dt.getTime() ;
	}
}
