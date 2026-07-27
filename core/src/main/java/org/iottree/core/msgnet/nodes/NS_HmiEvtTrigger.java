package org.iottree.core.msgnet.nodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAHmi;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.modules.RelationalDB_M;
import org.iottree.core.msgnet.modules.RelationalDB_Table;
import org.iottree.core.msgnet.nodes.NS_TagChgTrigger.ChgTP;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class NS_HmiEvtTrigger extends MNNodeStart
{
	static ILogger log = LoggerManager.getLogger(NS_HmiEvtTrigger.class) ;

	/**
	 * if true,then hmi client drawitem event bind which has no run name will be ignored
	 */
	boolean ignoreNoRunName = false;
	
	@Override
	public int getOutNum()
	{
		return 2;
	}

	@Override
	public String getTP()
	{
		return "hmi_evt";
	}

	@Override
	public String getTPTitle()
	{
		return g("hmi_evt");
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "\\uf0a4";
	}
	
	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
	static
	{
		OUT2RES.put(1,new OutResDef(RelationalDB_Table.class,false)) ;
	}
	
	@Override
	public Map<Integer,OutResDef> getOut2Res()
	{
		return OUT2RES ;
	}
	
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject ret = new JSONObject().put("ignore_no_runn", this.ignoreNoRunName) ;
		return ret ;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.ignoreNoRunName = jo.optBoolean("ignore_no_runn",false) ;
		//clearCache();
	}

	
	public RelationalDB_Table getRelationalDB_Table()
	{
		MNNodeRes noderes = this.getOutResNode(1);
		if (noderes == null)
			return null;
		if (!(noderes instanceof RelationalDB_Table))
		{
			return null;
		}

		return (RelationalDB_Table) noderes;
	}

	private RelationalDB_M getRelationalDB_M()
	{
		RelationalDB_Table mt = getRelationalDB_Table();
		if (mt == null)
			return null;
		return (RelationalDB_M) mt.getOwnRelatedModule();
	}

	private JavaTableInfo tableInfo = null;
	private DBConnPool connPool = null;
	private DataTable dataTable = null;

	protected synchronized void clearCache()
	{
		super.clearCache();
		tableInfo = null;
		connPool = null;
		dataTable = null;
	}

	private synchronized DBConnPool RT_getConnPool(StringBuilder failedr)
	{
		if (connPool != null)
			return connPool;

		RelationalDB_Table tb = getRelationalDB_Table();
		if (tb == null)
		{
			failedr.append("no related using RelationalDB_Table");
			return null;
		}
		connPool = tb.RT_getConnPool();
		return connPool;
	}

	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder();

		divsb.append("<div class=\"rt_blk\" style='position:relative;'><button onclick=\"mn_fire_node_evt('"
				+ this.getId() + "','create_tb')\">Create Table</button>");
		divsb.append("</div>");
		divblks.add(new DivBlk("hmi_evt_trigger", divsb.toString()));

		super.RT_renderDiv(divblks);
	}
	

	@Override
	public void RT_onRenderDivEvent(String evtn,JSONObject evt_pm, StringBuilder retmsg)
	{
		super.RT_onRenderDivEvent(evtn,evt_pm,retmsg);
		try
		{
			switch (evtn)
			{
			case "create_tb":
				JavaTableInfo jti = getTableInfo(retmsg);
				DBConnPool cp = RT_getConnPool(retmsg);
				if (jti == null || cp == null)
				{
					// retmsg.append("no Table or ConnPool found,you may not set
					// db resource node") ;
					return;
				}

				dataTable = DBUtil.createOrUpTable(cp, jti, true);
				if (dataTable != null)
					retmsg.append("create table ok");
				else
					retmsg.append("create table failed");
				return;
			}
		}
		catch ( Exception ee)
		{
			ee.printStackTrace();
			retmsg.append("Err:" + ee.getMessage());
		}
	}

	public boolean RT_fireHmiEvent(UAHmi.ClientEvent ce)
	{
		if(this.ignoreNoRunName)
		{
			if(Convert.isNullOrEmpty(ce.getRunName()))
				return false;
		}
		JSONObject jo = ce.toJO() ;
		long dt = System.currentTimeMillis();
		jo.put("_dt", dt) ;
		MNMsg m = new MNMsg().asPayloadJO(jo) ;
		this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
		
		try
		{
			StringBuilder failedr = new StringBuilder() ;
			if(!insertToDB(dt, ce, failedr))
			{
				if(failedr.length()>0)
					RT_DEBUG_ERR.fire("hmi_evt_trigger", failedr.toString());
			}
		}
		catch(Exception ee)
		{
			RT_DEBUG_ERR.fire("hmi_evt_trigger", "RT_fireHmiEvent", ee);
		}
		
		return true;
	}
	

	public JavaTableInfo getTableInfo(StringBuilder failedr) // throws Exception
	{
		if (tableInfo != null)
			return tableInfo;

		if (!isParamReady(failedr))
			return null;

		RelationalDB_Table tb = getRelationalDB_Table();
		if (tb == null)
		{
			failedr.append("no related RelationalDB_Table");
			return null;
		}
		
		UAPrj prj = (UAPrj) this.getBelongTo().getContainer();

		String tablename = tb.getTableName();
		if (Convert.isNullOrEmpty(tablename))
		{
			failedr.append("RelationalDB_Table has no table name");
			return null;
		}

		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		// ArrayList<JavaForeignKeyInfo> fks = new
		// ArrayList<JavaForeignKeyInfo>();

		XmlVal.XmlValType xvt_pk = XmlVal.XmlValType.vt_int64;// transValTp2XVT(this.pkCol.valTP)
																// ;
		if (xvt_pk == null)
		{
			failedr.append("pk has no column type found");
			return null;
		}
		pkcol = new JavaColumnInfo("_ts", true, xvt_pk, -1, false, false, "", false, -1, "", false, false);

		norcols.add(new JavaColumnInfo("prjn", false, XmlVal.XmlValType.vt_string, 50, false, false, null, false, -1, "", false,false));
		norcols.add(new JavaColumnInfo("hmi", false, XmlVal.XmlValType.vt_string, 100, false, false, null, false, -1, "", false,false));
		norcols.add(new JavaColumnInfo("user", false, XmlVal.XmlValType.vt_string, 30, false, false, null, false, -1, "", false,false));
		norcols.add(new JavaColumnInfo("diid", false, XmlVal.XmlValType.vt_string, 30, false, false, null, false, -1, "", false,false));
		norcols.add(new JavaColumnInfo("run_name", false, XmlVal.XmlValType.vt_string, 100, false, false, null, false, -1, "", false,false));
		norcols.add(new JavaColumnInfo("val", false, XmlVal.XmlValType.vt_string, 100, false, false, null, false, -1, "", false,false));
		
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, null);

		return tableInfo;
	}
	
	private boolean insertToDB(long ts, UAHmi.ClientEvent ce, StringBuilder failedr) throws SQLException
	{
		JavaTableInfo jti = getTableInfo(failedr);
		if (jti == null)
			return false;

		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return false;
		DBConnPool cp = this.RT_getConnPool(failedr);

		Connection conn = null;
		try
		{
			conn = cp.getConnection();
			StringBuilder insertsql = new StringBuilder();
			insertsql.append("insert into ").append(jti.getTableName()).append("(_ts,prjn,hmi,user,diid,run_name,val) values (?,?,?,?,?,?,?)");

			try (PreparedStatement ps = conn.prepareStatement(insertsql.toString());)
			{
				ps.setLong(1,ts);
				ps.setString(2, prj.getName());
				ps.setString(3, ce.getHmiPath());
				ps.setString(4, ce.getUserName());
				ps.setString(5, ce.getDIId());
				ps.setString(6, ce.getRunName());
				ps.setString(7, ce.getValStr());
				
				return ps.executeUpdate() > 0;
			}
		}
		finally
		{
			if (conn != null)
				cp.free(conn);
		}
	}
}
