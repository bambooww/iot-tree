package org.iottree.core.store.gdb.connpool;

import java.io.PrintStream;
import java.sql.*;
import java.util.*;

import org.w3c.dom.Element;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

public class DBConnPool extends IConnPool implements Runnable,IXmlDataable
{
	/**
	 * 
	 */
	static ILogger log = LoggerManager.getLogger(DBConnPool.class) ;
	
	
	public static DBConnPool createFromEle(Element db_ele,String url,ClassLoader cl)
		throws SQLException
	{
		String name = db_ele.getAttribute("name");
		if (name == null || name.equals(""))
			return null;
		
		DBType dbt = DBType.derby;
		String str_dbt = db_ele.getAttribute("type");
		if (str_dbt != null && !str_dbt.equals(""))
			dbt = DBType.valueOf(str_dbt);

		String driver = db_ele.getAttribute("driver");
		if(url==null||url.equals(""))
			url = db_ele.getAttribute("url");
		String database = db_ele.getAttribute("database") ;
		String usern = db_ele.getAttribute("username");
		String psw = db_ele.getAttribute("password");
		String init_num = db_ele.getAttribute("initnumber");
		String max_num = db_ele.getAttribute("maxnumber");
		
		
		
		DBConnPool db_cp = new DBConnPool(dbt, name, driver, url,database, usern,
				psw, init_num, max_num,cl);
		
		db_cp.relatedEle = db_ele ;
		String domain_scope = db_ele.getAttribute("domain_scope") ;
		if(Convert.isNotNullEmpty(domain_scope))
		{
			int k = domain_scope.indexOf("-") ;
			if(k>0)
			{
				db_cp.domainScopeMin = Integer.parseInt(domain_scope.substring(0,k)) ;
				db_cp.domainScopeMax = Integer.parseInt(domain_scope.substring(k+1)) ;
			}
		}
		
		return db_cp ;
	}

	private class ConnectionWarpper
	{
		public boolean equals(Object obj)
		{
			return (obj instanceof ConnectionWarpper)
					&& ((ConnectionWarpper) obj).conn == conn;
		}
		
		public boolean isClosed()
		{
			try
			{
				closed = conn.isClosed() || testTable();
			}
			catch (Throwable _t)
			{
				return true;
			}
			return closed
					|| System.currentTimeMillis() - createTime > DBConnPool.LIFE_TIME
					|| accessCount > DBConnPool.MAX_ACCESS_COUNT;
		}

		public void close()
		{
			try
			{
				//if (!closed)
				conn.close();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}

		public boolean testTable()
		// throws SQLException , IOException
		{
			if (testTableName == null || testSql == null)
				return false;
			Statement st = null;
			ResultSet rset = null;
			boolean closedFlag = true;
			try
			{
				st = conn.createStatement();
				int timeOut = st.getQueryTimeout();
				st.setQueryTimeout(1);
				rset = st.executeQuery(testSql);
				st.setQueryTimeout(timeOut);
				closedFlag = false;
			}
			catch (Throwable _t)
			{
				// log.log ("Error occurs during test [" + testTableName + "].")
				// ;
				// log.log (_t) ;
				return closedFlag;
			}
			finally
			{
				try
				{
					if (rset != null)
					{
						rset.close();
						rset = null;
					}
				}
				catch (Throwable _t)
				{
				}
				try
				{
					if (st != null)
					{
						st.close();
						st = null;
					}
				}
				catch (Throwable _t)
				{
				}

				// release Connection.
				if (closedFlag)
				{
					try
					{
						conn.close();
					}
					catch (Throwable _t)
					{
					}
				}
			}

			return closedFlag;
		}

		long createTime;

		int accessCount;

		GDBConn conn;

		boolean closed;

		int errorCount;
		
		transient Thread busyThread = null ;

		public ConnectionWarpper(GDBConn conn)
		{
			createTime = System.currentTimeMillis();
			accessCount = 0;
			this.conn = null;
			closed = false;
			errorCount = 0;
			this.conn = conn;
		}
		
		public Thread getBusyThread()
		{
			return busyThread ;
		}
	}

	private String dbName;

	private DBType dbType = DBType.derby;
	
	/**
	 * ���ӳض�Ӧ�ķ�Χ��Сֵ
	 */
	private int domainScopeMin = -1 ;
	
	/**
	 * ���ӳض�Ӧ��Χ���ֵ
	 * �˳�Ա����֧��һЩ�ֲ�ʽ�򻷾��µ�Զ�̷���֧��
	 */
	private int domainScopeMax = -1 ;

	private int totalNewConnections;

	private String driver;

	private String url;
	
	private String database=null;

	private String username;

	private String password;

	private int initialConnections, maxConnections;

	private boolean waitIfBusy;

	private Vector<ConnectionWarpper> availableConnections = new Vector<ConnectionWarpper>();

	private Vector<ConnectionWarpper> busyConnections = new Vector<ConnectionWarpper>();

	private boolean connectionPending;

	private String testTableName;

	private String testSql;

	private static long LIFE_TIME = 900000; // 900000����

	private static int MAX_ACCESS_COUNT = 100; // ����100��

	private long failedRetryLater = System.currentTimeMillis();
	
	private Exception failedRetryLaterEx = null ;

	private transient Properties connProp = null;

	private transient boolean bInited = false;
	
	
	transient Element relatedEle = null ;
	
	transient ClassLoader classLD = null ;

	public DBConnPool(DBType dbt, String dbname, String driver, String url,String database,
			String user, String psw, String init_num, String max_num,ClassLoader cl)
			//throws SQLException
	{
		Properties tmpp = new Properties();
		tmpp.setProperty("db.name", dbname);
		tmpp.setProperty("db.driver", driver);
		tmpp.setProperty("db.url", url);
		if(database!=null)
			tmpp.setProperty("db.database", database);
		if(Convert.isNotNullEmpty(user))
		{
			tmpp.setProperty("db.username", user);
			if(psw!=null)
				tmpp.setProperty("db.password", psw);
		}
		if (init_num != null)
			tmpp.setProperty("db.initnumber", init_num);
		if (max_num != null)
			tmpp.setProperty("db.maxnumber", max_num);

		createMe(dbt, tmpp,cl);
	}
	
	//Properties 

	public DBConnPool(DBType dbt, Properties p,ClassLoader cl) throws SQLException
	{
		createMe(dbt, p,cl);
	}
	
	public Element getRelatedEle()
	{
		return relatedEle ;
	}

	private void createMe(DBType dbt, Properties p,ClassLoader cl) //throws SQLException
	{
		if(cl==null)
			throw new IllegalArgumentException("no ClassLoad input") ;
		
		this.classLD = cl ;
		
		connProp = p;

		dbType = dbt;

		totalNewConnections = 0;
		connectionPending = false;
		testTableName = null;
		testSql = null;
		driver = p.getProperty("db.driver");
		url = p.getProperty("db.url");
		this.database = p.getProperty("db.database") ;
		username = p.getProperty("db.username");
		password = p.getProperty("db.password");
		dbName = p.getProperty("db.name");
		testTableName = p.getProperty("testtable.name");

		this.waitIfBusy = true;

		try
		{
			initialConnections = Integer.parseInt(p
					.getProperty("db.initnumber"));
			maxConnections = Integer.parseInt(p.getProperty("db.maxnumber"));
		}
		catch (Throwable e)
		{
			initialConnections = 1;
			maxConnections = 10;
		}

		if (driver == null || driver.length() <= 0 || url == null
				|| url.length() <= 0)
		{
			throw new IllegalArgumentException("Configure file format error.");
		}

			// init(driver, url, username, password, initnum, maxnum, true);

		setPool(this);

	}

	public String getDBName()
	{
		return dbName;
	}
	
	public String getDatabase()
	{
		return this.database ;
	}
	
	public DBInfo getDBInfo()
	{
		DBInfo dbi = new DBInfo();
		dbi.setInfo(dbType,
				driver,url,
				username,password,
				initialConnections,maxConnections);
		return dbi;
	}
	
	public int getDomainScopeMin()
	{
		return domainScopeMin ;
	}
	
	public int getDomainScopeMax()
	{
		return domainScopeMax ;
	}

	public Properties getConnProp()
	{
		return connProp;
	}

	public DBType getDBType()
	{
		return dbType;
	}

	protected void init() throws SQLException
	{
		if (bInited)
			return;

		String logName = dbName;
		if (dbName == null)
			logName = username;

		// if (testTableName != null)
		// {
		// testTableName = testTableName.trim();
		// if (testTableName.length() == 0)
		// testTableName = null;
		// }
		// if (testTableName != null)
		// testSql = "SELECT * FROM " + testTableName;
		// this.driver = driver;
		// this.url = url;
		// this.username = username;
		// this.password = password;
		// this.maxConnections = maxConnections;
		// this.waitIfBusy = true;
		if (initialConnections > maxConnections)
			initialConnections = maxConnections;
		
		try
		{
			for (int i = 0; i < initialConnections; i++)
				availableConnections.addElement(new ConnectionWarpper(
						makeNewConnection()));
		}
		catch (Throwable _t)
		{
			log.error("conn pool init error dbname="+dbName+"!");
			// _t.printStackTrace();
		}

		bInited = true;
	}

	protected void init(String driver, String url, String username,
			String password, int initialConnections, int maxConnections,
			boolean waitIfBusy) throws SQLException
	{
		if (bInited)
			return;

		try
		{
			String logName = dbName;
			if (dbName == null)
				logName = username;
		}
		catch (Throwable _t)
		{
			_t.printStackTrace();
		}
		if (testTableName != null)
		{
			testTableName = testTableName.trim();
			if (testTableName.length() == 0)
				testTableName = null;
		}
		if (testTableName != null)
			testSql = "SELECT * FROM " + testTableName;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.maxConnections = maxConnections;
		this.waitIfBusy = waitIfBusy;
		if (initialConnections > maxConnections)
			initialConnections = maxConnections;
		
		try
		{
			for (int i = 0; i < initialConnections; i++)
				availableConnections.addElement(new ConnectionWarpper(
						makeNewConnection()));
		}
		catch (Throwable _t)
		{
			log.error("conn pool init error dbname="+dbName);
			// _t.printStackTrace();
		}

		bInited = true;
	}

	public int getMaxConnectionNumber()
	{
		return maxConnections;
	}

	public int getCurrentConnectionNumber()
	{
		return availableConnections.size();
	}

	public synchronized void setMaxConnectionNumber(int maxlen)
	{
		if (maxlen <= availableConnections.size())
		{
			return;
		}
		else
		{
			maxConnections = maxlen;
			return;
		}
	}

	

	protected synchronized Connection getConn() throws SQLException
	{
		if(log.isDebugEnabled())
		{
			log.debug("DBConnPool getConn()  when free=["+availableConnections.size()+"] busy=["+busyConnections.size()+"]");
		}
		
		do
		{
			//���busyConnections�е��Ѿ��رյ�����
			//������֧�������ر����ӵĴ���
			ArrayList<ConnectionWarpper> bcds = new ArrayList<ConnectionWarpper>() ;
			for(ConnectionWarpper cw:busyConnections)
			{
				if(cw.conn.isClosed())
				{
					//System.out.println("find closed busy conn access count=="+cw.accessCount+" live="+(System.currentTimeMillis()-cw.createTime)) ;
					bcds.add(cw) ;
				}
			}
			for(ConnectionWarpper cw:bcds)
			{
				busyConnections.remove(cw) ;
			}
			
			
			//�������
			while (!availableConnections.isEmpty())
			{
				ConnectionWarpper existingConnection = (ConnectionWarpper) availableConnections
						.elementAt(0);
				availableConnections.removeElementAt(0);
				existingConnection.accessCount++;
				existingConnection.conn.accessCount ++ ;
				if (existingConnection.isClosed())
				{
					existingConnection.close();
				}
				else
				{
					existingConnection.busyThread = Thread.currentThread() ;
					busyConnections.addElement(existingConnection);
					return existingConnection.conn;
				}
			}
			if (totalConnections() < maxConnections && !connectionPending)
			{
				makeForegroundConnection();
			}
			else
			{
				if (!waitIfBusy)
					throw new SQLException("Connection limit reached");
				try
				{
					wait();
				}
				catch (Throwable ie)
				{
					ie.printStackTrace();
				}
			}
		}
		while (true);
	}

	private void makeBackgroundConnection()
	{
		connectionPending = true;
		try
		{
			Thread connectThread = new Thread(this,"gdb-dbconnpool");
			connectThread.start();
		}
		catch (OutOfMemoryError oome)
		{
			oome.printStackTrace();
		}
	}

	private void makeForegroundConnection() throws SQLException
	{
		Connection conn = makeNewConnection();
		if (conn == null)
		{
			throw new SQLException(
					"Can't Create new Connection. Connection is NULL.");
		}
		else
		{
			free(conn);
			return;
		}
	}

	public void run()
	{
		try
		{
			Connection connection = makeNewConnection();
			free(connection);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				synchronized (this)
				{
					connectionPending = false;
					notify();
				}
			}
			catch (Throwable _t)
			{
				_t.printStackTrace();
			}
		}
		return;
	}
	
	private Driver jdbcDrv = null ;
	
	private Driver getJdbcDrv() throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if(jdbcDrv!=null)
			return jdbcDrv ;
		
		synchronized(this)
		{
			jdbcDrv = (Driver) Class.forName(driver,true,this.classLD).newInstance() ;
		}
		
		return jdbcDrv ;
	}

	private GDBConn makeNewConnection() throws SQLException
	{
		if (System.currentTimeMillis() < failedRetryLater)
		{//
			if(failedRetryLaterEx!=null)
				throw new SQLException("server is in retry later["+dbName+"]!",failedRetryLaterEx);
			else
				throw new SQLException("server is in retry later["+dbName+"]!");
		}

		try
		{
			Properties info = new Properties() ;
			if(Convert.isNotNullEmpty(username))
			{
				info.put("user", username);
				
			    if (password != null) {
			        info.put("password", password);
			    }
			}
			
			Connection conn = getJdbcDrv().connect(url,info) ;
			return new GDBConn(this,conn);
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new SQLException("Can't find class for driver: " + driver);
		}
		catch (SQLException sqle)
		{
			failedRetryLaterEx = sqle ;
			failedRetryLater = System.currentTimeMillis() + 5000;
			throw sqle;
		}
		catch (Throwable e)
		{
			System.err.println("makeNewConnection err driver="+driver +" url="+url) ;
			throw new SQLException("some error newConnection = " + e.toString());
		}
	}

	@Override
	public synchronized void free(Connection conn)
	{
		if(conn==null)
			return ;
		
		if(!(conn instanceof GDBConn))
		{
			try
			{
				//close others
				conn.close() ;
			}
			catch(Exception ee)
			{}
			return ;
		}
		GDBConn gdbc = (GDBConn)conn;
		
		//�����־����
		gdbc.clearLogBuffer() ;
		
		ConnectionWarpper cw = new ConnectionWarpper(gdbc);
		int index = busyConnections.indexOf(cw);
		if (index >= 0)
		{
			cw = busyConnections.elementAt(index);
			busyConnections.removeElementAt(index);
		}
		if (!availableConnections.contains(cw))
			availableConnections.addElement(cw);
		notify();
	}

	public int totalConnections()
	{
		return availableConnections.size() + busyConnections.size();
	}

	public synchronized void close()
	{
		closeConnections(availableConnections);
		availableConnections = new Vector<ConnectionWarpper>();
		closeConnections(busyConnections);
		busyConnections = new Vector<ConnectionWarpper>();

		unsetPool(this);
	}

	private void closeConnections(Vector<ConnectionWarpper> connections)
	{
		for (int i = 0; i < connections.size(); i++)
			connections.elementAt(i).close();

	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append("DBConnectionPool(").append(url).append(",")
			.append(username).append(")")
			.append(", available=").append(availableConnections.size()).append(", busy=")
			.append(busyConnections.size()).append(", max=").append(maxConnections).append("\n");
		
		
		for(Object tmpo:busyConnections.toArray())
		{
			ConnectionWarpper cw = (ConnectionWarpper)tmpo ;
			sb.append("\nBusy Conn >>Thread>>").append(cw.busyThread.toString())
				.append(" Alive=").append(cw.busyThread.isAlive()).append(" Conn=").append(!cw.isClosed())
				.append(" livetime=").append(System.currentTimeMillis()-cw.createTime).append(" AccessCount=").append(cw.accessCount) ;
		}
		
		for(Object tmpo:this.availableConnections.toArray())
		{
			ConnectionWarpper cw = (ConnectionWarpper)tmpo ;
			sb.append("\nFree ").append(" Conn=").append(!cw.isClosed())
				.append(" livetime=").append(System.currentTimeMillis()-cw.createTime).append(" AccessCount=").append(cw.accessCount) ;
		}
		return sb.toString();
	}
	
	public String toPoolConnInfoStr()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append("Pool busynum=")
			.append(busyConnections.size())
			.append(" freenum=")
			.append(availableConnections.size()) ;
		return sb.toString() ;
	}

	protected void finalize() throws Throwable
	{
		try
		{
			close();
		}
		catch (Throwable _t)
		{
			_t.printStackTrace();
		}
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData() ;
		
		if(dbName!=null)
			xd.setParamValue("name",dbName);
		if(dbType!=null)
			xd.setParamValue("type",dbType.name());
		
		if(driver!=null)
			xd.setParamValue("driver", driver);
		
		if(url!=null)
			xd.setParamValue("url", url);
		if(username!=null)
			xd.setParamValue("username", username);
		if(password!=null)
			xd.setParamValue("password", password);
		if(initialConnections>0)
			xd.setParamValue("init_num", ""+initialConnections);
		if(maxConnections>0)
			xd.setParamValue("max_num", ""+maxConnections);
		
		
		
//		DBConnPool db_cp = new DBConnPool(dbt, name, driver, url, usern,
//				psw, init_num, max_num);
//		
//		db_cp.relatedEle = db_ele ;
//		String domain_scope = db_ele.getAttribute("domain_scope") ;
//		if(Convert.isNotNullEmpty(domain_scope))
//		{
//			int k = domain_scope.indexOf("-") ;
//			if(k>0)
//			{
//				db_cp.domainScopeMin = Integer.parseInt(domain_scope.substring(0,k)) ;
//				db_cp.domainScopeMax = Integer.parseInt(domain_scope.substring(k+1)) ;
//			}
//		}
		
		return xd;
	}

	@Override
	public void fromXmlData(XmlData xd)
	{
		dbName = xd.getParamValueStr("name");
		if(dbType!=null)
			xd.setParamValue("type",dbType.name());
		
		if(driver!=null)
			xd.setParamValue("driver", driver);
		
		if(url!=null)
			xd.setParamValue("url", url);
		if(username!=null)
			xd.setParamValue("username", username);
		if(password!=null)
			xd.setParamValue("password", password);
		if(initialConnections>0)
			xd.setParamValue("init_num", ""+initialConnections);
		if(maxConnections>0)
			xd.setParamValue("max_num", ""+maxConnections);
		
	}

}
