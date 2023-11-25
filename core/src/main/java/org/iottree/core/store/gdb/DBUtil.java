package org.iottree.core.store.gdb;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.*;
import java.util.List;
import java.util.Locale;

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
}
