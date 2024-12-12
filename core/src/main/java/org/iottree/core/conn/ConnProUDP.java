package org.iottree.core.conn;

import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnProUDP extends ConnProvider
{
	public static final String TP ="udp_msg" ;
	

	@Override
	public String getProviderType()
	{
		return TP;	
	}

	@Override
	public String getProviderTpt()
	{
		return "UDP Msg" ;
	}

	@Override
	public boolean isSingleProvider()
	{
		return true;
	}


	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtUDPMsg.class;
	}


	@Override
	protected long connpRunInterval()
	{
		return 1000;
	}
	
	public void start() throws Exception
	{
		List<ConnPt> pts = this.listConns() ;
		if(pts==null||pts.size()<=0)
			return ;
		
		super.start();
		
		for(ConnPt ci:pts)
		{
			if(!ci.isEnable())
				continue ;
			try
			{
				ConnPtUDPMsg conn = (ConnPtUDPMsg)ci ;
				conn.RT_start() ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop()
	{
		super.stop() ;
		
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtUDPMsg conn = (ConnPtUDPMsg)ci ;
				conn.RT_stop();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	@Override
	protected void connpRunInLoop() throws Exception
	{
//		for(ConnPt ci:this.listConns())
//		{
//			if(!ci.isEnable())
//				continue ;
//			
//			ConnPtUDPMsg citc = (ConnPtUDPMsg)ci ;
//			citc.RT_checkConn() ;
//		}
	}
}
