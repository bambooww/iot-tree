package org.iottree.core.msgnet.nodes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.IMNTagFilter;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.modules.RelationalDB_M;
import org.iottree.core.msgnet.modules.RelationalDB_Table;
import org.iottree.core.msgnet.modules.RelationalDB_JSON2TB.ColDef;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_Measurement;
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

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

/**
 * based on time series style,to record data to relational db.
 * 
 * 1,one tag TS data in one table
 * 
 * @author jason.zhu
 *
 */
public class NM_TagTSRec2RDB extends MNNodeMid
{
	/**
	 * fixed selected tagpath under cxtNodePath
	 */
	String tagId;

	int tagValMaxLen = -1;

	long minRecIntvMS = -1;

	public NM_TagTSRec2RDB()
	{
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}

	private static HashMap<Integer, OutResDef> OUT2RES = new HashMap<>();
	static
	{
		OUT2RES.put(1, new OutResDef(RelationalDB_Table.class, false));
	}

	@Override
	public Map<Integer, OutResDef> getOut2Res()
	{
		return OUT2RES;
	}

	public static final String TP = "tag_tsrec_rdb";

	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_tsrec_rdb");
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

	public String getTagId()
	{
		return this.tagId;
	}

	public UATag getTag()
	{
		if (Convert.isNullOrEmpty(this.tagId))
			return null;

		UAPrj uprj = this.getPrj();
		if (uprj == null)
			return null;
		return uprj.findTagById(this.tagId);// .getTagByPath(this.tagPath) ;
	}

	public int getTagValMaxLen()
	{
		return this.tagValMaxLen;
	}

	public long getMinRecIntvMS()
	{
		return this.minRecIntvMS;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (Convert.isNullOrEmpty(this.tagId))
		{
			failedr.append("no tag set");
			return false;
		}
		UATag tag = this.getTag();
		if (tag == null)
		{
			failedr.append("tag is null");
			return false;
		}
		ValTP vtp = tag.getValTp();
		if (vtp == ValTP.vt_str)
		{
			if (this.tagValMaxLen <= 0)
			{
				failedr.append("tag string value must has max len set");
				return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("tag_id", this.tagId);
		jo.put("tag_val_maxlen", this.tagValMaxLen);
		jo.put("min_rec_intv", this.minRecIntvMS);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.tagId = jo.optString("tag_id");
		this.tagValMaxLen = jo.optInt("tag_val_maxlen", -1);
		this.minRecIntvMS = jo.optLong("min_rec_intv", -1);

		clearCache();
	}
	

	@Override
	public String getPmTitle()
	{
		UATag tag = this.getTag() ;
		if(tag==null)
			return null ;
		return tag.getNodeCxtPathInPrj()+" ["+tag.getValTp()+"]" ;
	}

	private UAPrj getPrj()
	{
		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
		if (mnc == null || !(mnc instanceof UAPrj))
			return null;

		return (UAPrj) mnc;
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

	private synchronized void clearCache()
	{
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
		UATag tag = this.getTag();
		if (tag == null)
		{
			failedr.append("no tag set or not found");
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

		XmlVal.XmlValType xvt = tag.getValTp().toXVT();
		norcols.add(new JavaColumnInfo("val", false, xvt, this.tagValMaxLen, false, false, null, false, -1, "", false,
				false));

		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, null);

		return tableInfo;
	}

	private boolean insertToDB(long ts, Object v, StringBuilder failedr) throws SQLException
	{
		JavaTableInfo jti = getTableInfo(failedr);
		if (jti == null)
			return false;

		DBConnPool cp = this.RT_getConnPool(failedr);

		Connection conn = null;
		try
		{
			conn = cp.getConnection();
			StringBuilder insertsql = new StringBuilder();
			insertsql.append("insert into ").append(jti.getTableName()).append("(_ts,val) values (?,?)");

			try (PreparedStatement ps = conn.prepareStatement(insertsql.toString());)
			{
				ps.setLong(1, ts);
				ps.setObject(2, v);

				return ps.executeUpdate() > 0;
			}
		}
		finally
		{
			if (conn != null)
				cp.free(conn);
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		RelationalDB_Table rdb_tb = getRelationalDB_Table();
		if (rdb_tb == null)
		{
			RT_DEBUG_ERR.fire(TP,
					"No RelationalDB_Table found,it may has no DB_Table res node set from RelationalDB_M");
			return null;
		}
		String tablen = rdb_tb.getTableName();
		if (Convert.isNullOrEmpty(tablen))
		{
			RT_DEBUG_ERR.fire(TP, "RelationalDB_Table has no table name set");
			return null;
		}
		RelationalDB_M dbm = getRelationalDB_M();
		if (dbm == null)
		{
			RT_DEBUG_ERR.fire(TP, "No RelationalDB module found,it may has no table res node set from RelationalDB_M");
			return null;
		}

		UATag tag = this.getTag();
		if (tag == null)
		{
			RT_DEBUG_ERR.fire(TP, "No Tag found");
			return null;
		}

		UAVal val = tag.RT_getVal();
		if (!val.isValid())
			return null;

		Object v = val.getObjVal();
		if (v == null)
			return null;

		long ts = val.getValDT();
		StringBuilder failedr = new StringBuilder();
		if (!insertToDB(ts, v, failedr))
		{
			RT_DEBUG_ERR.fire(TP, failedr.toString());
			return null;
		}
		
		RT_DEBUG_ERR.clear(TP);
		MNMsg m = new MNMsg().asPayload(new JSONObject().put("_ts", ts).put("val", v));
		return RTOut.createOutIdx().asIdxMsg(0, m);
	}

	// public List<Point> RT_getRTDataToInfluxPt(String tablen)
	// {
	// UAPrj uprj = this.getPrj() ;
	// if(uprj==null)
	// return null;
	//
	// ArrayList<Point> rets = new ArrayList<>() ;
	//
	// if(bAllNorTag)
	// {
	// for(UATag nortag:uprj.listTagsNorAll())
	// {
	// Point pt = calTagPoint(tablen,nortag,nortag.getNodeCxtPathInPrj()) ;
	// if(pt==null)
	// continue ;
	// rets.add(pt) ;
	// }
	//
	// if(this.tagPaths!=null||this.tagPaths.size()>0)
	// { //only sys tag
	// for(String tagp:this.tagPaths)
	// {
	// UATag tag = uprj.getTagByPath(tagp) ;
	// if(tag==null)
	// continue ;
	// if(!tag.isSysTag())
	// continue ;
	// Point pt = calTagPoint(tablen,tag,tagp) ;
	// if(pt==null)
	// continue ;
	// rets.add(pt) ;
	// }
	// }
	// }
	// else
	// {
	// if(this.tagPaths==null||this.tagPaths.size()<=0)
	// return null ;
	//
	// for(String tagp:this.tagPaths)
	// {
	// UATag tag = uprj.getTagByPath(tagp) ;
	// if(tag==null)
	// continue ;
	//
	// Point pt = calTagPoint(tablen,tag,tagp) ;
	// if(pt==null)
	// continue ;
	// rets.add(pt) ;
	// }
	// }
	//
	// return rets ;
	// }

	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder();

		divsb.append("<div class=\"rt_blk\" style='position:relative;'><button onclick=\"mn_fire_node_evt('"
				+ this.getId() + "','create_tb')\">Create Table</button>");
		divsb.append("</div>");
		divblks.add(new DivBlk("tag_alert_trigger", divsb.toString()));

		super.RT_renderDiv(divblks);
	}

	@Override
	public void RT_onRenderDivEvent(String evtn, StringBuilder retmsg)
	{
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
	
}