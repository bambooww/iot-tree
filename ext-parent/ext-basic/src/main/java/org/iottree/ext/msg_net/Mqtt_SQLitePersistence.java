package org.iottree.ext.msg_net;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttPersistable;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.iottree.core.Config;

import java.io.File;
import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class Mqtt_SQLitePersistence implements MqttClientPersistence
{
	private String dbUrl = null;//"jdbc:sqlite:mqtt_persistence.db";
	
	private Connection conn;
	
	private String tableName ="" ;
	
	Mqtt_SQLitePersistence(String clientid) throws SQLException
	{
			String fp =  Config.getDataDynDirBase() +"msg_net/mqtt/db_"+clientid+".db" ;
			File f = new File(fp) ;
			if(!f.getParentFile().exists())
				f.getParentFile().mkdirs() ;
			
			dbUrl = "jdbc:sqlite:"+fp ;
			tableName = "tb_"+clientid ;
			
			conn = DriverManager.getConnection(dbUrl);
			String createTableSQL = "CREATE TABLE IF NOT EXISTS "+tableName+" (" + "key TEXT PRIMARY KEY,"
					+ "message BLOB)";
			try (Statement stmt = conn.createStatement())
			{
				stmt.execute(createTableSQL);
			}
	}
	
	public String getTableName()
	{
		return tableName ;
	}
	
	@Override
	public void open(String clientId, String serverURI) throws MqttPersistenceException
	{
		
	}

	@Override
	public void close() throws MqttPersistenceException
	{
//		try
//		{
//			if (conn != null)
//			{
//				conn.close();
//			}
//		}
//		catch ( SQLException e)
//		{
//			throw new MqttPersistenceException(e);
//		}
	}

	@Override
	public void put(String key, MqttPersistable message) throws MqttPersistenceException
	{
		String insertSQL = "INSERT OR REPLACE INTO "+tableName+"(key, message) VALUES(?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(insertSQL))
		{
			pstmt.setString(1, key);
			pstmt.setBytes(2, message.getPayloadBytes());
			pstmt.executeUpdate();
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}

	@Override
	public MqttPersistable get(String key) throws MqttPersistenceException
	{
		String selectSQL = "SELECT message FROM "+tableName+" WHERE key = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
		{
			pstmt.setString(1, key);
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					byte[] message = rs.getBytes("message");
					return new MqttPersistable() {
						@Override
						public byte[] getPayloadBytes()
						{
							return message;
						}

						@Override
						public int getPayloadOffset()
						{
							return 0;
						}

						@Override
						public int getPayloadLength()
						{
							return message.length;
						}

						@Override
						public byte[] getHeaderBytes()
						{
							return new byte[0];
						}

						@Override
						public int getHeaderOffset()
						{
							return 0;
						}

						@Override
						public int getHeaderLength()
						{
							return 0;
						}
					};
				}
				return null;
			}
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}

	@Override
	public void remove(String key) throws MqttPersistenceException
	{
		String deleteSQL = "DELETE FROM "+tableName+" WHERE key = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(deleteSQL))
		{
			pstmt.setString(1, key);
			pstmt.executeUpdate();
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}

	@Override
	public Enumeration<String> keys() throws MqttPersistenceException
	{
		String selectSQL = "SELECT key FROM "+tableName;
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL))
		{
			Hashtable<String, String> keys = new Hashtable<>();
			while (rs.next())
			{
				keys.put(rs.getString("key"), rs.getString("key"));
			}
			return keys.keys();
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}
	

	@Override
	public boolean containsKey(String key) throws MqttPersistenceException
	{
		String selectSQL = "SELECT key FROM "+tableName +" WHERE key = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(selectSQL))
		{
			pstmt.setString(1, key);
			try (ResultSet rs = pstmt.executeQuery(selectSQL))
			{
				return rs.next() ;
			}
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}

	@Override
	public void clear() throws MqttPersistenceException
	{
		String clearSQL = "DELETE FROM "+tableName;
		try (Statement stmt = conn.createStatement())
		{
			stmt.execute(clearSQL);
		}
		catch ( SQLException e)
		{
			throw new MqttPersistenceException(e);
		}
	}
	
	public int getSavedNum()
	{
		if(this.conn==null )
			return -1 ;
		
		try
		{
			if(this.conn.isClosed())
				return -1 ;
		}
		catch(Exception ee)
		{
			return -1 ;
		}
		
		String selectSQL = "SELECT count(*) FROM "+tableName;
		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL))
		{
			while (rs.next())
			{
				return rs.getInt(1) ;
			}
			return 0 ;
		}
		catch ( SQLException e)
		{
			e.printStackTrace();
			//throw new MqttPersistenceException(e);
			return -1 ;
		}
	}
}
