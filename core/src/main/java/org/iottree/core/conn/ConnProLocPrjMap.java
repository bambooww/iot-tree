package org.iottree.core.conn;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

/**
 * map other local projects tags or tags in node to self channel
 * 
 * @author jason.zhu
 *
 */
public class ConnProLocPrjMap extends ConnProvider
{
	public static final String TP = "loc_prj_map";

	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	@Override
	public String getProviderTpt()
	{
		return "Local Project Map" ;
	}

	public boolean isSingleProvider()
	{
		return true;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtLocPrjMap.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 1000;
	}

	public void stop()
	{
		super.stop();

		//disconnAll();
	}

//	public void disconnAll() // throws IOException
//	{
//		for (ConnPt ci : this.listConns())
//		{
//			try
//			{
//				ConnPtIOTTreeNode conn = (ConnPtIOTTreeNode) ci;
//				conn.disconnect();
//			} catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		
	}
}
