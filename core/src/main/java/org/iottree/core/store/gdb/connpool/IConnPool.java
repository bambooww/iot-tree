package org.iottree.core.store.gdb.connpool;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public abstract class IConnPool
{
	static Vector<IConnPool> ALL_CONN_POOLS = new Vector<IConnPool>();

	static Hashtable<String, IConnPool> NAME2POOL = new Hashtable<String, IConnPool>();

	/**
	 * 
	 * @return
	 */
	public static IConnPool[] getAllConnPools()
	{
		synchronized (ALL_CONN_POOLS)
		{
			IConnPool[] rets = new IConnPool[ALL_CONN_POOLS.size()];
			ALL_CONN_POOLS.toArray(rets);
			return rets;
		}
	}

	/**
	 * @param n
	 * @return
	 */
	public static IConnPool getConnPool(String n)
	{
		return NAME2POOL.get(n);
	}

	/**
	 * 
	 * @param ps
	 */
	public static void printAllPoolInfo(PrintStream ps)
	{
		IConnPool[] dps = getAllConnPools();
		for (IConnPool cp : dps)
		{
			ps.println(cp.toString());
		}
	}
	
	public static void printAllPoolInfo(PrintWriter ps)
	{
		IConnPool[] dps = getAllConnPools();
		for (IConnPool cp : dps)
		{
			ps.println(cp.toString());
		}
	}

	static void setPool(IConnPool cp)
	{
		synchronized (ALL_CONN_POOLS)
		{
			if (!ALL_CONN_POOLS.contains(cp))
				ALL_CONN_POOLS.add(cp);

			NAME2POOL.put(cp.getDBName(), cp);
		}
	}

	static void unsetPool(IConnPool cp)
	{
		synchronized (ALL_CONN_POOLS)
		{
			ALL_CONN_POOLS.remove(cp);
			NAME2POOL.remove(cp.getDBName());
		}
	}

	static ILogger log = LoggerManager.getLogger(DBConnPool.class
			.getCanonicalName());
	
	private Vector<IConnPoolToucher> connPoolLis = new Vector<IConnPoolToucher>();
	
	/**
	 * �Ƿ�����
	 */
	private boolean bRunning = true ;

	public abstract String getDBName();
	
	
	
	public abstract DBType getDBType();
	
	public abstract String getDatabase() ;
	
	protected abstract void init() throws SQLException;
	
	
	public void start()
	{
		if(bRunning)
			return ;
	}
	
	public void stop()
	{
		if(!bRunning)
			return ;
		
	}
	
	public boolean isRunning()
	{
		return bRunning ;
	}
	
	public Connection getConnection() throws SQLException
	{
		init();
		
		Connection conn = getConn();
		if (conn == null)
			return null;

		if (connPoolLis.size() <= 0)
			return conn;

		synchronized (this)
		{
			if (connPoolLis.size() <= 0)
				return conn;

			// �ͷŵڱ����ӣ��Ա���IConnPoolListenerʹ��
			this.free(conn);

			while (connPoolLis.size() > 0)
			{
				// ��ȡ��Toucher���һص�֪ͨ
				IConnPoolToucher cpt = connPoolLis
						.remove(connPoolLis.size() - 1);
				cpt.OnMeBeTouched();
			}
		}

		return getConn();
	}

	public synchronized void putToucher(IConnPoolToucher cpt)
	{
		connPoolLis.add(cpt);
	}
	
	public abstract DBInfo getDBInfo();
	
	protected abstract Connection getConn() throws SQLException;
	
	public abstract void free(Connection connection);// throws SQLException;
	
	public abstract void close();
}
