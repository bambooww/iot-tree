package org.iottree.core.store.gdb.autofit;

import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;

public class JavaForeignKeyInfo implements IXmlDataable
{
	//private String localTableName = null ;
	private String localColName = null ;
	
	private String refTableName = null ;
	private String refColName = null ;
	
	public JavaForeignKeyInfo()
	{}
	
	public JavaForeignKeyInfo(String localcn,String reftn,String refcn)
	{
		//localTableName = localtn ;
		localColName = localcn ;
		refTableName = reftn ;
		refColName = refcn ;
	}
	
//	public String getLocalTableName()
//	{
//		return localTableName ;
//	}
	
	public String getLocalColName()
	{
		return localColName ;
	}
	
	public String getRefTableName()
	{
		return refTableName ;
	}
	
	public String getRefColName()
	{
		return refColName ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = new XmlData();
		//xd.setParamValue("loc_tablename", localTableName) ;
		xd.setParamValue("loc_colname", localColName) ;
		xd.setParamValue("ref_tablename", refTableName) ;
		xd.setParamValue("ref_colname", refColName) ;
		
		return xd;
	}

	public void fromXmlData(XmlData xd)
	{
		//localTableName = xd.getParamValueStr("loc_tablename") ;
		localColName = xd.getParamValueStr("loc_colname") ;
		refTableName = xd.getParamValueStr("ref_tablename") ;
		refColName = xd.getParamValueStr("ref_colname") ;
	}
}
