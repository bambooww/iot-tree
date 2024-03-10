package org.iottree.core.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.store.gdb.DBResult;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.DBUtil.InstallCB;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlVal;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class StoreOutTb extends StoreOut
{
	static ILogger log = LoggerManager.getLogger(StoreOutTb.class) ;
	
	public static final String TP = "r_tb" ;
	
	@data_val(param_name = "sor_n")
	String sorName = null ;
	
	@data_val(param_name = "table")
	String tableName = null ;
	
	@data_val(param_name = "col_tag")
	String colTag = "tag" ;
	
	@data_val(param_name = "col_updt")
	String colUpDT = "up_dt" ;
	
	@data_val(param_name = "col_chgdt")
	String colChgDT = "chg_dt" ;
	
	@data_val(param_name = "col_valid")
	String colValid = "valid" ;
	
	@data_val(param_name = "col_valtp")
	String colValTp = "val_tp" ;
	
	@data_val(param_name = "col_valstr")
	String colValStr = "val_str" ;
	
	@data_val(param_name = "col_alertnum")
	String colAlertNum = "alert_num" ;
	
	@data_val(param_name = "col_alertinf")
	String colAlertInf = "alert_inf" ;
	
//	@data_val(param_name = "val_tp")
//	String valTp = "str" ;
	
	@Override
	public String getOutTp()
	{
		return TP;
	}

	@Override
	public String getOutTpTitle()
	{
		return "Realtime Data Table";
	}
	
	@Override
	public boolean isStoreHistory()
	{
		return false;
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
	
	public String getColValStr()
	{
		if(Convert.isNullOrEmpty(this.colValStr))
			return "val_str";
		return this.colValStr ;
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
		if(Convert.isNullOrEmpty(this.colValStr) || !Convert.checkVarName(this.colValStr,"Coloumn of value str", true, failedr))
		{
			failedr.append("or no Coloumn of value str input") ;
			return false;
		}
		return true ;
	}
	
	JavaTableInfo tableInfo = null ;
	
	public static final int MAX_ID_LEN =  20 ;
	
	private static final int MAX_ALERT_INF_LEN = 200 ;
	
	private JavaTableInfo getJavaTableInfo()
	{
		if(tableInfo!=null)
			return tableInfo;
		
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

		pkcol = new JavaColumnInfo(this.getColTag(),true, XmlVal.XmlValType.vt_string, tag_maxlen+10,
				false, false,"", false,-1,"",false,false);
		
		norcols.add(new JavaColumnInfo(this.getColUpDT(),false, XmlVal.XmlValType.vt_date, -1,
				true, false,this.getColUpDT()+"_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColChgDT(),false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValid(),false, XmlVal.XmlValType.vt_int16, 2,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValTp(),false, XmlVal.XmlValType.vt_string, 10,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValStr(),false, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColAlertNum(),false, XmlVal.XmlValType.vt_int16, 4,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColAlertInf(),false, XmlVal.XmlValType.vt_string, MAX_ALERT_INF_LEN,
				false, false,"", false,-1, "",false,false));
		
//		JavaColumnInfo(String coln,boolean b_pk, XmlVal.XmlValType vt, int maxlen,
//				boolean hasidx, boolean unique,String idxname, 
//				boolean autoval,long autoval_st,String default_strv,
//				boolean b_read_ondemand,boolean b_update_as_single)
		
		
		tableInfo = new JavaTableInfo(tableName, pkcol, norcols, fks);
		return tableInfo;
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
		DBConnPool cp = sor.getConnPool() ;
		try
		{
			DBUtil.createOrUpTable(cp,getJavaTableInfo());
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			e.printStackTrace();
			return false;
		}
		return true ;
	}
	
//	private void createOrUpTable(DBConnPool cp) throws Exception
//	{
//		Connection conn =null;
//		try
//		{
//			conn = cp.getConnection() ;
//			JavaTableInfo jti = getJavaTableInfo();
//			if(DBUtil.tableExists(conn, cp.getDatabase(), tableName))
//			{
//				// TODO check update col length
//				DBUtil.checkAndAlterTable(jti,cp,conn,tableName,null) ;
//				return ;
//			}
//			
//			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
//			
//			List<String> sqls = dbsql.getCreationSqls(jti);
//			DBUtil.runSqls(conn, sqls);
//		}
//		finally
//		{
//			if(conn!=null)
//				cp.free(conn);
//		}
//	}
//	
	
	DBConnPool connPool = null ;
	
	String[] synCols = null ;
	
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
		synCols = new String[] {this.getColUpDT(),this.getColChgDT(),this.getColValid(),
				this.getColValTp(),this.getColValStr(),this.getColAlertNum(),this.getColAlertInf()} ;

		DBUtil.createOrUpTable(connPool,getJavaTableInfo()) ;
		
		return true ;
	}
	
	@Override
	protected void RT_runInLoop()
	{
		//StoreHandlerRT sh = (StoreHandlerRT)this.belongTo ;
		Connection conn = null; 
		//getJavaTableInfo().
		DataTable dt = new DataTable() ;
		for(UATag tag: belongTo.listSelectedTags())
		{
			UAVal uav = tag.RT_getVal() ;
			if(uav==null)
				continue ;
			long valdt = uav.getValDT() ;
			long valchgdt = uav.getValChgDT() ;
			short valid = (short)(uav.isValid()?1:0);
			String valstr = uav.getStrVal(tag.getDecDigits());
			String valtp = tag.getValTp().getStr() ;
			List<ValAlert> vas = tag.getValAlerts();
			short alert_n = 0 ;
			String alert_inf = "" ;
			if(vas!=null&&vas.size()>0)
			{
				for(ValAlert va:vas)
				{
					if(va.RT_is_triggered())
					{
						alert_n ++ ;
						if(alert_n>1)
							alert_inf+=",";
						alert_inf += va.getAlertTitle() ;
					}
				}
				if(alert_inf.length()>MAX_ALERT_INF_LEN)
					alert_inf = alert_inf.substring(0,MAX_ALERT_INF_LEN-1) ;
			}
			
			DataRow dr = dt.createNewRow() ;
			dr.putValue(this.getColTag(), tag.getNodePathCxt());
			dr.putValue(this.getColUpDT(), new Date(valdt));
			dr.putValue(this.getColChgDT(), new Date(valchgdt));
			dr.putValue(this.getColValid(), valid);
			dr.putValue(this.getColValTp(), valtp);
			dr.putValue(this.getColValStr(), valstr);
			dr.putValue(this.getColAlertNum(), alert_n);
			dr.putValue(this.getColAlertInf(), alert_inf);
			
			dt.addRow(dr);
		}
		
		try
		{
			conn = connPool.getConnection() ;
			dt.synToDBTable(conn, this.tableName, this.getColTag(), synCols, true) ;
			rtRunOk = true;
			rtErrorInfo = null ;
		}
		catch(Exception e)
		{
			rtRunOk = false;
			rtErrorInfo = e.getMessage() ;
			if(log.isDebugEnabled())
				log.debug("Store Out ["+this.sorName+"] error", e);
		}
		finally
		{
			connPool.free(conn);
		}
	}
}
