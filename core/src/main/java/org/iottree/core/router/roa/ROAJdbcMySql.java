package org.iottree.core.router.roa;

import java.util.List;

import org.iottree.core.router.JoinIn;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterOuterAdp;

public class ROAJdbcMySql extends ROAJdbc
{

	public ROAJdbcMySql(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getDBTp()
	{
		return "mysql";
	}

	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return new ROAJdbcMySql(rm);
	}

	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji, String recved_txt) throws Exception
	{
		
	}

}
