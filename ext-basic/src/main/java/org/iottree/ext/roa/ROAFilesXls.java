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
		
	}

	@Override
	protected boolean RT_start_ov()
	{
		return false;
	}

	@Override
	public void RT_stop()
	{
		
	}

	@Override
	public boolean RT_isRunning()
	{
		return false;
	}

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

}
