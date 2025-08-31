package org.iottree.core.store.gdb.conf;

import java.util.*;
import org.w3c.dom.Element;


/**
 * Func节点内部的sql语句内容
 * 
 * @author Jason Zhu
 */
public class FuncContentForDB
{
	public static FuncContentForDB parseContent(Func fi,Element ele) throws Exception
	{
		FuncContentForDB fcfdb = new FuncContentForDB();
		
		fcfdb.funcItem = fi ;
		
		if(ele.getTagName().equals(Gdb.TAG_CONTENT_FOR_DB))
		{
			String strdbt = ele.getAttribute(Gdb.ATTR_DBTYPE);
			if(strdbt!=null&&!strdbt.equals(""))
				fcfdb.dbType = DBType.valueOf(strdbt);
			
			HashSet<String> hs = new HashSet<String>() ;
			hs.add(Gdb.TAG_SQL);
			hs.add(Gdb.TAG_FILE);
			Element[] sqleles = XDataHelper.getCurChildElement(ele, hs);
			if(sqleles==null||sqleles.length<=0)
				return null ;
			
			for(Element sqlele:sqleles)
			{
				IOperItem ioi = null ;
				
				if(Gdb.TAG_SQL.equals(sqlele.getNodeName()))
				{
					ioi = SqlItem.parseSqlItem(fi.getBelongTo().getBelongTo(),fi, sqlele);
				}
				else if(Gdb.TAG_FILE.equals(sqlele.getNodeName()))
				{
					ioi = FileItem.parseFileItem(fi.getBelongTo().getBelongTo(),fi, sqlele);
				}
				
				fcfdb.operItems.add(ioi);
			}
			
			if(fcfdb.operItems.size()<=0)
				return null ;
			
			return fcfdb;
		}
		else if(ele.getTagName().equals(Gdb.TAG_CONTENT))
		{//兼容旧的格式,直接把Content作为SqlItem处理
			SqlItem sqli = SqlItem.parseSqlItem(fi.getBelongTo().getBelongTo(),fi,ele);
			if(sqli==null)
				return null ;
			sqli.exeType = fi.exeType;
			sqli.callType = fi.funcType ;
			
			fcfdb.operItems.add(sqli);
			return fcfdb;
		}
		else
		{
			return null ;
		}
	}
	
	private transient Func funcItem = null ;
	
	private DBType dbType = DBType.all;
	
	private ArrayList<IOperItem> operItems = new ArrayList<IOperItem>() ;
	
	private FuncContentForDB()
	{
		
	}
	
	public Func getBelongTo()
	{
		return funcItem ;
	}
	
	public DBType getDBType()
	{
		return dbType ;
	}
	
	public ArrayList<IOperItem> getOperItems()
	{
		return operItems ;
	}
	
	public boolean isChangeData()
	{
		for(IOperItem ioi:operItems)
		{
			if(ioi.isChangedData())
				return true ;
		}
		
		return false;
	}
}
