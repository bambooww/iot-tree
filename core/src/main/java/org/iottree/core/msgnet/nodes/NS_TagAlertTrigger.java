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
import org.iottree.core.msgnet.modules.RelationalDB_Table;
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
	public void RT_onRenderDivEvent(String evtn,StringBuilder retmsg)
	{
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
	
	private synchronized void clearCache()
	{
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
}
