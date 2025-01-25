package org.iottree.core.msgnet.nodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
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
import org.iottree.core.store.gdb.xorm.XORMProperty;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * support all tags rt data syn to relation db table
 * @author jason.zhu
 *
 */
public class NM_TagRT2RDB  extends MNNodeMid 
{
	/**
	 * if true,any err tag rt data will no update to db table.
	 * so the data item in db row will not be updated and can be checked by update_dt
	 */
	private boolean ignoreFailedData = true ;
	
	/**
	 * when tag is delete,keepDeleted=true will keep old tag row,false will delete tag.
	 */
	private boolean keepDeleted = true ;
	
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
			return g("ok_out") ;
		if(idx==1)
			return g("failed_out") ;
		if(idx==2)
			return "Relational DB Out" ;
		
		return null ;
	}

	@Override
	public String getTP()
	{
		return "tag_rt_rdb";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_rt_rdb");
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "\\uf02c";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		RelationalDB_Table rdb = getUsingRDBTable() ;
		if(rdb==null)
		{
			failedr.append("not set RDB Table") ;
			return false;
		}
		
		JavaTableInfo jti = getPrjTableInfo(failedr) ;
		if(jti==null)
			return false ;
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("ignore_failed", ignoreFailedData) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
		this.ignoreFailedData = jo.optBoolean("ignore_failed",true) ;
		
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
	
//	public boolean RT_fireByEventTrigger(ValAlert va,Object curval)// throws Exception
//	{
//		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
//			return false ;
//		
//		MNMsg msg = new MNMsg();
//		JSONObject jo = va.RT_get_triggered_jo() ;
//		jo.putOpt("tag_val", curval) ;
//		msg.asPayload(jo);
//
//		RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, msg).asIdxMsg(2, msg));
//		//RT_doDBUpdate();
//		try
//		{
//			StringBuilder failedr = new StringBuilder() ;
//			if(!RT_recordAlertItem(va,""+ curval,true,failedr)) //,DBConnPool cp,DataTable dt,int keep_days,boolean b_outer)
//			{
//				this.RT_DEBUG_WARN.fire("RT_fireByEventTrigger",failedr.toString());
//			}
//		}
//		catch(Exception ee)
//		{
//			this.RT_DEBUG_WARN.fire("RT_fireByEventTrigger",ee.getMessage(),null,ee);
//		}
//		return true ;
//	}
//	
//	
//
//	public boolean RT_fireByEventRelease(ValAlert va,Object curval) //throws Exception
//	{
//		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
//			return false ;
//		
//		MNMsg msg = new MNMsg();
//		JSONObject jo = va.RT_get_release_jo() ;
//		jo.putOpt("tag_val", curval) ;
//		msg.asPayload(jo);
//
//		RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, msg).asIdxMsg(2, msg));
//		//RT_doDBUpdate();
//		
//		try
//		{
//			StringBuilder failedr = new StringBuilder() ;
//			if(!RT_recordAlertItem(va,""+ curval,false,failedr)) //,DBConnPool cp,DataTable dt,int keep_days,boolean b_outer)
//			{
//				this.RT_DEBUG_WARN.fire("RT_fireByEventRelease",failedr.toString());
//			}
//		}
//		catch(Exception ee)
//		{
//			this.RT_DEBUG_WARN.fire("RT_fireByEventRelease",ee.getMessage(),null,ee);
//		}
//		return true ;
//	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;

		divsb.append("<div class=\"rt_blk\" style='position:relative;'><button onclick=\"mn_fire_node_evt('"+this.getId()+"','create_tb')\">Create Table</button>") ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk(getTP(),divsb.toString())) ;
		
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
				JavaTableInfo jti = getPrjTableInfo(retmsg) ;
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
	
	private JavaTableInfo tableInfo = null ;
	private DBConnPool connPool = null ;
	private DataTable dataTable = null ;
	
	private boolean RT_netStartInit = false;
	private boolean RT_bInitOk = false;
	
	private HashMap<String,TagRow> tag2tagrow = null ;
	
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
	
	
	private DataTable RT_getDataTable(StringBuilder failedr) throws Exception
	{
		if(dataTable!=null)
			return dataTable ;
		
		JavaTableInfo jti = getPrjTableInfo(failedr) ;
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
	
	private boolean RT_recordAlertItem(ValAlert va,String curval,boolean b_triggered_or_release,StringBuilder failedr) //,int keep_days,boolean b_outer)
		throws Exception
	{
		JavaTableInfo jti = getPrjTableInfo(failedr) ;
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
			dr.putValue("AlertTP", va.getAlertTitle());
			dr.putValue("Value",curval);
			dr.putValue("Level", va.getAlertLvl());
			dr.putValue("Prompt",va.getAlertPrompt());
			
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
	
	//do update only
	private void RT_doSynUpdate()
	{
		UAPrj prj= this.getBelongTo().getBelongTo().getBelongToPrj() ;
		List<UATag> tags = prj.listTagsAll() ;
		for(UATag tag:tags)
		{
			
		}
	}
	
	private List<TagRow> loadPrjRows(DBConnPool cp) throws Exception
	{
		UAPrj prj= this.getBelongTo().getBelongTo().getBelongToPrj() ;
		String prjn = prj.getName() ;
		StringBuilder failedr = new StringBuilder() ;
		JavaTableInfo jti = getPrjTableInfo(failedr) ;
		if(jti==null)
			return null ;
		String sql = "select * from "+jti.getTableName()+" where PrjName=?" ;
		Connection conn = null ;
		boolean b_autocommit = true ;
		try
		{
			conn = cp.getConnection() ;
			b_autocommit = conn.getAutoCommit() ;
			ArrayList<TagRow> rets = new ArrayList<>() ;
			try(PreparedStatement ps = conn.prepareStatement(sql) ;)
			{
				ps.setString(1, prjn);
				try(ResultSet rs = ps.executeQuery();)
				{
					DataTable dtb = DBResult.transResultSetToDataTable(rs,jti.getTableName(),0, -1) ;
					for(DataRow dr:dtb.getRows())
					{
						TagRow tr = DBResult.transDataRow2XORMObj(TagRow.class, dr);
						rets.add(tr) ;
					}
				}
			}
			return rets ;
		}
		finally
		{
			if(conn!=null)
			{
				conn.setAutoCommit(b_autocommit);
				cp.free(conn);
			}
		}
	}
	
	private boolean loadAndSynTable(StringBuilder failedr) throws Exception
	{
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			return false;
		JavaTableInfo jti = this.getPrjTableInfo(failedr) ;
		if(jti==null)
			return false ;
		
		List<TagRow> trs = loadPrjRows(cp) ;
		
		HashMap<String,TagRow> t2tr = new HashMap<>() ;
		for(TagRow tr:trs)
		{
			t2tr.put(tr.getTagPath(),tr) ;
		}
		tag2tagrow = t2tr ;
		
		//syn table
		HashMap<String,TagRow> del_t2tr = new HashMap<>() ;
		del_t2tr.putAll(t2tr) ;
		UAPrj prj= this.getBelongTo().getBelongTo().getBelongToPrj() ;
		List<UATag> tags = prj.listTagsAll() ;
		ArrayList<TagRow> insert_tagr = new ArrayList<>() ;
		ArrayList<TagRow> update_tagr = new ArrayList<>() ;
		for(UATag tag:tags)
		{
			String tagp = tag.getNodeCxtPathInPrj() ;
			TagRow oldtr = del_t2tr.remove(tagp) ;
			TagRow tagrow = this.createTagRow(prj.getName(), tag) ;
			if(oldtr!=null && !oldtr.checkTagChg(tagrow))
				continue ;
			
			if(oldtr==null)
			{
				tagrow.autoId = IdCreator.newSeqId() ;
				insert_tagr.add(tagrow) ;
			}
			else
			{
				tagrow.autoId = oldtr.autoId ;
				update_tagr.add(tagrow) ;
			}
		}
		
		if(insert_tagr.size()<=0 && update_tagr.size()<=0 && del_t2tr.size()<=0)
			return true ;//not chged
		
		Connection conn = null ;
		boolean b_autocommit = true ;
		try
		{
			conn = cp.getConnection() ;
			b_autocommit = conn.getAutoCommit() ;
			conn.setAutoCommit(false);
			if(insert_tagr.size()>0)
			{
				String insert_sql = TagRow.INSERT_getSql(jti.getTableName()) ;
				try(PreparedStatement ps = conn.prepareStatement(insert_sql) ;)
				{
					for(TagRow tr:insert_tagr)
					{
						tr.INSERT_setPS(ps).executeUpdate() ;
					}
				}
			}
			if(update_tagr.size()>0)
			{
				String up_tag_sql = TagRow.UPDATE_TAG_getSql(jti.getTableName()) ;
				try(PreparedStatement ps = conn.prepareStatement(up_tag_sql) ;)
				{
					for(TagRow tr:insert_tagr)
					{
						tr.UPDATE_TAG_setPS(ps).executeUpdate() ;
					}
				}
			}
			if(del_t2tr.size()>0)
			{
				String del_tag_sql = TagRow.DEL_TAG_getSql(jti.getTableName()) ;
				try(PreparedStatement ps = conn.prepareStatement(del_tag_sql) ;)
				{
					for(TagRow tr:insert_tagr)
					{
						TagRow.DEL_TAG_setPS(ps, tr.getAutoId()).executeUpdate() ;
					}
				}
			}
			conn.commit();
		}
		finally
		{
			if(conn!=null)
			{
				conn.setAutoCommit(b_autocommit);
				cp.free(conn);
			}
		}
		return true ;
	}
	
	
	
	/**
	 * do table syn
	 */
	@Override
	protected void RT_onBeforeNetRun()
	{
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			DataTable dt = RT_getDataTable(failedr) ; 
			if(dt==null)
			{
				RT_bInitOk = false; 
				return ;
			}
			RT_bInitOk =  loadAndSynTable(failedr);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			RT_bInitOk = false; 
		}
		finally
		{
			RT_netStartInit = true ;
		}
	}
	
	private int updateRtVal(StringBuilder failedr) throws Exception
	{
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			return -1;
		JavaTableInfo jti = this.getPrjTableInfo(failedr) ;
		if(jti==null)
			return -1 ;
		
		UAPrj prj= this.getBelongTo().getBelongTo().getBelongToPrj() ;
		List<UATag> tags = prj.listTagsAll() ;
		ArrayList<TagRow> update_tagr = new ArrayList<>() ;
		for(UATag tag:tags)
		{
			String tagp = tag.getNodeCxtPathInPrj() ;
			TagRow tr = tag2tagrow.get(tagp) ;
			if(tr==null)
				continue ; //may restart msg net
			
			TagRow tagrow = this.createTagRow(prj.getName(), tag) ;
			if(!tr.checkValChg(tagrow))
				continue ;
			tr.setValByOth(tagrow);
			update_tagr.add(tr) ;
		}
		
		if(update_tagr.size()<=0)
			return 0 ;
		
		Connection conn = null ;
		boolean b_autocommit = true ;
		try
		{
			conn = cp.getConnection() ;
			b_autocommit = conn.getAutoCommit() ;
			conn.setAutoCommit(false);
			
			String up_val_sql = TagRow.UPDATE_VAL_getSql(jti.getTableName()) ;
			try(PreparedStatement ps = conn.prepareStatement(up_val_sql) ;)
			{
				for(TagRow tr:update_tagr)
				{
					tr.UPDATE_VAL_setPS(ps).executeUpdate() ;
				}
			}
			
			conn.commit();
			return update_tagr.size() ;
		}
		finally
		{
			if(conn!=null)
			{
				conn.setAutoCommit(b_autocommit);
				cp.free(conn);
			}
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		if(!RT_bInitOk)
		{
			if(!RT_netStartInit)
			{
				RT_onBeforeNetRun() ;
			}
			
			if(!RT_bInitOk)
			{
				this.RT_DEBUG_WARN.fire("RT_TagRT2RDB_Init","Not Init Ok",null,null);
				return null ;
			}
		}
		
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			int resn = updateRtVal(failedr);
			if(resn<0)
			{
				this.RT_DEBUG_WARN.fire("RT_TagRT2RDB_UP_RT_VAL",failedr.toString(),null,null);
				return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayload(failedr.toString())) ;
			}
			this.RT_DEBUG_INF.fire("RT_TagRT2RDB_UP_RT_VAL", "update changed tags num="+resn);
			return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(resn)) ;
		}
		catch(Exception ee)
		{
			this.RT_DEBUG_WARN.fire("RT_TagRT2RDB_UP_RT_VAL",ee.getMessage(),null,ee);
			return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayload(ee.getMessage())) ;
		}
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		if(idx==1)
			return "red";
		return super.RT_getOutColor(idx);
	}
	
	
	private TagRow createTagRow(String prjn,UATag tag)
	{
		UAVal val = tag.RT_getVal();
//		if(val==null||!val.isValid())
//		{
//			if(this.ignoreFailedData)
//				return null ;
//		}
		
		TagRow ret = new TagRow() ;
		
		ret.prjN = prjn ;
		ret.tagPath = tag.getNodeCxtPathInPrj() ;
		ret.tagTT = tag.getTitle() ;
		ret.valTP = tag.getValTp().getStr() ;
		ret.indicator = tag.getIndicator() ;
		ret.unit = tag.getUnit() ;
		if(val!=null)
		{
			ret.valid = val.isValid() ;
			ret.strv = val.getStrVal(tag.getDecDigits()) ;
			ret.upDT =new Date( val.getValDT()) ;
			ret.chgDT = new Date(val.getValChgDT()) ;
		}
		else
		{
			ret.valid = false;
		}
		ret.hasAlert = tag.hasAlerts() ;
		if(ret.hasAlert)
		{
			List<ValAlert> alerts = tag.getValAlerts() ;
			StringBuilder alertstr = new StringBuilder() ;
			if(alerts!=null && alerts.size()>0)
			{
				try
				{
					alertstr.append(alerts.get(0).toJO().toString()) ;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			ret.alertInf = alertstr.toString() ;
		}
		return ret ;
	}
	
	private static final String[] COL_NAMES_INSERT = new String[] {
			"AutoId","PrjName","TagPath","TagTitle","ValTP","Indicator","StrVal","Unit","UpDT","ChgDT","Valid","HasAlert","AlertInf"
		} ;
	
	public JavaTableInfo getPrjTableInfo(StringBuilder failedr) //throws Exception
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
		
		norcols.add(new JavaColumnInfo("PrjName",false, XmlVal.XmlValType.vt_string, 40,
				true, false,"PrjName_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("TagPath",false, XmlVal.XmlValType.vt_string, 150,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("TagTitle",false, XmlVal.XmlValType.vt_string, 150,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("ValTP",false, XmlVal.XmlValType.vt_string, 10,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Indicator",false, XmlVal.XmlValType.vt_string, 30,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("StrVal",false, XmlVal.XmlValType.vt_string, 200,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Unit",false, XmlVal.XmlValType.vt_string, 15,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("UpDT",false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("ChgDT",false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Valid",false, XmlVal.XmlValType.vt_bool, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("HasAlert",false, XmlVal.XmlValType.vt_bool, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("AlertInf",false, XmlVal.XmlValType.vt_string, 300,
				false, false,"", false,-1, "",false,false));

		
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, fks);
		return tableInfo;
	}
	
	
	public static class TagRow
	{
		@XORMProperty(name = "AutoId", has_col = true, is_pk = true, max_len = 20, is_auto = false)
		String autoId = null;

		@XORMProperty(name = "PrjName", max_len = 200, has_col = true, order_num = 8)
		String prjN = null;

		@XORMProperty(name = "TagPath", max_len = 20, has_col = true, order_num = 18)
		String tagPath;
		
		@XORMProperty(name = "TagTitle", max_len = 20, has_col = true, order_num = 18)
		String tagTT ;
		
		@XORMProperty(name = "ValTP", max_len = 20, has_col = true, order_num = 18)
		String valTP ;
		
		@XORMProperty(name = "Indicator", max_len = 20, has_col = true, order_num = 18)
		String indicator ;
		
		@XORMProperty(name = "StrVal", max_len = 20, has_col = true, order_num = 18)
		String strv ;
		
		@XORMProperty(name = "Unit", max_len = 20, has_col = true, order_num = 18)
		String unit ;
		
		@XORMProperty(name = "UpDT", max_len = 20, has_col = true, order_num = 18)
		Date upDT ;
		
		@XORMProperty(name = "ChgDT", max_len = 20, has_col = true, order_num = 18)
		Date chgDT ;
		
		@XORMProperty(name = "Valid", max_len = 20, has_col = true, order_num = 18)
		boolean valid ;
		
		@XORMProperty(name = "HasAlert", max_len = 20, has_col = true, order_num = 18)
		boolean hasAlert ;
		
		@XORMProperty(name = "AlertInf", max_len = 20, has_col = true, order_num = 18)
		String alertInf ;
		
		public TagRow()
		{}
		
		private boolean chkStrChged(String str1,String str2)
		{
			if(Convert.isNullOrEmpty(str1))
			{
				return Convert.isNotNullEmpty(str2) ;
			}
			return !str1.equals(str2) ;
		}
		
		public boolean checkTagChg(TagRow oth)
		{
			if(chkStrChged(this.tagTT,oth.tagTT))
				return true ;
			if(chkStrChged(this.valTP,oth.valTP))
				return true ;
			if(chkStrChged(this.indicator,oth.indicator))
				return true ;
			if(chkStrChged(this.unit,oth.unit))
				return true ;
			return false;
		}
		
		public boolean checkValChg(TagRow oth)
		{
			if(this.valid!=oth.valid)
				return true ;
			
			if(this.hasAlert!=oth.hasAlert)
				return true ;
			
			if(this.valid)
			{
				if(chkStrChged(this.strv,oth.strv))
					return true ;
			}
			if(this.upDT.getTime()!=oth.upDT.getTime())
				return true ;
			if(this.chgDT.getTime()!=oth.chgDT.getTime())
				return true ;
			return false;
		}
		
		public void setValByOth(TagRow oth)
		{
			this.valid = oth.valid ;
			this.strv = oth.strv ;
			this.hasAlert = oth.hasAlert ;
			this.upDT = oth.upDT ;
			this.chgDT = oth.chgDT ;
			this.alertInf = oth.alertInf ;
		}
		
		public String getAutoId()
		{
			return this.autoId ;
		}
		
		public String getPrjName()
		{
			return this.prjN ;
		}
		
		public String getTagPath()
		{
			return this.tagPath ;
		}
		
		public String getTagTitle()
		{
			return this.tagTT ;
		}
		
		public String getValTP()
		{
			return this.valTP ;
		}
		
		public String getIndicator()
		{
			return this.indicator ;
		}
		
		public String getStrVal()
		{
			return this.strv ;
		}
		
		public String getUnit()
		{
			return this.unit ;
		}
		
		public Date getUpDT()
		{
			return this.upDT ;
		}
		
		public Date getChgDT()
		{
			return this.chgDT ;
		}
		
		public boolean isValid()
		{
			return this.valid ;
		}
		
		public boolean getHasAlert()
		{
			return this.hasAlert ;
		}
		
		public String getAlertInf()
		{
			return this.alertInf ;
		}
		
		public static String INSERT_getSql(String tablen)
		{
			return "insert into "+tablen +"(AutoId,PrjName,TagPath,TagTitle,ValTP,Indicator,StrVal,Unit,UpDT,ChgDT,Valid,HasAlert,AlertInf) values (?,?,?,?,?,?,?,?,?,?,?,?,?)" ;
		}
		
		public PreparedStatement INSERT_setPS(PreparedStatement ps) throws SQLException
		{
			ps.setString(1, this.autoId);
			ps.setString(2, this.prjN);
			ps.setString(3, this.tagPath);
			ps.setString(4, this.tagTT);
			ps.setString(5, this.valTP);
			ps.setString(6, this.indicator);
			ps.setString(7, this.strv);
			ps.setString(8, this.unit);
			ps.setTimestamp(9, new Timestamp(this.upDT.getTime()));
			ps.setTimestamp(10, new Timestamp(this.chgDT.getTime()));
			ps.setBoolean(11, this.valid);
			ps.setBoolean(12, this.hasAlert);
			ps.setString(13, this.alertInf);
			return ps ;
		}
		
		public static String UPDATE_TAG_getSql(String tablen)
		{
			return "update "+tablen +" set TagTitle=?,ValTP=?,Indicator=?,Unit=? where AutoId=?" ;
		}
		
		public PreparedStatement UPDATE_TAG_setPS(PreparedStatement ps) throws SQLException
		{
			ps.setString(1, this.tagTT);
			ps.setString(2, this.valTP);
			ps.setString(3, this.indicator);
			ps.setString(5, this.autoId);
			return ps ;
		}
		
		public static String UPDATE_VAL_getSql(String tablen)
		{
			return "update "+tablen +" set StrVal=?,UpDT=?,ChgDT=?,Valid=?,HasAlert=?,AlertInf=? where AutoId=?" ;
		}
		
		public PreparedStatement UPDATE_VAL_setPS(PreparedStatement ps) throws SQLException
		{
			ps.setString(1, this.strv);
			ps.setTimestamp(2, new Timestamp(this.upDT.getTime()));
			ps.setTimestamp(3, new Timestamp(this.chgDT.getTime()));
			ps.setBoolean(4, this.valid);
			ps.setBoolean(5, this.hasAlert);
			ps.setString(6, this.alertInf);
			ps.setString(7, this.autoId);
			return ps ;
		}
		
		public static String DEL_TAG_getSql(String tablen)
		{
			return "delete from "+tablen +" where AutoId=?" ;
		}
		
		public static PreparedStatement DEL_TAG_setPS(PreparedStatement ps,String autoid) throws SQLException
		{
			ps.setString(1, autoid);
			return ps ;
		}
	}
}
