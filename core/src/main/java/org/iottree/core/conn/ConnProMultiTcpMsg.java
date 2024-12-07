package org.iottree.core.conn;

import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.conn.ConnProTcpServer.AcceptedSockHandler;

/**
 * 多tcp链接组，里面的每个接入可能包含多个链接，所有链接不区分直接取数据
 * 
 * 取数据的方式通过预处理
 * 
 * @author jason.zhu
 *
 */
public class ConnProMultiTcpMsg  extends ConnProvider
{

	@Override
	public String getProviderType()
	{
		return "multi_tcp_msg";
	}

	@Override
	public String getProviderTpt()
	{
		return "Tcp Msg";
	}

	@Override
	public boolean isSingleProvider()
	{
		return true;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtMSGMultiTcp.class;
	}

	@Override
	protected long connpRunInterval()
	{
		return 1000;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{
		//System.out.println("11");
		//monitor all connpt to find broken tcp
		
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
				ConnPtMSGMultiTcp conn = (ConnPtMSGMultiTcp)ci ;
				conn.RT_start() ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void stop()
	{
		super.stop();
		
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtMSGMultiTcp conn = (ConnPtMSGMultiTcp)ci ;
				conn.RT_stop();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean isRunning()
	{
		return super.isRunning();
	}
}
