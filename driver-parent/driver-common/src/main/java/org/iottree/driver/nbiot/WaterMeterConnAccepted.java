package org.iottree.driver.nbiot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.driver.nbiot.msg.WMMsg;
import org.iottree.driver.nbiot.msg.WMMsgReceipt;
import org.iottree.driver.nbiot.msg.WMMsgReport;
import org.iottree.driver.nbiot.msg.WMMsgValveResp;
import org.json.JSONObject;

public class WaterMeterConnAccepted
{
	public static String TP = "tcp_accepted";
	
	private static HashSet<WaterMeterConnAccepted> allCAS = new HashSet<>() ;
	
	public static synchronized void addConn(WaterMeterConnAccepted ca)
	{
		allCAS.add(ca) ;
		System.out.println("new conn found,cur conn num="+allCAS.size()) ;
	}
	
	public static synchronized void removeConn(WaterMeterConnAccepted ca)
	{
		allCAS.remove(ca) ;
		System.out.println("remove conn ,cur conn num="+allCAS.size()) ;
	}
	
	public static List<WaterMeterConnAccepted> listConns()
	{
		ArrayList<WaterMeterConnAccepted> rets = new ArrayList<>() ;
		rets.addAll(allCAS) ;
		return rets ;
	}

	Socket sock = null;

	InputStream inputS = null;

	OutputStream outputS = null;
	
	Boolean bValve = null;
	
	IOnReport onReport= null;
	
	public WaterMeterConnAccepted()
	{
	}

	public void setOnReport(IOnReport onrep)
	{
		this.onReport = onrep ;
	}
	
	public String getConnType()
	{
		return "tcp_client" ;
	}
	
	public boolean setAcceptedSocket(Socket sock)// throws IOException
	{
		if(this.sock!=null)
		{//
			disconnect();
			//this.fireConnInvalid();
		}
		
		
		try
		{
			this.sock = sock ;
			inputS = sock.getInputStream();
			outputS = sock.getOutputStream();
	
			addConn(this);
			// this.fireConnReady();
			return true ;
		}
		catch (Exception ee)
		{
			disconnect();
			return false;
		}
	}
	
	
	int report_st = 0;

	void runInTh(long timeout_ms) throws Exception
	{
		WMMsg msg = null ;
		long st = System.currentTimeMillis() ;
		int run = 1;
		do
		{
			Thread.sleep(1);
			
			msg = WMMsg.parseMsg(inputS);
			if(msg==null)
			{
				if(System.currentTimeMillis()-st>=timeout_ms)
				{
					return; //
				}
				continue ;
			}
			
			System.out.println("recved msg=\r\n") ;
			System.out.println(msg) ;
			if(msg instanceof WMMsgReport)
			{
				System.out.println(" find report ="+msg);
				List<WMMsg> req_msgs = this.onReport.onMsgReport((WMMsgReport)msg) ;
				if(req_msgs==null||req_msgs.size()<=0)
				{
					WMMsgReceipt receipt = ((WMMsgReport)msg).createReceipt(false) ;
					receipt.setMeterAddr(msg.getMeterAddr());
					receipt.writeOut(outputS);
					return ;
				}
				
				run = req_msgs.size() ;
				WMMsgReceipt receipt = ((WMMsgReport)msg).createReceipt(true) ;
				receipt.setMeterAddr(msg.getMeterAddr());
				receipt.writeOut(outputS);
				
				for(WMMsg req:req_msgs)
				{
					req.writeOut(outputS);
				}
				
			}
			else if(msg instanceof WMMsgValveResp)
			{
				System.out.println(" find valve resp="+msg.toString());
				run --;
			}
		}
		while(run>0);
	}

	protected InputStream getInputStreamInner()
	{
		return inputS;
	}


	protected OutputStream getOutputStreamInner()
	{
		return outputS;
	}

	

	synchronized void disconnect() //throws IOException
	{
		if (sock == null)
			return;

		try
		{
			try
			{
				if (inputS != null)
					inputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (outputS != null)
					outputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (sock != null)
					sock.close();
			} catch (Exception e)
			{
			}
			
			removeConn(this) ;
		} finally
		{
			inputS = null;
			outputS = null;
			sock = null;
		}
	}

	private long lastChk = -1;

	void checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			//connect();
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}

	public String getDynTxt()
	{
		return "";
	}

	public boolean isClosed()
	{
		if(sock==null)
			return true ;
		return sock.isClosed();
	}


	public boolean isConnReady()
	{
		return sock!=null;
	}
	
	public String getConnErrInfo()
	{
		if(sock==null)
			return "no connection" ;
		else
			return null ;
	}

	public void close() throws IOException
	{
		disconnect();
	}

}
