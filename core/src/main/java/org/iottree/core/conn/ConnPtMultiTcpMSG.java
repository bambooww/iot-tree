package org.iottree.core.conn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.ConnDev;
import org.iottree.core.ConnPt;
import org.iottree.core.DevDriver;
import org.iottree.core.DevDriverMsgOnly;
import org.iottree.core.IConnPtDevFinder;
import org.iottree.core.UACh;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.rxjava3.disposables.Disposable;

/**
 * 基于tcp的同类监听器，每个tcp会自动推送一定格式的信息，此时可以使用此接收对信息进行预处理，形成消息
 * 
 * 1）TcpRunner
 *   tcp server,允许限定数量的tcp接入
 *   一个或多个tcp client
 * 2）每个链接的数据处理器
 * 
 * @author jason.zhu
 *
 */
public class ConnPtMultiTcpMSG extends ConnPt implements IConnPtDevFinder
{
	private static ILogger log = LoggerManager.getLogger(ConnPtMultiTcpMSG.class) ;
	
	private static void sleep(long ms)
	{
		try
		{
		Thread.sleep(ms);
		}
		catch(Exception ee) {}
	}
	
	public static class SockItem implements Disposable
	{
		String connId = null ;
		
		Socket sock = null ;
		
		long acceptedDT = System.currentTimeMillis() ;
		
		private InputStream inputS = null ;
		PushbackInputStream inputPS = null ;
		
		OutputStream outputS = null ;
		
		TcpRunner belongTo = null ;
		
		TcpDataPro dataPro = null ;
		
		public SockItem(String connid,Socket sk,TcpRunner belongto,TcpDataPro datapro) //throws SocketException
		{
			this.connId = connid ;
			this.sock = sk ;
			this.belongTo = belongto ;
			this.dataPro = datapro ;
			
			//this.dataPro.
			//sk.setSoTimeout(60000);
		}
		
		public String getConnId()
		{
			return connId ;
		}
		
		public Socket getSocket()
		{
			return sock ;
		}
		
		public PushbackInputStream getInputStream() throws IOException
		{
			if(inputPS!=null)
				return inputPS ;
			if(sock==null)
				return null ;
			inputS = sock.getInputStream() ;
			inputPS = new PushbackInputStream(inputS) ;
			return inputPS ;
		}
		
		public OutputStream getOutputStream() throws IOException
		{
			if(outputS!=null)
				return outputS ;
			if(sock==null)
				return null ;
			outputS = sock.getOutputStream() ;
			return outputS ;
		}
		
		public long getAcceptedDT()
		{
			return acceptedDT ;
		}
		
		public boolean isTimeout(long to)
		{
			return System.currentTimeMillis()>this.acceptedDT+to ;
		}

		@Override
		public void dispose()
		{
			RT_stop();
		}

		@Override
		public boolean isDisposed()
		{
			return sock==null;
		}
		
		public boolean checkConn()
		{
			Socket sk = this.sock ;
			if(sk==null)
				return false;
			if(sk.isClosed())
			{
				RT_stop() ;
				return false;
			}
			return true ;
		}

		private Thread th = null ;
		
		private Runnable runner = new Runnable() {

			@Override
			public void run()
			{
				try
				{
					while(th!=null)
					{
						dataPro.RT_runInLoop(SockItem.this);
						sleep(3) ;
					}
				}
				catch(Exception ee)
				{
					//ee.printStackTrace();
					if(log.isDebugEnabled())
						log.debug(ee);
				}
				finally
				{
					RT_stop() ;
				}
			}} ;
	
		public synchronized void RT_start()
		{
			if(th!=null)
				return ;
			
			th = new Thread(runner) ;
			th.start();
		}
		
		public synchronized void RT_stop()
		{
			this.belongTo.RT_onSockStoped(this) ;
			Thread t = th ;
			if(t==null)
				return ;
			t.interrupt();
			th = null ;
			
			if(inputS!=null)
			{
				try
				{
				inputS.close();
				}
				catch(Exception eee) {}
				finally
				{
					inputS = null ;
				}
			}
			
			if(outputS!=null)
			{
				try
				{
					outputS.close();
				}
				catch(Exception eee) {}
				finally {outputS = null ;}
				
			}
			
			if(sock!=null)
			{
				try
				{
					sock.close();
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
				finally
				{
					sock=  null ;
				}
			}
		}
		
		public boolean RT_isRunning()
		{
			return th!=null ;
		}
	}
	
	/**
	 * 对于每个tcp的数据处理器
	 * @author jason.zhu
	 *
	 */
	public static abstract class TcpDataPro
	{
		ConnPtMultiTcpMSG belongTo ;
		
		TcpRunner tcpRunner ;

		int maxLen = 1024 ;
		
		
		public TcpDataPro(ConnPtMultiTcpMSG belongto,TcpRunner tcp_runner)
		{
			this.belongTo = belongto ;
			this.tcpRunner = tcp_runner ;
		}
		
		public abstract String getTP() ;
		
		public abstract String getTPT() ;
		
		public int getMaxLen()
		{
			return this.maxLen ;
		}
		
		public abstract TcpDataPro copyMe(TcpRunner tcp_run);
		

		private long RT_lastGit = -1 ;
		
		protected int RT_chkAvailableTO(PushbackInputStream inputs) throws IOException
		{
			int len = inputs.available() ;
			if(len<=0)
			{
				if(RT_lastGit<=0)
				{
					RT_lastGit = System.currentTimeMillis() ;
				}
				else
				{
					if(System.currentTimeMillis()-RT_lastGit>this.tcpRunner.getRecvBrokenTO())
						throw new IOException("recv time out") ;
				}
				return 0;
			}
			
			RT_lastGit = System.currentTimeMillis() ;
			return len ;
		}
		
		
		public abstract void RT_runInLoop(SockItem si) throws Exception;
		
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("_tp",this.getTP()) ;
			jo.put("max_len", this.maxLen) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.maxLen = jo.optInt("max_len", 1024) ;
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = new XmlData() ;
			xd.setParamValue("_tp", this.getTP());
			xd.setParamValue("max_len", this.maxLen);
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			this.maxLen = xd.getParamValueInt32("max_len", 1024) ;
		}
	}
	
	/**
	 * 数据切分 start  end  
	 *           
	 * @author jason.zhu
	 *
	 */
	public static class TcpDataProSpliter extends TcpDataPro
	{
		
		byte[] start = null ;
		
		boolean keepStart = false; //切分之后，是否要保留起始数据
		
		byte[] end = null ;
		
		boolean keepEnd = false;//切分之后，是否要保留结束数据
		
		public TcpDataProSpliter(ConnPtMultiTcpMSG belongto,TcpRunner tcp_run)
		{
			super(belongto,tcp_run);
		}

		
		public String getTP()
		{
			return "sp" ;
		}
		
		public String getTPT()
		{
			return "Bytes Spliter" ;
		}
		
		public String getStartHex()
		{
			if(start==null)
				return "" ;
			return Convert.byteArray2HexStr(start) ;
		}
		
		public boolean isKeepStart()
		{
			return keepStart ;
		}
		
		public String getEndHex()
		{
			if(end==null)
				return "" ;
			return Convert.byteArray2HexStr(end) ;
		}
		
		public boolean isKeepEnd()
		{
			return keepEnd ;
		}
		
		public TcpDataPro copyMe(TcpRunner tcp_run)
		{
			TcpDataProSpliter ret = new TcpDataProSpliter(this.belongTo,tcp_run) ;
			ret.start = this.start ;
			ret.keepStart = this.keepStart ;
			ret.end = this.end ;
			ret.keepEnd = this.keepEnd ;
			ret.maxLen = this.maxLen ;
			return ret ;
		}
		
		
		public JSONObject toJO()
		{
			JSONObject jo = super.toJO() ;
			if(start!=null&&start.length>0)
				jo.put("hex_s", Convert.byteArray2HexStr(start)) ;
			jo.put("keep_s", keepStart) ;
			if(end!=null&&end.length>0)
				jo.put("hex_e", Convert.byteArray2HexStr(end)) ;
			jo.put("keep_e", keepEnd) ;
			
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			super.fromJO(jo);
			String hex_s = jo.optString("hex_s") ;
			if(Convert.isNotNullEmpty(hex_s))
				this.start = Convert.hexStr2ByteArray(hex_s) ;
			this.keepStart = jo.optBoolean("keep_s",false) ;
			
			String hex_e = jo.optString("hex_e") ;
			if(Convert.isNotNullEmpty(hex_e))
				this.end = Convert.hexStr2ByteArray(hex_e) ;
			this.keepEnd = jo.optBoolean("keep_e",false) ;
			
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = super.toXmlData() ;
			if(start!=null&&start.length>0)
				xd.setParamValue("hex_s", Convert.byteArray2HexStr(start)) ;
			xd.setParamValue("keep_s", keepStart) ;
			if(end!=null&&end.length>0)
				xd.setParamValue("hex_e", Convert.byteArray2HexStr(end)) ;
			xd.setParamValue("keep_e", keepEnd) ;
			
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			super.fromXmlData(xd);
			String hex_s = xd.getParamValueStr("hex_s") ;
			if(Convert.isNotNullEmpty(hex_s))
				this.start = Convert.hexStr2ByteArray(hex_s) ;
			this.keepStart = xd.getParamValueBool("keep_s",false) ;
			
			String hex_e = xd.getParamValueStr("hex_e") ;
			if(Convert.isNotNullEmpty(hex_e))
				this.end = Convert.hexStr2ByteArray(hex_e) ;
			this.keepEnd = xd.getParamValueBool("keep_e",false) ;
			
		}
		
		/**
		 * state mach
		 */
		public void RT_runInLoop(SockItem si) throws Exception
		{
			
			if(start!=null && start.length>0 && end!=null && end.length>0)
			{
				RT_runStartEndLoop(si) ;
			}
			else if(start!=null && start.length>0 || end!=null && end.length>0)
			{
				RT_runDivLoop(si) ;
			}
			else
			{
				throw new Exception("start end are null") ;
			}
			
			
		}
		
		private boolean checkSame(byte[] bs1,byte[] bs2)
		{
			for(int i = 0 ; i < bs1.length ; i ++)
			{
				if(bs1[i]!=bs2[i]) return false;
			}
			return true ;
		}
		
		private int RT_st = 0 ;
		
		
		
		ByteArrayOutputStream baos = null ;
		
		private void RT_runStartEndLoop(SockItem si) throws Exception
		{
			PushbackInputStream inputs = si.getInputStream() ;
			if(inputs==null)
				throw new Exception("conn may be broken") ;

			int len = RT_chkAvailableTO(inputs) ;
			if(len<=0)
				return ;
			
			switch(RT_st)
			{
			case 0: // nor
				if(len<start.length)
					return ;
				byte[] b_s = new byte[start.length] ;
				inputs.read(b_s) ;
				if(!checkSame(start,b_s))
				{
					if(b_s.length>1)
						inputs.unread(b_s, 1, b_s.length-1);
				}
				else
				{
					RT_st = 1 ;
					baos = new ByteArrayOutputStream() ;
					if(this.keepStart)
						baos.write(start);
				}
				return ;
			case 1: //found start
				byte[] ret = RT_readToEnd(end,inputs,this.keepEnd) ;
				if(ret!=null)
					this.belongTo.RT_onMsgGit(ret) ;
				baos = null ;
				RT_st = 0 ;
				return ;
			}
		}
		
		private byte[] RT_readToEnd(byte[] endbs,PushbackInputStream inputs,boolean bkeep) throws IOException
		{
			do
			{
				int c = inputs.read() ;
				if(c!=(end[0]&0xFF))
				{
					baos.write(c);
					continue ;
				}
				
				//check end
				if(end.length>1)
				{
					for(int i = 1 ; i < end.length ;i ++)
					{
						c = inputs.read() ;
						if(c!=(end[i]&0xFF))
							return null ;
					}
				}
				//
				if(bkeep)
					baos.write(end);
				byte[] ret = baos.toByteArray() ;
				return ret ;
			}
			while(baos.size()<this.maxLen) ;
			
			return null ;
		}
		
		private void RT_runDivLoop(SockItem si) throws Exception
		{
			byte[] div = (start!=null&&start.length>0)?start:end ;
			PushbackInputStream inputs = si.getInputStream() ;
			if(inputs==null)
				throw new Exception("conn may be broken") ;

			int len = RT_chkAvailableTO(inputs) ;
			if(len<=0)
				return ;
			
//			int len = inputs.available() ;
//			if(len<=0)
//				return ;
			
			switch(RT_st)
			{
			case 0: // nor
				if(len<div.length)
					return ;
				byte[] b_s = new byte[div.length] ;
				inputs.read(b_s) ;
				if(!checkSame(start,b_s))
				{
					if(b_s.length>1)
						inputs.unread(b_s, 1, b_s.length-1);
				}
				else
				{
					RT_st = 1 ;
					baos = new ByteArrayOutputStream() ;
					if(start!=null&&start.length>0&&this.keepStart)
						baos.write(start);
				}
				return ;
			case 1: //found start
				byte[] ret = RT_readToEnd(div,inputs,end!=null&&end.length>0&&this.keepEnd) ;
				if(ret!=null)
					this.belongTo.RT_onMsgGit(ret) ;
				baos = null ;
				return ;
			}
		}
	}
	
	/**
	 * 基于字符串的行
	 * 
	 * @author jason.zhu
	 *
	 */
	public static class TcpDataProStrLine extends TcpDataPro
	{
		
		private transient byte[] readBuf = null;
		private transient int RT_st = 0 ;
		private transient int RT_rlen = 0 ;
		//String encod = "UTF-8" ;
		
		public TcpDataProStrLine(ConnPtMultiTcpMSG belongto,TcpRunner tcp_run)
		{
			super(belongto,tcp_run);
		}

		
		public String getTP()
		{
			return "ln" ;
		}
		
		public String getTPT()
		{
			return "Str Line" ;
		}
		
		public TcpDataPro copyMe(TcpRunner tcp_run)
		{
			TcpDataProStrLine ret = new TcpDataProStrLine(this.belongTo,tcp_run) ;
			//ret.encod = this.encod ;
			ret.maxLen = this.maxLen ;
			ret.readBuf = new byte[this.maxLen] ;
			return ret ;
		}
		
		@Override
		public void RT_runInLoop(SockItem si) throws Exception
		{
			PushbackInputStream inputs = si.getInputStream() ;
			if(inputs==null)
				throw new Exception("conn may be broken") ;

			switch(RT_st)
			{
			case 0:
				int len = RT_chkAvailableTO(inputs) ;
				if(len<=0)
					return ;
				
				int cc = inputs.read() ;
				if(cc=='\r' || cc=='\n')
				{
					RT_st = 1 ;
					RT_rlen = 0 ;
				}
				break ;
			case 1: //in reading
				len = RT_chkAvailableTO(inputs) ;
				if(len<=0)
					return ;
				cc = inputs.read();
				
				if(RT_rlen==0)
				{
					if(cc=='\r' || cc=='\n')
						return ;
				}
				
				readBuf[RT_rlen] = (byte)cc ;
				RT_rlen ++ ;
				byte[] ret = readToEnd(inputs) ;
				if(ret==null)
				{
					RT_st = 0 ;
					RT_rlen =0 ; //丢弃
					return ;
				}
				this.belongTo.RT_onMsgGit(ret) ;
				//RT_st = 1 ;
				//System.out.println(new String(ret)) ;
				RT_rlen =0 ;
				return ;
			default:
				break ;
			}
		}
		
		private byte[] readToEnd(PushbackInputStream inputs) throws IOException
		{
			int cc ;
			do
			{
				cc = inputs.read();
				if(cc=='\r'||cc=='\n')
				{
					byte[] ret = new byte[RT_rlen] ;
					System.arraycopy(readBuf, 0, ret, 0, ret.length);
					return ret;
				}
				readBuf[RT_rlen] = (byte)cc ;
				RT_rlen ++ ;
				if(RT_rlen>=readBuf.length)
				{
					return null ;
				}
			}while(cc>0) ;
			return null ;
		}
		
//		public JSONObject toJO()
//		{
//			JSONObject jo = super.toJO() ;
//			jo.put("enc", encod) ;
//			return jo ;
//		}
//		
//		public void fromJO(JSONObject jo)
//		{
//			super.fromJO(jo);
//			this.encod = jo.optString("enc","UTF-8") ;
//		}

	}
	
	public static class TcpDataProSzy extends TcpDataPro
	{
		
		public TcpDataProSzy(ConnPtMultiTcpMSG belongto,TcpRunner tcp_run)
		{
			super(belongto,tcp_run);
		}

		
		public String getTP()
		{
			return "szy" ;
		}
		
		public String getTPT()
		{
			return "SZY206-2016" ;
		}
		
		public TcpDataPro copyMe(TcpRunner tcp_run)
		{
			TcpDataProSzy ret = new TcpDataProSzy(this.belongTo,tcp_run) ;
			ret.maxLen = this.maxLen ;
			return ret ;
		}
		
		private int RT_st = 0 ;
		
		private transient int readLen = -1 ;
		
		private transient byte[] readBuf = new byte[256] ;

		@Override
		public void RT_runInLoop(SockItem si) throws Exception
		{
			PushbackInputStream inputs = si.getInputStream() ;
			if(inputs==null)
				throw new Exception("conn may be broken") ;

			switch(RT_st)
			{
			case 0:
				int len = RT_chkAvailableTO(inputs) ;
				if(len<=3)
					return ;
				
				inputs.read(readBuf,0,3) ;
				if(readBuf[0]==0x68 && readBuf[2]==0x68)
				{//get head
					readLen = readBuf[1] & 0xFF ;
					RT_st = 1 ;
				}
				else
				{
					inputs.unread(readBuf ,1, 2);
				}
				break ;
			case 1:
				len = RT_chkAvailableTO(inputs) ;
				if(len<readLen+2)
					return ;
				inputs.read(readBuf,3,readLen+2) ;
				if(readBuf[readLen+4]==0x16)
				{
					byte[] ret = new byte[readLen+5] ;
					System.arraycopy(readBuf, 0, ret, 0, ret.length);
					this.belongTo.RT_onMsgGit(ret) ;
					RT_st = 0 ;
				}
				else
				{//discard
					RT_st = 0 ;
				}
				break ;
			default:
				break ;
			}
		}
	}
	
	private TcpDataPro transXD2Pro(XmlData xd)
	{
		if(xd==null)
			return null ;
		String tp = xd.getParamValueStr("_tp") ;
		if(tp==null)
			return null ;
		TcpDataPro tr = null ;
		switch(tp)
		{
		case "sp":
			tr = new TcpDataProSpliter(this,null) ;
			break ;
		case "ln":
			tr = new TcpDataProStrLine(this,null) ;
			break ;
		case "szy":
			tr = new TcpDataProSzy(this,null) ;
		}
		if(tr==null)
			return null ;
		tr.fromXmlData(xd);
		return tr ;
	}
	
	private TcpDataPro transJO2Pro(JSONObject jo)
	{
		if(jo==null)
			return null ;
		String tp = jo.optString("_tp") ;
		if(tp==null)
			return null ;
		TcpDataPro tr = null ;
		switch(tp)
		{
		case "sp":
			tr = new TcpDataProSpliter(this,null) ;
			break ;
		case "ln":
			tr = new TcpDataProStrLine(this,null) ;
			break ;
		case "szy":
			tr = new TcpDataProSzy(this,null) ;
		}
		if(tr==null)
			return null ;
		tr.fromJO(jo);
		return tr ;
	}
	
	public static TcpDataPro[] ALL_DATAPROS = new TcpDataPro[] {new TcpDataProSpliter(null,null),new TcpDataProStrLine(null,null),new TcpDataProSzy(null,null)} ;
	
	public static abstract class TcpRunner
	{
		private ArrayList<SockItem> socks = new ArrayList<>() ;

		TcpDataPro rtdataPro = null ;
		
		private int recvBrokenTO = 60000 ;
		
		public abstract String getTP() ;
		
		public int getRecvBrokenTO()
		{
			if(this.recvBrokenTO<=0)
				return 60000 ;
			return this.recvBrokenTO ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("_tp",this.getTP()) ;
			jo.put("recv_broken_to", this.recvBrokenTO);
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.recvBrokenTO = jo.optInt("recv_broken_to", 60000);
			if(this.recvBrokenTO<=0)
				this.recvBrokenTO = 60000 ;
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = new XmlData() ;
			xd.setParamValue("_tp", this.getTP());
			xd.setParamValue("recv_broken_to", this.recvBrokenTO);
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			this.recvBrokenTO = xd.getParamValueInt32("recv_broken_to", 60000);
			if(this.recvBrokenTO<=0)
				this.recvBrokenTO = 60000 ;
		}
		
		public void RT_start(TcpDataPro datapro)
		{
			rtdataPro = datapro ;
		}
		
		public abstract void RT_stop();
		
		public abstract boolean RT_isRunning();
		
		public abstract String RT_getRunInf() ;
		
		synchronized void RT_onSockCreated(SockItem si) throws SocketException
		{
			si.sock.setSoTimeout(this.recvBrokenTO);
			socks.add(si) ;
		}
		
		synchronized void RT_onSockStoped(SockItem si)
		{
			socks.remove(si) ;
		}
		
		public List<SockItem> RT_listSockItems()
		{
			return RT_listSockItems(false) ;
		}
		
		public List<SockItem> RT_listSockItems(boolean b_syn_list)
		{
			if(!b_syn_list)
				return this.socks ;
			
			synchronized(this)
			{
				return new ArrayList<>(this.socks) ;
			}
		}
		
		protected void RT_clearSockItems()
		{
			this.socks = new ArrayList<>() ;
		}
	}
	
	public static class TcpServerRun extends TcpRunner
	{
		
		String localIP = null;

		int localPort = 26000;
		
		int maxConn = 5 ;
		
		Thread acceptTh = null ;
		
		ServerSocket serverSock = null;
		
		public String getTP()
		{
			return "s";
		}
		
		public String getLocalIP()
		{
			if(this.localIP==null)
				return "" ;
			
			return this.localIP ;
		}
		
		public int getLocalPort()
		{
			return this.localPort ;
		}
		
		public int getMaxConn()
		{
			return maxConn ;
		}
		
		private Runnable acceptRunner = new Runnable() {
			public void run()
			{
				try
				{
					serverSock = new ServerSocket(localPort, 100);

					System.out.println(
							"ConnPtMultiTcpMsg.TcpServerRun started..<<<<<.,ready to recv client connection on port=" + localPort);

					while (acceptTh != null)
					{
						Socket client = serverSock.accept();
						if(RT_listSockItems().size()>=maxConn)
						{
							if(log.isWarnEnabled())
								log.warn("ConnPtMultiTcpMsg.TcpServerRun receive max conn="+maxConn+", so accept from "+client.getInetAddress()+" will be closed");
							client.close(); 
							continue ;
						}
						
						SockItem si = new SockItem(null,client,TcpServerRun.this,rtdataPro.copyMe(TcpServerRun.this)) ;
						RT_onSockCreated(si);
						//socks.add(si) ;
						si.RT_start() ;
					}
				} catch (Exception e)
				{
					System.out.println("ConnPtMultiTcpMsg.TcpServerRun Stop Error with port=" + localPort);
					//e.printStackTrace();
					if (log.isDebugEnabled())
						log.debug("", e);
					// if (log.IsErrorEnabled)
					// log.error(e);
				} finally
				{
					// Stop listening for new clients.
					// close();

					if (log.isDebugEnabled())
						log.debug("ConnPtMultiTcpMsg.TcpServerRun on port=[" + localPort + "] Server stoped..");
					// serverThread = null ;

					RT_stop();
				}
			}
		};
		
		public synchronized void RT_start(TcpDataPro datapro)
		{
			if(this.acceptTh!=null)
				return ;
			
			super.RT_start(datapro);
			
			this.acceptTh = new Thread(acceptRunner,"iottree-cpt_multi_msg-tcpserver ,port="+this.localPort);
			this.acceptTh.start();
		}
		
		public synchronized void RT_stop()
		{
			Thread t = this.acceptTh ;
			if(t!=null)
				t.interrupt();
			this.acceptTh = null ;
			
			if (serverSock != null)
			{
				try
				{
					serverSock.close();
				} catch (Exception e)
				{
				}

				serverSock = null;
			}

			List<SockItem> sis = this.RT_listSockItems(true) ;
			for(SockItem ci:sis)
			{
				ci.dispose();
			}
			
			RT_clearSockItems();
		}
		
		
		public synchronized boolean RT_isRunning()
		{
			return this.acceptTh!=null ;
		}
		
		@Override
		public String RT_getRunInf()
		{
			List<SockItem> sis= this.RT_listSockItems() ;
//			for(SockItem si:)
//			{
//				System.out.println("sock conn="+si.sock.isConnected()) ;
//			}
			return "Server ["+sis.size()+"/"+maxConn+"]" ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = super.toJO() ;
			jo.putOpt("loc_ip",this.localIP) ;
			jo.putOpt("loc_port",this.localPort) ;
			jo.put("max_c",this.maxConn) ;
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			super.fromJO(jo);
			this.localIP = jo.optString("loc_ip") ;
			this.localPort = jo.optInt("loc_port", 26000) ;
			this.maxConn = jo.optInt("max_c",5) ;
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = super.toXmlData() ;
			xd.setParamValue("loc_ip",this.localIP) ;
			xd.setParamValue("loc_port",this.localPort) ;
			xd.setParamValue("max_c",this.maxConn) ;
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			super.fromXmlData(xd);
			this.localIP = xd.getParamValueStr("loc_ip") ;
			this.localPort = xd.getParamValueInt32("loc_port", 26000) ;
			this.maxConn = xd.getParamValueInt32("max_c",5) ;
		}
	}
	
	private static class TcpClientItem
	{
		String remoteHost = null ;
		
		int remotePort = -1 ;
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("host", remoteHost) ;
			jo.putOpt("port", remotePort) ;
			return jo ;
		}
		
		public String getConnId()
		{
			return remoteHost+":"+remotePort ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.remoteHost = jo.optString("host") ;
			this.remotePort = jo.optInt("port", -1);
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = new XmlData() ;
			xd.setParamValue("host",this.remoteHost) ;
			xd.setParamValue("port",this.remotePort) ;
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			this.remoteHost =  xd.getParamValueStr("host") ;
			this.remotePort = xd.getParamValueInt32("port", -1);
		}
	}
	
	public static class TcpClientsRun extends TcpRunner
	{
		ArrayList<TcpClientItem> tcItems = null;//new ArrayList<>() ;
		
		int connTimeout = 3000 ;
		
		Thread monTh = null ;

		public String getTP()
		{
			return "c";
		}
		
		public int getConnTimeout()
		{
			return connTimeout ;
		}
		
		public List<TcpClientItem> listClientItems()
		{
			return this.tcItems ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = super.toJO() ;
			jo.put("conn_to", this.connTimeout) ;
			JSONArray jarr = new JSONArray() ;
			if(tcItems!=null)
			{
				for(TcpClientItem tci:this.tcItems)
				{
					jarr.put(tci.toJO()) ;
				}
				jo.put("cis", jarr) ;
			}
			return jo ;
		}
		
		public void fromJO(JSONObject jo)
		{
			super.fromJO(jo);
			this.connTimeout = jo.optInt("conn_to",3000) ;
			JSONArray jarr = jo.optJSONArray("cis") ;
			if(jarr!=null)
			{
				ArrayList<TcpClientItem> cis = new ArrayList<>(jarr.length()) ;
				int n = jarr.length() ;
				for(int i = 0 ; i < n ; i ++)
				{
					JSONObject tmpjo = jarr.getJSONObject(i) ;
					TcpClientItem tci = new TcpClientItem() ;
					tci.fromJO(tmpjo);
					cis.add(tci) ;
				}
				this.tcItems = cis ;
			}
		}
		
		public XmlData toXmlData()
		{
			XmlData xd = super.toXmlData() ;
			xd.setParamValue("conn_to", this.connTimeout) ;
			List<XmlData> xds = xd.getOrCreateSubDataArray("cis") ;
			if(tcItems!=null)
			{
				for(TcpClientItem tci:this.tcItems)
				{
					xds.add(tci.toXmlData()) ;
				}
			}
			return xd ;
		}
		
		public void fromXmlData(XmlData xd)
		{
			super.fromXmlData(xd);
			this.connTimeout = xd.getParamValueInt32("conn_to",3000) ;
			List<XmlData> xds = xd.getSubDataArray("cis") ;
			if(xds!=null)
			{
				ArrayList<TcpClientItem> cis = new ArrayList<>(xds.size()) ;
				for(XmlData tmpxd:xds)
				{
					TcpClientItem tci = new TcpClientItem() ;
					tci.fromXmlData(tmpxd);
					cis.add(tci) ;
				}
				this.tcItems = cis ;
			}
		}
		
		private SockItem getSockItem(String host,int port)
		{
			String cid = host+":"+port ;
			List<SockItem> sis = this.RT_listSockItems() ;
			for(SockItem si:sis)
			{
				if(cid.equals(si.getConnId()))
					return si ;
			}
			return null ;
		}
		
		private void RT_monConns()
		{
			List<TcpClientItem> cis = listClientItems() ;
			if(cis==null||cis.size()<=0)
				return ;
			for(TcpClientItem ci:cis)
			{
				SockItem si = getSockItem(ci.remoteHost,ci.remotePort) ;
				if(si!=null && si.checkConn())
					continue ;
				
				RT_connTo(ci) ;
			}
		}
		
		private SockItem RT_connTo(TcpClientItem tci)
		{
			Socket sock = null ;
			
			if(log.isTraceEnabled())
				log.trace(" TcpClientsRun try connect to "+tci.getConnId()) ;
			
			SockItem si = null ;
			try
			{

				sock = new Socket();
				sock.connect(new InetSocketAddress(tci.remoteHost, tci.remotePort),this.connTimeout);
				sock.setTcpNoDelay(true);
				sock.setKeepAlive(true);
				
				si = new SockItem(tci.getConnId(), sock, this, this.rtdataPro.copyMe(this)) ;
				//socks.add(si) ;
				RT_onSockCreated(si);
				//socks.add(si) ;
				si.RT_start() ;
				return si;
			}
			catch ( Exception ee)
			{
				if(log.isDebugEnabled())
				{
					log.debug(" TcpClientsRun will disconnect by connect err:"+ee.getMessage()) ;
					ee.printStackTrace(); 
				}
				if(si!=null)
					si.dispose();
				return null;
			}
		}
		
		private Runnable monRunner = new Runnable() {
			public void run()
			{
				try
				{
					
					while (monTh != null)
					{
						RT_monConns() ;
						
						Thread.sleep(10);
					}
				} catch (Exception e)
				{
					//e.printStackTrace();
					if (log.isDebugEnabled())
						log.debug("ConnPtMultiTcpMsg.TcpClientsRun Stop Error", e);
					// if (log.IsErrorEnabled)
					// log.error(e);
				} finally
				{
					// Stop listening for new clients.
					// close();

					if (log.isDebugEnabled())
						log.debug("ConnPtMultiTcpMsg.TcpClientsRun stoped..");
					// serverThread = null ;

					RT_stop();
				}
			}
		};
		

		@Override
		public synchronized void RT_start(TcpDataPro datapro)
		{
			if(this.monTh!=null)
				return ;
			
			super.RT_start(datapro);
			
			this.monTh = new Thread(monRunner);
			this.monTh.start();
		}

		@Override
		public synchronized void RT_stop()
		{
			Thread t = this.monTh ;
			if(t!=null)
				t.interrupt();
			this.monTh = null ;

			List<SockItem> sis = this.RT_listSockItems(true) ;
			for(SockItem ci:sis)
			{
				ci.dispose();
			}
			
			RT_clearSockItems();
		}

		@Override
		public boolean RT_isRunning()
		{
			return this.monTh!=null;
		}

		@Override
		public String RT_getRunInf()
		{
			int nn = tcItems!=null?tcItems.size():0 ;
			List<SockItem> sis= this.RT_listSockItems() ;
			return "Clients ["+sis.size()+"/"+nn+"]" ;
		}
	}
	
	private static TcpRunner transXD2Runner(XmlData xd)
	{
		if(xd==null)
			return null ;
		String tp = xd.getParamValueStr("_tp") ;
		if(tp==null)
			return null ;
		TcpRunner tr = null ;
		switch(tp)
		{
		case "s":
			tr = new TcpServerRun() ;
			break ;
		case "c":
			tr = new TcpClientsRun() ;
			break ;
		}
		if(tr==null)
			return null ;
		tr.fromXmlData(xd);
		return tr ;
	}
	
	private static TcpRunner transJO2Runner(JSONObject jo)
	{
		if(jo==null)
			return null ;
		String tp = jo.optString("_tp") ;
		if(tp==null)
			return null ;
		TcpRunner tr = null ;
		switch(tp)
		{
		case "s":
			tr = new TcpServerRun() ;
			break ;
		case "c":
			tr = new TcpClientsRun() ;
			break ;
		}
		if(tr==null)
			return null ;
		tr.fromJO(jo);
		return tr ;
	}
	
	
	public static final String TP = "multi_tcp_msg" ;
	
	TcpRunner tcpRunner = null ;
	
	TcpDataPro dataPro = null ;
	
	
	@Override
	public String getConnType()
	{
		return TP;
	}

	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public void RT_checkConn()
	{
		
	}

	@Override
	public boolean isConnReady()
	{
		return this.RT_isRunning();
	}

	@Override
	public String getConnErrInfo()
	{
		return null;
	}
	
	public TcpRunner getTcpRunner()
	{
		return this.tcpRunner ;
	}
	
	public TcpDataPro getTcpDataPro()
	{
		return this.dataPro ;
	}
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData() ;
		if(tcpRunner!=null)
			xd.setSubDataSingle("tcp_run", tcpRunner.toXmlData());
		if(dataPro!=null)
			xd.setSubDataSingle("data_pro", dataPro.toXmlData());
		return xd ;
	}
	
	public boolean fromXmlData(XmlData xd,StringBuilder failedr)
	{
		if(!super.fromXmlData(xd, failedr))
			return false;
		XmlData tmpxd = xd.getSubDataSingle("tcp_run") ;
		this.tcpRunner = transXD2Runner(tmpxd) ;
		tmpxd = xd.getSubDataSingle("data_pro") ;
		this.dataPro = transXD2Pro(tmpxd);
		return true ;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		if(this.RT_isRunning())
			throw new Exception("cannot set while in running") ;
		super.injectByJson(jo);
		JSONObject tmpjo = jo.optJSONObject("tcp_run") ;
		this.tcpRunner = transJO2Runner(tmpjo) ;
		tmpjo =  jo.optJSONObject("data_pro") ;
		this.dataPro = transJO2Pro(tmpjo);
	}
	


	@Override
	public LinkedHashMap<String, ConnDev> getFoundConnDevs()
	{
		return null;
	}
	
	
	private transient boolean drvMsgOnlyGotten = false;
	private transient DevDriverMsgOnly drvMsgOnly = null ;
	
	private DevDriverMsgOnly getDriverMsgOnly()
	{
		if(drvMsgOnlyGotten)
			return drvMsgOnly ;
		
		try
		{
			UACh ch = this.getJoinedCh() ;
			if(ch==null)
				return null;
			DevDriver dd = ch.getDriver() ;
			if(dd==null)
				return null;
			
			if(dd instanceof DevDriverMsgOnly)
			{
				drvMsgOnly = (DevDriverMsgOnly)dd ;
			}
			return drvMsgOnly ;
		}
		catch(Exception e)
		{
			log.warn(e);
			return null ;
		}
		finally
		{
			drvMsgOnlyGotten = true ;
		}
	}
	
	private void RT_onMsgGit(byte[] msg)
	{
		DevDriverMsgOnly drv = getDriverMsgOnly() ;
		if(drv==null)
			return ;
		drv.RT_onConnMsgIn(msg);
	}
	
	public synchronized boolean RT_start()
	{
		if(tcpRunner==null || dataPro==null)
		{
			return false;
		}
		
		tcpRunner.RT_start(this.dataPro);
		return true ;
	}
	
	public synchronized void RT_stop()
	{
		if(tcpRunner==null)
			return ;
		tcpRunner.RT_stop();
	}
	
	public boolean RT_isRunning()
	{
		if(tcpRunner==null)
			return false;
		return tcpRunner.RT_isRunning();
	}
	
	@Override
	public String RT_getConnRunInfo()
	{
		if(tcpRunner==null)
			return "";
		return tcpRunner.RT_getRunInf() ;
	}

}
