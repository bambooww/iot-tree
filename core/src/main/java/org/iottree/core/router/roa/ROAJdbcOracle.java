package org.iottree.core.router.roa;

import org.iottree.core.router.JoinIn;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterOuterAdp;

public class ROAJdbcOracle extends ROAJdbc
{

	public ROAJdbcOracle(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getDBTp()
	{
		return "oracle";
	}

	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return new ROAJdbcOracle(rm);
	}

	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji, Object recved_txt) throws Exception
	{
		
	}

}
