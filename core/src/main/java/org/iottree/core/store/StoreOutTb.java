package org.iottree.core.store;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.store.gdb.DBUtil;
import org.iottree.core.store.gdb.DataRow;
import org.iottree.core.store.gdb.DataTable;
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
		return this.colValStr ;
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
		
		norcols.add(new JavaColumnInfo(this.getColUpDT(),false, XmlVal.XmlValType.vt_int64, -1,
				true, false,this.getColUpDT()+"_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColChgDT(),false, XmlVal.XmlValType.vt_int64, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValid(),false, XmlVal.XmlValType.vt_int16, 2,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValTp(),false, XmlVal.XmlValType.vt_string, 10,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo(this.getColValStr(),false, XmlVal.XmlValType.vt_string, 20,
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
	
	public boolean initOut(StringBuilder failedr)
	{
		if(!super.initOut(failedr))
			return false;
		
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
		Connection conn = null;
		try
		{
			conn = cp.getConnection() ;
			if(DBUtil.tableExists(conn, cp.getDatabase(), tableName))
			{
				failedr.append("table ["+this.tableName+"] is aleady existed") ;
				return false;
			}
			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
			JavaTableInfo jti = getJavaTableInfo();
			List<String> sqls = dbsql.getCreationSqls(jti);
			//List<String> sqls = Arrays.asList("create table "+this.tableName+"(Tag );") ; 
			//dbsql.getUpdateByPkIdSql(jti, tablename)
			DBUtil.runSqls(conn, sqls);
		}
		catch(Exception e)
		{
			failedr.append(e.getMessage()) ;
			e.printStackTrace();
			return false;
		}
		finally
		{
			cp.free(conn);
		}
		return true ;
	}
	
	DBConnPool connPool = null ;
	
	String[] synCols = null ;
	
	@Override
	protected boolean RT_init(StringBuilder failedr) throws Exception
	{
		SourceJDBC sor = (SourceJDBC)StoreManager.getSourceByName(this.sorName) ;
		if(sor==null)
		{
			failedr.append("no source found with name="+this.sorName) ;
			return false;
		}
		connPool = sor.getConnPool() ;
		synCols = new String[] {this.getColUpDT(),this.getColChgDT(),this.getColValid(),this.getColValTp(),this.getColValStr()} ;
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
			DataRow dr = dt.createNewRow() ;
			dr.putValue(this.getColTag(), tag.getNodePathCxt());
			dr.putValue(this.getColUpDT(), valdt);
			dr.putValue(this.getColChgDT(), valchgdt);
			dr.putValue(this.getColValid(), valid);
			dr.putValue(this.getColValTp(), valtp);
			dr.putValue(this.getColValStr(), valstr);
			
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
