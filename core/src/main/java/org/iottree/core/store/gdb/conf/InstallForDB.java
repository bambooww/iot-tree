package org.iottree.core.store.gdb.conf;

import java.util.ArrayList;

import org.w3c.dom.Element;

public class InstallForDB
{
	public static InstallForDB parseContent(Gdb g,Element ele)
			throws Exception
	{
		InstallForDB fcfdb = new InstallForDB();

		String strdbt = ele.getAttribute(Gdb.ATTR_DBTYPE);
		if (strdbt != null && !strdbt.equals(""))
			fcfdb.dbType = DBType.valueOf(strdbt);
		
		fcfdb.relatedTableName = ele.getAttribute(Gdb.ATTR_TABLE_NAME) ;

		Element[] sqleles = XDataHelper.getCurChildElement(ele, Gdb.TAG_SQL);
		if (sqleles == null || sqleles.length <= 0)
			return null;

		for (Element sqlele : sqleles)
		{
			SqlItem sqli = SqlItem.parseSqlItem(g,null, sqlele);
			if (sqli == null)
				continue;

			fcfdb.sqlItems.add(sqli);
		}

		if (fcfdb.sqlItems.size() <= 0)
			return null;

		return fcfdb;

	}

	private DBType dbType = DBType.all;
	
	private String relatedTableName = null ;

	private ArrayList<SqlItem> sqlItems = new ArrayList<SqlItem>();

	private InstallForDB()
	{

	}

	public DBType getDBType()
	{
		return dbType;
	}
	
	public String getRelatedTableName()
	{
		return this.relatedTableName ;
	}

	public ArrayList<SqlItem> getSqlItems()
	{
		return sqlItems;
	}
}
