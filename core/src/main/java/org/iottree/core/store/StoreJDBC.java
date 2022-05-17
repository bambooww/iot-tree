package org.iottree.core.store;

import java.util.LinkedHashMap;

public class StoreJDBC extends Store
{
	static class Drv
	{
		String name = null ;
		
		String driverClassName = null ;
		
		String jdbcUrl = null ;
		
		public Drv(String n)
		{
			this.name = n ;
		}
		
		public String getName()
		{
			return name ;
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
		regDrv(new Drv("mysql").asDriverClassName("com.mysql.jdbc.Driver")
				.asJdbcUrl("jdbc:mysql://{$host}:{$port}/{$db_name}?useUnicode=true&characterEncoding=UTF-8")
				);
		
		regDrv(new Drv("sqlserver").asDriverClassName("com.microsoft.jdbc.sqlserver.SQLServerDriver")
				.asJdbcUrl("jdbc:microsoft:sqlserver://{$host}:{$port};DatabaseName={$db_name};SelectMethod=cursor")
				);
	}
	
	
	String drvName = null ;
	
	String dbHost = null ;
	
	int dbPort = -1 ;
	
	String dbName = null ;
	
	String dbUser = null ;
	
	String dbPsw = null ;
	
	public StoreJDBC()
	{}
	
	public StoreJDBC(String n)
	{
		super(n) ;
	}

	public String getStoreTp()
	{
		return "jdbc";
	}
	
	public String getDrvName()
	{
		return this.drvName ;
	}
	
	public String getDBHost()
	{
		return this.dbHost ;
	}
	
	public int getDBPort()
	{
		return this.dbPort ;
	}
	
	public String getDBUser()
	{
		return this.dbUser ;
	}
	
	public String getDBPsw()
	{
		return this.dbPsw ;
	}
}
