package org.iottree.core.msgnet.store;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.iottree.core.station.PlatInsManager;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.Query;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

/**
 * IOT-Tree中的项目树中的采集数据，如何映射到对应的存储中——还是使用表概念
 * 
 * 这样可以实现相对通用的写入和读取配置支持
 * 
 * 1）一个项目中的数据来源可以根据需要映射到多个存储中
 * 2）可以通过映射，自动输出查询数据
 * 3）也可以为消息流提供专有节点，用来接收station的数据，并自动进行存储
 * 
 * @author zzj
 *
 */
public class StoreTb
{
	/**
	 * AggregateUnit
	 * @author zzj
	 *
	 */
	public static enum AggrUnit
	{
		ns, // 1 nanosecond
		us, // 1 microsecond
		ms, // 1 millisecond
		s,  // 1 second
		m,  // 1 minute
		h,  // 1 hour
		d,  // 1 day
		w,  // 1 week
		mo, // 1 calendar month
		y ; // 1 calendar year
		
		//public long calc
		public static int parseStr()
		{
			return 0 ;
		}
	}
	
	public static int calcMaxPtNum(long startdt,long enddt,long intv)
	{
		return 0 ; // TODO
	}
	
	UAPrj prj ;
	
	String tbName = null ;
	
	String tbTitle = null ;
	
	//String stationId = null ;
	
	//String prjName = null ;
	
	/**
	 * path prefix - under prj 
	 */
	String cxtNodePath = null ;
	
	/**
	 * all tagpath under cxtNodePath 
	 */
	boolean bAllTagSubPaths = false;
	
	/**
	 * fixed selected tagpath under cxtNodePath
	 */
	ArrayList<String> tagSubPaths = null ;

	/**
	 * store pos
	 */
	String storeSorName = null ;
	
	public StoreTb(UAPrj prj)
	{
		this.prj = prj ;
	}
	
//	public String getStationId()
//	{
//		return this.stationId ;
//	}
//	
//	public String getPrjName()
//	{
//		return this.prjName ;
//	}
	
	public String getTbName()
	{
		return this.tbName ;
	}
	
	public String getTbTitle()
	{
		if(Convert.isNullOrEmpty(this.tbTitle))
			return this.tbName ;
		
		return this.tbTitle ;
	}
	
	public String getCxtNodePath()
	{
		return this.cxtNodePath ;
	}
	
	public boolean isValid(StringBuilder failedr)
	{
//		if(Convert.isNullOrEmpty(stationId))
//		{
//			failedr.append("no station id set") ;
//			return false;
//		}
//		if(Convert.isNullOrEmpty(prjName))
//		{
//			failedr.append("no Prj Name set") ;
//			return false;
//		}
		if(Convert.isNullOrEmpty(this.cxtNodePath))
		{
			failedr.append("no Context Node Path set") ;
			return false;
		}
		
		if(!this.bAllTagSubPaths && tagSubPaths.size()<=0)
		{
			failedr.append("no Sub Tags set") ;
			return false;
		}
		
		if(Convert.isNullOrEmpty(this.tbName))
		{
			failedr.append("no Table Name found") ;
			return false;
		}
		return true ;
	}

	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
//		jo.putOpt("station_id", stationId) ;
//		jo.putOpt("prj_name", this.prjName) ;
		jo.putOpt("tablen", this.tbName) ;
		jo.putOpt("tablet", this.tbTitle) ;
		jo.putOpt("cxt_nodep", this.cxtNodePath) ;
		jo.putOpt("b_all_subt", this.bAllTagSubPaths) ;
		jo.putOpt("tag_subts", this.tagSubPaths) ;
		
		return jo ;
	}
	
	public void fromJO(JSONObject jo)
	{
//		this.stationId = jo.optString("station_id") ;
//		this.prjName = jo.optString("prj_name") ;
		this.tbName = jo.optString("tablen") ;
		this.tbTitle = jo.optString("tablet") ;
		this.cxtNodePath = jo.optString("cxt_nodep") ;
		this.bAllTagSubPaths = jo.optBoolean("b_all_subt",false) ;
		JSONArray jarr = jo.optJSONArray("tag_subts") ;
		if(jarr!=null)
		{
			ArrayList<String> subts = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
				subts.add(jarr.getString(i)) ;
			this.tagSubPaths = subts ;
		}
	}
	
	public UANodeOCTagsCxt getCxtNode()
	{
		List<String> nps = Convert.splitStrWith(this.cxtNodePath, "/");
		if(nps==null||nps.size()<=0)
			return null ;
		
		nps.remove(0) ;
		if(nps.size()==0)
			return prj;
		return (UANodeOCTagsCxt)prj.getDescendantNodeByPath(nps) ;
	}
	
	public LinkedHashMap<String,UATag> getSubPath2Tags()
	{
		LinkedHashMap<String,UATag> rets = new LinkedHashMap<>() ;
		UANodeOCTagsCxt cxtn = getCxtNode() ;
		if(cxtn==null)
			return rets ;
		for(UATag tag : cxtn.listTagsNorAll())
		{
			String subp = "/"+tag.getNodeCxtPathIn(cxtn,"/") ;
			if(!bAllTagSubPaths)
			{
				if(tagSubPaths==null)
					continue ;
				if(!tagSubPaths.contains(subp))
					continue ;
			}
			
			rets.put(subp,tag) ;
		}
		return rets ;
	}
	
	public UATag getUATagBySubPath(List<String> subps)
	{
		if(subps==null||subps.size()<=0)
			return null ;
		
		UANodeOCTagsCxt cxtn = getCxtNode() ;
		if(cxtn==null)
			return null ;
		//List<String> subps = Convert.splitStrWith(subp, "/.") ;
		UANode n = cxtn.getDescendantNodeByPath(subps) ;
		if(n==null || !(n instanceof UATag))
			return null ;
		return (UATag)n ;
	}
	
	public  List<Point> RT_transRTDataToInfluxPt(JSONObject rtdata)
	{
		UANodeOCTagsCxt gcxt = getCxtNode() ;
		if(gcxt==null)
			return  null;
		
		JSONObject curn = rtdata ;
		if(this.tagSubPaths==null||this.tagSubPaths.size()<=0)
			return null ;
		
		List<String> cxtnps = Convert.splitStrWith(this.cxtNodePath, "/") ;
		if(cxtnps==null||cxtnps.size()<=0)
			return null ;
		
		if(!cxtnps.get(0).equals(curn.optString("n")))
			return null ;
		
		for(int i = 1 ; i < cxtnps.size() ; i ++)
		{
			curn = RT_getSubNodeByName(curn,cxtnps.get(i)) ;
			if(curn==null)
				return null ;
		}
		
		//find cxt node
		ArrayList<Point> rets = new ArrayList<>() ;
		for(String tagsubp:this.tagSubPaths)
		{
			List<String> subps = Convert.splitStrWith(tagsubp, "/") ;
			if(subps==null||subps.size()<=0)
				continue ;
			JSONObject tagjo = RT_getSubTagBySubPath(curn,subps) ;
			if(tagjo==null)
				continue ;
			
			UANode tagn = gcxt.getDescendantNodeByPath(subps) ;
			if(tagn==null || !(tagn instanceof UATag))
				continue ;
			UATag tag = (UATag)tagn ;
			
			Point pt = calTagPoint(tag,subps,tagjo) ;
			if(pt==null)
				continue ;
			rets.add(pt) ;
		}
		
		return rets ;
	}
	
	
	private JSONObject RT_getSubNodeByName(JSONObject curn,String n)
	{
		JSONArray jarr = curn.getJSONArray("subs") ;
		if(jarr==null)
			return null ;
		int num = jarr.length() ;
		for(int i = 0 ; i < num ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			if(n.equals(tmpjo.optString("n")))
				return tmpjo ;
		}
		return null ;
	}
	
	private JSONObject RT_getSubTagBySubPath(JSONObject curn,List<String> subpaths)
	{
		int n ;
		if(subpaths==null||(n=subpaths.size())<=0)
			return null ;
		for(int i = 0 ; i < n - 1 ; i ++)
		{
			curn = RT_getSubNodeByName(curn,subpaths.get(i)) ;
			if(curn==null)
				return null ;
		}
		
		JSONArray jarr = curn.optJSONArray("tags") ;
		if(jarr==null)
			return null ;
		String tagn = subpaths.get(n-1) ;
		n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tjo= jarr.getJSONObject(i) ;
			if(tagn.equals(tjo.optString("n")))
				return tjo ;
		}
		return null ;
	}
	
	
	private Point calTagPoint(UATag tag,List<String> subps,JSONObject tagjo)
	{
		boolean valid = tagjo.optBoolean("valid",false) ;
		if(!valid)
			return null ;
		String m = tbName ;
		Point point = Point.measurement(m);
		long ts = tagjo.optLong("dt",-1) ;
		if(ts<=0)
			return null ;
		String strv = tagjo.optString("strv") ;
		if(strv==null)
			return null ;
		point.time(ts,WritePrecision.MS);
		
		//System.out.println(Convert.toFullYMDHMS(new Date(ts))) ;
		String fn = Convert.combineStrWith(subps, '.') ;
		
		ValTP vtp = tag.getValTp() ;
		Object v = UAVal.transStr2ObjVal(vtp, strv) ;
		
		if(v instanceof Number)
			point.addField(fn,(Number)v) ;
		else if(v instanceof String)
			point.addField(fn,(String)v) ;
		else if(v instanceof Boolean)
			point.addField(fn,(Boolean)v) ;
		else // if(v==null)
			point.addField(fn, (Number)null) ;
		
		return point ;
	}
	//
	
	public TagsVals RT_readVals()
	{
		return null ;
	}
	
	

//	private StoreTbWriter_NE getStoreTbWriter(String tablen)
//	{
//		MNManager mnm = null ;// IOTPlatformManager.getInstance().getMNManager();
//		for(MNNet net:mnm.listNets())
//		{
//			for(MNNode n:net.getNodeMapAll().values())
//			{
//				if(n instanceof StoreTbWriter_NE)
//				{
//					StoreTbWriter_NE ne = (StoreTbWriter_NE)n ;
//					StoreTb st = ne.getStoreTb() ;
//					if(st==null)
//						continue ;
//					if(tablen.equals(st.getTbName()))
//						return ne ;
//				}
//			}
//		}
//		return null ;
//	}
	
	public TagVal queryTagLastVal(InfluxDB_M dbm,List<String> tag_subps)
	{
//		StoreTbWriter_NE stb_w = getStoreTbWriter(this.tbName) ;
//		if(stb_w==null)
//			return null ;
//		
//		InfluxDB_M dbm = (InfluxDB_M)stb_w.getOwnRelatedModule() ;
//		if(dbm==null)
//			return null ;
//		
		InfluxDBClient dbc = dbm.RT_getClient() ;
		if(dbc==null)
			return null ;
		
		UATag tag = this.getUATagBySubPath(tag_subps) ;
		if(tag==null)
			return null ;
		
		String tag_subpath = Convert.combineStrWith(tag_subps, '.') ;
		
		String flux_vars = "bkt = \""+dbm.getInfluxBucket()+"\"\r\n" + 
				"m = \""+this.tbName+"\" \r\n" + 
				"f = \""+tag_subpath+"\"\r\n" ;
		String flux = flux_vars+"from(bucket: bkt)\r\n" + 
				"		  |> range(start: 0)\r\n" + 
				"		  |> filter(fn: (r) => r._measurement ==m and r._field == f)\r\n" + 
				"		  |> last()" ;
		
		QueryApi qapi = dbc.getQueryApi() ;
		List<FluxTable> fts = qapi.query(flux) ;
		if(fts.size()<=0)
			return null ;
		List<FluxRecord> frs = fts.get(0).getRecords() ;
		if(frs.size()<=0)
			return null ;
		FluxRecord rec = frs.get(0) ;
		Instant inst = rec.getTime() ;
		Date dt = Date.from(inst) ;
		
		Object val = rec.getValue() ;
		TagVal tv = new TagVal(tag,dt.getTime(),val) ;
		return tv ;
	}
	
	public Long queryDurSecondsFrom(InfluxDB_M dbm,List<String> tag_subps,long fromdt,Double num_min,Double num_max,Boolean bool_v)
	{
		UATag tag = this.getUATagBySubPath(tag_subps) ;
		if(tag==null)
			return null ;
		
		ValTP vtp = tag.getValTp() ;
		String cond ="" ;
		if(vtp.isNumberVT())
		{
			if(num_min==null&&num_max==null)
				throw new IllegalArgumentException("tag number val must has num condition") ;
			if(num_min!=null)
				cond += "r._value>="+num_min ;
			if(num_max!=null)
			{
				if(cond.length()>0)
					cond += " and r._value<="+num_max ;
				else
					cond += "r._value<="+num_max ;
			}
		}
		else if(vtp==ValTP.vt_bool)
		{
			if(bool_v==null)
				throw new IllegalArgumentException("tag bool val must has bool_v condition") ;
			cond = "r._value=="+bool_v ;
		}
		else
		{
			throw new IllegalArgumentException("tag valtp="+vtp+" no support queryDurSecondsFrom") ;
		}
		
//		StoreTbWriter_NE stb_w = getStoreTbWriter(this.tbName) ;
//		if(stb_w==null)
//			return null ;
//		
//		InfluxDB_M dbm = (InfluxDB_M)stb_w.getOwnRelatedModule() ;
//		if(dbm==null)
//			return null ;
		
		InfluxDBClient dbc = dbm.RT_getClient() ;
		if(dbc==null)
			return null ;
		
		String sdtstr = Convert.toUTCFormat(new Date(fromdt)) ;
		String tag_subpath = Convert.combineStrWith(tag_subps, '.') ;
		String flux_vars = "bkt = \""+dbm.getInfluxBucket()+"\"\r\n" + 
				"m = \""+this.tbName+"\" \r\n" + 
				"st = "+sdtstr+"\r\n" + 
				"f = \""+tag_subpath+"\"\r\n" ;
		String flux = flux_vars+" from(bucket: bkt)\r\n" + 
				"			  |> range(start: st)\r\n" + 
				"			  |> filter(fn: (r) => r._measurement == m and r._field ==f)\r\n" + 
				"			  |> map(fn: (r) => ({r with is_above_threshold: if "+cond+" then 1 else 0}))  // 标记是否超过阈值\r\n" + 
				"			  |> stateDuration(fn: (r) => r.is_above_threshold == 1, column: \"duration\", unit: 1s)\r\n" + 
				"			  |> filter(fn: (r) => r.is_above_threshold == 1)  // 只保留超过阈值的时间段\r\n" + 
				"			  |> sum(column: \"duration\")  // 计算时间差之和" ;
		QueryApi qapi = dbc.getQueryApi() ;
		List<FluxTable> fts = qapi.query(flux) ;
		if(fts.size()<=0)
			return null ;
		List<FluxRecord> frs = fts.get(0).getRecords() ;
		if(frs.size()<=0)
			return null ;
		FluxRecord rec = frs.get(0) ;
		//Instant inst = rec.getTime() ;
		//Date dt = Date.from(inst) ;
		
		Long val = (Long)rec.getValueByKey("duration") ;
		return val ;
	}
	
	public TagsVals queryTagVals(InfluxDB_M dbm,List<String> tagpaths,long start_dt,long end_dt)
	{
//		StoreTbWriter_NE stb_w = getStoreTbWriter(this.tbName) ;
//		if(stb_w==null)
//			return null ;
//		
//		InfluxDB_M dbm = (InfluxDB_M)stb_w.getOwnRelatedModule() ;
//		if(dbm==null)
//			return null ;
		
		InfluxDBClient dbc = dbm.RT_getClient() ;
		if(dbc==null)
			return null ;
		
		QueryApi qapi = dbc.getQueryApi() ;
		Query q = new Query() ;
		qapi.query(q) ;
		String res = qapi.queryRaw("") ;
		String ss = "";
		return null ;
	}
	
	public JSONArray queryMaxMeanMinIntv(InfluxDB_M dbm,String tag_subpath,long start_dt,long end_dt,
			int aggrn,AggrUnit au)
	{
		List<String> tag_subps = Convert.splitStrWith(tag_subpath, "/.") ;
		return queryMaxMeanMinIntv(dbm,tag_subps,start_dt,end_dt,aggrn,au) ;
	}

	public JSONArray queryMaxMeanMinIntv(InfluxDB_M dbm,List<String> tag_subps,long start_dt,long end_dt,
			int aggrn,AggrUnit au)
	{
//		StoreTbWriter_NE stb_w = getStoreTbWriter(this.tbName) ;
//		if(stb_w==null)
//			return null ;
//		
//		InfluxDB_M dbm = (InfluxDB_M)stb_w.getOwnRelatedModule() ;
//		if(dbm==null)
//			return null ;
		
		InfluxDBClient dbc = dbm.RT_getClient() ;
		if(dbc==null)
			return null ;
		
		if(end_dt<=0)
			end_dt= System.currentTimeMillis() ;
		String sdtstr = Convert.toUTCFormat(new Date(start_dt)) ;
		String edtstr = Convert.toUTCFormat(new Date(end_dt)) ;
		String flux_vars = "bkt = \""+dbm.getInfluxBucket()+"\"\r\n" + 
		"st = "+sdtstr+"\r\n" + 
		"et = "+edtstr+"\r\n" + 
		"m = \""+this.tbName+"\" \r\n" + 
		"f = \""+Convert.combineStrWith(tag_subps, '.')+"\"\r\n" + 
		"intv = "+aggrn+""+au.name()+"\r\n" ;
		
		QueryApi qapi = dbc.getQueryApi() ;
		
		String flux = flux_vars+MMM ;
		List<FluxTable> fts = qapi.query(flux) ;
		//String rowcsv = qapi.queryRaw(flux) ;
		JSONArray jarr = new JSONArray() ;
		if(fts.size()>0)
		{
			for(FluxRecord rec:fts.get(0).getRecords())
			{
				Instant inst = rec.getTime() ;
				Date dt = Date.from(inst) ;
				
				Object max = rec.getValueByKey("max") ;
				Object min = rec.getValueByKey("min") ;
				Object mean = rec.getValueByKey("mean") ;
				JSONObject jo = new JSONObject() ;
				jo.put("dt", dt.getTime()) ;
				jo.put("max", max) ;
				jo.put("mean", mean) ;
				jo.put("min", min) ;
				jarr.put(jo) ;
			}
		}
		return jarr ;
	}
	
	
	private static final String MMM  =
			"\r\n" + 
			"max_tb = from(bucket:bkt)\r\n" + 
			"  |>range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n" + 
			"  |> aggregateWindow(every: intv, fn: max, createEmpty: false)\r\n" + 
			"  |> keep(columns: [\"_time\", \"_value\"])\r\n" + 
			"  |> rename(columns: {_value: \"max\"})\r\n" + 
			"\r\n" + 
			"\r\n" + 
			"min_tb = from(bucket:bkt)\r\n" + 
			"  |>range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n" + 
			"  |> aggregateWindow(every: intv, fn: min, createEmpty: false)\r\n" + 
			"  |> keep(columns: [\"_time\", \"_value\"])\r\n" + 
			"  |> rename(columns: {_value: \"min\"})\r\n" + 
			"\r\n" + 
			"mean_tb = from(bucket:bkt)\r\n" + 
			"  |>range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) =>r._measurement == m and r._field==f)\r\n" + 
			"  |> aggregateWindow(every: intv, fn: mean, createEmpty: false)\r\n" + 
			"  |> keep(columns: [\"_time\", \"_value\"])\r\n" + 
			"  |> rename(columns: {_value: \"mean\"})\r\n" + 
			"\r\n" + 
			"tb1 = join(tables: {max: max_tb, min: min_tb}, on: [\"_time\"])\r\n" + 
			"tb2 =join(tables: {max: tb1, mean: mean_tb}, on: [\"_time\"])\r\n" + 
			"tb2  |> yield()\r\n";
}
