package org.iottree.core.store.gdb.connpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;


/**
 * ͨ��Derbyʵ��Ƕ��ʽ�����ݿ� ����֧��ϵͳ�ڲ��Դ������ݿ�
 * 
 * @author Jason Zhu
 * 
 */
public class InnerConnPool extends IConnPool
{
	/**
	 * ��JVM���̽���ǰ����Ҫ���е�ֹͣ���ݿ����
	 * 
	 * @author Jason Zhu
	 * 
	 */
	static class ShutdownRunner implements Runnable
	{
		public void run()
		{
			boolean gotSQLExc = false;
			try
			{
				//System.out.println("<<<shut down derby .....");
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
				//System.out.println("<<<shut down derby succ!!");
			}
			catch (SQLException se)
			{
				gotSQLExc = true;
			}
			
			if (!gotSQLExc)
            {
                System.out.println("<<<derby database did not shut down normally");
            }
            else
            {
                System.out.println("<<<derby database shut down normally");
            }
		}

	}

	static boolean bInit = false;

	static Object locker = new Object();

	static HashMap<String, InnerConnPool> name2icp = new HashMap<String, InnerConnPool>();

	public static InnerConnPool getInnerConnPool(String db_file_dir)
	{
		try
		{
			InnerConnPool icp = name2icp.get(db_file_dir);
			if (icp != null)
				return icp;

			synchronized (locker)
			{
				icp = name2icp.get(db_file_dir);
				if (icp != null)
					return icp;

				icp = new InnerConnPool(db_file_dir);
				name2icp.put(db_file_dir, icp);
				return icp;
			}
		}
		finally
		{
			if (!bInit)
			{
				synchronized (locker)
				{
					if (!bInit)
					{
						// ȷ�����ݿ���JVM���̽���ʱ�ܹ��ر�
						ShutdownRunner sdr = new ShutdownRunner();
						Runtime.getRuntime().addShutdownHook(new Thread(sdr));
						bInit = true;
					}
				}
			}
		}
	}

	public static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	public static String protocol = "jdbc:derby:";

	private String dbDir = null;

	private InnerConnPool(String db_dir)
	{
		dbDir = db_dir;
		
		setPool(this);
	}
	
	public String getDBName()
	{
		return "" ;
	}
	
	public DBInfo getDBInfo()
	{
		DBInfo dbi = new DBInfo();
		dbi.setInfo(DBType.derby,
				driver,getConnUrl(),
				"user1","user1",
				0,20);
		return dbi;
	}
	
	public String getDatabase()
	{
		return null ;
	}

	public DBType getDBType()
	{
		return DBType.derby;
	}
	
	private String getConnUrl()
	{
		return protocol + dbDir
		+ ";create=true";
	}

	protected Connection getConn() throws SQLException
	{
		try
		{
			Class.forName(driver).newInstance();

			Connection conn = null;
			Properties props = new Properties();
			props.put("user", "user1");
			props.put("password", "user1");

			return DriverManager.getConnection(getConnUrl(), props);

		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SQLException("Can't find class for driver: " + driver);
		}
		catch (Throwable e)
		{
			throw new SQLException("some error newConnection = " + e.toString());
		}
	}

	public void free(Connection connection)// throws SQLException
	{
		if(connection==null)
			return ;
		
		try
		{
			connection.close();
		}
		catch(SQLException ee)
		{
			
		}
	}

	public void close()
	{

	}

	@Override
	protected void init() throws SQLException
	{
		
	}
	
	public String toString()
	{
		String info = "InnerConnPool";
		return info;
	}
}
