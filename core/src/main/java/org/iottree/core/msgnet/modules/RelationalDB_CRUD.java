package org.iottree.core.msgnet.modules;

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
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.GDB;
import org.iottree.core.store.gdb.IDBSelectCallback;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.portal.Widget;
import org.iottree.portal.Widget.EventResult;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * databalse table - create read update delete
 */
public class RelationalDB_CRUD extends MNNodeMid
{
	ArrayList<JavaColumnInfo> cols = new ArrayList<JavaColumnInfo>();
	
	//JavaColumnInfo pkcol = null;
	
	@Override
	public String getTP()
	{
		return "r_db_crud";
	}
	
	@Override
	public String getTPTitle()
	{
		return "CRUD";
	}

	@Override
	public String getColor()
	{
		return "#e7b686";
	}

	@Override
	public String getIcon()
	{
		return "\\uf1c0";
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
	
	public List<JavaColumnInfo> getCols()
	{
		return this.cols;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.cols==null||this.cols.size()<=0)
		{
			failedr.append("no column set") ;
			return false;
		}
		
		String tbn = this.getRDBTableName() ;
		if(Convert.isNullOrEmpty(tbn))
		{
			failedr.append("no related DB Table node or has no table name set")  ;
			return false;
		}
		return true;
	}
	
	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		
		JSONArray jarr = new JSONArray() ;
		for(JavaColumnInfo col:this.cols)
		{
			jarr.put(col.toJO()) ;
		}
		jo.put("cols", jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("cols") ;
		ArrayList<JavaColumnInfo> cols = new ArrayList<JavaColumnInfo>();
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				JavaColumnInfo jci = new JavaColumnInfo() ;
				jci.fromJO(tmpjo);
				cols.add(jci) ;
			}
		}
		this.cols = cols ;
		clearCache();
	}
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
//		JSONObject ret_jo = ret.toJO() ;
//		return RTOut.createOutIdx().asIdxMsg(0,new MNMsg().asPayload(ret_jo));
		return null;
	}

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
		divsb.append("</div>") ;
		divblks.add(new DivBlk("r_db_crud",divsb.toString())) ;
		
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
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			retmsg.append("Err:"+ee.getMessage()) ;
		}
	}
	
	
	// ----  db ---
	
	public RelationalDB_M getOwnerRDB_M()
	{
		return (RelationalDB_M)this.getOwnRelatedModule();
	}
	
	
	
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
	
	public String getRDBTableName()
	{
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			return null ;
		}
		return tb.getTableName() ;
	}
	
	public JavaTableInfo getTableInfo(StringBuilder failedr) //throws Exception
	{
		if(tableInfo!=null)
			return tableInfo;
		
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			if(failedr!=null)
				failedr.append("no related RelationalDB_Table") ;
			return null ;
		}
		//UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		
		String tablename = tb.getTableName() ;
		if(Convert.isNullOrEmpty(tablename))
		{
			if(failedr!=null)
				failedr.append("RelationalDB_Table has no table name") ;
			return null ;
		}
		
		if(!isParamReady(failedr))
			return null ;
		
		
		JavaColumnInfo pkcol = null;
		ArrayList<JavaColumnInfo> norcols = new ArrayList<>() ;
		for(JavaColumnInfo col:this.cols)
		{
			if(pkcol==null && col.isPk())
				pkcol = col ;
			else
				norcols.add(col) ;
		}
		
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, null);
		return tableInfo;
	}
	
	/**
	 * find RelationalDB_CRUD node instance which related table name is in prj
	 * @param prj
	 * @param table_name
	 * @return
	 */
	public static RelationalDB_CRUD findNodeByTableName(UAPrj prj,String table_name)
	{
		if(Convert.isNullOrEmpty(table_name))
			return null ;
		
		for(MNNet net:prj.listMNNetsAll())
		{
			List<RelationalDB_CRUD> nds = net.findItemByTpMark(RelationalDB_CRUD.class, null) ;
			if(nds==null || nds.size()<=0)
				continue ;
			for(RelationalDB_CRUD nd:nds)
			{
				if(table_name.equals(nd.getRDBTableName()))
					return nd ;
			}
		}
		
		return null ;
	}
	
//	private DataTable RT_getDataTable(StringBuilder failedr) throws Exception
//	{
//		if(dataTable!=null)
//			return dataTable ;
//		
//		JavaTableInfo jti = getTableInfo(failedr) ;
//		if(jti==null)
//			return null ;
//
//		DBConnPool cp = RT_getConnPool(failedr) ;
//		if(cp==null)
//			return null ;
//
//		dataTable = DBUtil.createOrUpTable(cp,jti,true) ;
//		if(dataTable==null)
//			failedr.append("failed to create table") ;
//		
//		return dataTable ;
//	}
	
	public DataTable DB_queryAsTable(String[] cols,String[] opers,Object[] vals,String orderby,int pageidx,int pagesize)
		throws Exception
	{
		DataTable dt = DB_queryAsTable(cols,opers,vals,null,null,
				orderby, pageidx * pagesize, pagesize,null) ;
		dt.setPageSize(pagesize);
		dt.setPageCur(pageidx);
		return dt;
	}
	
	private DataTable DB_queryAsTable(String[] cols,String[] opers,Object[] vals,
			boolean[] null_ignores,String more_wherestr,
			String orderby, int idx, int count,IDBSelectCallback cb)
		throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			throw new Exception("getConnPool err:"+failedr.toString()) ;
		
		JavaTableInfo jti = this.getTableInfo(failedr) ;
		if(jti==null)
			throw new Exception("getTableInfo err:"+failedr.toString()) ;
		
		Connection conn = null ;
		
		try
		{
			conn = cp.getConnection() ;
			DataTable dt = GDB.queryByColOperVal(conn, jti,cols,opers,vals,
					null_ignores,more_wherestr,orderby, idx, count,true,cb) ;
			
			return dt ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	public int DB_insertRow(JSONObject row_jo) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			throw new Exception("getConnPool err:"+failedr.toString()) ;
		
		JavaTableInfo jti = this.getTableInfo(failedr) ;
		if(jti==null)
			throw new Exception("getTableInfo err:"+failedr.toString()) ;
		
		Connection conn = null ;
		
		try
		{
			conn = cp.getConnection() ;
			return GDB.insertRow(conn, jti, row_jo);
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	public int DB_updateRowByPkId(String pkid,String[] cols,JSONObject row_jo) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			throw new Exception("getConnPool err:"+failedr.toString()) ;
		
		JavaTableInfo jti = this.getTableInfo(failedr) ;
		if(jti==null)
			throw new Exception("getTableInfo err:"+failedr.toString()) ;
		
		Connection conn = null ;
		
		try
		{
			conn = cp.getConnection() ;
			return GDB.updateRowByPkId(conn, jti, pkid,cols,row_jo);
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	public int DB_deleteRowByPkId(String pkid) throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		DBConnPool cp = RT_getConnPool(failedr) ;
		if(cp==null)
			throw new Exception("getConnPool err:"+failedr.toString()) ;
		
		JavaTableInfo jti = this.getTableInfo(failedr) ;
		if(jti==null)
			throw new Exception("getTableInfo err:"+failedr.toString()) ;
		
		Connection conn = null ;
		
		try
		{
			conn = cp.getConnection() ;
			return GDB.delRowByPkId(conn, jti, pkid);
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	public class WidgetTable extends WidgetBase
	{
		String insName ;
		String insTitle ;
		
		public WidgetTable(String ins_name,String ins_title)
		{
			super(RelationalDB_CRUD.this);
			this.insName = ins_name ;
			this.insTitle = ins_title ;
		}

		@Override
		public String getTPName()
		{
			return "table";
		}

		@Override
		public String getTPTitle()
		{
			return "List Data By Table";
		}

		@Override
		public String getInsName()
		{
			return this.insName ;
		}
		
		@Override
		public String getInsTitle()
		{
			return this.insTitle ;
		}
		
		@Override
		public List<ParamDef> getParamDefs()
		{
			return null;
		}
		
		/**
		 * mvc model read data to update view
		 * @return
		 */
		@Override
		public JSONObject MODEL_readData(JSONObject view_pm)
		{
			return null ;
		}
		
		/**
		 * on view triggered event
		 * @param event
		 * @param evt_pm
		 */
		@Override
		public EventResult CTRL_onEvent(String event,JSONObject evt_pm)
		{
			switch(event)
			{
			case "add":
				return new EventResult(true,null) ;
			}
			return null ;
		}
	}
	

	@Override
	public LinkedHashMap<String,Widget> getWidgets()
	{
		LinkedHashMap<String,Widget> ret = new LinkedHashMap<>() ;
		WidgetTable wt_all = new WidgetTable("all","All Data") ;
		ret.put(wt_all.getInsName(),wt_all) ;
		return ret ;
	}
	
	
	// -- 
	private static LinkedHashMap<String,JavaTableInfo> name2jti = new LinkedHashMap<>() ;
	
	public static void registerTableInfo(String name,JavaTableInfo jti)
	{
		name2jti.put(name,jti) ;
	}
	
	public static LinkedHashMap<String,JavaTableInfo> getRegisteredName2JTI()
	{
		return name2jti ;
	}
}
