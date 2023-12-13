package org.iottree.core.store.gdb.connpool;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

public class DBInfo implements IXmlDataable
{
	public static class DbTypeItem
	{
		String name = null;

		DBType dbt = DBType.derby;

		String defaultUrl = null;

		String driver = null;

		public DbTypeItem(String n, DBType dbt, String drv,
				String defaultu)
		{
			name = n;
			this.dbt = dbt;
			driver = drv;
			defaultUrl = defaultu;
		}
		
		public String getTypeName()
		{
			return name ;
		}
		
		public DBType getDBType()
		{
			return dbt ;
		}
		
		public String getDriver()
		{
			return driver ;
		}
		
		public String getDefaultUrl()
		{
			return defaultUrl ;
		}
		
		

		public String toString()
		{
			return name;
		}
	}

	public static ArrayList<DbTypeItem> allDbTypes = new ArrayList<DbTypeItem>();

	public static DbTypeItem defaultDBType = new DbTypeItem("Default",
			DBType.derby, "", "");
	static
	{
		//allDbTypes.add(defaultDBType);

		allDbTypes.add(new DbTypeItem("MS Sql Server",
				DBType.sqlserver,
				"com.microsoft.jdbc.sqlserver.SQLServerDriver",
				"jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=mydb"));
		
		allDbTypes.add(new DbTypeItem("Oracle", DBType.oracle,
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:oci8:@?database??"));
		allDbTypes.add(new DbTypeItem("Oracle(Thin)", DBType.oracle,
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@?host??:1521:?database??"));
		allDbTypes.add(new DbTypeItem("Derby Server", DBType.derby,
				"org.apache.derby.jdbc.EmbeddedDriver",
		"jdbc:derby://localhost/"));
	}

	public static DbTypeItem findTypeItem(DBInfo dbi)
	{
		if (dbi.connProp == null || dbi.connProp.size() <= 0)
			return defaultDBType;

		for (DbTypeItem ti : allDbTypes)
		{
			if (ti.dbt != dbi.dbType)
				continue;

			if (ti.driver.equals(dbi.getDriver()))
				return ti;
		}

		return null;
	}
	
	
	public DBType dbType = DBType.derby;
	public Properties connProp = new Properties();
	
	public DBInfo()
	{}
	
	public void setInfo(DBType dbt,
			String driver,String url,
			String user,String psw,
			int initn,int maxn)
	{
		dbType = dbt ;
		connProp.setProperty("db.driver", driver);
		connProp.setProperty("db.url",url);

		connProp.setProperty("db.username", user);
		connProp.setProperty("db.password", psw);
		connProp.setProperty("db.initnumber", ""+initn);
		connProp.setProperty("db.maxnumber", ""+maxn);
	}
	
	public DBType getDBType()
	{
		return dbType ;
	}
	
	public String getDriver()
	{
		if(connProp==null)
			return null ;
		
		return connProp.getProperty("db.driver");
	}
	
	public String getUrl()
	{
		if(connProp==null)
			return null ;
		
		String u = connProp.getProperty("db.url");
		if(u==null)
			return null ;
		
		return u.trim();
	}
	
	public String getUser()
	{
		if(connProp==null)
			return null ;
		
		return connProp.getProperty("db.username");
	}
	
	public String getPsw()
	{
		if(connProp==null)
			return null ;
		
		return connProp.getProperty("db.password");
	}
	
	public String getInitNum()
	{
		if(connProp==null)
			return null ;
		
		return connProp.getProperty("db.initnumber");
	}
	
	public String getMaxNum()
	{
		if(connProp==null)
			return null ;
		
		return connProp.getProperty("db.maxnumber");
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof DBInfo))
			return false;
		
		DBInfo odbi = (DBInfo)o;
		
		if(dbType!=odbi.dbType)
			return false;
		
		if(connProp==null||connProp.size()<=0)
			return false;
		
		if(!getDriver().equals(odbi.getDriver()))
			return false;
		
		if(!getUrl().equals(odbi.getUrl()))
			return false;
		
		if(!getUser().equals(odbi.getUser()))
			return false;
		
		return true ;
	}
	
	public String toString()
	{
		return getDBType()+"-"+getUrl();
	}
	
	
	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		
		if(connProp!=null)
		{
			for(Enumeration<?> en = connProp.propertyNames() ; en.hasMoreElements();)
			{
				String pn = (String)en.nextElement() ;
				String pv = connProp.getProperty(pn);
				xd.setParamValue(pn, pv);
			}
		}
		
		xd.setParamValue("db_type", dbType.toString());
		
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		dbType = DBType.valueOf(xd.getParamValueStr("db_type")) ;
		
		connProp = new Properties();
		for(String tmpn:xd.getParamNames())
		{
			connProp.setProperty(tmpn, xd.getParamValueStr(tmpn));
		}
		
		connProp.remove("db_type");
	}
}