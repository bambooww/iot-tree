package org.iottree.core.sim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class SimCPTcp extends SimCP
{
	public static final int DEF_PORT = 12000 ;
	
	
	
	@data_val(param_name = "server_ip")
	String serverIp = null ;
	
	@data_val(param_name = "server_port")
	int serverPort = DEF_PORT ;
	//SerialPort serialPort = null;
	
	class TcpConn extends SimConn
	{
		Socket socket = null ;
		
		InputStream inputs = null ;
		
		OutputStream outputs = null ;
		
		public TcpConn(SimChannel ch,SimCPTcp cp,Socket sock) throws IOException
		{
			super(ch,cp) ;
			this.socket = sock ;
			this.socket.setSoTimeout(10000);
			//this.socket.set
			this.inputs = sock.getInputStream() ;
			this.outputs = sock.getOutputStream() ;
			
			increaseConn(this);
		}

		@Override
		public InputStream getConnInputStream()
		{
			return inputs;
		}

		@Override
		public OutputStream getConnOutputStream()
		{
			return outputs ;
		}

		@Override
		public void pulseConn() throws Exception
		{
			socket.sendUrgentData(0);
		}

		@Override
		public void close() throws IOException
		{
			try
			{
				try
				{
					super.close();
				}
				catch(Exception e)
				{}
				
				try
				{
					inputs.close();
				}
				catch(Exception e) {}
				
				try
				{
					outputs.close();
				}
				catch(Exception e) {}
				
				try
				{
					socket.close();
				}
				catch(Exception e) {}
				
				decreaseConn(this);
			}
			finally
			{
				
			}
		}
	}
	
	ArrayList<TcpConn> allConns = new ArrayList<>() ;
	
	void increaseConn(TcpConn tc)
	{
		synchronized(allConns)
		{
			allConns.add(tc) ;
			//System.out.println(" sim cp tcp add conn,cur num="+allConns.size()) ;
		}
	}
	
	void decreaseConn(TcpConn tc)
	{
		synchronized(allConns)
		{
			allConns.remove(tc) ;
			//System.out.println(" sim cp tcp remove conn,cur num="+allConns.size()) ;
		}
	}
	
	public int getConnsNum()
	{
		return allConns.size();
	}
	
	public synchronized List<SimConn> listAllConns()
	{
		ArrayList<SimConn> rets = new ArrayList<>() ;
		rets.addAll(allConns) ;
		return rets ;
	}
	
	transient private ServerSocket serverSock = null;
	
	public String getServerIp()
	{
		if(this.serverIp==null)
			return "" ;
		return this.serverIp ;
	}
	
	public int getServerPort()
	{
		return serverPort ;
	}
	
	public String getConnTitle()
	{
		return "Tcp-"+this.serverPort ;
	}

	@Override
	public String toConfigStr()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fromConfig(JSONObject jo)
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public List<SimConn> getConns()
	{
		//ArrayList<SimConn> rets = 
		return  listAllConns();
		//return allConns ;
	}
//	public void stop()
//	{
//		super.stop();
//		
//		synchronized(this)
//		{
//			stopServer() ;
//			
//			Thread t = this.acceptTh ;
//			if(t!=null)
//				t.interrupt();
//			this.acceptTh = null ;
//			
//			disconnAll();
//		}
//	}
//	
//	private void stopServer()
//	{
//		if (serverSock != null)
//		{
//			try
//			{
//				serverSock.close();
//			} catch (Exception e)
//			{
//			}
//
//			serverSock = null;
//		}
//
//		// server = null;
//		acceptTh = null;
//	}

	@Override
	public boolean RT_init(StringBuilder failedr)
	{
		return true;
	}

	@Override
	synchronized public void RT_runInLoop() throws Exception
	{
		if(serverSock==null)
		{
			serverSock = new ServerSocket(serverPort) ;
			System.out.println(" Simulator Tcp Server accepted at port="+serverPort) ;
		}
		
		Socket sock = serverSock.accept() ;
		
		TcpConn tc = new TcpConn(this.getRelatedCh(),this,sock) ;
		this.getRelatedCh().onConnOk(tc);
		tc.RT_start();
	}

	public void RT_stop()
	{
		if(serverSock!=null)
		{
			try
			{
				serverSock.close();
				serverSock = null ;
			}
			catch(Exception e)
			{}
		}
		
		for(SimConn sc:listAllConns())
		{
			try
			{
				sc.close();
			}
			catch(Exception e)
			{
				
			}
		}
		
	}
}
