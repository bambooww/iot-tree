package org.iottree.ext.roa;

import java.util.List;

import org.iottree.core.router.JoinIn;
import org.iottree.core.router.JoinOut;
import org.iottree.core.router.RouterManager;
import org.iottree.core.router.RouterObj;
import org.iottree.core.router.RouterOuterAdp;

public class ROAFilesXls extends ROAFiles
{

	public ROAFilesXls(RouterManager rm)
	{
		super(rm);
	}

	@Override
	public String getTp()
	{
		return "xls";
	}

	@Override
	public RouterOuterAdp newInstance(RouterManager rm)
	{
		return null;
	}

	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji, RouterObj recved_ob) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean RT_start()
	{
		// TODO Auto-generated method stub
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

	@Override
	public List<JoinIn> getJoinInList()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JoinOut> getJoinOutList()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
