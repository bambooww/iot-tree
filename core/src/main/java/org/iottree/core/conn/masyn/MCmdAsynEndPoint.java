package org.iottree.core.conn.masyn;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.Future;

import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.DesInputStream;
import org.iottree.core.util.DesOutputStream;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;


/**
 * based on stream connection,
 * one point will support send MCmdAsyn cmd
 * using inner listener thread,to recv another ep's msg,and handle it
 * 
 * @author Jason Zhu
 * 
 */
public class MCmdAsynEndPoint implements Runnable
{
	static ILogger log = LoggerManager.getLogger(MCmdAsynEndPoint.class);

	static Object lockObj = new Object();

	static ArrayList<MCmdAsynEndPoint> ALL_CLIENTS = new ArrayList<MCmdAsynEndPoint>();

	static void increaseCount(MCmdAsynEndPoint c)
	{
		synchronized (lockObj)
		{
			ALL_CLIENTS.add(c);
//			c.container.increaseCount(c) ;
		}
	}

	static void decreaseCount(MCmdAsynEndPoint c)
	{
		// System.out.println("client disconn="+c) ;
		synchronized (lockObj)
		{
			ALL_CLIENTS.remove(c);
//			c.container.decreaseCount(c) ;
		}
	}

	public static int getClientConnCount()
	{
		return ALL_CLIENTS.size();
	}

	public static MCmdAsynEndPoint[] getAllClients()
	{
		synchronized (lockObj)
		{
			MCmdAsynEndPoint[] rets = new MCmdAsynEndPoint[ALL_CLIENTS.size()];
			ALL_CLIENTS.toArray(rets);
			return rets;
		}
	}

	public static List<MCmdAsynEndPoint> getAllClientsList()
	{
		synchronized (lockObj)
		{
			List<MCmdAsynEndPoint> rets = new ArrayList<MCmdAsynEndPoint>();
			rets.addAll(ALL_CLIENTS);
			return rets;
		}
	}
	
	/**
	 * get all EndPoints by belongTo
	 * @param bt
	 * @return
	 */
	public static List<MCmdAsynEndPoint> getAllClientsByBelongTo(Object bt)
	{
		List<MCmdAsynEndPoint> rets = new ArrayList<MCmdAsynEndPoint>();
		//System.out.println("all client size="+ALL_CLIENTS.size()) ;
		synchronized (lockObj)
		{
			for(MCmdAsynEndPoint ep:ALL_CLIENTS)
			{
				if(ep.belongTo==bt)
				{
					//System.out.println("all client size="+ALL_CLIENTS.size()) ;
					rets.add(ep);
				}
			}
		}
		
		return rets;
	}
	
//	public static MCmdAsynEndPoint getAllClientsByBelongToId(Object bt,String id)
//	{
//		synchronized (lockObj)
//		{
//			//List<MCmdAsynEndPoint> rets = new ArrayList<MCmdAsynEndPoint>();
//			for(MCmdAsynEndPoint ep:ALL_CLIENTS)
//			{
//				if(ep.belongTo==bt && id.equals(ep.getClientInfo().getId()))
//					return ep ;
//			}
//			//return rets;
//			return null ;
//		}
//	}
//
//	public static MCmdAsynEndPoint getClientById(String clientid)
//	{
//		for (MCmdAsynEndPoint c : ALL_CLIENTS)
//		{
//			if (clientid.equals(c.getClientInfo().getId()))
//				return c;
//		}
//		return null;
//	}

	static long TMP_ID_C = 0;

	synchronized static long newTmpId()
	{
		TMP_ID_C++;
		return TMP_ID_C;
	}

	/**
	 * dummy cmd ,used to send heart pulse
	 */
	static MCmdAsyn DUMMY_CMD = new MCmdAsyn("___",null);

	
	Thread thread = null;

	String id = null ;
	//MCmdAsynContainer container = null ;
	/**
	 * belongTo object
	 */
	Object belongTo = null ;
	// boolean bRun = false;

	//private MCmdAsynClientInfo clientInfo = null;

	/**
	 * server may send pulse interval to keep conn
	 */
	transient private long lastPulse = -1;

	/**
	 * cmd continuely err num
	 */
	transient int cmdErrorNum = 0;

	//MCmdAsynHandler handler = null;
	MCmdAsynStateM statM = null ;
	
	ConnPtStream connPtStream = null ;
	
	transient boolean bRun = false;
	
	/**
	 * 
	 */
	transient long createDT = -1 ;

	public MCmdAsynEndPoint(String id,Object belongto,MCmdAsynStateM statm) //throws Exception// IMCmdHandler
	{
		this.id = id ;
//		MCmdAsynContainer container,
//		this.container = container ;
		this.belongTo = belongto ;
		
		this.statM = statm ;
		// cmdHandler = cmdhandler ;
//		socket = tcp;
//		
//		clientInfo = new MCmdAsynClientInfo(tcp.getInetAddress()
//				.getHostAddress(), tcp.getPort(), null);

		increaseCount(this);
		
		
		createDT = System.currentTimeMillis() ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public void setConnPtStream(ConnPtStream cps)
	{
		this.connPtStream = cps ;
	}
	
	
	private InputStream getInputStream()
	{
		if(this.connPtStream==null)
			return null ;
		return connPtStream.getInputStream() ;
	}
	
	public OutputStream getOutputStream()
	{
		if(this.connPtStream==null)
			return null ;
		return connPtStream.getOutputStream();
	}
//	/**
//	 * 获得对应的容器
//	 * @return
//	 */
//	public MCmdAsynContainer getContainer()
//	{
//		return container ;
//	}
	
	public long getCreateDT()
	{
		return createDT ;
	}

//	public void setHanler(MCmdAsynHandler h)
//	{
//		//this.statM = sm ;
//		this.handler = h ;
//	}
	
	public void setStateM(MCmdAsynStateM sm)
	{
		this.statM = sm ;
	}
	
	/**
	 * 获得当前连接点的状态机
	 * @return
	 */
	public MCmdAsynStateM getStateM()
	{
		return this.statM ;
	}
	
	/**
	 * 由外界监控线程定时调用的驱动状态机运行的方法
	 *
	 */
	public void pulseStateMachine()
	{
		if(this.statM==null)
			return ;
		
		this.statM.pulseWithInterval() ;
	}


//	public MCmdAsynClientInfo getClientInfo()
//	{
//		return clientInfo;
//	}
//	
//	public String setIdSession(String id)
//	{
//		return clientInfo.setIdSession(id) ;
//	}
//	
//	public String getId()
//	{
//		return clientInfo.getId() ;
//	}
//	
//	public String getSessionId()
//	{
//		return clientInfo.getSessionId() ;
//	}

	synchronized public void start()
	{
		if (bRun)
			return;

		bRun = true ;
		thread = new Thread(this, "iottree-MsgCmdEndPoint");
		thread.start();
	}

	 synchronized void stop()
	 {
		 if(thread!=null)
		 {
			 thread.interrupt() ;
			 thread = null;
		 }
		 
		 bRun = false ;
	 }
	 
	 public boolean isRunning()
	 {
		 return bRun ;
	 }
	 
	 public boolean sendCmdAsyn(MCmdAsyn mca) throws Exception
	 {
		 return sendCmdAsyn(mca,null);
	 }

	 private transient long lastSendDT = -1 ;
	 /**
	  * 异步发送数据，此方法经过测试，就算调用成功，也无法判断数据
	  * 是否成功到达。只能通过返回信息处理
	  * @param mca
	  * @throws Exception
	  */
	 synchronized public boolean sendCmdAsyn(MCmdAsyn mca,ITransListener tlis) throws Exception
	 {
		 try
		 {
			 OutputStream outs = connPtStream.getOutputStream() ;
			 mca.writeOut(outs,tlis) ;
			 return true ;
		 }
		 catch(SocketException se)
		 {
			 se.printStackTrace();
			 dispose();
			 return false;
		 }
		 finally
		 {
			 lastSendDT = System.currentTimeMillis();
		 }
	 }
	 
	 /**
	  * 发送测试指令，用来发现通道断开
	  * @throws Exception
	  */
	 void sendCmdDummy()
	 {
		 if(System.currentTimeMillis()-lastSendDT<5000)
			 return ;
		 try
		 {
			 sendCmdAsyn(DUMMY_CMD);
		 }
		 catch(Exception ee)
		 {}
	 }
	 
	 public void sendFileStart(File f)
	 {
		 
	 }
	 
	 public void sendFileContent()
	 {
		 
	 }
	 
	 public void sendFileEnd()
	 {
		 
	 }
//	 /**
//	  * 给内部使用的id发送
//	  * 一般在一个新链接建立的时候
//	  */
//	 void sendIdInner(String id) throws Exception
//	 {
//		 
//		 outstream.write((id+"\n").getBytes()) ;
//		 outstream.flush() ;
//	 }
//	 
//	 public static final int MAX_ID_LEN = 100 ;
//	 
//	 String readIdInner(int timeout) throws Exception
//	 {
//		int oldso = socket.getSoTimeout();
//		
//		try
//		{
//			socket.setSoTimeout(timeout) ;
//			int c ;
//			StringBuffer idsb = new StringBuffer() ;
//			while((c=instream.read())!='\n' && idsb.length()<MAX_ID_LEN)
//			{
//				idsb.append((char)c) ;
//			}
//			
//			if(idsb.length()>=MAX_ID_LEN)
//				return null ;
//			
//			return idsb.toString() ;
//		}
//		catch (SocketTimeoutException e)
//        {
//			return null ;
//        }
//		finally
//		{
//			socket.setSoTimeout(oldso) ;
//		}
//	 }

	private void fireCmdRecved(MCmdAsyn mca)
	{
		if (this.statM != null)
		{
			//System.out.println("fire cmd recv="+mca) ;
			try
			{
				if(mca.isAckCmd())
				{//特殊处理ack命令
					if(statM instanceof IAckListener)
					{//注意，IAckListener必须由StateM实现
						((IAckListener)statM).onAckRecved(mca.getAckId(),mca.getAckCmd()) ;
					}
					return;
				}
				
				//正常消息，判断是否需要回复ack命令
				MCmdAsyn ackm = mca.createAckCmd();
				if(ackm!=null)
				{//往回发送ack
					//System.out.println("create ack cmd >"+ackm) ;
					this.sendCmdAsyn(ackm,null) ;
					
					//对于有ack信息的命令，判断此命令是否已经被接收，如果已经被接收
					//则丢弃不做后续的处理,考虑
//					if(container.checkAndSetAckId(getId(),ackm.getAckId(),ackm.getAckCmd()))
//						return ;//此数据已经被接收
				}
				
				this.statM.onMCmdAsynRecved(mca);
			}
			catch(Exception ee)
			{
				if(log.isErrorEnabled())
					log.error(ee) ;
			}
		}
	}
	
	/**
	 * 判断是否支持发送，如链接状态机验证通过
	 * 
	 * @return
	 */
	public boolean checkStateOk()
	{
		if(statM==null)
			return false;
		
		if(!statM.checkStateMOk())
			return false;
		
		return bRun ;
	}

	public void run()
	{
		try
		{
			// 终端CONNECT OK返回还没有tcp相应快
			// 所以应该稍等,再发送指令
			// Thread.sleep(1000) ;
			
			if (log.isDebugEnabled())
				log.debug(" conn="+this.connPtStream.getName()) ;

			while (bRun)
			{
				MCmdAsyn rcmd = null;
				
				String fb = null ;
				if(statM!=null)
					fb = statM.getRecvFileBase();
				
				InputStream inputs = this.getInputStream() ;
				if(inputs==null)
					break ;
				rcmd = MCmdAsyn.readFrom(fb,inputs);
				//System.out.println("r <<<<<<<<<<<<<"+rcmd) ;
				if (rcmd == null)
					continue;
			
				fireCmdRecved(rcmd);
			}
		}
		catch (Exception ee)
		{
			//ee.printStackTrace() ;
			if (log.isDebugEnabled())
				log.debug("MCmdAsynEndPoint break with "+ee.getMessage());
		}
		finally
		{
			if(log.isDebugEnabled())
				log.debug("msg recv err－broken [id:"+this.id+"]");
			
			close();
			
			if(this.statM!=null)
				this.statM.onMCmdAsynBroken() ;
			
//			if(this.belongTo!=null && this.belongTo instanceof MCmdAsynClient)
//			{
//				MCmdAsynClient.StListener lis =((MCmdAsynClient)belongTo).stLis ;
//				if(lis!=null)
//					lis.onAsynClientConnChanged(getId(), MCmdAsynClient.STCONN_BROKEN);
//			}
			
			bRun = false;
			thread = null ;
		}
	}

	public void close()
	{
		//stop() ;
		bRun = false;
		
		if(this.connPtStream!=null)
		{
			try
			{
				this.connPtStream.close();
			}
			catch(Exception e)
			{
				
			}
		}

		decreaseCount(this);
	}
	
	
	public void dispose()
	{
		stop() ;
		close() ;
	}

	public String toString()
	{
		return this.id+" "+this.bRun ;
	}
}
