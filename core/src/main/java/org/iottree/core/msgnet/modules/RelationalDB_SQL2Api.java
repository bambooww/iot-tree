package org.iottree.core.msgnet.modules;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.annotion.outer_api;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.IDBSelectCallback;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.iottree.core.util.temp.TxtTemplate;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationalDB_SQL2Api extends MNNodeMid
{
	static Lan lan = Lan.getLangInPk(RelationalDB_SQL2Api.class) ;
	
	String paramSql = null ;
	
	JSONObject inSample = null;
	JSONObject outSample = null;
	
	int maxRowNum = 100 ;
	
	private transient TxtTemplate temp = null ;
	
	@Override
	public String getTP()
	{
		return "r_db_sql2api";
	}
	
	@Override
	public String getTPTitle()
	{
		return "SQL 2 Api";
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
	
//	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
//	static
//	{
//		OUT2RES.put(1,new OutResDef(RelationalDB_Table.class,true)) ;
//	}
//	
//	@Override
//	public Map<Integer,OutResDef> getOut2Res()
//	{
//		return OUT2RES ;
//	}
	
	public synchronized TxtTemplate getTemplate()
	{
		if(this.temp!=null)
			return this.temp ;
		if(Convert.isNullOrEmpty(paramSql))
			return null ;
		return this.temp = new TxtTemplate(0,null,paramSql) ;
	}
	
	@Override
	protected synchronized void clearCache()
	{
		super.clearCache();
		this.temp = null ;
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.paramSql))
		{
			failedr.append("no param sql set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("max_row", this.maxRowNum) ;
		jo.putOpt("pm_sql", this.paramSql) ;
		if(this.inSample!=null)
			jo.put("in_sample", this.inSample.toString(4)) ;
		if(this.outSample!=null)
			jo.put("out_sample", this.outSample.toString(4)) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.maxRowNum = jo.optInt("max_row",100) ;
		if(this.maxRowNum<=0)
			this.maxRowNum  = 100 ;
		
		this.paramSql = jo.optString("pm_sql") ;
		String ins = jo.optString("in_sample") ;
		if(Convert.isNullOrEmpty(ins))
			this.inSample = null ;
		else
			this.inSample = new JSONObject(ins) ;
		
		String outs = jo.optString("out_sample") ;
		if(Convert.isNullOrEmpty(outs))
			this.outSample = null ;
		else
			this.outSample = new JSONObject(outs) ;
		clearCache();
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
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
			pld =new JSONObject();
		
		String sql =calcSql(pld) ;
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			JSONArray jarr = doSql(pld,failedr) ;
			if(jarr!=null)
			{
				MNMsg m = new MNMsg().asPayload(jarr) ;
				return RTOut.createOutIdx().asIdxMsg(0, m);
			}
			else
			{
				RT_DEBUG_ERR.fire("sql_query", sql, failedr.toString());
				return null ;
			}
		}
		catch(Exception ee)
		{
			RT_DEBUG_ERR.fire("sql_query", sql, ee);
			MNMsg outm = new MNMsg().asPayload(ee.getMessage()) ;
			return RTOut.createOutIdx().asIdxMsg(1, outm) ;
		}
	}
	
	private String calcSql(JSONObject injo)
	{
		TxtTemplate tt = this.getTemplate() ;
		if(tt==null)
			return null ;
		return tt.getContStr(injo, false);
	}
	
	@outer_api(name="do_sql")
	public JSONArray doSql(JSONObject injo,StringBuilder failedr) throws Exception
	{
		DBConnPool cp = RT_getConnPool() ;
		if(cp==null)
		{
			failedr.append("no connpool found") ;
			return null ;
		}
		String sql =calcSql(injo) ;
		
		Connection conn = null;
		int rowidx = -1;
		
		int count = this.maxRowNum ;
		if(injo!=null)
		{
			rowidx = injo.optInt("_rowidx",-1) ;
			count = injo.optInt("_count",count) ;
		}
		
		try
		{
			conn = cp.getConnection() ;
			return runSqlTemplate(conn,rowidx,count,sql) ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
	
	@Override
	protected JSONObject[] extOuterApiIOSample(String apin)
	{
		if("do_sql".equals(apin))
		{
			return new JSONObject[] {inSample,outSample} ;
		}
		return null ;
	}
	
	
	private JSONArray runSqlTemplate(Connection conn,int rowidx,int count,String sql) throws Exception
	{
		try(Statement st = conn.createStatement();)
		{
			try(ResultSet rs = st.executeQuery(sql);)
			{
				return doRowJArrOut(rs,rowidx,count);
			}
		}
	}
	
	private JSONArray doRowJArrOut(ResultSet rs,int rowidx,int count) throws Exception
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
				JSONObject jo = dr.toJO(true,true,true) ;
				jarr.put(jo) ;
				return true; //false will stop query
			}}) ;
		
		
		return jarr ;
	}
	
	@Override
	public String RT_getInTitle()
	{
		return "any msg or input pm";
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		if(idx==0)
			return "run out" ;
		else
			return null ;
	}
	
	

}
