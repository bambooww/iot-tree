package org.iottree.core.msgnet.modules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationalDB_JSON2TB extends MNNodeMid
{
	public static final String COL_REC_MS = "_rec_ms" ;
	
	private static XmlVal.XmlValType transValTp2XVT(ValTP vtp)
	{
		switch(vtp)
		{
		case vt_str:
			return XmlVal.XmlValType.vt_string;
		case vt_int32:
			return XmlVal.XmlValType.vt_int32;
		case vt_float:
			return XmlVal.XmlValType.vt_float;
		case vt_bool:
			return XmlVal.XmlValType.vt_bool;
		case vt_int16:
			return XmlVal.XmlValType.vt_int16;
		case vt_int64:
			return XmlVal.XmlValType.vt_int64;
		case vt_double:
			return XmlVal.XmlValType.vt_double;
		case vt_date:
			return XmlVal.XmlValType.vt_date;
		default:
			return null;
		}
	}
	
	/**
	 * db column definition
	 * 
	 * @author jason.zhu
	 *
	 */
	public static class ColDef
	{
		public String jsonPN ;
		
		public boolean bAuto = false;
		
		public String colName ;
		
		public String title ;
		
		public ValTP valTP ;
		
		public int maxLen =-1;
		
		public boolean hasIdx = false;
		
		public boolean isNumValTP()
		{
			if(valTP==null)
				return false;
			return valTP.isNumberVT() ;
		}
		
		public String getShowTitle()
		{
			if(Convert.isNullOrEmpty(this.title))
				return "["+colName+"]" ;
			return this.title+"["+colName+"]" ;
		}
		
		public boolean isPkValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(jsonPN) && !bAuto)
			{
				failedr.append("pk column must has json prop or auto set") ;
				return false;
			}
			if(bAuto)
			{
				if(this.valTP!=ValTP.vt_str)
				{
					failedr.append("auto pk column must string value type") ;
					return false;
				}
			}
			if(Convert.isNullOrEmpty(colName))
			{
				failedr.append("pk column must has name set") ;
				return false;
			}
			return true;
		}
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(jsonPN))
			{
				failedr.append("column must has json prop set") ;
				return false;
			}
			if(Convert.isNullOrEmpty(colName))
			{
				failedr.append("column must has name set") ;
				return false;
			}
			
			if(valTP==null)
			{
				failedr.append("column must has value type") ;
				return false;
			}
			if(valTP==ValTP.vt_str && maxLen<=0)
			{
				failedr.append("string column must has max length") ;
				return false;
			}
				
			return true ;
		}
		
		/**
		 * transfer input value to db column value,which make fit to sql
		 * @param inpv
		 * @return
		 */
		public Object transColVal(Object inpv,StringBuilder failedr)
		{
			if(inpv==null)
				return null ;
			
			Number num_v = null ;
			if(this.valTP.isNumberVT())
			{
				if(!(inpv instanceof Number))
				{
					failedr.append("value is not number") ;
					return null;
				}
				
				num_v = (Number)inpv;
			}
			
			switch(this.valTP)
			{
			case vt_str:
				if(!(inpv instanceof String))
				{
					failedr.append("value is not string") ;
					return null;
				}
				int ll = ((String)inpv).length() ;
				if(ll>this.maxLen)
				{
					failedr.append("string value len "+ll+" > max_len "+this.maxLen) ;
					return null;
				}
				return inpv;
			case vt_int32:
				return num_v.intValue() ;
			case vt_float:
				return num_v.floatValue() ;
			case vt_int16:
				return num_v.shortValue();
			case vt_int64:
				return num_v.longValue();
			case vt_double:
				return num_v.doubleValue();
			case vt_bool:
				if(inpv instanceof Boolean)
					return inpv ;
				if(inpv instanceof Number)
					return ((Number)inpv).intValue()>0 ;
				failedr.append("value is bool or int") ;
				return null;
			case vt_date:
				if(inpv instanceof java.util.Date)
					return inpv ;
				if(inpv instanceof Long)
					return new Date((Long)inpv);
				if(inpv instanceof String)
				{
					Calendar cal = Convert.toCalendar((String)inpv) ; // yyyy-MM-dd hh:mm:ss
					if(cal==null)
					{
						failedr.append("date str must like yyyy-MM-dd hh:mm:ss") ;
						return null ;
					}
					return cal.getTime() ;
				}
				failedr.append("value is Date int32 or yyyy-MM-dd hh:mm:ss string") ;
				return null ;
			default:
				return null;
			}
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("jo_pn", this.jsonPN) ;
			jo.put("bauto", this.bAuto) ;
			jo.putOpt("col", this.colName) ;
			jo.putOpt("title",this.title) ;
			if(valTP!=null)
				jo.putOpt("val_tp",valTP.name()) ;
			jo.put("max_len",this.maxLen) ;
			jo.put("has_idx", this.hasIdx) ;
			return jo ;
		}
		
		public static ColDef fromJO(JSONObject jo)
		{
			ColDef ret = new ColDef() ;
			ret.jsonPN = jo.optString("jo_pn") ;
			ret.bAuto = jo.optBoolean("bauto",false) ;
			ret.colName = jo.optString("col",null) ;
			ret.title = jo.optString("title",null) ;
			String tp = jo.optString("val_tp") ;
			if(Convert.isNotNullEmpty(tp))
			{
				ret.valTP = ValTP.valueOf(tp) ;
			}
			ret.maxLen = jo.optInt("max_len",-1) ;
			ret.hasIdx = jo.optBoolean("has_idx") ;
			
			return ret;
		}
	}
	
	ColDef pkCol = null ;
	
	boolean pkAutoCreate = false;
	
	List<ColDef> norCols = null ;
	
	//boolean 
	
	@Override
	public String getTP()
	{
		return "r_db_json2tb";
	}
	
	@Override
	public String getTPTitle()
	{
		return "JSON To Table";
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
		return 3;
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
		if(pkCol==null)
		{
			failedr.append("no pk column set") ;
			return false;
		}
		if(this.norCols==null||this.norCols.size()<=0)
		{
			failedr.append("no normal column set") ;
			return false;
		}
		if(!pkCol.isPkValid(failedr))
			return false;
		for(ColDef cd:this.norCols)
		{
			if(!cd.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		if(pkCol!=null)
			jo.put("pk_col",pkCol.toJO()) ;
		JSONArray jarr = new JSONArray() ;
		if(norCols!=null)
		{
			for(ColDef cd:this.norCols)
			{
				jarr.put(cd.toJO()) ;
			}
		}
		jo.put("nor_cols",jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONObject jo0 = jo.optJSONObject("pk_col") ;
		if(jo0!=null)
			this.pkCol = ColDef.fromJO(jo0) ;
		
		JSONArray jarr = jo.getJSONArray("nor_cols") ;
		ArrayList<ColDef> cds = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				jo0 = jarr.getJSONObject(i) ;
				ColDef cd = ColDef.fromJO(jo0) ;
				cds.add(cd) ;
			}
		}
		this.norCols = cds ;
		
		clearCache();
	}
	
	public ColDef getPkColDef()
	{
		return this.pkCol ;
	}
	
	public List<ColDef> getNorColDefs()
	{
		return this.norCols ;
	}
	
	public ColDef getNorColDef(String col_name)
	{
		if(this.norCols==null)
			return null ;
		for(ColDef cd:this.norCols)
		{
			if(col_name.equals(cd.colName))
				return cd ;
		}
		return null ;
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
			return "Insert/Update Out" ;
		if(idx==1)
			return "Error Out" ;
		if(idx==2)
			return "Relational DB Table" ;
		
		return null ;
	}


	
	public RelationalDB_Table getUsingRDBTable()
	{
		MNNodeRes noderes = this.getOutResNode(2) ;
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
	
	public JavaTableInfo getTableInfo(StringBuilder failedr) //throws Exception
	{
		if(tableInfo!=null)
			return tableInfo;
		
		RelationalDB_Table tb = getUsingRDBTable() ;
		if(tb==null)
		{
			failedr.append("no related RelationalDB_Table") ;
			return null ;
		}
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		
		String tablename = tb.getTableName() ;
		if(Convert.isNullOrEmpty(tablename))
		{
			failedr.append("RelationalDB_Table has no table name") ;
			return null ;
		}
		
		if(!isParamReady(failedr))
			return null ;
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		//ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		XmlVal.XmlValType xvt_pk = transValTp2XVT(this.pkCol.valTP) ;
		if(xvt_pk==null)
		{
			failedr.append("pk has no column type found") ;
			return null ;
		}
		pkcol = new JavaColumnInfo(this.pkCol.colName,true,xvt_pk, this.pkCol.maxLen,
				false, false,"", false,-1,"",false,false);
		
		for(ColDef cd:this.norCols)
		{
			XmlVal.XmlValType xvt = transValTp2XVT(cd.valTP) ;
			norcols.add(new JavaColumnInfo(cd.colName,false, xvt, cd.maxLen,
					cd.hasIdx, false,cd.colName+"_idx", false,-1, "",false,false));
		}
		// add _rec_ms 记录毫秒数
		norcols.add(new JavaColumnInfo(COL_REC_MS,false, XmlVal.XmlValType.vt_int64,-1,
				true, false,COL_REC_MS+"_idx", false,-1, "",false,false));
		
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, null);
		
		return tableInfo;
	}
	
	
	
	private DataTable RT_getDataTable(StringBuilder failedr) throws Exception
	{
		if(dataTable!=null)
			return dataTable ;
		
		JavaTableInfo jti = getTableInfo(failedr) ;
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
	
	private DataRow transJO2Row(DataTable dt ,JSONObject jo,StringBuilder failedr)
	{
		DataRow dr = dt.createNewRow() ;
		
		Object v = null;
		if(this.pkCol.bAuto)
		{
			v = IdCreator.newSeqId();
		}
		else
		{
			v = jo.opt(this.pkCol.jsonPN) ;
			if(v==null||"".equals(v))
			{
				failedr.append("jo has no pk prop ="+this.pkCol.jsonPN) ;
				return null ; //
			}
			v = this.pkCol.transColVal(v, failedr);
			if(v==null || "".equals(v))
			{
				failedr.append(" @ jo."+this.pkCol.jsonPN) ;
				return null ;
			}
		}
		dr.putValue(this.pkCol.colName,v) ;
		
		for(ColDef cd:this.norCols)
		{
			v = jo.opt(cd.jsonPN) ;
			if(v==null)
			{
				continue ;
			}
			v = cd.transColVal(v, failedr);
			if(v==null)
			{
				failedr.append(" @ jo."+cd.jsonPN) ;
				return null ;
			}
			dr.putValue(cd.colName,v) ;
		}
		dr.putValue(COL_REC_MS,System.currentTimeMillis()) ;
		
		return dr;
	}
	
	private boolean RT_addOrUpdateJO(JSONObject jo,StringBuilder failedr) //,int keep_days,boolean b_outer)
		throws Exception
	{
		JavaTableInfo jti = getTableInfo(failedr) ;
		if(jti==null)
			return false;
		
		DBConnPool cp = this.RT_getConnPool(failedr) ;
		DataTable dt = this.RT_getDataTable(failedr) ;
		if(cp==null||dt==null)
			return false;
		
		DataRow dr = transJO2Row(dt ,jo, failedr) ;
		if(dr==null)
			return false;
		
		Connection conn =null;
		try
		{
			conn = cp.getConnection() ;
			int up_c = dr.doUpdateDB(conn, jti.getTableName(), this.pkCol.colName, jti.getNorColNames());
			if(up_c<=0)
				up_c = dr.doInsertDB(conn, jti.getTableName(), jti.getAllColNamesArr()) ;
			return up_c>0 ;
		}
		finally
		{
			if(conn!=null)
				cp.free(conn);
		}
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

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		JSONObject jo = msg.getPayloadJO(null) ;
		if(jo==null)
			return null;
		StringBuilder failedr = new StringBuilder() ;
		boolean b = RT_addOrUpdateJO(jo,failedr) ;
		if(!b)
		{
			MNMsg outm = new MNMsg().asPayload(failedr.toString()) ;
			return RTOut.createOutIdx().asIdxMsg(1, outm) ;
		}
		MNMsg outm = new MNMsg().asPayloadJO(jo) ;
		return RTOut.createOutIdx().asIdxMsg(0, outm) ;
	}
}
