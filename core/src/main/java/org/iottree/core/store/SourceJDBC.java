package org.iottree.core.store;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.plugin.PlugDir;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.store.gdb.ConnPoolMgr;
import org.iottree.core.store.gdb.connpool.DBConnPool;
import org.iottree.core.store.gdb.connpool.DBType;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class SourceJDBC extends Source
{
	public static class Drv
	{
		PlugDir plugDir = null ;
		
		String name = null ;
		String title = null ;
		
		String driverClassName = null ;
		
		String jdbcUrl = null ;
		
		String defaultPortStr = "" ;
		
		public PlugDir getPlugDir()
		{
			return this.plugDir ;
		}
		
		public String getName()
		{
			return name ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public String getDriverClassName()
		{
			return this.driverClassName ;
		}
		
		public String getDefaultPortStr()
		{
			if(this.defaultPortStr==null)
				return "" ;
			return this.defaultPortStr ;
		}
		
		public Drv asDriverClassName(String cn)
		{
			this.driverClassName = cn ;
			return this ;
		}
		
		public String getJdbcUrl()
		{
			return this.jdbcUrl ;
		}
		
		public Drv asJdbcUrl(String u)
		{
			this.jdbcUrl = u ;
			return this ;
		}
		
		public String calJdbcUrl(String host,int port,String dbname)
		{
			String tmps = this.jdbcUrl ;
			tmps = tmps.replace("{$host}", host) ;
			tmps = tmps.replace("{$port}", ""+port) ;
			tmps = tmps.replace("{$db_name}", dbname) ;
			return tmps ;
		}
	}
	
	private static Drv parseDrv(PlugDir pd)
	{
		Drv d = new Drv() ;
		d.name = pd.getName() ;
		d.title = pd.getTitle() ;
		d.plugDir = pd ;
		JSONObject jo = pd.getConfigJO() ;
		if(jo==null)
			return null ;
		d.driverClassName = jo.optString("jdbc_class");
		d.jdbcUrl = jo.optString("jdbc_url") ;
		if(Convert.isNullOrEmpty(d.driverClassName))
			return null ;
		if(Convert.isNullOrEmpty(d.jdbcUrl))
			return null ;
		
		d.defaultPortStr = jo.optString("jdbc_port_default","") ;
		return d ;
	}
	
	static LinkedHashMap<String,Drv> name2driver = null;//new LinkedHashMap<>() ;
//	
//	private static void regDrv(Drv d)
//	{
//		name2driver.put(d.getName(), d) ;
//	}
//	
//	public static final String DRV_MYSQL = "mysql" ;
//	
//	public static final String DRV_SQLSERVER = "sqlserver" ;
//	
//	static
//	{
//		regDrv(new Drv(DRV_MYSQL,"MySql").asDriverClassName("com.mysql.jdbc.Driver")
//				.asJdbcUrl("jdbc:mysql://{$host}:{$port}/{$db_name}?useUnicode=true&characterEncoding=UTF-8")
//				);
//		
//		regDrv(new Drv(DRV_SQLSERVER,"SQL Server").asDriverClassName("com.microsoft.jdbc.sqlserver.SQLServerDriver")
//				.asJdbcUrl("jdbc:microsoft:sqlserver://{$host}:{$port};DatabaseName={$db_name};SelectMethod=cursor")
//				);
//	}
//	
//	public static Collection<Drv> listDrvs()
//	{
//		return name2driver.values();
//	}
	
	private static LinkedHashMap<String,Drv> getName2Driver()
	{
		if(name2driver!=null)
			return name2driver;
		
		LinkedHashMap<String,Drv> n2d = new LinkedHashMap<>();
		LinkedHashMap<String,PlugDir> n2dir = PlugManager.getInstance().LIB_getPlugs("jdbc") ;
		if(n2dir==null)
		{
			name2driver = n2d ;
			return n2d ;
		}
		for(PlugDir pd:n2dir.values())
		{
			Drv d = parseDrv(pd) ;
			if(d==null)
				continue ;
			n2d.put(d.getName(), d) ;
		}
		name2driver = n2d ;
		return n2d ;
	}
	
	public static List<Drv> listJDBCDrivers()
	{
		LinkedHashMap<String,Drv> n2d = getName2Driver() ;
		ArrayList<Drv> rets = new ArrayList<Drv>() ;
		rets.addAll(n2d.values()) ;
		return rets ;
	}
	
	public static Drv getJDBCDriver(String name)
	{
		return getName2Driver().get(name) ;
	}
	
	@data_val(param_name = "drv_name")
	String drvName = null ;
	
	@data_val(param_name = "db_host")
	String dbHost = null ;
	
	@data_val(param_name = "db_port")
	int dbPort = -1 ;
	
	@data_val(param_name = "db_name")
	String dbName = null ;
	
	@data_val(param_name = "db_user")
	String dbUser = null ;
	
	@data_val(param_name = "db_psw")
	String dbPsw = null ;
	
	public SourceJDBC()
	{
		super();
	}
	
//	public StoreJDBC(String n,String t)
//	{
//		super(n,t) ;
//	}
	
	public SourceJDBC setJDBCInfo(String drv_name,String db_host,int db_port,String db_name,String db_user,String db_psw)
	{
		this.drvName = drv_name ;
		this.dbHost = db_host ;
		this.dbPort = db_port ;
		this.dbName = db_name ;
		this.dbUser = db_user;
		this.dbPsw = db_psw ;
		return this ;
	}

	public String getSorTp()
	{
		return "jdbc";
	}
	
	public String getSorTpTitle()
	{
		return "DB:"+this.drvName ;
	}
	
	public String getDrvName()
	{
		return this.drvName ;
	}
	
	public String getDBHost()
	{
		if(this.dbHost==null)
			return "" ;
		return this.dbHost ;
	}
	
	public int getDBPort()
	{
		return this.dbPort ;
	}
	
	public String getDBName()
	{
		if(this.dbName==null)
			return "" ;
		return this.dbName ;
	}
	
	public String getDBUser()
	{
		if(this.dbUser==null)
			return "" ;
		return this.dbUser ;
	}
	
	public String getDBPsw()
	{
		if(this.dbPsw==null)
			return "" ;
		
		return this.dbPsw ;
	}
	
	private transient DBConnPool connPool = null ;
	
//	private DBType getDBType()
//	{
//		switch(this.drvName)
//		{
//		case DRV_MYSQL:
//			return DBType.mysql;
//		case DRV_SQLSERVER:
//			return DBType.sqlserver;
//		default:
//			return null ;
//		}
//	}
	
	public DBConnPool getConnPool()
	{
		if(connPool!=null)
			return connPool ;
		
		Drv drv = getName2Driver().get(this.drvName) ;
		String url = drv.calJdbcUrl(this.dbHost, this.dbPort, this.dbName) ;
		connPool = new DBConnPool(null, this.getName(), drv.getDriverClassName(), url,this.dbName, this.dbUser,
				this.dbPsw, "0", "10",drv.plugDir.getOrLoadCL());
		return connPool ;
	}
	
	public boolean checkConn(StringBuilder failedr)
	{
		DBConnPool dbc = getConnPool() ;
		if(dbc==null)
		{
			failedr.append("no connection pool created,may be config error") ;
			return false;
		}
		
		Connection conn = null;
		try
		{
			conn = dbc.getConnection() ;
			return true ;
		}
		catch(Exception ee)
		{
			failedr.append(ee.getMessage()) ;
			ee.printStackTrace();
			return false;
		}
		finally
		{
			if(conn!=null)
				dbc.free(conn);
		}
	}
	
//	public Connection DB_getConn()
//	{
//		getConnPool().
//	}
}
