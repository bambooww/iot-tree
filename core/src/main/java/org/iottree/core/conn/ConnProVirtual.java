package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

/**
 * virtual conn provider 
 * it can provider virtual conns,and virtual connections can be connected to any channel
 * 1) channel driver will be masked by virtual connection. so,if a channel is set to be connected a virtual
 *     inner driver will stop,and related tags will updated by virtual connection directly.
 * 2) virtual conn can be set script to update channel's tags. then,a virtual connection is only fit for one channel
 *     it can be used for channel testing in which you project has no real device connected will real driver.
 *     
 * @author jason.zhu
 *
 */
public class ConnProVirtual  extends ConnProvider
{

	@Override
	public String getProviderType()
	{
		return "virtual";
	}
	
	@Override
	public String getProviderTpt()
	{
		return "Virtual" ;
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{ 
		return ConnPtVirtual.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 1;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtVirtual cptv = (ConnPtVirtual)ci ;
			cptv.runVirtualInLoop() ;
		}
	}

}
