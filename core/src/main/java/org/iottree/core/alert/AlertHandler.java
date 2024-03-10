package org.iottree.core.alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.iottree.core.UAPrj;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONArray;
import org.json.JSONObject;

@data_class
public class AlertHandler extends JSObMap //implements IJsProp
{
	
	@data_val
	String id = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	@data_val(param_name = "n")
	private String name = "" ;
	
	@data_val(param_name = "t")
	private String title = "" ;
	
	@data_val(param_name = "d")
	private String desc="" ;
	
	/**
	 * 
	 */
	@data_val(param_name = "b_inner_record")
	private boolean bInnerRecord = true;
	
	@data_val(param_name = "inner_record_days")
	private int innerRecordDays = 100 ;
	
	@data_val(param_name = "b_outer_record")
	private boolean bOuterRecord = false;
	/**
	 * outer record data store source
	 */
	@data_val(param_name = "outer_record_sor")
	private String outerRecordSor = "" ;
	
	@data_val(param_name = "outer_record_days")
	private int outerRecordDays = 100 ;
	
	/**
	 * tag paths in this handler
	 */
	
	List<String> alertUids = new ArrayList<>() ;
	
	@data_val(param_name = "alert_uids")
	private String get_AlertUids()
	{
		if(alertUids==null)
			return "" ;
		return Convert.combineStrWith(alertUids, ',') ;
	}
	@data_val(param_name = "alert_uids")
	private void set_AlertUids(String str)
	{
		alertUids = Convert.splitStrWith(str, ",") ;
	}
	
	List<String> alertOutIds = new ArrayList<>() ;
	@data_val(param_name = "out_ids")
	private String get_OutIds()
	{
		if(alertOutIds==null)
			return "" ;
		return Convert.combineStrWith(alertOutIds, ',') ;
	}
	@data_val(param_name = "out_ids")
	private void set_OutIds(String str)
	{
		alertOutIds = Convert.splitStrWith(str, ",") ;
	}
	
	@data_val(param_name = "lvl")
	int alertLevel = 0 ;
	
	@data_val(param_name = "trigger_en")
	boolean bTriggerEn = true ;
	
	@data_val(param_name = "trigger_c")
	String triggerColor = null ;
	
	@data_val(param_name = "release_en")
	boolean bReleaseEn = true ;
	
	@data_val(param_name = "release_c")
	String releaseColor = null ;
	
	transient UAPrj prj = null ;
	
	transient List<AlertOut> alertOuts = null ;
	
	//transient List<ValAlert> alertOuts = null ;
	
	public AlertHandler()
	{
		this.id = CompressUUID.createNewId() ;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return this.name ;
	}

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public String getTitle()
	{
		return title;
	}

	public String getDesc()
	{
		return desc;
	}
	
	public int getLevel()
	{
		return this.alertLevel ;
	}
	
	public boolean isTriggerEn()
	{
		return this.bTriggerEn ;
	}
	
	public String getTriggerColor()
	{
		if(this.triggerColor==null)
			return "" ;
		
		return this.triggerColor ;
	}
	
	public boolean isReleaseEn()
	{
		return this.bReleaseEn ;
	}
	
	public String getReleaseColor()
	{
		if(this.releaseColor==null)
			return "" ;
		return this.releaseColor ;
	}
	
	public boolean isInnerRecord()
	{
		return this.bInnerRecord ;
	}
	
	public int getInnerRecordDays()
	{
		return this.innerRecordDays ;
	}
	
	public boolean isOuterRecord()
	{
		return this.bOuterRecord ;
	}
	
	public String getOuterRecordSor()
	{
		if(this.outerRecordSor==null)
			return "" ;
		return this.outerRecordSor ;
	}
	
	public int getOuterRecordDays()
	{
		return this.outerRecordDays ;
	}
	
	public List<String> getAlertUids()
	{
		return this.alertUids ;
	}
	
	public boolean checkAlertUid(String alert_uid)
	{
		if(this.alertUids==null)
			return false;
		return this.alertUids.contains(alert_uid);
	}
	
	public boolean checkValAlertRelated(ValAlert va)
	{
		return this.checkAlertUid(va.getUid()) ;
	}

	public List<String> getAlertOutIds()
	{
		return this.alertOutIds ;
	}
	
	public synchronized List<AlertOut> getAlertOuts()
	{
		if(alertOuts!=null)
			return alertOuts;
		
		AlertManager amgr = AlertManager.getInstance(this.prj.getId()) ;
		ArrayList<AlertOut> aos = new ArrayList<>() ;
		if(this.alertOutIds!=null)
		{
			for(String outid:this.alertOutIds)
			{
				AlertOut ao = amgr.getOutById(outid) ;
				if(ao!=null)
					aos.add(ao) ;
			}
		}
		this.alertOuts = aos ;
		return aos ;
	}
	
	public synchronized void clearCache()
	{
		alertOuts = null ;
	}
	
	public synchronized void setInOutIds(String alert_uids,String out_ids)
	{
		this.alertUids = Convert.splitStrWith(alert_uids, ",") ;
		this.alertOutIds = Convert.splitStrWith(out_ids, ",") ;
		clearCache() ;
	}
	
	public JSONObject toJO() //throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		List<AlertOut> aos = getAlertOuts() ;
		JSONArray jarr = new JSONArray() ;
		for(AlertOut ao:aos)
		{
			jarr.put(ao.toJO()) ;
		}
		jo.put("outs", jarr) ;
		return jo ;
	}
	
	public JSONObject RT_toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id);
		jo.putOpt("n", this.name) ;
		jo.putOpt("t", this.title) ;
		jo.put("lvl", this.alertLevel);
		jo.put("trigger_en", this.bTriggerEn);
		jo.put("release_en", this.bReleaseEn);
		jo.putOpt("trigger_c", this.triggerColor) ;
		jo.putOpt("release_c", this.releaseColor) ;
		return jo ;
	}
	
	private transient HashMap<String,AlertItem> rt_vaId2ai = new HashMap<>() ;
	
	
	public List<AlertItem> RT_getAlertItems()
	{
		ArrayList<AlertItem> rets = new ArrayList<>(rt_vaId2ai.size()) ;
		synchronized(this)
		{
			rets.addAll(rt_vaId2ai.values()) ;
		}
		return rets;
	}
	
	synchronized void RT_processSelfSyn(AlertItem ai)
	{
		ValAlert va = ai.getValAlert() ;
		String vaid = va.getId() ;
		if(ai.bReleased)
			rt_vaId2ai.remove(vaid) ;
		else if(ai.bTriggerd)
			rt_vaId2ai.put(vaid, ai) ;
	}
	
	void RT_processOutAsyn(AlertItem ai)
	{
		//ValAlert va = ai.getValAlert() ;
		
		List<AlertOut> aos = getAlertOuts() ;
		if(aos==null||aos.size()<=0)
			return ;
		ai.setHandler(this);
		for(AlertOut ao:aos)
		{
			try
			{
				ao.sendAlert(ai.getUID(),ai);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	// record alert data

	JavaTableInfo recordJTI = null ;
	
	DBConnPool innerPool = null ;
	DataTable innerDT = null ;
	
	private transient long innerLastDelDT = -1 ; 
	
	DBConnPool outerPool = null ;
	DataTable outerDT = null ;
	
	private transient long outerLastDelDT = -1 ; 
	
	public static final int MAX_ID_LEN =  20 ;
	
//	private static final String[] COL_NAMES = new String[] {
//		"AutoId","TriggerDT","ReleaseDT","Tag","Type","Value","Level","Prompt"
//	} ;
	
	private static final String[] COL_NAMES_INSERT = new String[] {
			"AutoId","TriggerDT","Handler","Tag","Type","Value","Level","Prompt"
		} ;
	
	void RT_processRecordAsyn(AlertItem ai)// throws Exception
	{
		try
		{
			if(this.bInnerRecord)
			{
				if(innerPool!=null&&innerDT!=null)
				{
					RT_recordAlertItem(ai,innerPool,innerDT,this.innerRecordDays,false);
				}
			}
			
			if(this.bOuterRecord)
			{
				if(outerPool!=null&&outerDT!=null)
					RT_recordAlertItem(ai,outerPool,outerDT,this.outerRecordDays,true) ;
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	//private HashMap<String,String> valalert2lastid = new HashMap<>() ;
	
	private void RT_recordAlertItem(AlertItem ai,DBConnPool cp,DataTable dt,int keep_days,boolean b_outer)
		throws Exception
	{
		DataRow dr = dt.createNewRow() ;
		ValAlert va = ai.getValAlert() ;
		//AlertHandler ah = ai.getHandler() ;
		String row_id = va.RT_get_trigger_uid();//.getId() ;
		if(ai.bTriggerd)
		{
			dr.putValue("AutoId",row_id) ;
			dr.putValue("Tag", ai.getTag().getNodePathCxt());
			dr.putValue("TriggerDT", new Date(ai.getTriggerDT()));
			dr.putValue("Handler", this.getName());
			dr.putValue("Type", va.getAlertTitle());
			dr.putValue("Value", ai.getCurVal());
			dr.putValue("Level", this.getLevel());
			dr.putValue("Prompt",va.getAlertPrompt());
			
			Connection conn =null;
			try
			{
				conn = cp.getConnection() ;
				//System.out.println(" insert id=="+row_id) ;
				dr.doInsertDB(conn, recordJTI.getTableName(), COL_NAMES_INSERT) ;
				
				if(b_outer)
				{
					if(delOld(conn,recordJTI.getTableName(),"TriggerDT",keep_days,outerLastDelDT))
						outerLastDelDT = System.currentTimeMillis() ;
				}
				else
				{
					if(delOld(conn,recordJTI.getTableName(),"TriggerDT",keep_days,innerLastDelDT))
						innerLastDelDT = System.currentTimeMillis() ;
				}
			}
			finally
			{
				if(conn!=null)
					cp.free(conn);
			}
		}
		else if(ai.bReleased)
		{
			dr.putValue("AutoId",row_id) ;
			dr.putValue("ReleaseDT", new Date(ai.getReleaseDT()));
			Connection conn =null;
			try
			{
				conn = cp.getConnection() ;
				dr.doUpdateDB(conn, recordJTI.getTableName(), "AutoId", new String[] {"ReleaseDT"});
			}
			finally
			{
				if(conn!=null)
					cp.free(conn);
			}
		}
	}
	
	//private long lastDelDT = -1 ;
	
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
	
	
	
//	private static JavaTableInfo getJavaTableInfo()
//	{
//		if(tableInfo!=null)
//			return tableInfo;
//		
//		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
//		JavaColumnInfo pkcol = null;
//		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();
//
//		pkcol = new JavaColumnInfo("AutoId",true, XmlVal.XmlValType.vt_string, 30,
//				false, false,"", false,-1,"",false,false);
//		
//		
//		
//		norcols.add(new JavaColumnInfo("TriggerDT",false, XmlVal.XmlValType.vt_date, -1,
//				true, false,"TriggerDT_idx", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("ReleaseDT",false, XmlVal.XmlValType.vt_date, -1,
//				false, false,"", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("Handler",false, XmlVal.XmlValType.vt_string, 40,
//				false, false,"", false,-1, "",false,false));
////		norcols.add(new JavaColumnInfo(this.getColValid(),false, XmlVal.XmlValType.vt_int16, 2,
////				false, false,"", false,-1, "",false,false));
////		
//		int tag_maxlen = 20 ;
//		for(UATag tag:this.prj.listTagsAll())
//		{
//			String np = tag.getNodePath() ;
//			int len = np.length() ;
//			if(len>tag_maxlen)
//				tag_maxlen = len ;
//		}
//		norcols.add(new JavaColumnInfo("Tag",false, XmlVal.XmlValType.vt_string, tag_maxlen,
//				false, false,"", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("Type",false, XmlVal.XmlValType.vt_string, 20,
//				false, false,"", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("Value",false, XmlVal.XmlValType.vt_string, 20,
//				false, false,"", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("Level",false, XmlVal.XmlValType.vt_int16, 2,
//				false, false,"", false,-1, "",false,false));
//		
//		norcols.add(new JavaColumnInfo("Prompt",false, XmlVal.XmlValType.vt_string, 200,
//				false, false,"", false,-1, "",false,false));
////		norcols.add(new JavaColumnInfo(this.getColAlertInf(),false, XmlVal.XmlValType.vt_string, MAX_ALERT_INF_LEN,
////				false, false,"", false,-1, "",false,false));
//
//		String tablename = calTableName() ;
//		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, fks);
//		return tableInfo;
//	}
	
	
	
	/**
	 * called by AlertManager RT_init()
	 */
	void RT_initHandler()
	{
		AlertManager amgr = AlertManager.getInstance(this.prj.getId()) ;
		recordJTI = amgr.getAlertsTableInfo() ;
		if(this.bInnerRecord)
		{
			try
			{// TODO may move to AlertManager
				SourceJDBC innersor = StoreManager.getInnerSource(prj.getName()) ;
				innerPool = innersor.getConnPool() ;
				innerDT = DBUtil.createOrUpTable(innerPool,recordJTI,true) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
		if(this.bOuterRecord && Convert.isNotNullEmpty(this.outerRecordSor))
		{
			try
			{
				SourceJDBC outersor = (SourceJDBC)StoreManager.getSourceByName(outerRecordSor) ;
				outerPool = outersor.getConnPool() ;
				outerDT = DBUtil.createOrUpTable(outerPool,recordJTI,true) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	
	
//	private JsProp jsP = null;
//
//	@Override
//	public JsProp toJsProp()
//	{
//		if(jsP!=null)
//			return jsP ;
//		jsP = new JsProp("$alert",this,null,true,"AlertItem","Alert item in input env") ;
//		return jsP;
//	}
	
	@Override
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		switch(key)
		{
		case "id":
			return this.id ;
		case "name":
			return this.name ;
		case "title":
			return this.title ;
		case "trigger_color":
			return this.triggerColor ;
		case "release_color":
			return this.releaseColor ;
		case "level":
			return this.alertLevel ;
		}
		return null;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("id",null,String.class,false,"Handler ID","")) ;
		ss.add(new JsProp("name",null,String.class,false,"Handler Name","Handler unique name in project")) ;
		ss.add(new JsProp("title",null,String.class,false,"Handler Title","")) ;
		ss.add(new JsProp("trigger_color",null,String.class,false,"Trigger Color","")) ;
		ss.add(new JsProp("release_color",null,String.class,false,"Release Color","")) ;
		ss.add(new JsProp("level",null,Integer.class,false,"Alert Level","")) ;
		return ss ;
	}
}
