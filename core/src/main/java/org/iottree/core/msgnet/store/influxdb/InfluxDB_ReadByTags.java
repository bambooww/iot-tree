package org.iottree.core.msgnet.store.influxdb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.*;
import org.iottree.core.util.logger.*;
import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

/**
 * read tags history data with json output
 *  
 * @author jason.zhu
 *
 */
public class InfluxDB_ReadByTags extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(InfluxDB_ReadByTags.class);
	
	public static enum AggrTP
	{
		mean("均值"),
		max("最大值"),
		min("最小值"),
		max_sub_min("差值");
		
		final String title ;
		
		AggrTP(String tt)
		{
			this.title = tt ;
		}
		
		public String getTitle()
		{
			return this.title;
		}
		
		public static String toSelectOpts()
		{
			return "<option value=\"mean\">均值</option>\r\n" + 
					"			 	<option value=\"max\">最大值</option>\r\n" + 
					"			 	<option value=\"min\">最小值</option>\r\n" + 
					"			 	<option value=\"max_sub_min\">差值</option>" ;
		}
	}
	
	public static enum AggrUnit
	{
		//ns("纳秒"), // 1 nanosecond
		//us("微秒"), // 1 microsecond
		//ms("毫秒"), // 1 millisecond
		s("秒",1000),  // 1 second
		m("分钟",60000),  // 1 minute
		h("小时",3600000),  // 1 hour
		d("天",86400000),  // 1 day
		w("周",604800000);  // 1 week;
		//mo("月",-1), // 1 calendar month
		//y("年",-1) ; // 1 calendar year
		
		final String title ;
		final long gapMS;
		
		AggrUnit(String tt,long ms)
		{
			this.title = tt ;
			this.gapMS = ms ;
		}
		
		public String getTitle()
		{
			return this.title;
		}
		//public long calc
		public static int parseStr()
		{
			return 0 ;
		}
		
		public long getGapMS()
		{
			return this.gapMS ;
		}
		
		
		//public static 
	}
	
	public static class TagItem
	{
		String tagPath ;
		
		String varName ;
		
		private UATag tag = null ;
		
		public TagItem(String tagpath,String var_n,boolean b_must_ok)
		{
			this.tagPath = tagpath ;
			this.varName = var_n ;
		}
		
		private TagItem()
		{}
		
		public String getVarName()
		{
			if(Convert.isNullOrEmpty(this.varName))
				return tagPath ;
			return varName ;
		}
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.tagPath))
			{
				failedr.append("tag path cannot be null or empty") ;
				return false;
			}
			
			if(tag==null)
			{
				failedr.append("not tag with path ="+this.tagPath) ;
				return false;
			}
			return true ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("tag", this.tagPath) ;
			jo.putOpt("varn", varName) ;
			return jo ;
		}
		
		public static TagItem fromJO(JSONObject jo)
		{
			TagItem ret = new TagItem() ;
			ret.tagPath = jo.optString("tag") ;
			ret.varName = jo.optString("varn") ;
			return ret;
		}
	}
	
	
	ArrayList<TagItem> tagItems = new ArrayList<>() ;
	
	/**
	 * when no start_dt or end_dt param input
	 * it will used- Default query time period
	 */
	long defQTP = 300000 ;
	
	String measurement = null ;
	
	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "influxdb_readbytags";
	}

	@Override
	public String getTPTitle()
	{
		return "Read By Tags";
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
	
	public long getDefaultQTP()
	{
		return this.defQTP ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.measurement))
		{
			failedr.append("no measurement found") ;
			return false;
		}
		if(tagItems==null||tagItems.size()<=0)
		{
			failedr.append("no tag set");
			return false ;
		}
		
		for(TagItem ti:this.tagItems)
		{
			if(!ti.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("m", this.measurement) ;
		jo.put("def_qtp", this.defQTP) ;
		JSONArray jarr = new JSONArray() ;
		if(tagItems!=null)
		{
			for(TagItem ccr:this.tagItems)
			{
				JSONObject tmpjo = ccr.toJO() ;
				jarr.put(tmpjo) ;
			}
		}
		jo.put("tags",jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.defQTP = jo.optLong("def_qtp",300000) ;
		this.measurement = jo.optString("m") ;
		
		JSONArray jarr = jo.optJSONArray("tags") ;
		ArrayList<TagItem> ccrs = new ArrayList<>() ;
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				TagItem ccr = TagItem.fromJO(tmpjo);
				if(ccr!=null)
					ccrs.add(ccr) ;
				
				String tagpath = ccr.tagPath ;
				if(Convert.isNotNullEmpty(tagpath))
				{
					
					UATag tag =prj.getTagByPath(tagpath) ;
					ccr.tag = tag ;
				}
			}
		}
		this.tagItems = ccrs ;
	}

	@Override
	public String getOutColor(int idx)
	{
		if(idx==1)
			return "red";
		return super.getOutColor(idx);
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "result";
		case 1:
			return "error";
		}
		return null ;
	}
	
	public List<String> getTagPaths()
	{
		if(this.tagItems==null||this.tagItems.size()<=0)
			return Arrays.asList();
		
		ArrayList<String> rets = new ArrayList<>() ;
		for(TagItem ti:this.tagItems)
		{
			rets.add(ti.tagPath) ;
		}
		return rets ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		List<String> tagpaths = this.getTagPaths() ;
		if(tagpaths==null||tagpaths.size()<=0)
			return null ;
		
		JSONObject pm = msg.getPayloadJO(null) ;
		long enddt = System.currentTimeMillis() ;
		long startdt = enddt - this.defQTP ;
		if(pm!=null)
		{
			startdt = pm.optLong("start_dt",startdt) ;
			enddt = pm.optLong("end_dt",enddt) ;
		}
		if(startdt>=enddt)
		{
			JSONObject err_jo = new JSONObject() ;
			err_jo.put("success", false) ;
			err_jo.put("error", "invalid input parameter") ;
			return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayloadJO(err_jo)) ;
		}
		
		HashMap<String,JSONArray> tag2data = queryTagsVals(tagpaths, startdt, enddt,null) ;
		
		JSONObject out_jo = new JSONObject() ;
		if(tag2data!=null)
		{
			for(Map.Entry<String,JSONArray> tag2d:tag2data.entrySet())
			{
				String tagp = tag2d.getKey() ;
				TagItem ti = getTagItemByPath(tagp) ;
				if(ti==null)
					continue ;
				JSONArray jarr = tag2d.getValue() ;
				out_jo.put(ti.varName,jarr) ;
			}
		}
		return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayloadJO(out_jo));
	}
	
	public TagItem getTagItemByPath(String path)
	{
		if(this.tagItems==null)
			return null ;
		for(TagItem ti:this.tagItems)
		{
			if(path.equals(ti.tagPath))
				return ti ;
		}
		return null ;
	}
	
	private InfluxDBClient RT_getDBClient()
	{
		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return null;

		return dbm.RT_getClient() ;
	}

	public static interface IDataCB
	{
		public void on_record_jo(String tagp,JSONObject jo) ;
	}
	
	private HashMap<String,JSONArray> queryTagsVals(List<String> tagpaths, long start_dt, long end_dt,
			Map<String,Integer> tag2dec_ptn) throws Exception
	{
		HashMap<String,JSONArray> rets = new HashMap<>() ;
		boolean res = queryTagsVals(tagpaths, start_dt, end_dt,tag2dec_ptn,new IDataCB() {

			@Override
			public void on_record_jo(String tagp,JSONObject jo)
			{
				JSONArray jarr = rets.get(tagp) ;
				if(jarr==null)
				{
					jarr = new JSONArray() ;
					rets.put(tagp,jarr) ;
				}
				jarr.put(jo);
			}}) ; 
		
		if(!res)
			return null ;
		return rets;
	}
	
	private boolean queryTagsVals(List<String> tagpaths, long start_dt, long end_dt,
			Map<String,Integer> tag2dec_ptn,IDataCB data_cb) throws Exception
	{
		if(tagpaths==null || tagpaths.size()<=0)
			return false ;
		
		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return false;
		InfluxDBClient dbc = dbm.RT_getClient();
		if (dbc == null)
			return false;

		ArrayList<String> adjps = new ArrayList<>(tagpaths.size());
		for(String tagp:tagpaths)
		{
			List<String> sss= Convert.splitStrWith(tagp, "/.");
			adjps.add(Convert.combineStrWith(sss, '.')) ;
		}
		
		String tb = this.measurement ;
		if (end_dt <= 0)
			end_dt = System.currentTimeMillis();
		String sdtstr = Convert.toUTCFormat(new Date(start_dt));
		String edtstr = Convert.toUTCFormat(new Date(end_dt));
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "st = " + sdtstr + "\r\n" + "et = " + edtstr
				+ "\r\n" + "m = \"" + tb + "\" \r\n" ;

		QueryApi qapi = dbc.getQueryApi();

		String flux = flux_vars + MULTI_TAGS_PREFIX;
		boolean bfirst = true ;
		for(String tagp:adjps)
		{
			if(bfirst) bfirst=false;
			else
				flux += " or " ;
			flux += "r[\"_field\"] == \""+tagp+"\"\r\n" ;
		}
		
		flux += ")\r\n" ;
//		flux += "  |> aggregateWindow(every: 1m, fn: last, createEmpty: false)\r\n" + 
//				"  |> yield(name: \"last\")";

		if(log.isDebugEnabled())
			log.debug(flux);
		//System.out.println(flux) ;
		List<FluxTable> fts = qapi.query(flux);
		if (fts == null || fts.size() <= 0)
			return false;
		
		int ftn = fts.size() ;
		for(int i = 0 ; i < ftn ; i ++)
		{
			FluxTable ft = fts.get(i);
			int cc = -1;
			for (FluxRecord rec : ft.getRecords())
			{
				cc++;
				Instant inst = rec.getTime();
				Date dt = Date.from(inst);
	
				String field_tagp = rec.getField() ;
				Object vv = rec.getValueByKey("_value");
				if(vv!=null&&vv instanceof Number && tag2dec_ptn!=null)
				{
					Integer dec_ptn = tag2dec_ptn.get(field_tagp) ;
					if(dec_ptn!=null && dec_ptn>0)
					{
						double d = formatDouble(((Number)vv).doubleValue(),dec_ptn) ;
						vv = d ;
					}
				}
				
				JSONObject jo = new JSONObject();
				jo.put("dt", dt.getTime());
				jo.put("v", vv);
				
				data_cb.on_record_jo(field_tagp,jo);
			}
		}
		return true;
	}
	
	public HashMap<String,JSONArray> queryTagsAggrs(List<String> tagpaths, long start_dt, long end_dt,
			int aggr_num,AggrUnit unit,Map<String,String> tagp2aggrtp,Map<String,Integer> tag2dec_ptn) throws Exception
	{
		//Date testdt = new Date(start_dt) ;
		//System.out.println("start dt="+Convert.toFullYMDHMS(testdt)) ;
		
		if(tagpaths==null|tagpaths.size()<=0)
			return null ;
		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return null;
		InfluxDBClient dbc = dbm.RT_getClient();
		if (dbc == null)
			return null;

		ArrayList<String> adjps = new ArrayList<>(tagpaths.size());
		for(String tagp:tagpaths)
		{
			List<String> sss= Convert.splitStrWith(tagp, "/.");
			adjps.add(Convert.combineStrWith(sss, '.')) ;
		}
		
		HashMap<String,JSONArray> rets = new HashMap<>() ;
		for(String tagp:adjps)
		{
			String aggr_tp = tagp2aggrtp.get(tagp) ;
			JSONArray jarr = queryMaxMeanMinIntv(tagp, start_dt, end_dt, aggr_num, unit,aggr_tp,tag2dec_ptn) ;
			rets.put(tagp,jarr) ;
		}
		
		return rets;
	}
	
	public JSONArray queryMaxMeanMinIntv(String tag_path, long start_dt, long end_dt, int aggrn, AggrUnit au)
	{
		return queryMaxMeanMinIntv(tag_path, start_dt, end_dt, aggrn, au,null,null) ;
	}
	
	public JSONArray queryMaxMeanMinIntv(String tag_path, long start_dt, long end_dt, int aggrn, AggrUnit au,String aggr_tp)
	{
		return queryMaxMeanMinIntv(tag_path, start_dt, end_dt, aggrn, au,aggr_tp,null);
	}

	/**
	 * 一次查询一个tag
	 */
	public JSONArray queryMaxMeanMinIntv(String tag_path, long start_dt, long end_dt, int aggrn, AggrUnit au,
			String aggr_tp,Map<String,Integer> tag2dec_ptn)
	{
		List<String> tag_subps = Convert.splitStrWith(tag_path, "/.");
		String tagp =  Convert.combineStrWith(tag_subps, '.');
		
		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return null;
		InfluxDBClient dbc = dbm.RT_getClient();
		if (dbc == null)
			return null;
		
		//return queryMaxMeanMinIntv(tag_subps, start_dt, end_dt, aggrn, au,aggr_tp,tag2dec_ptn);
		String tb = this.measurement;
		if (end_dt <= 0)
			end_dt = System.currentTimeMillis();
		String sdtstr = Convert.toUTCFormat(new Date(start_dt));
		String edtstr = Convert.toUTCFormat(new Date(end_dt));
		
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "st = " + sdtstr + "\r\n" + "et = " + edtstr
				+ "\r\n" + "m = \"" + tb + "\" \r\n" + "f = \"" + tagp + "\"\r\n"
				+ "intv = " + aggrn + "" + au.name() + "\r\n";

		QueryApi qapi = dbc.getQueryApi();

		String flux = flux_vars + MMM;
		List<FluxTable> fts = qapi.query(flux);
		// String rowcsv = qapi.queryRaw(flux) ;
		JSONArray jarr = new JSONArray();
		if (fts.size() > 0)
		{
			for (FluxRecord rec : fts.get(0).getRecords())
			{
				Instant inst = rec.getTime();
				Date dt = Date.from(inst);
				Object max = rec.getValueByKey("max");
				Object min = rec.getValueByKey("min");
				Object mean = rec.getValueByKey("mean");
				JSONObject jo = new JSONObject();
				jo.put("dt", dt.getTime());
				//jo.put("max", max);
				//jo.put("mean", mean);
				//jo.put("min", min);
				Object vv = null ;
				if(Convert.isNotNullEmpty(aggr_tp))
				{
					AggrTP tp = AggrTP.valueOf(aggr_tp) ;
					switch(tp)
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
					}
				}
				
				if(vv!=null&& vv instanceof Number && tag2dec_ptn!=null)
				{//小数点转换
					Integer dec_ptn = tag2dec_ptn.get(tagp) ;
					if(dec_ptn!=null && dec_ptn>0)
					{
						double d = formatDouble(((Number)vv).doubleValue(),dec_ptn) ;
						vv = d ;
					}
				}
				
				if(vv!=null)
					jo.put("v", vv) ;
				jarr.put(jo);
			}
		}
		return jarr;
	}

//	private JSONArray queryMaxMeanMinIntv(List<String> tag_subps, long start_dt, long end_dt, int aggrn, AggrUnit au,String aggr_tp
//			,Map<String,Integer> tag2dec_ptn)
//	{
//		
//
//		String tagp = Convert.combineStrWith(tag_subps, '.');
//		
//	}
	
	/**
	 * 查询某个tag在一个时间段内的变化次数
	 * @param tagpath
	 * @param from_dt
	 * @param to_dt
	 * @param mode
	 * @return
	 * @throws Exception
	 */
	public int queryTagChgNum(String tagpath,long from_dt,long to_dt,int mode) throws Exception
	{
		UATag tag = this.getPrj().getTagByPath(tagpath) ;
		if(tag==null)
			throw new Exception("no tag found with path="+tagpath) ;
		ValTP vtp = tag.getValTp() ;
		if(vtp==null)
			throw new Exception("unknown tag value tp") ;
		
		String qstr = null ;
		if(vtp==ValTP.vt_bool)
		{
			switch(mode)
			{
			case 0:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", ">") ;
				break;
			case 1:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", "<") ;
				break;
			case 2:
			default:
				qstr = VAL_CHG_COUNT_BOOL.replace("{op}", "!=") ;
				break ;
			}
		}
		else if(vtp.isNumberVT())
		{
			switch(mode)
			{
			case 0:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", ">") ;
				break;
			case 1:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", "<") ;
				break;
			case 2:
			default:
				qstr = VAL_CHG_COUNT_NUM.replace("{op}", "!=") ;
				break ;
			}
		}
		else
		{
			throw new Exception("not support tag value tp="+vtp) ;
		}
		
		String tb = this.measurement;

		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return -1;
		InfluxDBClient dbc = dbm.RT_getClient();
		if (dbc == null)
			return -1;

		String sdtstr = Convert.toUTCFormat(new Date(from_dt));
		String edtstr = Convert.toUTCFormat(new Date(to_dt));
		
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "m = \"" + tb + "\" \r\n" + "st = " + sdtstr
				+ "\r\net="+edtstr + "\r\nf = \"" + tagpath + "\"\r\n";
		String flux = flux_vars + qstr ;
		QueryApi qapi = dbc.getQueryApi();
		try
		{
			List<FluxTable> fts = qapi.query(flux);
			if (fts.size() <= 0)
				return -1;
			List<FluxRecord> frs = fts.get(0).getRecords();
			if (frs.size() <= 0)
				return -1;
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
	
	
	public long queryTagDurMS(String tagpath,long from_dt,long to_dt,String cond) throws Exception
	{
		if(Convert.isNullOrEmpty(cond))
			return -1 ;
		//把 cond中的 $val 换成 r._value
		cond = cond.replaceAll("\\$val", "r._value") ;
		//把 ___ 换成 r._value > 19.8
		String flux_str = VAL_CHG_DUR_MS.replaceAll("___", cond) ;
		UATag tag = this.getPrj().getTagByPath(tagpath) ;
		if(tag==null)
			throw new Exception("no tag found with path="+tagpath) ;
		
		String tb = measurement;

		InfluxDB_M dbm = (InfluxDB_M) this.getOwnRelatedModule();
		if (dbm == null)
			return -1;
		InfluxDBClient dbc = dbm.RT_getClient();
		if (dbc == null)
			return -1;

		String sdtstr = Convert.toUTCFormat(new Date(from_dt));
		String edtstr = Convert.toUTCFormat(new Date(to_dt));
		
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "m = \"" + tb + "\" \r\n" + "st = " + sdtstr
				+ "\r\net="+edtstr + "\r\nf = \"" + tagpath + "\"\r\n";
		String flux = flux_vars + flux_str ;
		
		QueryApi qapi = dbc.getQueryApi();
		List<FluxTable> fts = qapi.query(flux);
		if (fts.size() <= 0)
			return -1;
		List<FluxRecord> frs = fts.get(0).getRecords();
		if (frs.size() <= 0)
			return -1;
		FluxRecord rec = frs.get(0);
		// Instant inst = rec.getTime() ;
		// Date dt = Date.from(inst) ;
		Number obj = (Number)rec.getValueByKey("stateDuration");
		// Long val = (Long) rec.getValueByKey("duration");
		return obj.longValue();
	}

	static final String MMM = "\r\n" + "max_tb = from(bucket:bkt)\r\n" + "  |>range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n"
			+ "  |> aggregateWindow(every: intv, fn: max,createEmpty: false,location: {zone: \"UTC\",offset:8h})\r\n"
			+ "  |> keep(columns: [\"_time\", \"_value\"])\r\n" + "  |> rename(columns: {_value: \"max\"})\r\n" + "\r\n"
			+ "\r\n" + "min_tb = from(bucket:bkt)\r\n" + "  |>range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n"
			+ "  |> aggregateWindow(every: intv, fn: min, createEmpty: false,location: {zone: \"UTC\",offset:8h} )\r\n"
			+ "  |> keep(columns: [\"_time\", \"_value\"])\r\n" + "  |> rename(columns: {_value: \"min\"})\r\n" + "\r\n"
			+ "mean_tb = from(bucket:bkt)\r\n" + "  |>range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n"
			+ "  |> aggregateWindow(every: intv, fn: mean, createEmpty: false,location: {zone: \"UTC\",offset:8h})\r\n"
			+ "  |> keep(columns: [\"_time\", \"_value\"])\r\n" + "  |> rename(columns: {_value: \"mean\"})\r\n"
			+ "\r\n" + "tb1 = join(tables: {max: max_tb, min: min_tb}, on: [\"_time\"])\r\n"
			+ "tb2 =join(tables: {max: tb1, mean: mean_tb}, on: [\"_time\"])\r\n" + "tb2  |> yield()\r\n";
	
	static final String AGGREGATE_MAX  = "\r\n" + "max_tb = from(bucket:bkt)\r\n" + "  |>range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n"
			+ "  |> aggregateWindow(every: intv, fn: max, createEmpty: false)\r\n"
			+ "  |> keep(columns: [\"_time\", \"_value\"])\r\n" + "  |> rename(columns: {_value: \"max\"})\r\n" + "\r\n"
			+ "max_tb  |> yield()\r\n";

	static final String SINGLE_TAG = "from(bucket: bkt)\r\n" + "  |> range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) => r[\"_measurement\"] ==m)\r\n" + "  |> filter(fn: (r) => r[\"_field\"] == f)";
	
	static final String MULTI_TAGS_PREFIX = "from(bucket: bkt)\r\n" + "  |> range(start: st, stop: et)\r\n"
			+ "  |> filter(fn: (r) => r[\"_measurement\"] ==m)\r\n" + 
			"  |> filter(fn: (r) => ";
	
	
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
	
	private static final String VAL_CHG_DUR_MS = "from(bucket: bkt)\r\n" + 
			"  |> range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> filter(fn: (r) => ___ )\r\n" + 
			"  |> stateDuration(fn: (r) => ___ , unit: 1ms)\r\n" + 
			"  |> last()" ;
	
	private static final String BEFORE_AFTER_AT = "// 查询时间点前的最近一个数据点\r\n" + 
			"beforePoint = from(bucket:bkt)\r\n" + 
			"  |> range(start: before_dt, stop: at_dt)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> sort(columns: [\"_time\"], desc: true)\r\n" + 
			"  |> limit(n: 1)\r\n" + 
			"  |> map(fn: (r) => ({ r with source: \"before\" }))\r\n" + 
			"\r\n" + 
			"// 查询时间点后的最近一个数据点\r\n" + 
			"afterPoint = from(bucket:bkt)\r\n" + 
			"  |> range(start: at_dt, stop: after_dt)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> sort(columns: [\"_time\"], desc: false)\r\n" + 
			"  |> limit(n: 1)\r\n" + 
			"  |> map(fn: (r) => ({ r with source: \"after\" }))\r\n" + 
			"\r\n" + 
			"// 组合两个查询结果\r\n" + 
			"union(tables: [beforePoint, afterPoint])" ;
	
	private static double formatDouble(double value, int dec_ptn) {
        double scaleFactor = Math.pow(10, dec_ptn);
        return Math.round(value * scaleFactor) / scaleFactor;
    }
}
