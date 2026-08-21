package org.iottree.core.msgnet.store.influxdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.msgnet.modules.RelationalDB_Table;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

public class InfluxDB_TagAggr2RDB extends MNNodeMid
{
	private static ILogger log = LoggerManager.getLogger(InfluxDB_TagAggr2RDB.class) ;
			
	//private static Lan lan = Lan.getLangInPk(InfluxDB_TagAggr2RDB.class) ;
	
	public static enum AggrTP
	{
		mean("mean","均值"),
		max("maximum","最大值"),
		min("minimum","最小值"),
		max_sub_min("max-min","差值"),
		r_e_c("rising edge count","上升沿计数"),
		f_e_c("falling edge count","下降沿计数"),
		rf_e_c("Rising and falling edge count","上升下降沿计数")
		;
		
		final String title_cn ;
		final String title_en ;
		
		AggrTP(String tt_en,String tt_cn)
		{
			this.title_en = tt_en ;
			this.title_cn = tt_cn ;
		}
		
		public String getTitle()
		{
			if("cn".equals(Lan.getUsingLang()))
				return this.title_cn;
			return this.title_en;
		}
		
		public static String toSelectOpts()
		{
			StringBuilder sb = new StringBuilder() ;
			for(AggrTP atp:AggrTP.values())
			{
				sb.append("<option value=\"").append(atp.name()).append("\">").append(atp.getTitle()).append("</option>\r\n");
			}
			return sb.toString();
		}
		
		public static JSONArray toNameTitleJarr()
		{
			JSONArray jarr = new JSONArray() ;
			for(AggrTP atp:AggrTP.values())
			{
				jarr.put(new JSONObject().put("n",atp.name()).put("t",atp.getTitle())) ;
			}
			return jarr ;
		}
	}
	
	public static enum AggrDT
	{
		every_day(0,3600000*24),
		every_hour(1,3600000),
		every_min(2,60000);
		
		private final int val ;
		private final long gap ;
		
		AggrDT(int v,long gp)
		{
			val = v ;
			gap = gp ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public static AggrDT valOfInt(int i)
		{
			switch(i)
			{
			case 1:
				return every_hour ;
			case 2:
				return every_min;
			default:
				return every_day ;
			}
		}
		
		public String getTitle()
		{
			//return lan.g(this.name()) ;
			switch(val)
			{
			case 1:
				return "Every Hour";
			case 2:
				return "Every Minute";
			default:
				return "Every Day";
			}
		}
		
		public long getGapMS()
		{
			return gap;
		}
	}
	
	public static UAVal.ValTP[] VAL_TPS_ALL = new UAVal.ValTP[] {
			UAVal.ValTP.vt_int32,UAVal.ValTP.vt_int64,UAVal.ValTP.vt_int16,
			UAVal.ValTP.vt_float,UAVal.ValTP.vt_double} ;//,UAVal.ValTP.vt_date};
	
	public static JSONArray toValTpsAllJArr()
	{
		JSONArray jarr = new JSONArray() ;
		for(UAVal.ValTP atp:VAL_TPS_ALL)
		{
			jarr.put(atp.getStr()) ;
		}
		return jarr ;
	}
	
	public static String toValTpsAllSelectOpts(ValTP selvt)
	{
		StringBuilder sb = new StringBuilder() ;
		for(UAVal.ValTP atp:VAL_TPS_ALL)
		{
			String seled = "" ;
			if(selvt==atp)
				seled = "selected" ;
			sb.append("<option "+seled+" value=\"").append(atp.getStr()).append("\">").append(atp.getStr()).append("</option>\r\n");
		}
		return sb.toString();
	}
	
	public static class AggrTag
	{
		InfluxDB_TagAggr2RDB owner ;
		
		public String tagPath  = null ;
		
		public String dbCol = null ;
		
		public AggrTP aggrTP = AggrTP.mean ;
		
		public UAVal.ValTP valTP = UAVal.ValTP.vt_int32 ;
		
		public AggrTag(InfluxDB_TagAggr2RDB owner)
		{
			this.owner = owner ;
		}
		
		public String getTagPath()
		{
			return this.tagPath ;
		}
		
		public UATag getTag()
		{
			if(Convert.isNullOrEmpty(this.tagPath))
				return null ;
			UAPrj prj = owner.getPrj() ;
			return prj.getTagByPath(this.tagPath) ;
		}

		public JSONObject toJO()
		{
			return new JSONObject().put("tagp",this.tagPath).put("dbc", this.dbCol)
					.put("aggr_tp", aggrTP.name()).putOpt("val_tp", this.valTP.getStr()) ;
		}
		
		public static AggrTag fromJO(InfluxDB_TagAggr2RDB owner,JSONObject jo)
		{
			AggrTag ret = new AggrTag(owner) ;
			
			ret.tagPath = jo.optString("tagp") ;
			ret.dbCol = jo.optString("dbc") ;
			ret.aggrTP = AggrTP.valueOf(jo.optString("aggr_tp","mean")) ;
			ret.valTP = UAVal.getValTp(jo.optString("val_tp","int32")) ;
			if(ret.valTP==null)
				ret.valTP = ValTP.vt_int32 ;
			return ret ;
		}
	}
	
	String measurement =null ;
	
	AggrDT aggrDT = AggrDT.every_day ;
	
	LinkedHashMap<String,AggrTag> dbc2ats = new LinkedHashMap<>() ;
	
	@Override
	public String getTP()
	{
		return "influxdb_tagaggr2rdb";
	}

	@Override
	public String getTPTitle()
	{
		return "Tag Aggregation To RDB";
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
	public int getOutNum()
	{
		return 2;
	}
	
	public LinkedHashMap<String,AggrTag> getDBCol2AggrTagMap()
	{
		return dbc2ats;
	}

	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
	static
	{
		OUT2RES.put(1,new OutResDef(RelationalDB_Table.class,true)) ;
	}
	
	@Override
	public Map<Integer,OutResDef> getOut2Res()
	{
		return OUT2RES ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.measurement))
		{
			failedr.append("no measurement set") ;
			return false;
		}
		if(dbc2ats==null||dbc2ats.size()<=0)
		{
			failedr.append("no db column - tag set") ;
			return false;
		}
		
		return true;
	}
	
	

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("m", this.measurement) ;
		//jo.put("aggr_tp", this.aggrDT)
		jo.put("aggr_dt", this.aggrDT.val) ;
		
		JSONArray jarr = new JSONArray() ;
		for(AggrTag at:this.dbc2ats.values())
		{
			jarr.put(at.toJO()) ;
		}
		jo.put("aggr_tags", jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("m") ;
		this.aggrDT = AggrDT.valOfInt(jo.optInt("aggr_dt",0)) ;
		
		JSONArray jarr = jo.optJSONArray("aggr_tags");
		if(jarr!=null)
		{
			int n =  jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				AggrTag at = AggrTag.fromJO(this,tmpjo) ;
				if(at==null)
					continue ;
				this.dbc2ats.put(at.dbCol,at) ;
			}
		}
		
		clearCache();
	}
	
	
	
	public AggrDT getAggrDT()
	{
		return this.aggrDT ;
	}
	
	public String getMeasurement()
	{
		return this.measurement ;
	}
	
	
	private static class QueResult
	{
		Date st;
		Date et;
		
		HashMap<String,Object> dbc2val = new HashMap<>() ;
		
		public QueResult(Date st,Date et)
		{
			this.st = st ;
			this.et = et ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().put(COL_ST, this.st.getTime()).put("st", Convert.toFullYMDHMS(st))
					.put(COL_ET, this.et.getTime()).put("et", Convert.toFullYMDHMS(et));
			JSONObject tmpjo = new JSONObject();
			ret.put("col_val", tmpjo) ;
			for(Map.Entry<String,Object> sv2i:dbc2val.entrySet())
			{
				String dbc = sv2i.getKey() ;
				Object v = sv2i.getValue() +"";
				tmpjo.putOpt(dbc,v) ;
			}
			return ret ;
		}
	}
	
	private InfluxDBClient getInfluxDBClient()
	{
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		return dbm.RT_getClient() ;
	}
	
	public Object queryMaxMeanMin(String tag_path, Date start_dt, Date end_dt,AggrTP aggr_tp) //,Map<String,Integer> tag2dec_ptn)
	{
		List<String> tag_subps = Convert.splitStrWith(tag_path, "/.");
		String tagp =  Convert.combineStrWith(tag_subps, '.');
		
		InfluxDBClient client = getInfluxDBClient() ;
		if(client==null)
			return null ;
		
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		//return queryMaxMeanMinIntv(tag_subps, start_dt, end_dt, aggrn, au,aggr_tp,tag2dec_ptn);
		String tb = this.measurement;
		if (end_dt==null)
			end_dt = new Date();
		String sdtstr = Convert.toUTCFormat(start_dt);
		String edtstr = Convert.toUTCFormat(end_dt);
		
		String flux_vars = "bkt = \"" +dbm.getBucket() + "\"\r\n" + "st = " + sdtstr + "\r\n" + "et = " + edtstr
				+ "\r\n" + "m = \"" + tb + "\" \r\n" + "f = \"" + tagp + "\"\r\n";
				

		QueryApi qapi = client.getQueryApi();

		String flux = flux_vars + MMM;
		List<FluxTable> fts = qapi.query(flux);
		// String rowcsv = qapi.queryRaw(flux) ;
		//JSONArray jarr = new JSONArray();
		if (fts.size() > 0)
		{
			for (FluxRecord rec : fts.get(0).getRecords())
			{
				//Instant inst = rec.getTime();
				//Date dt = Date.from(inst);
				Object max = rec.getValueByKey("max");
				Object min = rec.getValueByKey("min");
				Object mean = rec.getValueByKey("mean");
				Number count = (Number)rec.getValueByKey("count");
				if(count.intValue()==0)
					return null ;
				Object vv = null ;

				switch(aggr_tp)
				{
				case max:
					vv = max;break;
				case min:
					vv = min ;break;
				case mean:
					vv = mean ;break;
				case max_sub_min:
					if(min instanceof Number && max instanceof Number)
					{
						double max_v = ((Number)max).doubleValue() ;
						double min_v = ((Number)min).doubleValue() ;
						vv = max_v-min_v;break;
					}
				default:
					return null ;
				}
				
//				if(vv!=null&& vv instanceof Number && tag2dec_ptn!=null)
//				{//
//					Integer dec_ptn = tag2dec_ptn.get(tagp) ;
//					if(dec_ptn!=null && dec_ptn>0)
//					{
//						double d = Convert.formatDouble(((Number)vv).doubleValue(),dec_ptn) ;
//						vv = d ;
//					}
//				}
				
				//if(vv!=null)
				//	jo.put("v", vv) ;
				//jarr.put(jo);
				return vv;
			}
		}
		//return jarr;
		return null ;
	}
	
	/**
	 * 查询某个tag在一个时间段内的变化次数
	 * @param tagpath
	 * @param from_dt
	 * @param to_dt
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public Integer queryTagChgNum(String tagpath,Date from_dt,Date to_dt,AggrTP mode,StringBuilder failedr) //throws Exception
	{
		UATag tag = this.getPrj().getTagByPath(tagpath) ;
		if(tag==null)
		{
			failedr.append("no tag found with path="+tagpath) ;
			return null ;
		}
		ValTP vtp = tag.getValTp() ;
		if(vtp==null)
		{
			failedr.append("unknown tag value tp") ;
			return null ;
		}
		
		String qstr = null ;
		if(vtp==ValTP.vt_bool)
		{
			switch(mode)
			{
			case r_e_c:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", ">") ;
				break;
			case f_e_c:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", "<") ;
				break;
			case rf_e_c:
			default:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", "!=") ;
				break ;
			}
		}
		else if(vtp.isNumberVT())
		{
			switch(mode)
			{
			case r_e_c:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", ">") ;
				break;
			case f_e_c:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", "<") ;
				break;
			case rf_e_c:
			default:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", "!=") ;
				break ;
			}
		}
		else
		{
			failedr.append("not support tag value tp="+vtp) ;
			return null ;
		}
		
		String tb = this.measurement;

		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		if (dbm == null)
			return null;

		InfluxDBClient client = getInfluxDBClient() ;
		if(client==null)
			return null ;

		String sdtstr = Convert.toUTCFormat(from_dt);
		String edtstr = Convert.toUTCFormat(to_dt);
		
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "m = \"" + tb + "\" \r\n" + "st = " + sdtstr
				+ "\r\net="+edtstr + "\r\nf = \"" + tagpath + "\"\r\n";
		String flux = flux_vars + qstr ;
		QueryApi qapi = client.getQueryApi();
		try
		{
			List<FluxTable> fts = qapi.query(flux);
			if (fts.size() <= 0)
				return null;
			List<FluxRecord> frs = fts.get(0).getRecords();
			if (frs.size() <= 0)
				return null;
			FluxRecord rec = frs.get(0);
			// Instant inst = rec.getTime() ;
			// Date dt = Date.from(inst) ;
			Number obj = (Number)rec.getValue() ;
			// Long val = (Long) rec.getValueByKey("duration");
			return obj.intValue() ;
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
			{
				log.debug("run flux err:"+ee.getMessage());
				log.debug(flux);
			}
			throw ee ;
		}
	}
	
	private QueResult queryStEt(Date start_dt,Date end_dt)
	{
		QueResult ret = new QueResult(start_dt,end_dt) ;
		
		for(AggrTag at:this.dbc2ats.values())
		{
			Object v = null;
			switch(at.aggrTP)
			{
			case max:
			case min:
			case mean:
			case max_sub_min:
				v = queryMaxMeanMin(at.tagPath,start_dt,end_dt,at.aggrTP) ;
			case r_e_c:
			case f_e_c:
			case rf_e_c:
				StringBuilder failedr = new StringBuilder() ;
				v = queryTagChgNum(at.tagPath,start_dt,end_dt,at.aggrTP,failedr) ;
			}
			
			if(v==null || !(v instanceof Number))
				continue ;
			Number nv = (Number)v ;
			switch(at.valTP)
			{
			case vt_int32:
				v = nv.intValue() ;
				break ;
			case vt_int64:
				v = nv.longValue() ;
				break ;
			case vt_int16:
				v = nv.shortValue() ;
				break ;
			case vt_float:
				v = nv.floatValue() ;
				break ;
			case vt_double:
				v = nv.doubleValue() ;
				break ;
			default:
				continue ;
			}
			ret.dbc2val.put(at.dbCol,v) ;
		}
		if(ret.dbc2val.size()<=0)
			return null ;
		return ret ;
	}
	
	private static class DurItem
	{
		Date start_dt ;
		Date end_dt ;
		
		public DurItem(Date sdt,Date edt)
		{
			this.start_dt = sdt ;
			this.end_dt = edt ;
		}
	}
	
	private DurItem calDurItemAt(long dt)
	{
		Date at = new Date(dt) ;
		switch(this.aggrDT)
		{
		case every_day:
			Date s = Convert.calDayStart(at) ;
			Date e = Convert.calDayEnd(at) ;
			return new DurItem(s,e) ;
		case every_hour:
			s = Convert.calHourStart(at) ;
			e = Convert.calHourEnd(at) ;
			return new DurItem(s,e) ;
		case every_min:
			s = Convert.calMinuteStart(at) ;
			e = Convert.calMinuteEnd(at) ;
			return new DurItem(s,e) ;
		default: //every day
			return null ;
		}
	}
	
	public List<DurItem> calDurItemsInPeriod(long st,long et)
	{
		ArrayList<DurItem> dis = new ArrayList<>() ;
		do
		{
			DurItem di = calDurItemAt(st) ;
			dis.add(di) ;
			st += this.aggrDT.gap ;
		}while(st<=et) ;
		return dis ;
	}
	
private transient DurItem lastDur = null ; 
	
	private QueResult queAndUpRDB(DurItem di) throws Exception
	{
		QueResult ret = queryStEt(di.start_dt,di.end_dt) ;
		if(ret==null)
			return null;
		//JSONObject ret_jo = ret.toJO() ;
		StringBuilder failedr = new StringBuilder() ;
		if(!this.RT_addOrUpdate(ret, failedr))
			throw new Exception(failedr.toString()) ;
		return ret;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		DurItem di = calDurItemAt(System.currentTimeMillis()) ;
		if(this.lastDur!=null && !di.equals(lastDur))
		{
			queAndUpRDB(this.lastDur) ; //end of last dur
		}
		QueResult ret = queAndUpRDB(di) ;
		if(ret==null)
			return null ;
		lastDur = di ;
		JSONObject ret_jo = ret.toJO() ;
		return RTOut.createOutIdx().asIdxMsg(0,new MNMsg().asPayload(ret_jo));
	}

	static final String VAL_CHG_COUNT_NUM = "from(bucket:bkt)\r\n" + 
			"  |> range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> difference()\r\n" + 
			"  |> filter(fn: (r) => r._value {op} 0)\r\n" + 
			"  |> count()";
	
	
	private static final String VAL_CHG_COUNT_BOOL = "from(bucket:bkt)\r\n" + 
			"  |> range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> map(fn: (r) => ({ r with _value_num: if r._value then 1 else 0}))\r\n" + 
			"  |> difference(columns: [\"_value_num\"])\r\n" + 
			"  |> filter(fn: (r) => r._value_num {op} 0)\r\n" + 
			"  |> count()";

	static final String MMM = "from(bucket: bkt)\r\n" + 
			"  |> range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) => r[\"_measurement\"] == m \r\n" + 
			"                   and r[\"_field\"] == f)\r\n" + 
			"  \r\n" + 
			"  // trans _value to float and using reduce calc max, min, sum, count\r\n" + 
			"  |> toFloat()\r\n"+
			"  |> reduce(\r\n" + 
			"      identity: {max: float(v: \"-Inf\"), min: float(v: \"+Inf\"), sum: 0.0, count: 0.0},\r\n" + 
			"      fn: (r, accumulator) => ({\r\n" + 
			"          max: if r._value > accumulator.max then r._value else accumulator.max,\r\n" + 
			"          min: if r._value < accumulator.min then r._value else accumulator.min,\r\n" + 
			"          sum: accumulator.sum + r._value,\r\n" + 
			"          count: accumulator.count + 1.0\r\n" + 
			"      })\r\n" + 
			"  )\r\n" + 
			"  // calc mean\r\n" + 
			"  |> map(fn: (r) => ({\r\n" + 
			"      r with\r\n" + 
			"      mean: if r.count > 0.0 then r.sum / r.count else 0.0\r\n" + 
			"  }))\r\n" + 
			"  \r\n" + 
			"  //format out\r\n" + 
			"  |> keep(columns: [\"_field\", \"max\", \"min\", \"mean\",\"count\"])";
	
	public RelationalDB_Table getUsingRDBTable()
	{
		MNNodeRes noderes = this.getOutResNode(1) ;
		if(noderes==null || !(noderes instanceof RelationalDB_Table))
			return null;
		return (RelationalDB_Table)noderes ;
	}

	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;

		divsb.append("<div class=\"rt_blk\" style='position:relative;'><button onclick=\"mn_fire_node_evt('"+this.getId()+"','create_tb')\">Create Table</button>") ;
		divsb.append("<button onclick=\"mn_open_node_dlg('"+this.getId()+"','syn_old_data','Synchronize Old Data')\">Synchronize Old Data</button>") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("influxdb_tagaggr2rdb",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
	
	@Override
	public void RT_onRenderDivEvent(String evtn,JSONObject evt_pm,StringBuilder retmsg)
	{
		super.RT_onRenderDivEvent(evtn,evt_pm,retmsg);
		try
		{
			switch(evtn)
			{
			case "create_tb":
				JavaTableInfo jti = getTableInfo(retmsg) ;
				DBConnPool cp = RT_getConnPool(retmsg) ;
				if(jti==null || cp==null)
				{
					//retmsg.append("no Table or ConnPool found,you may not set db resource node") ;
					return ;
				}
				dataTable = DBUtil.createOrUpTable(cp,jti,true) ;
				if(dataTable!=null)
					retmsg.append("create table ok") ;
				else
					retmsg.append("create table failed") ;
				return ;
			case "syn_in_period":
				if(evt_pm==null)
				{
					retmsg.append("no event pm input") ;
					return ;
				}
				long st = evt_pm.optLong("st",-1) ;
				long et = evt_pm.optLong("et",-1) ;
				if(st<=0||et<=0)
				{
					retmsg.append("event pm has no st et int64 ms input") ;
					return ;
				}
				int cc = this.synAllDurInPeriod(st, et) ;
				retmsg.append("syn succ item num="+cc) ;
				return ;
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			retmsg.append("Err:"+ee.getMessage()) ;
		}
	}
	
	
	// ----  db ---
	
	private JavaTableInfo tableInfo = null ;
	private DBConnPool connPool = null ;
	private DataTable dataTable = null ;
	
	protected synchronized void clearCache()
	{
		super.clearCache();
		
		tableInfo = null ;
		connPool = null ;
		dataTable = null ;
	}
	
	
	private synchronized DBConnPool RT_getConnPool(StringBuilder failedr)
	{
		if(connPool!=null)
			return connPool ;
		
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			failedr.append("no related using RelationalDB_Table") ;
			return null ;
		}
		connPool = tb.RT_getConnPool() ;
		return connPool ;
	}
	
	public static final String COL_ST = "_st" ;
	public static final String COL_ET = "_et" ;
	
	public JavaTableInfo getTableInfo(StringBuilder failedr) //throws Exception
	{
		if(tableInfo!=null)
			return tableInfo;
		
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			failedr.append("no related RelationalDB_Table") ;
			return null ;
		}
		//UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		
		String tablename = tb.getTableName() ;
		if(Convert.isNullOrEmpty(tablename))
		{
			failedr.append("RelationalDB_Table has no table name") ;
			return null ;
		}
		
		if(!isParamReady(failedr))
			return null ;
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		
		pkcol = new JavaColumnInfo(COL_ST,true,XmlVal.XmlValType.vt_int64,-1,
				false, false,"", false,-1,"",false,false);
		
		norcols.add(new JavaColumnInfo(COL_ET,false,XmlVal.XmlValType.vt_int64,-1,
				false, false,"", false,-1,"",false,false));
		
		for(AggrTag sv:this.dbc2ats.values())
		{
			norcols.add(new JavaColumnInfo(sv.dbCol,false, sv.valTP.toXVT(),-1,
					false, false,null, false,-1, "",false,false));
		}
		
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, null);
		return tableInfo;
	}
	

	private DataTable RT_getDataTable(StringBuilder failedr) throws Exception
	{
		if(dataTable!=null)
			return dataTable ;
		
		JavaTableInfo jti = getTableInfo(failedr) ;
		if(jti==null)
			return null ;

		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			return null ;

		dataTable = DBUtil.createOrUpTable(cp,jti,true) ;
		if(dataTable==null)
			failedr.append("failed to create table") ;
		
		return dataTable ;
	}
	
	private DataRow transVal2Row(DataTable dt ,QueResult qr,StringBuilder failedr)
	{
		DataRow dr = dt.createNewRow() ;
		
		dr.putValue(COL_ST,qr.st.getTime()) ;
		dr.putValue(COL_ET,qr.et.getTime()) ;
		for(AggrTag sv:this.dbc2ats.values())
		{
			Object v = qr.dbc2val.get(sv.dbCol) ;
			dr.putValue(sv.dbCol,v) ;
		}
		
		return dr;
	}
	
	private synchronized boolean RT_addOrUpdate(QueResult qr,StringBuilder failedr) //,int keep_days,boolean b_outer)
		throws Exception
	{
		JavaTableInfo jti = getTableInfo(failedr) ;
		if(jti==null)
			return false;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return false;
		
		DataRow dr = transVal2Row(dt ,qr, failedr) ;
		if(dr==null)
			return false;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			int up_c = dr.doUpdateDB(conn, jti.getTableName(), COL_ST, jti.getNorColNames());
			if(up_c<=0)
				up_c = dr.doInsertDB(conn, jti.getTableName(), jti.getAllColNamesArr()) ;
			return up_c>0 ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	/**
	 * 
	 * @param startdt
	 * @param enddt
	 * @throws Exception 
	 */
	public int synAllDurInPeriod(long st,long et) throws Exception
	{
		int cc = 0 ;
		if(et>System.currentTimeMillis())
			et = System.currentTimeMillis();
		List<DurItem> dis = calDurItemsInPeriod(st,et);
		for(DurItem di:dis)
		{
			QueResult ret = queryStEt(di.start_dt,di.end_dt) ;
			if(ret==null)
				continue ;
			StringBuilder failedr = new StringBuilder() ;
			if(this.RT_addOrUpdate(ret, failedr))
				cc ++ ;
		}
		return cc;
	}
	
	@outer_api(name="aggr_items_cur_mon"
		,title_cn="读取当月标签聚合记录",title_en="Read the tag aggregation records for the current month"
		,desc_en="There are mean, maximum and minimum values, difference, rising edge, falling edge, and rising and falling edge counts"
		,desc_cn="有均值，最大最小值，差值，上升沿、下降沿和上升下降沿计数")
	public JSONArray statDurInCurrentMonth(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calMonthStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return readAggrItemInPeriod(inputjo,failedr) ;
	}
	
	@outer_api(name="aggr_items_cur_year"
		,title_cn="读取当年标签聚合记录",title_en="Read the tag aggregation records for the current year"
		,desc_en="There are mean, maximum and minimum values, difference, rising edge, falling edge, and rising and falling edge counts"
		,desc_cn="有均值，最大最小值，差值，上升沿、下降沿和上升下降沿计数")
	public JSONArray statDurInCurrentYear(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calYearStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return readAggrItemInPeriod(inputjo,failedr) ;
	}
	
	@outer_api(name="aggr_items"
			,title_cn="读取一个时间段标签聚合记录",title_en="Read a time period tags aggregation record"
			,desc_en="There are mean, maximum and minimum values, difference, rising edge, falling edge, and rising and falling edge counts"
			,desc_cn="有均值，最大最小值，差值，上升沿、下降沿和上升下降沿计数",
			in_sample = "{st:1767196800000l,et:1784627683879l}",
			out_sample = "")
	public JSONArray readAggrItemInPeriod(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
		{
			failedr.append("not input") ;
			return null ;
		}
		long st = inputjo.optLong("st",-1) ;
		long et = inputjo.optLong("et",-1) ;
		if(st<=0||et<=0)
		{
			failedr.append("not st or et (int64) input") ;
			return null ;
		}
		
		JavaTableInfo jti = getTableInfo(failedr) ;
		if(jti==null)
			return null;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		if(cp==null)
			return null ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return null;
		
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("select _st,_et") ;
		for(AggrTag sv:this.dbc2ats.values())
		{
			sqlsb.append(",").append(sv.dbCol) ;
			//norcols.add(new JavaColumnInfo(sv.dbCol,false, XmlVal.XmlValType.vt_int32,-1,
			//		false, false,null, false,-1, "",false,false));
		}
		sqlsb.append(" from ").append(jti.getTableName()).append(" where ")
			.append(COL_ST).append(">=? and ").append(COL_ST).append("<?") ;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			JSONArray ret = new JSONArray() ;
			try (PreparedStatement ps = conn.prepareStatement(sqlsb.toString()))
			{
				ps.setLong(1, st);
				ps.setLong(2, et);
				try(ResultSet rs=ps.executeQuery())
				{
					while(rs.next())
					{
						JSONObject tmpjo = new JSONObject() ;
						long _st = rs.getLong("_st") ;
						long _et = rs.getLong("_et") ;
						tmpjo.put("_st", _st) ;
						tmpjo.put("_et", _et) ;
						for(AggrTag sv:this.dbc2ats.values())
						{
							Object v = rs.getObject(sv.dbCol) ;
							tmpjo.putOpt(sv.dbCol, v) ;
						}
						ret.put(tmpjo) ;
					}
				}
			}
			return ret ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	@outer_api(name="sum_items_cur_mon"
			,title_cn="求和-当月聚合记录",title_en="Sum up - aggregate records in current month"
			)
	public JSONObject sumAggrItemCurMon(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calMonthStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return sumAggrItemInPeriod(inputjo,failedr);
	}
	
	@outer_api(name="sum_items_cur_year"
			,title_cn="求和-当年聚合记录",title_en="Sum up - aggregate records in current year"
			)
	public JSONObject sumAggrItemCurYear(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calYearStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return sumAggrItemInPeriod(inputjo,failedr);
	}
	
	@outer_api(name="sum_items"
			,title_cn="求和-一个时间段聚合记录",title_en="Sum up - aggregate records for a time period"
			)
	public JSONObject sumAggrItemInPeriod(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
		{
			failedr.append("not input") ;
			return null ;
		}
		long st = inputjo.optLong("st",-1) ;
		long et = inputjo.optLong("et",-1) ;
		if(st<=0||et<=0)
		{
			failedr.append("not st or et (int64) input") ;
			return null ;
		}
		HashMap<String,Number> col2num = sumAggrItemInPeriod(st,et,failedr);
		if(col2num==null)
			return null ;
		return new JSONObject(col2num) ;
	}
	
	public HashMap<String,Number> sumAggrItemInPeriod(long st,long et,StringBuilder failedr) throws Exception
	{
		JavaTableInfo jti = getTableInfo(failedr) ;
		if(jti==null)
			return null;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		if(cp==null)
			return null ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return null;
		
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("select ") ;
		//boolean bfirst = true ;
		sqlsb.append("min(_st) as _st,max(_et) as _et") ;
		for(AggrTag sv:this.dbc2ats.values())
		{
//			if(bfirst)
//				bfirst = false;
//			else
//				sqlsb.append(",") ;
			sqlsb.append(",sum("+sv.dbCol+") as "+sv.dbCol) ;
		}
		sqlsb.append(" from ").append(jti.getTableName()).append(" where ")
			.append(COL_ST).append(">=? and ").append(COL_ST).append("<?") ;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			HashMap<String,Number> ret = new HashMap<>() ;
			try (PreparedStatement ps = conn.prepareStatement(sqlsb.toString()))
			{
				ps.setLong(1, st);
				ps.setLong(2, et);
				try(ResultSet rs=ps.executeQuery())
				{
					if(rs.next())
					{
						long _st = rs.getLong("_st") ;
						long _et = rs.getLong("_et") ;
						ret.put("_st", _st) ;
						ret.put("_et", _et) ;
						for(AggrTag sv:this.dbc2ats.values())
						{
							Object v = rs.getObject(sv.dbCol) ;
							if(v==null)
								continue ;
							ret.put(sv.dbCol, (Number)v) ;
						}
					}
				}
			}
			return ret ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	@Override
	protected Object[] extOuterApiIOSample(String apin)
	{
		final JSONObject input = new JSONObject().put("st", 1767196800000l).put("et", 1784627683879l) ;
		JSONArray out_aggr_jo = new JSONArray() ;
		JSONObject tmpjo = new JSONObject() ;
		for(AggrTag sv:this.dbc2ats.values())
		{
			tmpjo.put("_st", 1767196800000l).put("_et", 1784627699999l);
			if(sv.valTP.isNumberFloat())
				tmpjo.put(sv.dbCol,3.14) ;
			else
				tmpjo.put(sv.dbCol,100) ;
		}
		out_aggr_jo.put(tmpjo) ;
		out_aggr_jo.put(tmpjo) ;
		
		JSONObject sum_out = new JSONObject().put("_st", 1767196800000l).put("_et", 1784627699999l);
		for(AggrTag sv:this.dbc2ats.values())
		{
			sum_out.put(sv.dbCol, 100) ;
		}
		
		switch(apin)
		{
		
		case "aggr_items_cur_year":
		case "aggr_items_cur_mon":
			return new Object[] {null,out_aggr_jo};
		case "aggr_items":
			return new Object[] {input,out_aggr_jo};
		case "sum_items_cur_year":
		case "sum_items_cur_mon":
			return new JSONObject[] {null,sum_out};
		case "sum_items":
			return new JSONObject[] {input,sum_out};
		}
		return null ;
	}
}
