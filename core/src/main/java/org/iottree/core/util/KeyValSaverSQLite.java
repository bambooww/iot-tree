package org.iottree.core.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class KeyValSaverSQLite extends KeyValSaver
{
	String dbUrl ;
	
	String tableName ;
	
	private Connection conn;
	
	public KeyValSaverSQLite(File f) throws Exception
	{
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		dbUrl = "jdbc:sqlite:" + f.getCanonicalPath();
		tableName = "prj_tb";

		conn = DriverManager.getConnection(dbUrl);
		String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + "key TEXT PRIMARY KEY,"
				+ "val BLOB)";
		try (Statement stmt = conn.createStatement())
		{
			stmt.execute(createTableSQL);
		}
	}
	
	public boolean delByKey(String key) throws SQLException
	{
		String deleteSQL = "DELETE FROM " + tableName + " WHERE key = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL))
		{
			pstmt.setString(1, key);
			return pstmt.executeUpdate() == 1;
		}
	}
	/**
	 * 
	 * @param key
	 * @param value null表示删除
	 */
	public void setKeyVal(String key,String value) throws SQLException
	{
		if(value==null)
		{
			delByKey(key) ;
			return ;
		}

		String insertSQL = "INSERT OR REPLACE INTO " + tableName + "(key, val) VALUES(?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSQL))
		{
			pstmt.setString(1, key);
			pstmt.setBytes(2, value.getBytes());
			pstmt.executeUpdate() ;
		}
	}
	
	public String getValByKey(String key) throws Exception
	{
		String selectSQL = "SELECT key,val FROM " + tableName + " where key=? ";
		try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
		{
			pstmt.setString(1, key);
			try(ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					byte[] bs = rs.getBytes("val");
					if(bs==null)
						return null ;
					return new String(bs,"UTF-8") ;
				}
			}
		}
		return null ;
	}
}
