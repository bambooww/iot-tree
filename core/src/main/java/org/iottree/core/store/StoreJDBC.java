package org.iottree.core.store;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class StoreJDBC extends Store
{
	public static class Drv
	{
		String name = null ;
		String title = null ;
		
		String driverClassName = null ;
		
		String jdbcUrl = null ;
		
		public Drv(String n,String t)
		{
			this.name = n ;
			this.title = t ;
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
			return "" ;
		}
	}
	
	static LinkedHashMap<String,Drv> name2driver = new LinkedHashMap<>() ;
	
	private static void regDrv(Drv d)
	{
		name2driver.put(d.getName(), d) ;
	}
	
	static
	{
		regDrv(new Drv("mysql","MySql").asDriverClassName("com.mysql.jdbc.Driver")
				.asJdbcUrl("jdbc:mysql://{$host}:{$port}/{$db_name}?useUnicode=true&characterEncoding=UTF-8")
				);
		
		regDrv(new Drv("sqlserver","SQL Server").asDriverClassName("com.microsoft.jdbc.sqlserver.SQLServerDriver")
				.asJdbcUrl("jdbc:microsoft:sqlserver://{$host}:{$port};DatabaseName={$db_name};SelectMethod=cursor")
				);
	}
	
	public static Collection<Drv> listDrvs()
	{
		return name2driver.values();
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
	
	public StoreJDBC()
	{}
	
//	public StoreJDBC(String n,String t)
//	{
//		super(n,t) ;
//	}
	
	public StoreJDBC setJDBCInfo(String drv_name,String db_host,int db_port,String db_name,String db_user,String db_psw)
	{
		this.drvName = drv_name ;
		this.dbHost = db_host ;
		this.dbPort = db_port ;
		this.dbName = db_name ;
		this.dbUser = db_user;
		this.dbPsw = db_psw ;
		return this ;
	}

	public String getStoreTp()
	{
		return "jdbc";
	}
	
	public String getStoreTpTitle()
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
}
