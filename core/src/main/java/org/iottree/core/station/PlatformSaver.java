package org.iottree.core.station;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.iottree.core.Config;
import org.iottree.core.UAPrj;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.DBType;
import org.json.JSONObject;

/**
 * 支持station发送的数据进行存储的支持
 * 
 * @author jason.zhu
 *
 */
public class PlatformSaver
{
	
	static DBConnPool connPool = null ;
	

	private static DBConnPool getConnPool() throws IOException
	{
		if(connPool!=null)
			return connPool ;
		
		JSONObject dbjo = PlatformManager.getInstance().getRTDataDB() ;
		if(dbjo==null)
			throw new IOException("no rt_data_db config found in platform.json") ;
		// "rt_data_db":{"db_host":"localhost","db_port":33306,"db_name":"iottree_recved_data","db_user":"user1","tbs_db_user":"tbs_db_user"}
		String db_host = dbjo.getString("db_host") ;
		int db_port = dbjo.getInt("db_port") ;
		String db_name = dbjo.getString("db_name") ;
		String db_user = dbjo.getString("db_user") ;
		String db_psw = dbjo.optString("db_psw") ;
		String url = "jdbc:mysql://"+db_host+":"+db_port+"/"+db_name+"?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8" ;//"jdbc:sqlite:{$$data_db_sqlite}recved_rt.db";
		//File f = PlatformManager.getInstance().getRTDataDir() ;
		//if(!f.exists())
		//	f.mkdirs() ;
		//String fp = f.getCanonicalPath().replaceAll("\\\\", "/") ;
		//if(!fp.endsWith("/"))
		//	fp += "/" ;
		//url = url.replace("{$$data_db_sqlite}",fp) ;
		//System.out.println("db url="+url) ;
		SourceJDBC.Drv drv = SourceJDBC.getJDBCDriver("mysql8");
		connPool = new DBConnPool(DBType.mysql, "", "com.mysql.cj.jdbc.Driver", url,db_name, db_user,db_psw, "0", "10",drv.getPlugDir().getOrLoadCL());
		return connPool ;
	}
	
	
	String tableName ;

	public PlatformSaver(String tablename) throws Exception
	{
		this.tableName = tablename ;
		
		Connection connection = null;
		try
		{
			connection = getConnPool().getConnection() ;
			try (Statement stmt = connection.createStatement())
			{
				//stmt.executeUpdate("CREATE TABLE IF NOT EXISTS "+getTableName()+" (timestamp INTEGER PRIMARY KEY, json TEXT)");
				 String createTableSQL = "CREATE TABLE IF NOT EXISTS "+tableName+" (" +
                          "keyid varchar(30)  PRIMARY KEY, " +
                         "json TEXT NOT NULL)";
				 stmt.executeUpdate(createTableSQL);
			}
		}
		finally
		{
			getConnPool().free(connection);
		}
	}
	
	void storeJson(String json, String keyid) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = getConnPool().getConnection() ;
			String sql = "INSERT INTO "+tableName+" (keyid, json) VALUES (?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sql))
			{
	            pstmt.setString(1, keyid);
	            pstmt.setString(2, json);
	            pstmt.executeUpdate();
	        }
		}
		finally
		{
			getConnPool().free(connection);
		}
		
	}
	
	void storeJsonMulti(Map<String,String> keyid2json) throws Exception
	{
		Connection connection = null;
		try
		{
			connection = getConnPool().getConnection() ;
			connection.setAutoCommit(false);
			String sql = "INSERT INTO "+tableName+" (keyid, json) VALUES (?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sql))
			{
				for(Map.Entry<String, String> k2j:keyid2json.entrySet())
				{
		            pstmt.setString(1, k2j.getKey());
		            pstmt.setString(2, k2j.getValue());
		            pstmt.executeUpdate();
				}
	        }
		}
		finally
		{
			if(connection!=null)
			{
				connection.setAutoCommit(true);
				getConnPool().free(connection);
			}
		}
		
	}

	public String retrieveAndDeleteJson() throws Exception
	{
		String selectSql = "SELECT keyid, json FROM "+tableName+" ORDER BY keyid LIMIT 1";
		String deleteSql = "DELETE FROM "+tableName+" WHERE keyid = ?";
		String json = null;

		Connection connection = null;
		try
		{
			connection = getConnPool().getConnection() ;
			try (Statement selectStmt = connection.createStatement(); ResultSet rs = selectStmt.executeQuery(selectSql))
			{
	
				if (rs.next())
				{
					String keyid = rs.getString("keyid");
					json = rs.getString("json");
	
					try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql))
					{
						deleteStmt.setString(1, keyid);
						deleteStmt.executeUpdate();
					}
				}
			}
			
			return json ;
		}
		finally
		{
			getConnPool().free(connection);
		}
	}

//	/**
//	 * 存放到数据库中。
//	 * 
//	 * @param prj
//	 * @param jo
//	 * @throws Exception 
//	 */
//	void onPrjRtDataRecved(UAPrj prj, JSONObject jo) throws Exception
//	{
//		storeJson(jo.toString(), System.currentTimeMillis()) ; 
//	}
}
