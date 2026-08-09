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
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

/**
 * Tag‘s value must limit to fixed status
 * 
 * this node 
 * @author jason.zhu
 *
 */
public class InfluxDB_TagStDur2RDB extends MNNodeMid
{
	//private static Lan lan = Lan.getLangInPk(InfluxDB_TagStDur2RDB.class) ;
	
	public static enum DurTP
	{
		every_day(0,3600000*24),
		every_hour(1,3600000);
		
		private final int val ;
		private final long gap ;
		
		DurTP(int v,long gp)
		{
			val = v ;
			gap = gp ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public static DurTP valOfInt(int i)
		{
			switch(i)
			{
			case 1:
				return every_hour ;
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
			default:
				return "Every Day";
			}
		}
		
		public long getGapMS()
		{
			return gap;
		}
	}
	
	public static class StatusVal
	{
		String strv ;
		
		String dbCol ;
		
		public StatusVal(String strv,String dbcol)
		{
			this.strv = strv ;
			this.dbCol = dbcol ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().put("strv", this.strv).put("dbc", this.dbCol) ;
		}
		
		public static StatusVal fromJO(boolean bfloat,JSONObject jo)
		{
			String strv = jo.optString("strv") ;
			String dbc = jo.optString("dbc") ;
			if(Convert.isNullOrEmpty(strv)||Convert.isNullOrEmpty(dbc))
				return null ;
			if(bfloat)
				strv = Double.parseDouble(strv)+"";
			else
				strv = Long.parseLong(strv)+"" ;
			return new StatusVal(strv,dbc) ;
		}
	}
	
	String measurement =null ;
	
	String tagPath  = null ;
	
	DurTP durTP = DurTP.every_day ;
	
	LinkedHashMap<String,StatusVal> strv2sv = new LinkedHashMap<>() ;
	
	//private transient DurItem cur_duritem = null ;
	
	@Override
	public String getTP()
	{
		return "influxdb_tagstdur2rdb";
	}

	@Override
	public String getTPTitle()
	{
		return "Tag Status Duration To RDB";
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
		if(Convert.isNullOrEmpty(this.tagPath))
		{
			failedr.append("no tag path set") ;
			return false;
		}
		if(strv2sv.size()<=0)
		{
			failedr.append("no status value set") ;
			return false;
		}
		return true;
	}
	
	

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("m", this.measurement) ;
		jo.putOpt("tagp", this.tagPath) ;
		jo.put("dur_tp", this.durTP.val) ;
		JSONArray jarr = new JSONArray() ;
		for(StatusVal sv:this.strv2sv.values())
		{
			jarr.put(sv.toJO()) ;
		}
		jo.put("st_vals", jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.measurement = jo.optString("m") ;
		this.tagPath = jo.optString("tagp") ;
		UAPrj prj = this.getPrj() ;
		boolean bfloat = false;
		if(prj!=null && Convert.isNotNullEmpty(this.tagPath))
		{
			UATag tag = prj.getTagByPath(this.tagPath) ;
			if(tag!=null)
				bfloat = tag.getValTp().isNumberFloat() ;
		}
		this.durTP = DurTP.valOfInt(jo.optInt("dur_tp",0)) ;
		JSONArray jarr = jo.optJSONArray("st_vals") ;
		LinkedHashMap<String,StatusVal> s2v = new LinkedHashMap<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StatusVal sv = StatusVal.fromJO(bfloat ,tmpjo) ;
				if(sv==null)
					continue ;
				s2v.put(sv.strv,sv) ;
			}
		}
		this.strv2sv = s2v ;
		clearCache();
	}
	
	public String getTagPath()
	{
		return this.tagPath ;
	}
	
	public UATag getTag()
	{
		if(Convert.isNullOrEmpty(this.tagPath))
			return null ;
		
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return null ;
		return prj.getTagByPath(this.tagPath) ;
	}
	
	public DurTP getDurTP()
	{
		return this.durTP ;
	}
	
	public String getMeasurement()
	{
		return this.measurement ;
	}
	
	private static class QueResult
	{
		Date st;
		Date et;
		HashMap<StatusVal,Integer> sv2secs = new HashMap<>() ;
		
		public QueResult(Date st,Date et)
		{
			this.st = st ;
			this.et = et ;
		}
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().put(COL_ST, this.st.getTime()).put("st", Convert.toFullYMDHMS(st))
					.put(COL_ET, this.et.getTime()).put("et", Convert.toFullYMDHMS(et));
			JSONArray jarr = new JSONArray();
			ret.put("st_val_secs", jarr) ;
			for(Map.Entry<StatusVal,Integer> sv2i:sv2secs.entrySet())
			{
				StatusVal sv = sv2i.getKey() ;
				JSONObject tmpjo = new JSONObject().put("strv", sv.strv).put("dbc",sv.dbCol).put("secs", sv2i.getValue()) ;
				jarr.put(tmpjo) ;
			}
			return ret ;
		}
	}
	
	private QueResult queryAt(Date start_dt,Date end_dt)
	{
		long l_st = start_dt.getTime() ;
		long l_et = end_dt.getTime() ;
		if(l_et>System.currentTimeMillis())
		{
			l_et = System.currentTimeMillis() ;
			end_dt = new Date(l_et) ;
		}
		
		if(l_st>l_et)
			return null ;
		
		
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		InfluxDBClient client = dbm.RT_getClient() ;
		QueryApi qapi = client.getQueryApi() ;
		
		String sdtstr = Convert.toUTCFormat(start_dt);
		String edtstr = Convert.toUTCFormat(end_dt);
		
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return null;

		//MNNet net = this.getBelongTo()
		UATag tag = prj.getTagByPath(this.tagPath) ;
		if(tag==null)
			return null;
		
		boolean bfloat = tag.getValTp().isNumberFloat() ;
		
		String flux_vars = "bkt = \"" + dbm.getBucket() + "\"\r\n" + "st = " + sdtstr + "\r\n" + "et = " + edtstr
				+ "\r\n" + "m = \"" + this.measurement + "\" \r\n" + "f = \""+this.tagPath+"\"\r\n";

		String flux = flux_vars + FLUX_T;
		List<FluxTable> fts = qapi.query(flux);
		if(fts==null||fts.size()<=0)
			return null ;
		FluxTable ft = fts.get(0) ;
		QueResult ret = new QueResult(start_dt,end_dt) ;
		for (FluxRecord rec : ft.getRecords())
		{
			Number ssv = (Number)rec.getValueByKey("status") ;
			if(ssv==null)
				continue ;
			String status_v = null;
			if(bfloat)
				status_v = ssv.doubleValue()+"" ;
			else
				status_v = ssv.longValue()+"" ;
			StatusVal sv = this.strv2sv.get(status_v) ;
			if(sv==null)
				continue ;
			int  dur_sec = ((Number)rec.getValueByKey("dur_sec")).intValue() ;
			ret.sv2secs.put(sv,dur_sec) ;
		}
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
		
		@Override
		public boolean equals(Object oth)
		{
			if(oth==null||!(oth instanceof DurItem))
				return false;
			DurItem dioth = (DurItem)oth ;
			if(!this.start_dt.equals(dioth.start_dt))
				return false;
			if(!this.end_dt.equals(dioth.end_dt))
				return false;
			return true;
		}
	}
	
	private DurItem calDurItemAt(long dt)
	{
		Date at = new Date(dt) ;
		switch(this.durTP)
		{
		case every_hour:
			Date day_s = Convert.calHourStart(at) ;
			Date day_e = Convert.calHourEnd(at) ;
			return new DurItem(day_s,day_e) ;
		default: //every day
			day_s = Convert.calDayStart(at) ;
			day_e = Convert.calDayEnd(at) ;
			return new DurItem(day_s,day_e) ;
		}
	}
	
	public List<DurItem> calDurItemsInPeriod(long st,long et)
	{
		ArrayList<DurItem> dis = new ArrayList<>() ;
		do
		{
			DurItem di = calDurItemAt(st) ;
			dis.add(di) ;
			st += this.durTP.gap ;
		}while(st<=et) ;
		return dis ;
	}
	
	private transient DurItem lastDur = null ; 
	
	private QueResult queAndUpRDB(DurItem di) throws Exception
	{
		QueResult ret = queryAt(di.start_dt,di.end_dt) ;
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

	static final String FLUX_T  = "from(bucket: bkt)\r\n" + 
			"  |> range(start: st, stop: et)\r\n" + 
			"  |> filter(fn: (r) => r._measurement == m and r._field==f)\r\n" + 
			"  |> aggregateWindow(every: 1s, fn: last, createEmpty: true)\r\n" +  //create 1s samples
			"  |> fill(usePrevious: true)\r\n" +  //keep last status until next status
			"  |> group(columns: [\"_value\"])\r\n" +  //group by status values
			"  |> duplicate(column: \"_value\", as: \"duration\")\r\n" + //copy _value to duration,exit group key limit
			"  |> count(column: \"duration\")\r\n" + //count all status num(1s)
			"  |> duplicate(column: \"_value\", as: \"status\")"+
			"  |> duplicate(column: \"duration\", as: \"dur_sec\")\r\n" + 
			"  |> keep(columns: [\"status\", \"dur_sec\"])";
	
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
		divblks.add(new DivBlk("influxdb_tstd2rdb",divsb.toString())) ;
		
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
		//this.cur_duritem = null ;
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
		
		for(StatusVal sv:this.strv2sv.values())
		{
			norcols.add(new JavaColumnInfo(sv.dbCol,false, XmlVal.XmlValType.vt_int32,-1,
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
		for(StatusVal sv:this.strv2sv.values())
		{
			Integer sec = qr.sv2secs.get(sv) ;
			if(sec==null)
				sec = 0;
			dr.putValue(sv.dbCol,sec) ;
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
		List<DurItem> dis = calDurItemsInPeriod(st,et);
		for(DurItem di:dis)
		{
			QueResult ret = queryAt(di.start_dt,di.end_dt) ;
			if(ret==null)
				continue ;
			StringBuilder failedr = new StringBuilder() ;
			if(this.RT_addOrUpdate(ret, failedr))
				cc ++ ;
		}
		return cc;
	}
	
	@outer_api(name="dur_in_cur_mon",
			title_en="Calculate the duration of tag status for the current month (in seconds)",title_cn="计算当月标签状态持续时间(秒)")
	public JSONObject statDurInCurrentMonth(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calMonthStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return statDurInPeriod(inputjo,failedr) ;
	}
	
	@outer_api(name="dur_in_cur_year",
			title_en="Calculate the duration of tag status for the current year (in seconds)",title_cn="计算当年标签状态持续时间(秒)")
	public JSONObject statDurInCurrentYear(JSONObject inputjo,StringBuilder failedr) throws Exception
	{
		if(inputjo==null)
			inputjo = new JSONObject() ;
		Date nowdt = new Date() ;
		inputjo.put("et", nowdt.getTime()) ;
		Date monst = Convert.calYearStart(nowdt) ;
		inputjo.put("st", monst.getTime()) ;
		return statDurInPeriod(inputjo,failedr) ;
	}
	
	@outer_api(name="dur_in_period",
			title_en="Calculate the duration (in seconds) of tag status during a time period",title_cn="计算在一个时间段标签状态持续时间(秒)")
	public JSONObject statDurInPeriod(JSONObject inputjo,StringBuilder failedr) throws Exception
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
		sqlsb.append("select ") ;
		boolean bfirst = true;
		for(StatusVal sv:this.strv2sv.values())
		{
			if(bfirst)
				bfirst = false;
			else
				sqlsb.append(",") ;
			sqlsb.append("sum("+sv.dbCol+") as "+sv.dbCol) ;
			//norcols.add(new JavaColumnInfo(sv.dbCol,false, XmlVal.XmlValType.vt_int32,-1,
			//		false, false,null, false,-1, "",false,false));
		}
		sqlsb.append(" from ").append(jti.getTableName()).append(" where ")
			.append(COL_ST).append(">=? and ").append(COL_ST).append("<?") ;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			JSONObject ret = new JSONObject() ;
			try (PreparedStatement ps = conn.prepareStatement(sqlsb.toString()))
			{
				ps.setLong(1, st);
				ps.setLong(2, et);
				try(ResultSet rs=ps.executeQuery())
				{
					if(rs.next())
					{
						for(StatusVal sv:this.strv2sv.values())
						{
							int v = rs.getInt(sv.dbCol) ;
							ret.put(sv.dbCol,v) ;
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
	
	private static final JSONObject input_st_et = new JSONObject().put("st", 1767196800000l).put("et", 1784627683879l) ;
	//private static final JSONObject ouput_dur = new JSONObject().put("st", 1767196800000l).put("et", 1784627683879l) ;
	@Override
	protected JSONObject[] extOuterApiIOSample(String apin)
	{
		JSONObject out_jo = new JSONObject() ;
		for(StatusVal sv:this.strv2sv.values())
		{
			out_jo.put(sv.dbCol,100) ;
		}
		
		switch(apin)
		{
		case "dur_in_cur_mon":
			return new JSONObject[] {null,out_jo};
		case "dur_in_cur_year":
			return new JSONObject[] {null,out_jo};
		case "dur_in_period":
			return new JSONObject[] {input_st_et,out_jo};
		
		}
		return null ;
	}
}
