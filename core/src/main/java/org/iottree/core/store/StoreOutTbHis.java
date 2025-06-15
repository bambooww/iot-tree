package org.iottree.core.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.data_val;

public class StoreOutTbHis extends StoreOut
{
	public static final String TP = "r_tb_his" ;
	
	static ILogger log = LoggerManager.getLogger(StoreOutTbHis.class) ;
	
	@data_val(param_name = "sor_n")
	String sorName = null ;
	
	@data_val(param_name = "table")
	String tableName = null ;
	
	@data_val(param_name = "col_tag")
	String colTag = "tag" ;
	
	
	@data_val(param_name = "col_chgdt")
	String colChgDT = "chg_dt" ;
	
	@data_val(param_name = "col_updt")
	String colUpDT = "up_dt" ;
	
	@data_val(param_name = "col_valid")
	String colValid = "valid" ;
	
	@data_val(param_name = "col_valtp")
	String colValTp = "val_tp" ;
	
	@data_val(param_name = "col_valbool")
	String colValBool = "val_bool" ;
	
	@data_val(param_name = "col_valint")
	String colValInt = "val_int" ;
	
	@data_val(param_name = "col_valfloat")
	String colValFloat = "val_float" ;
	
	@data_val(param_name = "col_alertnum")
	String colAlertNum = "alert_num" ;
	
	@data_val(param_name = "col_alertinf")
	String colAlertInf = "alert_inf" ;
	
	@data_val(param_name = "keep_days")
	int keepDays = 100 ;
//	@data_val(param_name = "val_tp")
//	String valTp = "str" ;
	
	boolean has_bool = false;
	boolean has_int = false;
	boolean has_float = false;
	
	@Override
	public String getOutTp()
	{
		return TP;
	}

//	@Override
//	public String getOutTpTitle()
//	{
//		return "History Data Table";
//	}
	
	@Override
	public boolean isStoreHistory()
	{
		return true;
	}

	public String getSorName()
	{
		if(this.sorName==null)
			return "" ;
		return this.sorName ;
	}
	
	public String getTableName()
	{
		if(this.tableName==null)
			return "" ;
		return this.tableName ;
	}
	
	public String getColAutoId()
	{
		return "AutoId" ;
	}
	
	public String getColTag()
	{
		if(Convert.isNullOrEmpty(this.colTag))
			return "tag";
		return this.colTag;
	}

	public String getColUpDT()
	{
		if(Convert.isNullOrEmpty(this.colUpDT))
			return "up_dt";
		return this.colUpDT;
	}
	
	public String getColChgDT()
	{
		if(Convert.isNullOrEmpty(this.colChgDT))
			return "chg_dt";
		return this.colChgDT;
	}
	
	public String getColValid()
	{
		if(Convert.isNullOrEmpty(this.colValid))
			return "valid";
		return this.colValid;
	}
	
	public String getColValTp()
	{
		if(Convert.isNullOrEmpty(this.colValTp))
			return "val_tp";
		return this.colValTp;
	}
	
	public String getColValBool()
	{
		if(Convert.isNullOrEmpty(this.colValBool))
			return "val_bool";
		return this.colValBool ;
	}
	
	public String getColValInt()
	{
		if(Convert.isNullOrEmpty(this.colValInt))
			return "val_int";
		return this.colValInt ;
	}
	
	public String getColValFloat()
	{
		if(Convert.isNullOrEmpty(this.colValFloat))
			return "val_float";
		return this.colValFloat ;
	}
	
	public String getColAlertNum()
	{
		if(Convert.isNullOrEmpty(this.colAlertNum))
			return "alert_num";
		return this.colAlertNum ;
	}
	
	public String getColAlertInf()
	{
		if(Convert.isNullOrEmpty(this.colAlertInf))
			return "alert_inf";
		return this.colAlertInf ;
	}
	
	public int getKeepDays()
	{
		return this.keepDays ;
	}
	
	public boolean checkValid(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.tableName) || !Convert.checkVarName(this.tableName,"table", true, failedr))
		{
			failedr.append("or no table name input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colTag) || !Convert.checkVarName(this.colTag,"Coloumn of Tag", true, failedr))
		{
			failedr.append("or no Coloumn of Tag input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colUpDT) || !Convert.checkVarName(this.colUpDT,"Coloumn of Update DateTime", true, failedr))
		{
			failedr.append("or no Coloumn of Update DateTime input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colChgDT) || !Convert.checkVarName(this.colChgDT,"Coloumn of Change DateTime", true, failedr))
		{
			failedr.append("or no Coloumn of Change DateTime input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colValid) || !Convert.checkVarName(this.colValid,"Coloumn of valid", true, failedr))
		{
			failedr.append("or no Coloumn of valid input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colValTp) || !Convert.checkVarName(this.colValTp,"Coloumn of value type", true, failedr))
		{
			failedr.append("or no Coloumn of value type input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colValInt) || !Convert.checkVarName(this.colValInt,"Coloumn of value int", true, failedr))
		{
			failedr.append("or no Coloumn of value int input") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.colValFloat) || !Convert.checkVarName(this.colValFloat,"Coloumn of value float", true, failedr))
		{
			failedr.append("or no Coloumn of value float input") ;
			return false;
		}
		return true ;
	}
	
	//JavaTableInfo tableInfo = null ;
	
	public static final int MAX_ID_LEN =  20 ;
	
	private static final int MAX_ALERT_INF_LEN = 200 ;
	
	private String[] allColNames = null ;
	
	private JavaTableInfo getJavaTableInfo() throws Exception
	{
		//if(tableInfo!=null)
		//	return tableInfo;
		has_bool = false;
		has_int = false;
		has_float = false;
		
		List<UATag> sel_tags = this.belongTo.listSelectedTags() ;
		if(sel_tags!=null)
		{
			for(UATag tag:sel_tags)
			{
				ValTP vt = tag.getValTp() ;
				if(vt==null)
					continue ;
				if(vt==ValTP.vt_bool)
				{
					has_bool = true ;
					continue ;
				}
				if(!vt.isNumberVT()) continue ;
				if(vt.isNumberFloat())
					has_float = true ;
				else
					has_int = true ;
			}
		}
		
		if(!has_bool && !has_int && !has_float)
			throw new Exception("no bool or number tag selected!") ;
		
		int tag_maxlen = 20 ;
		for(UATag tag:this.belongTo.prj.listTagsAll())
		{
			String np = tag.getNodePath() ;
			int len = np.length() ;
			if(len>tag_maxlen)
				tag_maxlen = len ;
		}
		

		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		pkcol = new JavaColumnInfo(getColAutoId(),true, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1,"",false,false);
		
		norcols.add(new JavaColumnInfo(this.getColTag(),true, XmlVal.XmlValType.vt_string, tag_maxlen+10,
				true, false,this.getColTag()+"_idx", false,-1,"",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColUpDT(),false, XmlVal.XmlValType.vt_date, -1,
				true, false,this.getColUpDT()+"_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColChgDT(),false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValid(),false, XmlVal.XmlValType.vt_int16, 2,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValTp(),false, XmlVal.XmlValType.vt_string, 10,
				false, false,"", false,-1, "",false,false));
		if(has_bool)
		{
			norcols.add(new JavaColumnInfo(this.getColValBool(),false, XmlVal.XmlValType.vt_bool, 2,
					false, false,"", false,-1, "",false,false));
		}
		
		if(has_int)
		{
			norcols.add(new JavaColumnInfo(this.getColValInt(),false, XmlVal.XmlValType.vt_int64, -1,
					false, false,"", false,-1, "",false,false));
		}
		
		if(has_float)
		{
			norcols.add(new JavaColumnInfo(this.getColValFloat(),false, XmlVal.XmlValType.vt_double, -1,
					false, false,"", false,-1, "",false,false));
		}
		
		norcols.add(new JavaColumnInfo(this.getColAlertNum(),false, XmlVal.XmlValType.vt_int16, 4,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColAlertInf(),false, XmlVal.XmlValType.vt_string, MAX_ALERT_INF_LEN,
				false, false,"", false,-1, "",false,false));
		

		//tableInfo = new JavaTableInfo(tableName, pkcol, norcols, fks);
		//return tableInfo;
		
		JavaTableInfo jti = new JavaTableInfo(tableName, pkcol, norcols, fks);
		List<String> ns = jti.getAllColNames() ;
		String[] ss = new String[ns.size()];
		ns.toArray(ss) ;
		this.allColNames = ss ;
		return jti ;
	}
	
	
//	private static void checkAndAlterTable(Class<?> xorm_class,IConnPool cp,Connection conn,String tablename,InstallCB cb)
//	{
//		try
//		{
//			DataTable dt = getDBTableStruct(conn,tablename);
//			ArrayList<JavaColumnInfo> add_cols = new ArrayList<JavaColumnInfo>() ;
//			ArrayList<JavaColumnInfo> alter_cols = new ArrayList<JavaColumnInfo>() ;
//			JavaTableInfo jti = XORMUtil.checkTableAlter(xorm_class, dt, add_cols, alter_cols);
//			
//			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
//			for(JavaColumnInfo addcol:add_cols)
//			{
//				ArrayList<String> sqls = new ArrayList<String>(2) ;
//				JavaColumnInfo beforejci = jti.getBeforeColumn(addcol.getColumnName()) ;
//				String aftercol = null ;
//				if(beforejci!=null)
//					aftercol = beforejci.getColumnName() ;
//				StringBuffer sqlsb = dbsql.constructAddColumnToTable(jti, addcol, aftercol);
//				sqls.add(sqlsb.toString()) ;
//				if(addcol.hasIdx())
//				{
//					sqlsb = dbsql.constructIndexTable(jti, addcol) ;
//					sqls.add(sqlsb.toString()) ;
//				}
//				
//				executeSqls(false,conn,sqls,cb) ;
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace() ;
//		}
//	}
	
	protected boolean initOutInner(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.sorName))
		{
			failedr.append("no Store source name") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.tableName))
		{
			failedr.append("no table name found") ;
			return false;
		}
		
		SourceJDBC sor = (SourceJDBC)StoreManager.getSourceByName(this.sorName) ;
		if(sor==null)
		{
			failedr.append("no source found with name="+this.sorName) ;
			return false;
		}
		//create table
		this.connPool = sor.getConnPool() ;
		try
		{
			DBUtil.createOrUpTable(connPool,this.getJavaTableInfo());
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			e.printStackTrace();
			return false;
		}
		return true ;
	}
	
//	JavaTableInfo tableInfo = null ;
//	
//	private void createOrUpTable(DBConnPool cp) throws Exception
//	{
//		Connection conn =null;
//		try
//		{
//			conn = cp.getConnection() ;
//			tableInfo = getJavaTableInfo();
//			if(DBUtil.tableExists(conn, cp.getDatabase(), tableName))
//			{
//				// TODO check update col length
//				DBUtil.checkAndAlterTable(tableInfo,cp,conn,tableName,null) ;
//				return ;
//			}
//			
//			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
//			
//			List<String> sqls = dbsql.getCreationSqls(tableInfo);
//			DBUtil.runSqls(conn, sqls);
//		}
//		finally
//		{
//			if(conn!=null)
//				cp.free(conn);
//		}
//	}
	
//	public DataTable getDBTable()
//		throws Exception
//	{
//			String sel_sql = "select ";
//			boolean bfirst = true ;
//			for(String s:allColNames)
//			{
//				if(bfirst) bfirst=false;
//				else sel_sql+=",";
//				sel_sql += s ;
//			}
//			sel_sql += " from "+this.getTableName()+" where 1=0" ;
//			
//			PreparedStatement ps = null ;
//			ResultSet rs = null ;
//			Connection conn = null;
//			try
//			{
//				conn = connPool.getConnection() ;
//				ps = conn.prepareStatement(sel_sql) ;
//				rs = ps.executeQuery() ;
//				return DBResult.transResultSetToDataTable(rs,this.getTableName(),0, -1) ;
//			}
//			finally
//			{
//				if(rs!=null)
//				{
//					try
//					{
//						rs.close() ;
//					}
//					catch(Exception ee) {}
//				}
//					
//				if(ps!=null)
//				{
//					try
//					{
//						ps.close() ;
//					}
//					catch(Exception ee) {}
//				}
//				
//				if(conn!=null)
//					connPool.free(conn);
//			}
//		}
	
	
	DBConnPool connPool = null ;
	
	//String[] synCols = null ;
	
	private transient DataTable dataTB = null ;
	
	@Override
	protected boolean RT_initInner(StringBuilder failedr) throws Exception
	{
		SourceJDBC sor = (SourceJDBC)StoreManager.getSourceByName(this.sorName) ;
		if(sor==null)
		{
			failedr.append("no source found with name="+this.sorName) ;
			return false;
		}
		connPool = sor.getConnPool() ;
//		synCols = new String[] {this.getColUpDT(),this.getColChgDT(),this.getColValid(),
//				this.getColValTp(),this.getColValStr(),this.getColAlertNum(),this.getColAlertInf()} ;

		dataTB = DBUtil.createOrUpTable(connPool,this.getJavaTableInfo(),true) ;
		
		//dataTB = getDBTable() ;
		return true ;
	}
	
	static class TagDbV
	{
		String autoId ;
		
		UATag tag ;
		
		long updt ;
		
		long chgdt ;
		
		boolean bvalid ;
		
		Object val ;
		
		public TagDbV(String autoId,UATag tag,long updt,long chgdt,boolean valid,Object val)
		{
			this.tag = tag ;
			this.updt = updt ;
			this.chgdt = chgdt ;
			this.bvalid = valid ;
			this.val = val ;
		}
		
		public boolean checkChanged(boolean bvalid,Object objv)
		{
			if(bvalid!=this.bvalid)
				return true ;
			
			if(!bvalid)
				return false;
			
			if(this.val==null)
			{
				if(objv!=null)
					return true ;
				else
					return false;
			}
			
			return !this.val.equals(objv) ;
		}
	}
	
	
	private void updateUpDT(Connection conn,TagDbV tagdbv) throws Exception
	{
		StringBuilder upsql = new StringBuilder() ;
		upsql.append("update ").append(this.getTableName()).append(" set ");
		upsql.append(this.getColUpDT()).append("=? where ").append(getColAutoId()).append("=?");
		PreparedStatement ps = null;
		long updt = System.currentTimeMillis();
		try
		{
			//System.out.println(upsql.toString());
			ps = conn.prepareStatement(upsql.toString()) ;
			ps.setObject(1, new Date(updt));
			ps.setString(2, tagdbv.autoId);
			
			ps.executeUpdate() ;
		}
		finally
		{
			tagdbv.updt = updt ;
			if(ps!=null)
				ps.close() ;
		}
	}
	

	private transient long lastDelDT = -1 ;
	
	private void delOld(Connection conn) throws SQLException
	{
		final long DAY_MS = 24*3600000 ;
		if(this.getKeepDays()<=0)
			return ;
		
		if(System.currentTimeMillis()-lastDelDT<DAY_MS)
			return ;
		
		long to_gap = this.getKeepDays()*DAY_MS ;
		Date olddt = new Date(System.currentTimeMillis()-to_gap) ;
		
		StringBuilder delsql = new StringBuilder() ;
		delsql.append("delete from ").append(this.getTableName());
		delsql.append(" where ").append(this.getColUpDT()).append("<?") ;
		
		PreparedStatement ps = null;
		try
		{
			ps = conn.prepareStatement(delsql.toString()) ;
			
			ps.setObject(1, olddt);
			ps.executeUpdate() ;
		}
		finally
		{
			lastDelDT = System.currentTimeMillis() ;
			if(ps!=null)
				ps.close() ;
		}
	}

	private transient HashMap<UATag,TagDbV> tag2lastv = new HashMap<>() ;
	
	
	private void insertChgNewRow(UATag tag,String newid,long updt,long chgdt,boolean bvalid,Object val,
			Connection conn,DataRow dr) throws SQLException
	{
		dr.doInsertDB(conn, this.getTableName(), this.allColNames) ;
		TagDbV dbv = new TagDbV(newid, tag, updt, chgdt, bvalid, val) ;
		
		tag2lastv.put(tag, dbv) ;
	}
	
	
	@Override
	protected void RT_runInLoop()
	{
		//StoreHandlerRT sh = (StoreHandlerRT)this.belongTo ;
		Connection conn = null;
		//getJavaTableInfo().
		try
		{

			conn = connPool.getConnection() ;
			for(UATag tag: belongTo.listSelectedTags())
			{
				UAVal uav = tag.RT_getVal() ;
				if(uav==null)
					continue ;
				
				TagDbV lastv = tag2lastv.get(tag) ;
				
				long valdt = uav.getValDT() ;
				long valchgdt = uav.getValChgDT() ;
				boolean b_valid = uav.isValid();
				short valid = (short)(b_valid?1:0);
				//String valstr = uav.getStrVal(tag.getDecDigits());
				Object val = uav.getObjVal() ;
				ValTP vt = tag.getValTp() ;
				String valtp =vt.getStr() ;
				List<ValEvent> vas = tag.getValAlerts();
				short alert_n = 0 ;
				String alert_inf = "" ;
				if(vas!=null&&vas.size()>0)
				{
					for(ValEvent va:vas)
					{
						if(va.RT_is_triggered())
						{
							alert_n ++ ;
							if(alert_n>1)
								alert_inf+=",";
							alert_inf += va.getEventTitle() ;
						}
					}
					if(alert_inf.length()>MAX_ALERT_INF_LEN)
						alert_inf = alert_inf.substring(0,MAX_ALERT_INF_LEN-1) ;
				}
				
				if(lastv!=null)
				{//check val not change
					if(!lastv.checkChanged(b_valid,val))
					{
						if(!b_valid) continue ;
						//do update dt
						updateUpDT(conn,lastv);
						continue ;
					}
				}
				
				//do insert
				
				DataRow dr = dataTB.createNewRow() ;
				String newid = IdCreator.newSeqId();//CompressUUID.createNewId() ;
				dr.putValue(this.getColAutoId(),newid) ;
				dr.putValue(this.getColTag(), tag.getNodePathCxt());
				dr.putValue(this.getColUpDT(), new Date(valdt));
				dr.putValue(this.getColChgDT(), new Date(valchgdt));
				dr.putValue(this.getColValid(), valid);
				dr.putValue(this.getColValTp(), valtp);
				if(val instanceof Boolean)
				{
					dr.putValue(this.getColValBool(), (Boolean)val);
				}
				if(val instanceof Number)
				{
					Number nv = (Number)val ;
					if(vt.isNumberFloat())
						dr.putValue(this.getColValFloat(), nv.doubleValue());
					else
						dr.putValue(this.getColValInt(), nv.longValue());
				}
				dr.putValue(this.getColAlertNum(), alert_n);
				dr.putValue(this.getColAlertInf(), alert_inf);
				
				//dt.addRow(dr);
				insertChgNewRow(tag,newid,valdt, valchgdt,b_valid,val,
						conn,dr) ; 
			}
			
			delOld(conn);
			//dt.synToDBTable(conn, this.tableName, this.getColTag(), synCols, true) ;
			rtRunOk = true;
			rtErrorInfo = null ;
		}
		catch(Exception e)
		{
			rtRunOk = false;
			rtErrorInfo = e.getMessage() ;
			if(log.isErrorEnabled())
				log.error("Store Out ["+this.sorName+"] error", e);
		}
		finally
		{
			connPool.free(conn);
		}
	}
	
	public Object getValInRow(DataRow dr)
	{
		String vtstr = dr.getValueStr(this.getColValTp(), "") ;
		
		ValTP vt = UAVal.getValTp(vtstr) ;
		
		if(vt==ValTP.vt_bool)
			return dr.getValueBool(this.getColValBool(), false) ;
		if(vt.isNumberFloat())
			return dr.getValueDouble(this.getColValFloat(), 0.0) ;
		return dr.getValueInt64(this.getColValInt(), 0) ;
	}
	
	// -- history
	public DataTable selectRecords(String tag_path,Date start_dt,Date end_dt,Boolean bvalid,int pageidx,int pagesize) throws Exception
	{
		if(pageidx<0||pagesize<=0)
			throw new IllegalArgumentException("invalid pageidx and pagesize") ;
		Connection conn = null;

		PreparedStatement ps = null;
		//Statement ps = null ;
		ResultSet rs = null;
		
		DBConnPool cp = connPool ;
		
		String sql = "select * from "+this.getTableName();
		String cond = null ;
		if(Convert.isNotNullEmpty(tag_path))
			cond = (cond==null?" where ":cond +" and ") + this.getColTag() +" = ?" ;
		if(start_dt!=null)
			cond = (cond==null?" where ":cond +" and ") + this.getColUpDT() +" >= ?" ;
		if(end_dt!=null)
			cond = (cond==null?" where ":cond +" and ") + this.getColUpDT() + " <= ?" ;
		if(bvalid!=null)
			cond = (cond==null?" where ":cond +" and ") + "Valid = ?" ;
		if(cond==null)
			cond = "" ;
		//sql += cond +" order by TriggerDT desc limit "+pagesize+" offset "+pageidx*pagesize;
		sql += cond +" order by "+this.getColUpDT() +" desc limit ? offset ?";
		try
		{
			conn = cp.getConnection();
			
			//ps = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			
			ps = conn.prepareStatement(sql);
			
			int pidx = 0 ;
			if(Convert.isNotNullEmpty(tag_path))
			{
				pidx ++ ;
				ps.setString(pidx, tag_path);
			}
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
			if(bvalid!=null)
			{
				pidx ++ ;
				ps.setBoolean(pidx, bvalid);
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
				dt = DBResult.transResultSetToDataTable(this.getTableName(),0,rs, 0, pagesize,null);
				
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
