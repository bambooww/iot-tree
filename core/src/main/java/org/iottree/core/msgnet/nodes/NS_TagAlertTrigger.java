package org.iottree.core.msgnet.nodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.msgnet.modules.RelationalDB_Table;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_TagAggr2RDB.AggrTag;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_TagStDur2RDB.StatusVal;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class NS_TagAlertTrigger  extends MNNodeStart 
{
	private HashSet<String> evt_ids = new HashSet<>() ;
	
	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 3;
	}
	
	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
	static
	{
		OUT2RES.put(2,new OutResDef(RelationalDB_Table.class,false)) ;
	}
	
	@Override
	public Map<Integer,OutResDef> getOut2Res()
	{
		return OUT2RES ;
	}

	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return g("triggered_out") ;
		if(idx==1)
			return g("released_out") ;
		if(idx==2)
			return "Relational DB Out" ;
		
		return null ;
	}
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}

	@Override
	public String getTP()
	{
		return "tag_alert_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_alert_trigger");
	}

	@Override
	public String getColor()
	{
		return "#ff8566";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a2";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.evt_ids==null||evt_ids.size()<=0)
		{
			failedr.append("no tag event set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("evt_ids", evt_ids) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("evt_ids") ;
		HashSet<String> ss = new HashSet<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String id = jarr.getString(i) ;
				ss.add(id) ;
			}
		}
		this.evt_ids = ss ;
		
		clearCache();
	}
	
	
	public RelationalDB_Table getUsingRDBTable()
	{
		MNNodeRes noderes = this.getOutResNode(2) ;
		if(noderes==null || !(noderes instanceof RelationalDB_Table))
			return null;
		return (RelationalDB_Table)noderes ;
	}
	
//	private void RT_doDBUpdate()
//	{
//		
//		IConnPool cp = dbt.RT_getConnPool() ;
//		if(cp==null)
//			return ;
//		
//		Connection conn = null;
//		try
//		{
//			conn = cp.getConnection() ;
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			if(conn!=null)
//				cp.free(conn);
//		}
//	}
	
	public boolean RT_fireByEventTrigger(ValEvent va,Object curval)// throws Exception
	{
		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
			return false ;
		
		MNMsg msg = new MNMsg();
		JSONObject jo = va.RT_get_triggered_jo() ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);

		RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, msg).asIdxMsg(2, msg));
		//RT_doDBUpdate();
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_recordAlertItem(va,""+ curval,true,failedr)) //,DBConnPool cp,DataTable dt,int keep_days,boolean b_outer)
			{
				this.RT_DEBUG_WARN.fire("RT_fireByEventTrigger",failedr.toString());
			}
		}
		catch(Exception ee)
		{
			this.RT_DEBUG_WARN.fire("RT_fireByEventTrigger",ee.getMessage(),null,ee);
		}
		return true ;
	}
	
	

	public boolean RT_fireByEventRelease(ValEvent va,Object curval) //throws Exception
	{
		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
			return false ;
		
		MNMsg msg = new MNMsg();
		JSONObject jo = va.RT_get_release_jo() ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);

		RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, msg).asIdxMsg(2, msg));
		//RT_doDBUpdate();
		
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!RT_recordAlertItem(va,""+ curval,false,failedr)) //,DBConnPool cp,DataTable dt,int keep_days,boolean b_outer)
			{
				this.RT_DEBUG_WARN.fire("RT_fireByEventRelease",failedr.toString());
			}
		}
		catch(Exception ee)
		{
			this.RT_DEBUG_WARN.fire("RT_fireByEventRelease",ee.getMessage(),null,ee);
		}
		return true ;
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;

		divsb.append("<div class=\"rt_blk\" style='position:relative;'><button onclick=\"mn_fire_node_evt('"+this.getId()+"','create_tb')\">Create Table</button>") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("tag_alert_trigger",divsb.toString())) ;
		
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
				JavaTableInfo jti = getAlertsTableInfo(retmsg) ;
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
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			retmsg.append("Err:"+ee.getMessage()) ;
		}
	}
	
	
	// ----  db ---
	private static final String[] COL_NAMES_INSERT = new String[] {
			"AutoId","TriggerDT","PrjName","Tag","AlertTP","Value","Level","Prompt"
		} ;

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
	
	public JavaTableInfo getAlertsTableInfo(StringBuilder failedr) //throws Exception
	{
		if(tableInfo!=null)
			return tableInfo;
		
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			failedr.append("no related RelationalDB_Table") ;
			return null ;
		}
//		connPool = tb.RT_getConnPool() ;
//		if(connPool==null)
//			return null ;
		
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		
		String tablename = tb.getTableName() ;
		if(Convert.isNullOrEmpty(tablename))
		{
			failedr.append("RelationalDB_Table has no table name") ;
			return null ;
		}
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		pkcol = new JavaColumnInfo("AutoId",true, XmlVal.XmlValType.vt_string, 30,
				false, false,"", false,-1,"",false,false);
		
		norcols.add(new JavaColumnInfo("TriggerDT",false, XmlVal.XmlValType.vt_date, -1,
				true, false,"TriggerDT_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("ReleaseDT",false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("PrjName",false, XmlVal.XmlValType.vt_string, 40,
				true, false,"PrjName_idx", false,-1, "",false,false));

		int tag_maxlen = 200 ;
		for(UATag tag:prj.listTagsAll())
		{
			String np = tag.getNodePath() ;
			int len = np.length() ;
			if(len>tag_maxlen)
				tag_maxlen = len ;
		}
		
		norcols.add(new JavaColumnInfo("Tag",false, XmlVal.XmlValType.vt_string, tag_maxlen,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("AlertTP",false, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Value",false, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Level",false, XmlVal.XmlValType.vt_int16, 2,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Prompt",false, XmlVal.XmlValType.vt_string, 200,
				false, false,"", false,-1, "",false,false));

		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, fks);
		
		return tableInfo;
	}
	
	private DataTable RT_getDataTable(StringBuilder failedr) throws Exception
	{
		if(dataTable!=null)
			return dataTable ;
		
		JavaTableInfo jti = getAlertsTableInfo(failedr) ;
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
	
	private boolean RT_recordAlertItem(ValEvent va,String curval,boolean b_triggered_or_release,StringBuilder failedr) //,int keep_days,boolean b_outer)
		throws Exception
	{
		JavaTableInfo jti = getAlertsTableInfo(failedr) ;
		if(jti==null)
			return false;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return false;
		
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		String prjname = "" ;
		if(prj!=null)
			prjname = prj.getName() ;
		
		DataRow dr = dt.createNewRow() ;
		//ValAlert va = ai.getValAlert() ;
		//AlertHandler ah = ai.getHandler() ;
		UATag tag = va.getBelongTo() ;
		String row_id = va.RT_get_trigger_uid();//.getId() ;
		if(b_triggered_or_release)
		{
			dr.putValue("AutoId",row_id) ;
			dr.putValue("PrjName", prjname);
			dr.putValue("Tag", tag.getNodePathCxt());
			dr.putValue("TriggerDT", new Date(va.RT_last_trigger_dt()));
			//dr.putValue("Handler", this.getName());
			dr.putValue("AlertTP", va.getEventTitle());
			dr.putValue("Value",curval);
			dr.putValue("Level", va.getEventLvl());
			dr.putValue("Prompt",va.getEventPrompt());
			
			Connection conn =null;
			try
			{
				conn = cp.getConnection() ;
				//System.out.println(" insert id=="+row_id) ;
				dr.doInsertDB(conn, jti.getTableName(), COL_NAMES_INSERT) ;
				
//				if(delOld(conn,jti.getTableName(),"TriggerDT",keep_days,outerLastDelDT))
//					outerLastDelDT = System.currentTimeMillis() ;
			}
			finally
			{
				if(conn!=null)
					cp.free(conn);
			}
		}
		else // release
		{
			dr.putValue("AutoId",row_id) ;
			dr.putValue("ReleaseDT", new Date(va.RT_last_released_dt()));
			Connection conn =null;
			try
			{
				conn = cp.getConnection() ;
				dr.doUpdateDB(conn, jti.getTableName(), "AutoId", new String[] {"ReleaseDT"});
			}
			finally
			{
				if(conn!=null)
					cp.free(conn);
			}
		}
		return true;
	}
	
	//private long lastDelDT = -1 ;
	
	@SuppressWarnings("unused")
	private boolean delOld(Connection conn,String tabename,String dt_col,int keep_days,long last_del_dt) throws SQLException
	{
		final long DAY_MS = 24*3600000 ;
		if(keep_days<=0)
			return false;
		
		if(System.currentTimeMillis()-last_del_dt<DAY_MS)
			return false;
		
		long to_gap = keep_days*DAY_MS ;
		Date olddt = new Date(System.currentTimeMillis()-to_gap) ;
		
		StringBuilder delsql = new StringBuilder() ;
		delsql.append("delete from ").append(tabename);
		delsql.append(" where ").append(dt_col).append("<?") ;
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(delsql.toString()) ;
			
			ps.setObject(1, olddt);
			ps.executeUpdate() ;
			//System.out.println(new Date()+" alert handler del old "+delsql.toString());
			return true;
		}
		finally
		{
			//lastDelDT = System.currentTimeMillis() ;
			if(ps!=null)
				ps.close() ;
		}
	}
	
	
	public static DataTable selectRecords(DBConnPool cp,String tablename,Date start_dt,Date end_dt,String handler_name,int pageidx,int pagesize) throws Exception
	{
		if(pageidx<0||pagesize<=0)
			throw new IllegalArgumentException("invalid pageidx and pagesize") ;
		Connection conn = null;

		PreparedStatement ps = null;
		//Statement ps = null ;
		ResultSet rs = null;
		
		String sql = "select * from "+tablename;
		String cond = null ;
		if(start_dt!=null)
			cond = (cond==null?" where ":cond +" and ") + "TriggerDT >= ?" ;
		if(end_dt!=null)
			cond = (cond==null?" where ":cond +" and ") + "TriggerDT <= ?" ;
		if(Convert.isNotNullEmpty(handler_name))
			cond = (cond==null?" where ":cond +" and ") + "Handler = ?" ;
		if(cond==null)
			cond = "" ;
		//sql += cond +" order by TriggerDT desc limit "+pagesize+" offset "+pageidx*pagesize;
		sql += cond +" order by TriggerDT desc limit ? offset ?";
		try
		{
			conn = cp.getConnection();
			
			//ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ps = conn.prepareStatement(sql);
			
			int pidx = 0 ;
			if(start_dt!=null)
			{
				pidx ++ ;
				ps.setDate(pidx, new java.sql.Date(start_dt.getTime()));
			}
			if(end_dt!=null)
			{
				pidx ++ ;
				ps.setDate(pidx, new java.sql.Date(end_dt.getTime()));
			}
			if(Convert.isNotNullEmpty(handler_name))
			{
				pidx ++ ;
				ps.setString(pidx, handler_name);
			}
			
			pidx ++ ;
			ps.setInt(pidx, pagesize);
			
			pidx ++ ;
			ps.setInt(pidx, pageidx*pagesize);
			
			DataTable dt = null;

				if (pagesize > 0)
				{
					ps.setMaxRows((pageidx+1)*pagesize);
				}

				rs = ps.executeQuery();
				dt = DBResult.transResultSetToDataTable(tablename,0,rs, 0, pagesize,null);
				
			return dt;
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e) {}
			}
			
			if(ps!=null)
			{
				try
				{
					ps.close();
				}
				catch(Exception e) {}
			}
			if (conn != null)
			{
				cp.free(conn);
			}
		}
	}
	
	@outer_api(name="count_evt"
			,title_en="Count the number of events/alarms during a time period",title_cn="计数一个时间段事件报警次数")
	public JSONObject countEvtNumInPeriod(JSONObject inputjo,StringBuilder failedr) throws Exception
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
		JSONArray tmp_jarr = inputjo.optJSONArray("tags") ;
		ArrayList<String> tags = null ;
		int n ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			tags = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tagp = tmp_jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tagp))
					tags.add(tagp) ;
			}
		}
		
		tmp_jarr = inputjo.optJSONArray("tps") ;
		ArrayList<String> tps = null ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			tps = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tp = tmp_jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tp))
					tps.add(tp) ;
			}
		}
		
		tmp_jarr = inputjo.optJSONArray("lvls") ;
		ArrayList<Integer> lvls = null ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			lvls = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				int lvl = tmp_jarr.getInt(i) ;
				lvls.add(lvl) ;
			}
		}
		
		JavaTableInfo jti = getAlertsTableInfo(failedr) ;
		if(jti==null)
			return null;
		
		
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(dt==null)
			return null;
		
		UAPrj prj = this.getPrj() ;
		String prjn = null;
		if(prj!=null)
			prjn = prj.getName() ;
		
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("select count(*) as cc  from "+dt.getTableName()+" where TriggerDT>=? and TriggerDT<?") ;
		if(Convert.isNotNullEmpty(prjn))
			sqlsb.append("  and PrjName=?") ;
		if(tags!=null&&tags.size()>0)
			sqlsb.append("  and Tag in ("+Convert.transIdsToSqlIn(tags)+")") ;
		if(tps!=null&&tps.size()>0)
			sqlsb.append("  and AlertTP in ("+Convert.transIdsToSqlIn(tps)+")") ;
		if(lvls!=null&&lvls.size()>0)
			sqlsb.append("  and Level in ("+Convert.transIdsToSqlIn(lvls)+")") ;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		if(cp==null)
			return null ;
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			JSONObject ret = new JSONObject() ;
			try (PreparedStatement ps = conn.prepareStatement(sqlsb.toString()))
			{
				java.sql.Timestamp sdate = new java.sql.Timestamp(st) ;
				java.sql.Timestamp edate = new java.sql.Timestamp(et) ;
				ps.setTimestamp(1, sdate);
				ps.setTimestamp(2, edate);
				if(Convert.isNotNullEmpty(prjn))
					ps.setString(3, prjn);
				
				try(ResultSet rs=ps.executeQuery())
				{
					if(rs.next())
					{
						int v = rs.getInt("cc") ;
						ret.put("count", v) ;
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
	
	private static class Dur
	{
		long tdt ;
		long rdt ;
		
		public Dur(long tdt,long rdt)
		{
			this.tdt = tdt ;
			this.rdt = rdt ;
		}
		
		public int getSeconds()
		{
			return (int)(rdt-tdt)/1000 ;
		}
		
		public boolean isHit(Dur d)
		{
			if(this.rdt<d.tdt)
				return false;
			if(this.tdt>d.rdt)
				return false;
			return true;
		}
		
		public void merge(Dur d)
		{
			if(this.tdt>d.tdt)
				this.tdt = d.tdt ;
			if(this.rdt<d.rdt)
				this.rdt = d.rdt ;
		}
	}
	
	@outer_api(name="stat_dur_cur_mon"
			,title_en="Calculate the duration of the alarm for the current month",title_cn="计算当月报警持续时间")
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
	
	@outer_api(name="stat_dur_cur_year"
	,title_en="Calculate the duration of the alarm for that year",title_cn="计算当年报警持续时间")
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
	
	@outer_api(name="stat_dur_period"
			,title_en="Calculate the alarm duration for a time period",title_cn="计算一个时间段报警持续时间")
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
		JSONArray tmp_jarr = inputjo.optJSONArray("tags") ;
		ArrayList<String> tags = null ;
		int n ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			tags = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tagp = tmp_jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tagp))
					tags.add(tagp) ;
			}
		}
		
		tmp_jarr = inputjo.optJSONArray("tps") ;
		ArrayList<String> tps = null ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			tps = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tp = tmp_jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tp))
					tps.add(tp) ;
			}
		}
		
		tmp_jarr = inputjo.optJSONArray("lvls") ;
		ArrayList<Integer> lvls = null ;
		if(tmp_jarr!=null&&(n=tmp_jarr.length())>0)
		{
			lvls = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				int lvl = tmp_jarr.getInt(i) ;
				lvls.add(lvl) ;
			}
		}
		
		JavaTableInfo jti = getAlertsTableInfo(failedr) ;
		if(jti==null)
			return null;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		if(cp==null)
			return null ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return null;
		
		UAPrj prj = this.getPrj() ;
		String prjn = null;
		if(prj!=null)
			prjn = prj.getName() ;
		
		StringBuilder sqlsb = new StringBuilder();
		sqlsb.append("select * from "+dt.getTableName()+" where TriggerDT>=? and TriggerDT<?") ;
		if(Convert.isNotNullEmpty(prjn))
			sqlsb.append("  and PrjName=?") ;
		if(tags!=null&&tags.size()>0)
			sqlsb.append("  and Tag in ("+Convert.transIdsToSqlIn(tags)+")") ;
		if(tps!=null&&tps.size()>0)
			sqlsb.append("  and AlertTP in ("+Convert.transIdsToSqlIn(tps)+")") ;
		if(lvls!=null&&lvls.size()>0)
			sqlsb.append("  and Level in ("+Convert.transIdsToSqlIn(lvls)+")") ;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			ArrayList<Dur> durs = new ArrayList<>() ;
			try (PreparedStatement ps = conn.prepareStatement(sqlsb.toString()))
			{
				java.sql.Timestamp sdate = new java.sql.Timestamp(st) ;
				java.sql.Timestamp edate = new java.sql.Timestamp(et) ;
				ps.setTimestamp(1, sdate);
				ps.setTimestamp(2, edate);
				if(Convert.isNotNullEmpty(prjn))
					ps.setString(3, prjn);
				
				try(ResultSet rs=ps.executeQuery())
				{
					while(rs.next())
					{
						java.sql.Timestamp tdt = rs.getTimestamp("TriggerDT") ;
						java.sql.Timestamp rdt = rs.getTimestamp("ReleaseDT") ;
						if(rdt==null)
							continue ;
						long rdt_ms =rdt.getTime() ;
						if(rdt_ms>et)
							rdt_ms = et ;
						long tdt_ms = tdt.getTime() ;
						if(rdt_ms<=tdt_ms)
							continue ;
						Dur d = new Dur(tdt_ms,rdt_ms) ;
						durs.add(d) ;
					}
				}
			}
			int rec_n = durs.size() ;
			//combine durs
			int ts = combineTotalSecs(durs) ;
			return new JSONObject().put("count",rec_n).put("total_seconds",ts) ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	private static int combineTotalSecs(List<Dur> durs)
	{
		if(durs==null||durs.size()<=0)
			return 0 ;
		int n = durs.size() ;
		ArrayList<Dur> sole_durs = new ArrayList<>() ;
		do
		{
			n = durs.size() ;
			Dur lastd = durs.remove(n-1) ;
			for(int k = n-2 ; k >=0 ; k --)
			{
				Dur d = durs.get(k) ;
				if(lastd.isHit(d))
				{
					d.merge(lastd) ;
					lastd = null ;
					break ;
				}
			}
			
			if(lastd!=null)//not hit any others,so it's sole dur
				sole_durs.add(lastd) ;
			
		}while(durs.size()>0) ;
		
		int r = 0 ;
		for(Dur d:sole_durs)
		{
			r += d.getSeconds();
		}
		return r ;
	}
	

	@Override
	protected JSONObject[] extOuterApiIOSample(String apin)
	{
		switch(apin)
		{
		case "count_evt":
			return new JSONObject[] {new JSONObject().put("st",1767196800000l).put("et", 1784627683879l)
					.put("tags", new JSONArray()).put("tps", new JSONArray()).put("lvls", new JSONArray())
					,new JSONObject().put("count", 3)};
		case "stat_dur_period":
			return new JSONObject[] {new JSONObject().put("st",1767196800000l).put("et", 1784627683879l)
					.put("tags", new JSONArray()).put("tps", new JSONArray()).put("lvls", new JSONArray())
					,new JSONObject().put("count", 3).put("total_seconds", 100)};
		case "stat_dur_cur_mon":
		case "stat_dur_cur_year":
			return new JSONObject[] {new JSONObject()
					.put("tags", new JSONArray()).put("tps", new JSONArray()).put("lvls", new JSONArray())
					,new JSONObject().put("count", 3).put("total_seconds", 100)};
		}
		
		return null ;
	}
	
}
