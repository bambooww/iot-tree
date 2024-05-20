package org.iottree.core.router.roa;

import java.util.List;

import org.iottree.core.router.JoinIn;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterOuterAdp;
import org.iottree.core.util.ILang;

public abstract class ROAJdbc extends RouterOuterAdp implements ILang
{

	public ROAJdbc(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getTp()
	{
		return "db_"+this.getDBTp();
	}
	
	public abstract String getDBTp() ;


//	@Override
//	public String getTpTitle()
//	{
//		return g("db_"+this.getDBTp());
//	}

	
	@Override
	public List<JoinIn> getJoinInList()
	{
		return null;
	}

	@Override
	public List<JoinOut> getJoinOutList()
	{
		return null;
	}


	@Override
	protected boolean RT_start_ov()
	{
		return false;
	}

	@Override
	public void RT_stop()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean RT_isRunning()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
