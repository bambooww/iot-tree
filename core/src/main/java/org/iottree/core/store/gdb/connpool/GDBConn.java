package org.iottree.core.store.gdb.connpool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.*;
import java.util.concurrent.Executor;

import org.iottree.core.store.gdb.ConnPoolMgr;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;


public class GDBConn implements Connection
{
	static ILogger logger = LoggerManager.getLogger(GDBConn.class) ;
	
	Connection innerConn = null ;
	DBConnPool belongPool = null ;
	
	int accessCount = 0 ;
	long createDT = System.currentTimeMillis() ;
	
	
	GDBConn(DBConnPool p,Connection conn)
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("GDBConn construct be called!");
//			for(StackTraceElement st : Thread.currentThread().getStackTrace())
//			{
//				logger.debug(st.toString());
//			}
		}
		
		belongPool = p ;
		innerConn = conn ;
	}
	
	/**
	 * �����־����
	 *
	 */
	public void clearLogBuffer()
	{
		//commitLogBuffer.clear() ;
	}
	
	/**
	 * ������ֱ�ӵ��ñ��������ͷŵ����ӳ���
	 *
	 */
	public void freeToPool()
	{
		belongPool.free(this) ;
	}
	
	public Statement createStatement() throws SQLException
	{
		return innerConn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql);
		return new GDBPreparedStatement(this,ps,sql);
	}
	
	public PreparedStatement prepareStatementNoLog(String sql) throws SQLException
	{
		return innerConn.prepareStatement(sql);
		//return new GDBPreparedStatement(this,ps,sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException
	{
		return innerConn.prepareCall(sql);
	}

	public String nativeSQL(String sql) throws SQLException
	{
		return innerConn.nativeSQL(sql);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
		innerConn.setAutoCommit(autoCommit) ;
	}

	public boolean getAutoCommit() throws SQLException
	{
		return innerConn.getAutoCommit();
	}

	public void commit() throws SQLException
	{
		
		innerConn.commit();
	}

	public void rollback() throws SQLException
	{
		innerConn.rollback();
	}

	public void close() throws SQLException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("GDBConn close() be called! Live["+(System.currentTimeMillis()-createDT)+"] AC["+this.accessCount+"]");
			logger.debug(belongPool.toPoolConnInfoStr()) ;
		}
		
		
		innerConn.close();
	}

	public boolean isClosed() throws SQLException
	{
		return innerConn.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException
	{
		return innerConn.getMetaData();
	}

	public void setReadOnly(boolean readOnly) throws SQLException
	{
		innerConn.setReadOnly(readOnly) ;
	}

	public boolean isReadOnly() throws SQLException
	{
		return innerConn.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException
	{
		innerConn.setCatalog(catalog) ;
	}

	public String getCatalog() throws SQLException
	{
		return innerConn.getCatalog();
	}

	public void setTransactionIsolation(int level) throws SQLException
	{
		innerConn.setTransactionIsolation(level) ;
	}

	public int getTransactionIsolation() throws SQLException
	{
		return innerConn.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException
	{
		return innerConn.getWarnings();
	}

	public void clearWarnings() throws SQLException
	{
		innerConn.clearWarnings() ;
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException
	{
		return innerConn.createStatement(resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql, resultSetType,
				resultSetConcurrency);
		return new GDBPreparedStatement(this,ps,sql) ;
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException
	{
		return innerConn.prepareCall(sql, resultSetType,
				resultSetConcurrency);
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		return innerConn.getTypeMap();
	}

	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException
	{
		innerConn.setTypeMap(arg0) ;
	}

	public void setHoldability(int holdability) throws SQLException
	{
		innerConn.setHoldability(holdability) ;
	}

	public int getHoldability() throws SQLException
	{
		return innerConn.getHoldability();
	}

	public Savepoint setSavepoint() throws SQLException
	{
		return innerConn.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException
	{
		return innerConn.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException
	{
		innerConn.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		innerConn.releaseSavepoint(savepoint) ;
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException
	{
		return innerConn.createStatement(resultSetType,
				resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql, resultSetType,
				resultSetConcurrency, resultSetHoldability);
		return new GDBPreparedStatement(this,ps,sql) ;
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException
	{
		return innerConn.prepareCall( sql,  resultSetType,
				 resultSetConcurrency,  resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql, autoGeneratedKeys);
		return new GDBPreparedStatement(this,ps,sql) ;
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql, columnIndexes);
		return new GDBPreparedStatement(this,ps,sql) ;
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException
	{
		PreparedStatement ps = innerConn.prepareStatement(sql, columnNames);
		return new GDBPreparedStatement(this,ps,sql) ;
	}

	public Clob createClob() throws SQLException
	{
		return innerConn.createClob();
	}

	public Blob createBlob() throws SQLException
	{
		return innerConn.createBlob();
	}

	public NClob createNClob() throws SQLException
	{
		return innerConn.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException
	{
		return innerConn.createSQLXML();
	}

	public boolean isValid(int timeout) throws SQLException
	{
		return innerConn.isValid(timeout);
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException
	{
		innerConn.setClientInfo(name, value) ;
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException
	{
		innerConn.setClientInfo(properties) ;
	}

	public String getClientInfo(String name) throws SQLException
	{
		return innerConn.getClientInfo(name);
	}

	public Properties getClientInfo() throws SQLException
	{
		return innerConn.getClientInfo();
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException
	{
		return innerConn.createArrayOf(typeName, elements);
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException
	{
		return innerConn.createStruct(typeName, attributes);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return innerConn.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return innerConn.isWrapperFor(iface);
	}

	public void setSchema(String schema) throws SQLException
	{
		// TODO Auto-generated method stub
		
	}

	public String getSchema() throws SQLException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public void abort(Executor executor) throws SQLException
	{
		// TODO Auto-generated method stub
		
	}

	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException
	{
		// TODO Auto-generated method stub
		
	}

	public int getNetworkTimeout() throws SQLException
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
