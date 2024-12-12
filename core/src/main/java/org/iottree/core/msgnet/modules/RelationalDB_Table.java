package org.iottree.core.msgnet.modules;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.IConnPool;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class RelationalDB_Table extends MNNodeRes
{
	String tableName = null ;
	
	String createSql = null ;
//	@Override
//	public int getOutNum()
//	{
//		return 1;
//	}

	@Override
	public String getTP()
	{
		return "r_db_tb";
	}

	@Override
	public String getTPTitle()
	{
		return "DB Table";
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
	public String getPmTitle()
	{
		return tableName;
	}
	
	public String getTableName()
	{
		return this.tableName ;
	}
	
	public String getCreateSql()
	{
		return createSql ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(tableName))
		{
			failedr.append("no table name") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("table", this.tableName) ;
		jo.putOpt("create_sql", this.createSql) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.tableName = jo.optString("table") ;
		this.createSql = jo.optString("create_sql") ;
	}

	// rt lines
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
//		if(outTP==OutTP.msg_per_ln)
//		{
//			StringBuilder divsb = new StringBuilder() ;
//			divsb.append("<div class=\"rt_blk\">Read Line CC= "+LINE_CC) ;
//			divsb.append("</div>") ;
//			divblks.add(new DivBlk("file_r_line_cc",divsb.toString())) ;
//		}
		
		super.RT_renderDiv(divblks);
	}
	
	public RelationalDB_M getOwnerRDB_M()
	{
		return (RelationalDB_M)this.getOwnRelatedModule();
	}
	
	public DBConnPool RT_getConnPool()
	{
		RelationalDB_M m = (RelationalDB_M)this.getOwnRelatedModule() ;
		if(m==null)
			return null ;
		SourceJDBC sorjdbc = m.getSourceJDBC() ;
		if(sorjdbc==null)
			return null ;
		return sorjdbc.getConnPool() ;
	}

	protected void RT_onBeforeNetRun()
	{
		StringBuilder failedr = new StringBuilder() ;
		createNoExistedTable(failedr) ;
	}
	
	private boolean createNoExistedTable(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.createSql))
		{
			failedr.append("no create sql found") ;
			return false;
		}
		DBConnPool cp = RT_getConnPool() ;
		if(cp==null)
		{
			failedr.append("no conn pool found") ;
			return false;
		}
		Connection conn = null ;
		try
		{
			conn = cp.getConnection() ;
			if(DBUtil.tableExists(conn, cp.getDatabase(), this.tableName))
				return true ;
			
			DBUtil.runSqls(conn, Arrays.asList(this.createSql));
			return true ;
		}
		catch(Exception ee)
		{
			failedr.append(ee.getMessage()) ;
			RT_DEBUG_ERR.fire("RDB_table", ee.getMessage(), ee);
			return false;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
	}
}
