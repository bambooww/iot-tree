package org.iottree.core.store.gdb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.iottree.core.store.gdb.DBUtil.InstallCB;
import org.iottree.core.store.gdb.autofit.DbSql;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.store.gdb.conf.DBType;
import org.iottree.core.store.gdb.conf.ExeType;
import org.iottree.core.store.gdb.conf.Gdb;
import org.iottree.core.store.gdb.conf.InParam;
import org.iottree.core.store.gdb.conf.SqlItem;
import org.iottree.core.store.gdb.conf.SqlItem.RuntimeItem;
import org.iottree.core.store.gdb.conf.XORM;
import org.iottree.core.store.gdb.connpool.IConnPool;
import org.iottree.core.store.gdb.xorm.XORMUtil;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class GDB
{
	static ILogger log = LoggerManager.getLogger(GDB.class) ;
	
//	public static void executeNoResultSql(String sqlstr) throws GdbException
//	{
//		IConnPool cp = getConnPool();
//		executeNoResultSql(cp, sqlstr);
//	}

	public static void executeNoResultSql(IConnPool cp, String sqlstr)
		throws GdbException
	{
		ArrayList<String> ss = new ArrayList<String>(1) ;
		ss.add(sqlstr) ;
		executeNoResultSqls(cp, ss,null) ;
	}
	
	private static void executeNoResultSqls(IConnPool cp, List<String> install_sqls,InstallCB cb)
		throws GdbException
	{
		Connection conn = null;
		
		try
		{
			conn = cp.getConnection() ;
			executeSqls(false,conn, install_sqls,cb);
		}
		catch (SQLException sqle)
		{
			if(cb!=null)
			{
				cb.onErrInfo("install err:" + sqle.getMessage());
			}
			throw new GdbException(sqle);
		}
		finally
		{
			
			if (conn != null)
			{

				cp.free(conn);
			}

			
		}
	}
	
	public static DBResult executeSql(boolean bresult,Connection conn, String sql)
		throws GdbException
	{
		ArrayList<String> sqls = new ArrayList<String>(1) ;
		sqls.add(sql) ;
		return executeSqls(bresult,conn, sqls,null) ;
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
	
	
	private static DataTable executeQuerySql(Connection conn, String sql,String tablename)
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
			
//			if (conn != null)
//			{
//				cp.free(conn);
//			}
		}
	}
	
	
	private static DataTable getDBTableStruct(Connection conn,String tablename)
		throws Exception
	{
		String sql = "select * from "+tablename+" where 1=0";
		return executeQuerySql(conn, sql,tablename);
	}
	
	/**
	 * 根据XORM检查对应的数据库表是否结构有差异，如果有则自动创建
	 * 相关列，目前对于列的修改还不支持
	 * @param xorm_class
	 * @param cp
	 * @param conn
	 * @param tablename
	 */
	private static void checkAndAlterTable(Class<?> xorm_class,IConnPool cp,Connection conn,String tablename,InstallCB cb)
	{
		try
		{
			DataTable dt = getDBTableStruct(conn,tablename);
			ArrayList<JavaColumnInfo> add_cols = new ArrayList<JavaColumnInfo>() ;
			ArrayList<JavaColumnInfo> alter_cols = new ArrayList<JavaColumnInfo>() ;
			JavaTableInfo jti = XORMUtil.checkTableAlter(xorm_class, dt, add_cols, alter_cols);
			
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
	
	public static DBResult executeParamSql(Connection conn,
			String param_sqlstr,HashMap<String,String> inparam_n2t,
			Hashtable params,int idx,int count,IDBSelectCallback cb)
		throws Exception
	{
		SqlItem si = SqlItem.parseSqlItem(param_sqlstr,inparam_n2t) ;
		DBResult dbr = new DBResult() ;
		executeParamSql(DBType.mysql,conn,si,params, idx, count,dbr,cb) ;
		return dbr ;
	}
	
	private static DataTable executeParamSql(DBType dbt,Connection conn,
			SqlItem si,Hashtable parms,
			int idx, int count,DBResult dbr,IDBSelectCallback cb
			) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try
		{
			RuntimeItem ri = si.getRuntimeItem(dbt, parms);
			String sql = ri.getJdbcSql();
			
			if (log.isDebugEnabled())
			{
				log.debug("gdb executeSqlQuery :" + sql);
				log.debug("input param------------->\n");
				printInParam(parms);
			}
			
			long run_cost_st = System.currentTimeMillis() ;
			
			ps = conn.prepareStatement(sql);
			

			// 设置参数
			prepareStatement(ps, ri, parms);

			DataTable dt = null;
			ExeType et = ri.getExeType();
			if (et == ExeType.dataset || et == ExeType.select
					|| et == ExeType.scalar)
			{
				if (count > 0)
				{// 需要支持翻页
					ps.setMaxRows(idx + count);
					// if (sql.fetchSize > 0)//fetch size
					// 表示结果集一次访问数据库取回多少条记录
					// rs.setFetchSize (sql.fetchSize) ;
				}

				rs = ps.executeQuery();
				dt = dbr.appendResultSet(si.getResultTableName(),0,rs, idx, count,cb);

			}
			else if (et == ExeType.update || et == ExeType.insert
					|| et == ExeType.delete)
			{
				if (count > 0)
					throw new GdbException("not page select db access!");

				dbr.rowsAffected = ps.executeUpdate();
				dt = dbr.appendRowsAffected(dbr.rowsAffected);
			}
			else
			{
				if (count > 0)
					throw new GdbException("not page select db access!");

				ps.execute();
			}
			
			si.setRunCostStEt(run_cost_st,System.currentTimeMillis(),sql,transInParam2Str(parms)) ;

			if (rs != null)
			{
				rs.close();
				rs = null;
			}

			ps.close();
			ps = null;
			return dt;
		}
		finally
		{
			if (rs != null)
				rs.close();

			if (ps != null)
				ps.close();
		}
	}
	
	private static void printInParam(Hashtable ht)
	{
		if (ht == null)
			return;

		for (Object o : ht.keySet())
		{
			Object ov = ht.get(o);
			if (ov instanceof Date)
			{
				Date d = (Date) ov;
				log.info(o.toString() + "=" + Convert.toFullYMDHMS(d));
			}
			else
			{
				log.info(o.toString() + "=" + ov.toString());
			}
		}
	}

	private static String transInParam2Str(Hashtable ht)
	{
		if (ht == null)
			return "";

		StringBuilder ret = new StringBuilder() ;
		for (Object o : ht.keySet())
		{
			Object ov = ht.get(o);
			if (ov instanceof Date)
			{
				Date d = (Date) ov;
				ret.append(o.toString()).append("=").append(Convert.toFullYMDHMS(d));
			}
			else
			{
				String ss = ov.toString() ;
				ret.append(o.toString()).append("=") ;
				if(ss.length()<100)
					ret.append(ss);
				else
					ret.append(ss.substring(0,100)).append("...");
			}
		}
		return ret.toString() ;
	}
	
	private static void prepareStatement(PreparedStatement ps, RuntimeItem ri,
			Hashtable parms) throws IOException, SQLException, GdbException
	{
		ArrayList<InParam> rtpns = ri.getRtParams();
		if (rtpns != null)
		{
			int pnn = rtpns.size();
			for (int i = 0; i < pnn; i++)
			{
				InParam ip = rtpns.get(i);
				String pName = ip.getName();
				// System.out.println("ps pn=====" + pName);
				int type = ip.getJdbcType();
				Object pValue = parms.get(ip.getName());

				if (pValue == null)
					pValue = ip.getDefaultVal();

				if (pValue == null)
				{
					ps.setNull(i + 1, type);
					continue;
				}

				switch (type)
				{
				case Types.BLOB:
				case Types.LONGVARBINARY:
					InputStream ins = null;
					if (pValue instanceof InputStream)
						ins = (InputStream) pValue;
					else if (pValue instanceof byte[])
						ins = new ByteArrayInputStream((byte[]) pValue);
					else
						ins = new ByteArrayInputStream(String.valueOf(pValue)
								.getBytes("UTF8"));

					ps.setBinaryStream(i + 1, ins, ins.available());
					break;
				case Types.CLOB:
				case Types.LONGVARCHAR:
					// Reader reader = null ;
					String valueString = String.valueOf(pValue);
					/*
					 * if (pValue instanceof Reader) { reader = (Reader) pValue ; }
					 * 
					 * else
					 */
					StringReader reader = new StringReader(valueString);

					ps.setCharacterStream(i + 1, reader, valueString.length());
					break;
				case Types.DATE:
					java.sql.Date theDate = null;
					if (pValue instanceof java.util.Date)
						theDate = new java.sql.Date(((java.util.Date) pValue)
								.getTime());
					else if (pValue instanceof java.util.Calendar)
						theDate = new java.sql.Date(
								((java.util.Calendar) pValue).getTime()
										.getTime());
					else
						throw new GdbException("Unsupport Parameter Type ["
								+ pValue.getClass().getName() + "] value ["
								+ pValue + "], can't convert it into Date ["
								+ pName + "]");
					ps.setDate(i + 1, theDate);
					break;
				case Types.TIME:
					java.sql.Time theTime = null;
					if (pValue instanceof java.util.Date)
						theTime = new java.sql.Time(((java.util.Date) pValue)
								.getTime());
					else if (pValue instanceof java.util.Calendar)
						theTime = new java.sql.Time(
								((java.util.Calendar) pValue).getTime()
										.getTime());
					else
						throw new GdbException("Unsupport Parameter Type ["
								+ pValue.getClass().getName() + "] value ["
								+ pValue + "], can't convert it into Time ["
								+ pName + "]");
					ps.setTime(i + 1, theTime);
					break;
				case Types.NULL:

					break;
				case Types.TIMESTAMP:
					java.sql.Timestamp theTimestamp = null;
					if (pValue instanceof java.util.Date)
						theTimestamp = new java.sql.Timestamp(
								((java.util.Date) pValue).getTime());
					else if (pValue instanceof java.util.Calendar)
						theTimestamp = new java.sql.Timestamp(
								((java.util.Calendar) pValue).getTime()
										.getTime());
					else
						throw new GdbException("Unsupport Parameter Type ["
								+ pValue.getClass().getName() + "] value ["
								+ pValue
								+ "], can't convert it into Timestamp ["
								+ pName + "]");
					ps.setTimestamp(i + 1, theTimestamp);
					break;
				case Types.FLOAT:
					float _float = 0;
					if (pValue instanceof Number)
						_float = ((Number) pValue).floatValue();
					else
					{
						try
						{
							_float = Float.parseFloat(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a float: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "], can't convert it into float ["
									+ pName + "]");
						}
					}
					ps.setFloat(i + 1, _float);
					break;
				case Types.REAL:
				case Types.DOUBLE:
					double _double = 0;
					if (pValue instanceof Number)
						_double = ((Number) pValue).doubleValue();
					else
					{
						try
						{
							_double = Double
									.parseDouble(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a double: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "], can't convert it into double ["
									+ pName + "]");
						}
					}
					ps.setDouble(i + 1, _double);
					break;
				case Types.BIGINT:
					long _long = 0;
					if (pValue instanceof Number)
						_long = ((Number) pValue).longValue();
					else
					{
						try
						{
							_long = Long.parseLong(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a long: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "], can't convert it into long [" + pName
									+ "]");
						}
					}
					ps.setLong(i + 1, _long);
					break;
				case Types.INTEGER:
					int _int = 0;
					if (pValue instanceof Number)
						_int = ((Number) pValue).intValue();
					else
					{
						try
						{
							_int = Integer.parseInt(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a int: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "] , can't convert it into int [" + pName
									+ "]");
						}
					}
					ps.setInt(i + 1, _int);
					break;
				case Types.DECIMAL:
				case Types.NUMERIC:
					BigDecimal dec = null;

					// if (pValue instanceof Number)
					// dec = new BigDecimal (((Number) pValue).doubleValue ()) ;
					// else
					{
						try
						{
							dec = new BigDecimal(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a DECIMAL: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "] , can't convert it into DECIMAL ["
									+ pName + "]");
						}
					}
					// System.out.println ("Dec : " + dec.scale ()) ;
					ps.setBigDecimal(i + 1, dec);
					break;
				case Types.SMALLINT:
					short _short = 0;
					if (pValue instanceof Number)
						_short = ((Number) pValue).shortValue();
					else
					{
						try
						{
							_short = Short.parseShort(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a short: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "] , can't convert it into short ["
									+ pName + "]");
						}
					}
					ps.setShort(i + 1, _short);
					break;
				case Types.TINYINT:
					byte _byte = 0;
					if (pValue instanceof Number)
						_byte = ((Number) pValue).byteValue();
					else if( (pValue instanceof String) &&
							("true".equalsIgnoreCase((String)pValue)||"false".equalsIgnoreCase((String)pValue)))
					{
						if("true".equalsIgnoreCase((String)pValue))
							_byte = 1 ;
						else if("false".equalsIgnoreCase((String)pValue))
							_byte = 0 ;
					}
					else
					{
						try
						{
							_byte = Byte.parseByte(String.valueOf(pValue));
						}
						catch (Throwable _t)
						{
							if (log.isDebugEnabled())
								log
										.debug(
												"When try to convert parameter as a byte: ",
												_t);

							throw new GdbException("Unsupport Parameter Type ["
									+ pValue.getClass().getName() + "] value ["
									+ pValue
									+ "] , can't convert it into byte ["
									+ pName + "]");
						}
					}
					ps.setByte(i + 1, _byte);
					break;
				case Types.BINARY:
				case Types.VARBINARY:
					byte[] bytes = null;
					if (pValue instanceof byte[])
						bytes = (byte[]) pValue;
					else
						bytes = String.valueOf(pValue).getBytes("UTF8");

					ps.setBytes(i + 1, bytes);
					break;
				case Types.BIT:
					if(pValue instanceof Boolean)
						ps.setBoolean(i+1, ((Boolean)pValue).booleanValue()) ;
					else if(pValue instanceof Number)
						ps.setBoolean(i+1, ((Number)pValue).intValue()>0) ;
					else if(pValue instanceof String)
					{
						if("true".equalsIgnoreCase((String)pValue))
							ps.setBoolean(i+1, true) ;
						else if("false".equalsIgnoreCase((String)pValue))
							ps.setBoolean(i+1, false) ;
					}
					else
						ps.setObject(i+1, pValue);
					break ;
				case Types.CHAR:
				case Types.VARCHAR:
				default:
					ps.setString(i + 1, String.valueOf(pValue));

					break;
				}
			}
		}

	}
	
	static Object prepareObjVal(Object o)
	{
		if(o==null)
			return null;
		
		if ((o instanceof java.util.Date)
				&& !(o instanceof java.sql.Timestamp))
		{
			return new java.sql.Timestamp(((java.util.Date) o)
					.getTime());
		}
		
		return o ;
	}
	
	
	//
	
	public static boolean updateXORMObjToDBWithHasColNameValues(Connection conn,String tablen,Object pkid,
			Class xorm_c, String[] property_names, Object[] prop_vals)
			throws ClassNotFoundException, GdbException
	{
		if (property_names == null || property_names.length <= 0
				|| prop_vals == null || prop_vals.length <= 0)
			throw new IllegalArgumentException(
					"xorm property and value cannot be empty!");

		if (property_names.length != prop_vals.length)
			throw new IllegalArgumentException(
					"xorm property and value length must same!");

		XORM xorm_conf = Gdb.getXORMByGlobal(xorm_c);
		if (xorm_conf == null)
			throw new IllegalArgumentException(
					"cannot get XORM config info with class="
							+ xorm_c.getCanonicalName());

		JavaTableInfo jti = xorm_conf.getJavaTableInfo();
		if(Convert.isNullOrEmpty(tablen))
			tablen = jti.getTableName();

		StringBuilder sb = new StringBuilder();
		sb.append("update ").append(tablen).append(" set ");
		sb.append(property_names[0]).append("=?");
		for (int i = 1; i < property_names.length; i++)
		{
			sb.append(',').append(property_names[i]).append("=?");
		}
		sb.append(" where ").append(jti.getPkColumnInfo().getColumnName())
				.append("=?");

		String sql = sb.toString();
		
		GDBTransInThread tit = GDBTransInThread.getTransInThread() ;

		//IConnPool cp = getConnPool(xorm_conf.getBelongToGdb());

		Connection conn0 = conn;
		PreparedStatement ps = null;
		boolean old_ac = true;
		
		try
		{
			ps = conn0.prepareStatement(sql);

			// Hashtable pm = XORMUtil.extractXORMObjAsSqlInputParam(xorm_obj);
			if (log.isDebugEnabled())
			{
				Hashtable pm = new Hashtable();
				for (int k = 0; k < property_names.length; k++)
				{
					if(prop_vals[k]==null)
						continue ;
					pm.put(property_names[k], prop_vals[k]);
				}
				log.debug("[updateXORMObjToDBWithHasColNames sql]==" + sql);
				log.debug("input param------------->\n");
				printInParam(pm);
			}

			long tt = System.currentTimeMillis();
			// JavaColumnInfo[] nor_jcis = jti.getNorColumnInfos();
			for (int i = 0; i < property_names.length; i++)
			{
				Object tmpo = prepareObjVal(prop_vals[i]);
				if (tmpo != null)
				{
					ps.setObject(1 + i, tmpo);
				}
				else
				{
					JavaColumnInfo jci = jti
							.getNorColumnInfo(property_names[i]);
					if (jci == null)
						throw new IllegalArgumentException(
								"no nor column found with property name="
										+ property_names[i]);
					ps.setNull(1 + i, jci.getSqlValType());
				}
			}

			ps.setObject(property_names.length + 1, pkid);

			int rowaff = ps.executeUpdate();

			ps.close();
			ps = null;
			
			//dtAutoUpdate(conn0,pkid,xorm_conf,null) ;
			
			return rowaff == 1;
		}
		catch (Exception e)
		{
			log.error(e);
			throw new GdbException(e);
		}
		finally
		{
			try
			{
				if (ps != null)
				{
					ps.close();
				}
			}
			catch (Throwable sqle)
			{
			}
			
		}
	}
}
