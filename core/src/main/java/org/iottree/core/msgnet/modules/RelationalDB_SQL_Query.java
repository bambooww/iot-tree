package org.iottree.core.msgnet.modules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.IDBSelectCallback;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Lan;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationalDB_SQL_Query extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(RelationalDB_SQL_Query.class) ;
	
	public static enum OutTP
	{
		msg_per_row_jo,
		//msg_firstrow_jo,
		multi_row_jarr;
		//multi_row_page;
		
		public String getTitle()
		{
			return lan.g("sql_que_"+this.name()) ;
		}
	}
	
	
	OutTP outTP = OutTP.msg_per_row_jo ;
	
	/**
	 * max row per msg,so it will out multi msg for multi rows
	 */
	int maxRowNum = 100 ;
	
	boolean joTimeVal2MS = false;
	
	boolean joCol2LowCase = false; //default UpperCase
	
	boolean joIgnoreNull = true; 
	
	@Override
	public String getTP()
	{
		return "r_db_sql_query";
	}
	
	@Override
	public String getTPTitle()
	{
		return "SQL Select";
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
	
	@Override
	public String getOutColor(int idx)
	{
		if(idx==1)
			return "red" ;
		return null ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("max_row", this.maxRowNum) ;
		jo.put("out_tp", outTP.name()) ;
		
		jo.put("jo_time2ms", joTimeVal2MS) ;
		jo.put("jo_col2lowcase", joCol2LowCase) ;
		jo.put("jo_ignorenull", joIgnoreNull ) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.maxRowNum = jo.optInt("max_row",100) ;
		if(this.maxRowNum<=0)
			this.maxRowNum  = 100 ;
		this.outTP = OutTP.valueOf(jo.optString("out_tp","msg_per_row_jo")) ;
		if(this.outTP==null)
			this.outTP = OutTP.msg_per_row_jo ;
		
		joTimeVal2MS = jo.optBoolean("jo_time2ms", false) ;
		joCol2LowCase = jo.optBoolean("jo_col2lowcase", false) ;
		joIgnoreNull = jo.optBoolean("jo_ignorenull",  true) ;
	}
	
	private DBConnPool RT_getConnPool()
	{
		RelationalDB_M m = (RelationalDB_M)this.getOwnRelatedModule() ;
		if(m==null)
			return null ;
		SourceJDBC sorjdbc = m.getSourceJDBC() ;
		if(sorjdbc==null)
			return null ;
		return sorjdbc.getConnPool() ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		Object pld = msg.getPayload();
		if(pld==null)
			return null;
		
		int rowidx = 0 ;
		int count = this.maxRowNum ;
		String sql = null;
		if(pld instanceof String)
		{
			sql = (String)pld ;
			if(sql==null||(sql=sql.trim()).equals(""))
				return null ;
		}
		else if(pld instanceof JSONObject)
		{
			JSONObject jo = (JSONObject)pld; 
			rowidx = jo.optInt("rowidx",0) ;
			count = jo.optInt("count",maxRowNum) ;
			
			if(count>this.maxRowNum || count<=0)
				count = this.maxRowNum ;
			sql = jo.optString("sql") ;
			if(sql==null||(sql=sql.trim()).equals(""))
				return null ;
		}
		
		DBConnPool cp = RT_getConnPool() ;
		if(cp==null)
			return null ;
		
		Connection conn = null;
		try
		{
			conn = cp.getConnection() ;
			runSqlQuery(conn,rowidx,count,sql) ;
			
			//RT_DEBUG_ERR.clear("sql_up");
			//return RTOut.createOutIdx().asIdxMsg(0, outm) ;
			return null ;
		}
		catch(Exception ee)
		{
			RT_DEBUG_ERR.fire("sql_query", sql, ee);
			MNMsg outm = new MNMsg().asPayload(ee.getMessage()) ;
			return RTOut.createOutIdx().asIdxMsg(1, outm) ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	private void runSqlQuery(Connection conn,int rowidx,int count,String sql) throws Exception
	{
		try(Statement st = conn.createStatement();)
		{
			try(ResultSet rs = st.executeQuery(sql);)
			{
				switch(this.outTP)
				{
				case msg_per_row_jo:
					doRowJoOut(rs,rowidx,count) ;
					return ;
				case multi_row_jarr:
					doRowJArrOut(rs,rowidx,count) ;
					return ;
				default:
					doRowJoOut(rs,rowidx,count) ;
					return ;
				}
			}
		}
	}
	
	private void doRowJoOut(ResultSet rs,int rowidx,int count) throws Exception
	{
		DBResult.transResultSetToDataTable(rs,"t1",rowidx, count,new IDBSelectCallback() {

			@Override
			public boolean onFindDataTable(int tableidx, DataTable dt) throws Exception
			{
				return true;
			}

			@Override
			public boolean onFindDataRow(int tableidx, DataTable dt, int rowidx, DataRow dr) throws Exception
			{
				JSONObject jo = dr.toJO(joIgnoreNull,joTimeVal2MS,joCol2LowCase) ;
				MNMsg m = new MNMsg().asPayload(jo) ;
				RelationalDB_SQL_Query.this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
				return true; //false will stop query
			}}) ;
	}
	
	private void doRowJArrOut(ResultSet rs,int rowidx,int count) throws Exception
	{
		JSONArray jarr = new JSONArray() ;
		DBResult.transResultSetToDataTable(rs,"t1",rowidx, count,new IDBSelectCallback() {

			@Override
			public boolean onFindDataTable(int tableidx, DataTable dt) throws Exception
			{
				return true;
			}

			@Override
			public boolean onFindDataRow(int tableidx, DataTable dt, int rowidx, DataRow dr) throws Exception
			{
				JSONObject jo = dr.toJO(joIgnoreNull,joTimeVal2MS,joCol2LowCase) ;
				jarr.put(jo) ;
				return true; //false will stop query
			}}) ;
		
		MNMsg m = new MNMsg().asPayload(jarr) ;
		RelationalDB_SQL_Query.this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
	}

	@Override
	public String RT_getInTitle()
	{
		return "Input SQL String or page query like \r\n {\"rowidx\":0,\"count\":10,\"sql\":\"select * from TB1 ...\"}";
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return "Insert or Update row affected" ;
		else
			return "error out" ;
	}
	
	
}
