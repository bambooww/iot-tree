package org.iottree.core.store.gdb.connpool;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;


/**
 * Ϊ���ݿ�ͬ����֧�ֵ����װ
 * 
 * �ڷֲ�ʽ����£������ô����֧�ּ�¼���ݿ�ĸ��±仯��־��
 * ����¼ÿ����ĸ������
 * 
 * ���磬ÿ����ĸ��²������м�¼�������й����У�ϵͳ���Ը�����־����ͬ��
 * 
 * ͬ��˼·���£�
 * һ���ۺϱ�ͬ����mode1�����ֱ��������������Server�ˣ��ͷֲ�ʽ�������ڴ���ˣ�
 * 	�������£����еĸ��²������ɷֲ�ʽ����������ȷֲ�ʽ���һ�������id��
 *  ͬ���龰1������˳�ʼ������Ҫ�����������е����ݣ���ʱ����ӷ���������ȫ���Լ������ݲ����漴��
 *  ͬ���龰2: ����������£���¼��־��ϵͳ�Զ��Ķ�ʱ��Server�˷�����־�����Server����Ӧ�ɹ�����ɾ��������־
 * ������ȫͬ����mode2�����ֱ�Server�˺ͷֲ�ʽ�˱�ṹ��ͬ��
 * 	���еĸ���ֻ��������У�����˿���ͨ�������첽���󣬼�ӵİѸ����ύ��Server�ˣ���
 *  ͬ���龰1������˳�ʼ������Ҫ�����������е����ݣ���ʱ����ӷ���������ȫ�������ݲ����漴��
 *  ͬ���龰2��ϵͳ�����У�����˶�ʱ���ҷ����������ʱ�������������¡���ͬ����Ը��ӣ�˼·���¡�
 *  	a,Server�˼�¼����ĸ��¼�¼������ʱ������б��ÿ�θ��º���־��Ҳ����ÿ��������־����һ��ʱ���
 *  	b,ÿ��Proxy��Ҳ��¼�Լ������ͬ��ʱ�����
 *  	c,Proxy��ʱ������������Լ��ĸ���ʱ�����ѯ��û���µĸ��£����������ݿͻ��˵�ʱ��������Ƿ���
 *  		�����ĸ�����־������У����͸�����־
 * 			�������ĳһ��proxy��ʱ����ܾɡ����޷�������־��������£���Ҫ��ͻ���ͬ�����е�����
 * 	  	d,����������α�֤��־����Ч��һ�ַ����Ǽ�¼���е���־�������ڱ��С��ͬʱ���²���Ƶ�Ⱥܸߵ�
 * 			��Ч�ʺܲ
 *   �ڴˣ�����һ����������־���ȡ���serverֻ��¼����ĸ�����־��ʱ��������Ҳ��ܲ�������������ơ�
 *   	�˲���Ӧ�ø���ʵ������Լ���������׼���ǣ���־���Ȼ��۵����ռ�����ݿռ�ֵӦ�û����Ϻͱ�����ռ��
 *   	�����ݿռ��ࡣ�������Ա�֤��õ�Ч��
 * @author Jason Zhu
 *
 */
public class GDBPreparedStatement implements PreparedStatement
{
	public static class AccessDBInfo
	{
		/**
		 * ������õ����ʣ���executeUpdate
		 */
		String accessName = null ;
		long startDT = System.currentTimeMillis() ;
		String sql = null ;
		
		public AccessDBInfo(GDBPreparedStatement ps,String accessn)
		{
			accessName = accessn ;
			//startDT = start_dt ;
			sql = ps.sqlStr ;
		}
		
		public AccessDBInfo(GDBPreparedStatement ps,String accessn,String sql)
		{
			accessName = accessn ;
			//startDT = start_dt ;
			this.sql = sql ;
		}
		
		public String getAcessName()
		{
			return accessName ;
		}
		
		public String getSqlStr()
		{
			return this.sql ;
		}
		
		public long getStartDT()
		{
			return startDT ;
		}
		
		public long getDuringMillisSec()
		{
			return System.currentTimeMillis() - startDT ;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder() ;
			
			sb.append(accessName).append('[')
				.append(getDuringMillisSec())
				.append("]ms \r\n\t")
				.append(sql) ;
			
			return sb.toString() ;
		}
	}
	/**
	 * �������ڷ������ݿ�����еı�����
	 * 
	 * ͨ���鿴��������������ݣ�����������ض���������ݿ�����
	 * ������Բ��ҷ������ݿ�ʱ�������Ӧ��
	 * 
	 */
	static Hashtable<GDBPreparedStatement,AccessDBInfo> ACCESS_DB_MON = new Hashtable<GDBPreparedStatement,AccessDBInfo>() ; 
	
	/**
	 * �о���Ϣ���еĵ�ǰ���ڷ������ݿ����Ϣ
	 * @return
	 */
	public static ArrayList<AccessDBInfo> listAccessDBInfo()
	{
		ArrayList<AccessDBInfo> ret = new ArrayList<AccessDBInfo>() ;
		
		for(AccessDBInfo adi:ACCESS_DB_MON.values())
		{
			ret.add(adi) ;
		}
		
		return ret ;
	}
	
	GDBConn gdbConn = null ;
	
	PreparedStatement innerPS = null ;
	
	String sqlStr = null ;
	
	/**
	 * ����Ĳ���
	 */
	String oper = null ;
	
	String dbName = null ;
	/**
	 * ��صı�����
	 */
	String tableName = null ;
	
//	/**
//	 * ��־����
//	 */
//	transient GDBLogItem logItem = null ;
	/**
	 * ����Wrapper��ʽ������
	 * @param conn
	 * @param innerps
	 */
	public GDBPreparedStatement(GDBConn conn,PreparedStatement innerps,String sqlstr)
	{
		gdbConn = conn ;
		dbName = gdbConn.belongPool.getDBName() ;
		innerPS = innerps ;
		
		//����sql������ͣ�������ݱ�����
		sqlStr = sqlstr ;
		
		initLog();
	}
	
	/**
	 * ��ʼ��ͬ����־��Ϣ����
	 *
	 */
	private void initLog()
	{
		String ss = sqlStr.trim().toLowerCase() ;
		if(ss.startsWith("insert"))
		{
			StringTokenizer st = new StringTokenizer(ss," 	(\r\n\t") ;
			st.nextToken() ;
			if(!st.hasMoreTokens())
				return ;
			
			String intostr = st.nextToken() ;
			if(!"into".equalsIgnoreCase(intostr))
				return ;
			
			if(!st.hasMoreTokens())
				return ;
			
			oper = "insert" ;
			tableName = st.nextToken() ;
			
//			GDBLogTable lt = GDBLogManager.getInstance().getLogTable(tableName);
//			if(lt!=null)
//				logItem = new GDBLogItem(tableName,sqlStr) ;
		}
		else if(ss.startsWith("update"))
		{
			StringTokenizer st = new StringTokenizer(ss," 	\r\n\t") ;
			st.nextToken() ;
			if(!st.hasMoreTokens())
				return ;
			
			if(!st.hasMoreTokens())
				return ;
			
			oper = "update" ;
			tableName = st.nextToken() ;
			
//			GDBLogTable lt = GDBLogManager.getInstance().getLogTable(tableName);
//			if(lt!=null)
//				logItem = new GDBLogItem(tableName,sqlStr) ;
		}
		else if(ss.startsWith("delete"))
		{
			StringTokenizer st = new StringTokenizer(ss," 	\r\n\t") ;
			st.nextToken() ;
			if(!st.hasMoreTokens())
				return ;
			
			String intostr = st.nextToken() ;
			if(!"from".equalsIgnoreCase(intostr))
				return ;
			
			if(!st.hasMoreTokens())
				return ;
			
			oper = "delete" ;
			tableName = st.nextToken() ;
			
//			GDBLogTable lt = GDBLogManager.getInstance().getLogTable(tableName);
//			if(lt!=null)
//				logItem = new GDBLogItem(tableName,sqlStr) ;
		}
	}
	
	public ResultSet executeQuery() throws SQLException
	{
		try
		{
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeQuery")) ;
			
			return innerPS.executeQuery();
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public int executeUpdate() throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeUpdate")) ;
			
			int r = innerPS.executeUpdate();
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}
	
	/**
	 * GDB�ڲ�ʹ�õķ��������磺�ֲ�ʽϵͳserver�յ�proxy����־�󣬸��±��ص����ݿ�
	 * ��ʹ�õķ���û�б�Ҫ������־��¼������ʹ�ø÷���
	 * 
	 * @return
	 * @throws SQLException
	 */
	int executeUpdateNoLog() throws SQLException
	{
		return innerPS.executeUpdate() ;
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException
	{
		innerPS.setNull(parameterIndex, sqlType);
//		
//		//log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_NULL,null,sqlType) ;
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException
	{
		innerPS.setBoolean(parameterIndex, x);
//		
//		//log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_BOOL,x) ;
	}

	public void setByte(int parameterIndex, byte x) throws SQLException
	{
		innerPS.setByte(parameterIndex, x);
		
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_BYTE,x) ;
	}

	public void setShort(int parameterIndex, short x) throws SQLException
	{
		innerPS.setShort(parameterIndex, x) ;
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_SHORT,x) ;
	}

	public void setInt(int parameterIndex, int x) throws SQLException
	{
		innerPS.setInt(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_INT,x) ;
	}

	public void setLong(int parameterIndex, long x) throws SQLException
	{
		innerPS.setLong(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_LONG,x) ;
	}

	public void setFloat(int parameterIndex, float x) throws SQLException
	{
		innerPS.setFloat(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_FLOAT,x) ;
	}

	public void setDouble(int parameterIndex, double x) throws SQLException
	{
		innerPS.setDouble(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_DOUBLE,x) ;
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException
	{
		innerPS.setBigDecimal(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_BIGD,x) ;
	}

	public void setString(int parameterIndex, String x) throws SQLException
	{
		innerPS.setString(parameterIndex, x) ;
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_STR,x) ;
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException
	{
		innerPS.setBytes(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_BYTES,x) ;
	}

	public void setDate(int parameterIndex, Date x) throws SQLException
	{
		innerPS.setDate(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_DATE,x) ;
	}

	public void setTime(int parameterIndex, Time x) throws SQLException
	{
		innerPS.setTime(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_TIME,x) ;
	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException
	{
		innerPS.setTimestamp(parameterIndex, x);
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_TIMESTAMP,x) ;
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException
	{
		innerPS.setAsciiStream(parameterIndex, x, length);
		// log
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,"asciistream",x) ;
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException
	{
		innerPS.setUnicodeStream(parameterIndex, x, length) ;
		// log
		//logItem.setLogParam(parameterIndex,"unicodestream",x) ;
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException
	{
		innerPS.setBinaryStream(parameterIndex, x, length);
		// log
		//logItem.setLogParam(parameterIndex,"bigdecimal",x) ;
	}

	public void clearParameters() throws SQLException
	{
		innerPS.clearParameters();
//		// log
//		if(logItem!=null)
//			logItem.clearLogParam();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException
	{
		innerPS.setObject(parameterIndex, x, targetSqlType) ;
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_OBJ,x,targetSqlType) ;
	}

	public void setObject(int parameterIndex, Object x) throws SQLException
	{
		innerPS.setObject(parameterIndex, x) ;
		
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_OBJ,x) ;
	}
	
	
	private void pushLogToConn()
	{
//		if(logItem==null)
//			return ;
//		
//		gdbConn.commitLogBuffer.add(logItem) ;
//		logItem = new GDBLogItem(tableName,sqlStr) ;
	}

	public boolean execute() throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"execute")) ;
			
			boolean b = innerPS.execute();
			
			//��־���͵�������
			pushLogToConn();
			
			return b ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this);
		}
	}

	public void addBatch() throws SQLException
	{
		innerPS.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException
	{
		innerPS.setCharacterStream(parameterIndex, reader, length);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException
	{
		innerPS.setRef(parameterIndex, x);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException
	{
		innerPS.setBlob(parameterIndex, x);
		// log
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,"blob",x) ;
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException
	{
		innerPS.setClob(parameterIndex, x);
		// log
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,"clob",x) ;
	}

	public void setArray(int parameterIndex, Array x) throws SQLException
	{
		innerPS.setArray(parameterIndex, x);
	}

	public ResultSetMetaData getMetaData() throws SQLException
	{
		return innerPS.getMetaData();
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException
	{
		innerPS.setDate(parameterIndex, x, cal);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException
	{
		innerPS.setTime(parameterIndex, x, cal) ;
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException
	{
		innerPS.setTimestamp(parameterIndex, x, cal) ;
	}

	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException
	{
		innerPS.setNull(parameterIndex, sqlType, typeName) ;
		// log
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,"null",sqlType) ;
	}

	public void setURL(int parameterIndex, URL x) throws SQLException
	{
		innerPS.setURL(parameterIndex, x) ;
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_URL,x.toString()) ;
	}

	public ParameterMetaData getParameterMetaData() throws SQLException
	{
		return innerPS.getParameterMetaData();
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException
	{
		innerPS.setRowId(parameterIndex, x) ;
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_ROWID,x) ;
	}

	public void setNString(int parameterIndex, String value)
			throws SQLException
	{
		innerPS.setNString(parameterIndex, value) ;
//		// log
//		if(logItem!=null)
//			logItem.setLogParam(parameterIndex,GDBLogParam.T_SET_NSTR,value) ;

	}

	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException
	{
		innerPS.setNCharacterStream(parameterIndex, value,
				length);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException
	{
		innerPS.setNClob(parameterIndex, value);
		// log
		//if(logItem!=null)
		//	logItem.setLogParam(parameterIndex,"nclob",value) ;

	}

	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException
	{
		innerPS.setClob(parameterIndex, reader, length);
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException
	{
		innerPS.setBlob(parameterIndex, inputStream, length);
	}

	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException
	{
		innerPS.setNClob(parameterIndex, reader, length);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException
	{
		innerPS.setSQLXML(parameterIndex, xmlObject);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException
	{
		innerPS.setObject(parameterIndex, x, targetSqlType,
				scaleOrLength);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException
	{
		innerPS.setAsciiStream(parameterIndex, x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException
	{
		innerPS.setBinaryStream(parameterIndex, x, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException
	{
		innerPS.setCharacterStream(parameterIndex, reader,
				length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException
	{
		innerPS.setAsciiStream(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException
	{
		innerPS.setBinaryStream(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException
	{
		innerPS.setCharacterStream(parameterIndex, reader);
	}

	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException
	{
		innerPS.setNCharacterStream(parameterIndex, value);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException
	{
		innerPS.setClob(parameterIndex, reader);
	}

	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException
	{
		innerPS.setBlob(parameterIndex, inputStream);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException
	{
		innerPS.setNClob(parameterIndex, reader);
	}

	public ResultSet executeQuery(String sql) throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeQuery",sql)) ;
			
			return innerPS.executeQuery(sql);
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public int executeUpdate(String sql) throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeUpdate",sql)) ;
			
			int r = innerPS.executeUpdate(sql);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public void close() throws SQLException
	{
		innerPS.close();
	}

	public int getMaxFieldSize() throws SQLException
	{
		return innerPS.getMaxFieldSize();
	}

	public void setMaxFieldSize(int max) throws SQLException
	{
		innerPS.setMaxFieldSize(max);
	}

	public int getMaxRows() throws SQLException
	{
		return innerPS.getMaxRows();
	}

	public void setMaxRows(int max) throws SQLException
	{
		innerPS.setMaxRows(max);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException
	{
		innerPS.setEscapeProcessing(enable);
	}

	public int getQueryTimeout() throws SQLException
	{
		return innerPS.getQueryTimeout();
	}

	public void setQueryTimeout(int seconds) throws SQLException
	{
		innerPS.setQueryTimeout(seconds);
	}

	public void cancel() throws SQLException
	{
		innerPS.cancel();
	}

	public SQLWarning getWarnings() throws SQLException
	{
		return innerPS.getWarnings();
	}

	public void clearWarnings() throws SQLException
	{
		innerPS.clearWarnings();
	}

	public void setCursorName(String name) throws SQLException
	{
		innerPS.setCursorName(name);
	}

	public boolean execute(String sql) throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"execute",sql)) ;
		
			return innerPS.execute(sql);
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public ResultSet getResultSet() throws SQLException
	{
		return innerPS.getResultSet();
	}

	public int getUpdateCount() throws SQLException
	{
		return innerPS.getUpdateCount();
	}

	public boolean getMoreResults() throws SQLException
	{
		return innerPS.getMoreResults();
	}

	public void setFetchDirection(int direction) throws SQLException
	{
		innerPS.setFetchDirection(direction);
	}

	public int getFetchDirection() throws SQLException
	{
		return innerPS.getFetchDirection();
	}

	public void setFetchSize(int rows) throws SQLException
	{
		innerPS.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException
	{
		return innerPS.getFetchSize();
	}

	public int getResultSetConcurrency() throws SQLException
	{
		return innerPS.getResultSetConcurrency();
	}

	public int getResultSetType() throws SQLException
	{
		return innerPS.getResultSetType();
	}

	public void addBatch(String sql) throws SQLException
	{
		innerPS.addBatch(sql);
	}

	public void clearBatch() throws SQLException
	{
		innerPS.clearBatch();
	}

	public int[] executeBatch() throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeBatch")) ;
		
			return innerPS.executeBatch();
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public Connection getConnection() throws SQLException
	{
		return innerPS.getConnection();
	}

	public boolean getMoreResults(int current) throws SQLException
	{
		return innerPS.getMoreResults(current);
	}

	public ResultSet getGeneratedKeys() throws SQLException
	{
		return innerPS.getGeneratedKeys();
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeUpdate",sql)) ;
		
			int r = innerPS.executeUpdate(sql, autoGeneratedKeys);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeUpdate",sql)) ;
	
			int r = innerPS.executeUpdate(sql, columnIndexes);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"executeUpdate",sql)) ;
	
			int r = innerPS.executeUpdate(sql, columnNames);
			
			this.pushLogToConn() ;
			
			return r;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"execute",sql)) ;
	
		
			boolean r = innerPS.execute(sql, autoGeneratedKeys);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"execute",sql)) ;
	
			boolean r = innerPS.execute(sql, columnIndexes);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException
	{
		try
		{
			//start mon
			ACCESS_DB_MON.put(this,new AccessDBInfo(this,"execute",sql)) ;
	
			boolean r = innerPS.execute(sql, columnNames);
			
			this.pushLogToConn() ;
			
			return r ;
		}
		finally
		{
			ACCESS_DB_MON.remove(this) ;
		}
	}

	public int getResultSetHoldability() throws SQLException
	{
		return innerPS.getResultSetHoldability();
	}

	public boolean isClosed() throws SQLException
	{
		return innerPS.isClosed();
	}

	public void setPoolable(boolean poolable) throws SQLException
	{
		innerPS.setPoolable(poolable);
	}

	public boolean isPoolable() throws SQLException
	{
		return innerPS.isPoolable();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return innerPS.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return innerPS.isWrapperFor(iface);
	}

	public void closeOnCompletion() throws SQLException
	{
		// TODO Auto-generated method stub
		
	}

	public boolean isCloseOnCompletion() throws SQLException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
