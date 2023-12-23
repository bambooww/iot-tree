package org.iottree.core.store.gdb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.IConnPool;

public class DBUtil
{
	public static boolean tableExists(Connection conn, String dbname,String tableName)
		throws SQLException
	{
			DatabaseMetaData dmd = conn.getMetaData() ;
			return tableExists(dmd,dbname, tableName) ;
	}
	
	public static boolean tableExistsCaseSensitive(Connection conn,  String dbname,String tableName)
		throws SQLException
	{
			DatabaseMetaData dmd = conn.getMetaData() ;
			return tableExistsCaseSensitive(dmd,dbname, tableName) ;
	}
	
	
	/**
	 * �жϱ��Ƿ����
	 * @param dbMetaData
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static boolean tableExists(DatabaseMetaData dbMetaData, String dbname,String tableName)
			throws SQLException
	{
		return (tableExistsCaseSensitive(dbMetaData, dbname,tableName)
				|| tableExistsCaseSensitive(dbMetaData, dbname,tableName
						.toUpperCase(Locale.US)) || tableExistsCaseSensitive(
				dbMetaData, dbname,tableName.toLowerCase(Locale.US)));
	}

	/**
	 * ���ִ�Сд�ж����ݿ���Ƿ����
	 * @param dbMetaData
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public static boolean tableExistsCaseSensitive(DatabaseMetaData dbMetaData,String dbname,
			String tableName) throws SQLException
	{
		ResultSet rsTables = dbMetaData.getTables(dbname, null, tableName, null);
//		while(rsTables.next())
//		{
//			String v1 = rsTables.getString(1) ;
//			String v2 = rsTables.getString(2) ;
//			String v3 = rsTables.getString(3) ;
//			String v4 = rsTables.getString(4) ;
//			System.out.println("v1="+v1+" v2="+v2+" v3="+v3+" v4="+v4) ;
//		}
		try
		{
			boolean found = rsTables.next();
			return found;
			//return true;
		}
		finally
		{
			rsTables.close();
		}
	}

	/**
	 * �ж����ݿ����Ƿ����
	 * @param dbMetaData
	 * @param tableName
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public static boolean columnExists(DatabaseMetaData dbMetaData, String tableName,
			String columnName) throws SQLException
	{
		return (columnExistsCaseSensitive(dbMetaData, tableName, columnName)
				|| columnExistsCaseSensitive(dbMetaData, tableName, columnName
						.toUpperCase(Locale.US))
				|| columnExistsCaseSensitive(dbMetaData, tableName, columnName
						.toLowerCase(Locale.US))
				|| columnExistsCaseSensitive(dbMetaData, tableName
						.toUpperCase(Locale.US), columnName)
				|| columnExistsCaseSensitive(dbMetaData, tableName
						.toUpperCase(Locale.US), columnName
						.toUpperCase(Locale.US))
				|| columnExistsCaseSensitive(dbMetaData, tableName
						.toUpperCase(Locale.US), columnName
						.toLowerCase(Locale.US))
				|| columnExistsCaseSensitive(dbMetaData, tableName
						.toLowerCase(Locale.US), columnName)
				|| columnExistsCaseSensitive(dbMetaData, tableName
						.toLowerCase(Locale.US), columnName
						.toUpperCase(Locale.US)) || columnExistsCaseSensitive(
				dbMetaData, tableName.toLowerCase(Locale.US), columnName
						.toLowerCase(Locale.US)));
	}

	/**
	 * �ж����ݿ�����Ƿ������-���ִ�Сд
	 * @param dbMetaData
	 * @param tableName
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public static boolean columnExistsCaseSensitive(DatabaseMetaData dbMetaData,
			String tableName, String columnName) throws SQLException
	{
		ResultSet rsTables = dbMetaData.getColumns(null, null, tableName,
				columnName);
		try
		{
			boolean found = rsTables.next();
			return found;
		}
		finally
		{
			rsTables.close();
		}
	}
	
	
	public static void runSqls(Connection conn, List<String> sqls)
		throws SQLException
	{
		PreparedStatement ps = null;
		boolean b_autocommit = true;
		try
		{
			b_autocommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			// f.getInParam(uniquekey)
			DBResult dbr = new DBResult();
	
			for (String sql : sqls)
			{
				//System.out.println("install sql:" + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
	
				ps.close();
				ps = null;
			}
	
			conn.commit();
			conn.setAutoCommit(b_autocommit);
			conn = null;
		}
		finally
		{
			try
			{// ������˵�����г���
				if (conn != null)
					conn.rollback();
	
				if (ps != null)
					ps.close();
			}
			catch (Throwable sqle)
			{
			}
			
			if (conn != null)
			{
				try
				{
					conn.setAutoCommit(b_autocommit);
				}
				catch (Throwable sqle)
				{
				}
			}
	
			
		}
	}
	
	public static interface InstallCB
	{
		public void onIntallInfo(String str) ;
		
		public void onErrInfo(String str) ;
		
	}

	
	public static DBResult executeSqls(boolean bresult,Connection conn, List<String> sqls,InstallCB cb)
			throws GdbException
	{
		PreparedStatement ps = null;
		boolean b_autocommit = true;
		try
		{
			//conn = cp.getConnection();
			b_autocommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			// f.getInParam(uniquekey)
			DBResult dbr = new DBResult();

			ResultSet rs = null ;
			
			for (String sql : sqls)
			{
				//System.out.println("install sql:" + sql);
				if(cb!=null)
				{
					cb.onIntallInfo("install sql:" + sql);
				}
				ps = conn.prepareStatement(sql);
				if(bresult)
				{
					rs = ps.executeQuery() ;
					dbr.appendResultSet("tb1",0,rs, 0, -1,null);
				}
				else
				{
					//dbr.
					ps.execute();
				}
				
				ps.close();
				ps = null;
			}

			conn.commit();
			conn.setAutoCommit(b_autocommit);
			//cp.free(conn);
			//conn = null;
			return dbr ;

		}
		catch (SQLException sqle)
		{
			if(cb!=null)
			{
				cb.onErrInfo("exesql err:" + sqle.getMessage());
			}
			throw new GdbException(sqle);
		}
		catch(Exception ee)
		{
			if(cb!=null)
			{
				cb.onErrInfo("exesql err:" + ee.getMessage());
			}
			throw new GdbException(ee);
		}
		finally
		{
			try
			{// 到这里说明运行出错
				if (conn != null)
					conn.rollback();

				if (ps != null)
					ps.close();
			}
			catch (Throwable sqle)
			{
			}
			
			if (conn != null)
			{
				try
				{
					conn.setAutoCommit(b_autocommit);
				}
				catch (Throwable sqle)
				{
				}
				
				//cp.free(conn);
			}

			
		}
	}

	public static DataTable executeQuerySql(Connection conn, String sql,String tablename)
			throws Exception
		{
			PreparedStatement ps = null;
			ResultSet rs = null ;
			
			try
			{
				// f.getInParam(uniquekey)
				DBResult dbr = new DBResult();
			
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				
				//
				return DBResult.transResultSetToDataTable(rs, tablename, 0, 0);

			}
			catch (SQLException sqle)
			{
				throw new GdbException(sqle);
			}
			finally
			{
				try
				{// 到这里说明运行出错
					if(rs!=null)
						rs.close() ;
					
					if (ps != null)
						ps.close();
				}
				catch (Throwable sqle)
				{
				}
				
//				if (conn != null)
//				{
//					cp.free(conn);
//				}
			}
		}
	
	public static DataTable getDBTableStruct(Connection conn,String tablename)
			throws Exception
		{
			String sql = "select * from "+tablename+" where 1=0";
			return executeQuerySql(conn, sql,tablename);
		}
	
	public static JavaTableInfo checkTableAlter(
			JavaTableInfo jti,DataTable query_dt,
			List<JavaColumnInfo> add_cols,List<JavaColumnInfo> alter_cols)
	{
		//
		jti.setTableName(query_dt.getTableName());
		//boolean bret = false;
		//目前只针对普通列进行判断
		for(JavaColumnInfo jci:jti.getNorColumnInfos())
		{
			String coln = jci.getColumnName() ;
			DataColumn dc = query_dt.getColumn(coln) ;
			if(dc==null)
			{//new column found
				add_cols.add(jci) ;
				//bret = true ;
				continue ;
			}
			
			// 变化判断 TODO
			if(dc.getJdbcType()!=jci.getSqlValType())
			{
				//System.out.println(jti.getTableName()+" > "+jci.toLnStr()+" - "+dc.toLnStr());
				continue ;
			}
			if(jci.isNeedMaxLen() && jci.getMaxLen()>0 && dc.getPreciesion()!=jci.getMaxLen())
			{
				//System.out.println(jti.getTableName()+" > "+jci.toLnStr()+" - "+dc.toLnStr());
				continue ;
			}
		}
		return jti;
	}

	public static void checkAndAlterTable(JavaTableInfo jti,DBConnPool cp,Connection conn,String tablename,InstallCB cb)
	{
		try
		{
			DataTable dt = DBUtil.getDBTableStruct(conn,tablename);
			ArrayList<JavaColumnInfo> add_cols = new ArrayList<JavaColumnInfo>() ;
			ArrayList<JavaColumnInfo> alter_cols = new ArrayList<JavaColumnInfo>() ;
			checkTableAlter(jti, dt, add_cols, alter_cols);
			
			DbSql dbsql = DbSql.getDbSqlByDBType(cp.getDBType()) ;
			for(JavaColumnInfo addcol:add_cols)
			{
				ArrayList<String> sqls = new ArrayList<String>(2) ;
				JavaColumnInfo beforejci = jti.getBeforeColumn(addcol.getColumnName()) ;
				String aftercol = null ;
				if(beforejci!=null)
					aftercol = beforejci.getColumnName() ;
				StringBuffer sqlsb = dbsql.constructAddColumnToTable(jti, addcol, aftercol);
				sqls.add(sqlsb.toString()) ;
				if(addcol.hasIdx())
				{
					sqlsb = dbsql.constructIndexTable(jti, addcol) ;
					sqls.add(sqlsb.toString()) ;
				}
				
				executeSqls(false,conn,sqls,cb) ;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}
}
