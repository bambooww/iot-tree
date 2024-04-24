package org.iottree.core.router.roa;

import org.iottree.core.router.JoinIn;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterObj;
import org.iottree.core.router.RouterOuterAdp;

public class ROAJdbcSQLServer extends ROAJdbc
{

	public ROAJdbcSQLServer(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getDBTp()
	{
		return "sqlserver";
	}

	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return new ROAJdbcSQLServer(rm);
	}

	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji, RouterObj recved_txt) throws Exception
	{
		
	}
}
