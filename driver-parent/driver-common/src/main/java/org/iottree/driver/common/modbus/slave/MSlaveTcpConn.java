package org.iottree.driver.common.modbus.slave;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.common.modbus.*;

/**
 * һ�ν�����tcp���Ӷ���
 * 
 * �ڲ�����һ���̣߳��ȴ�master����modbusָ��
 * @author jasonzhu
 *
 */
public class MSlaveTcpConn implements Runnable
{
	static ILogger log = LoggerManager.getLogger(MSlaveTcpConn.class);
	
	static Object lockObj = new Object();
    static ArrayList<MSlaveTcpConn> ALL_CONNS = new ArrayList<MSlaveTcpConn>() ;
    
    private static void increaseCount(MSlaveTcpConn c)
    {
    	synchronized(lockObj)
    	{
    		ALL_CONNS.add(c) ;
    	}
    }
    
    private static void decreaseCount(MSlaveTcpConn c)
    {
    	//System.out.println("client disconn="+c) ;
    	synchronized(lockObj)
    	{
    		ALL_CONNS.remove(c);
    	}
    }
    
    
    public static int getConnCount()
    {
    	return ALL_CONNS.size() ;
    }
    
    public static MSlaveTcpConn[] getAllConns()
    {
    	synchronized(lockObj)
    	{
    		MSlaveTcpConn[] rets = new MSlaveTcpConn[ALL_CONNS.size()];
    		ALL_CONNS.toArray(rets);
    		return rets;
    	}
    }
    
    
    public static void closeAllConns()
    {
    	for(MSlaveTcpConn pcf:getAllConns())
    		pcf.stopForce() ;
    }
    
    public static List<MSlaveTcpConn> getAllClientsList()
    {
    	synchronized(lockObj)
    	{
	    	List<MSlaveTcpConn> rets = new ArrayList<MSlaveTcpConn>() ;
	    	rets.addAll(ALL_CONNS);
	    	return rets ;
    	}
    }


	Socket socket = null ;
	//SerialPort serialPort = null;

	InputStream serInputs = null;

	OutputStream serOutputs = null;

	Thread thread = null;
	
	boolean bRun = false;
	
	//MSlaveDataProvider dataProvider = null ;
	MSlave belongTo = null ;

	public MSlaveTcpConn(MSlave ms,Socket socket)
			throws Exception// IMCmdHandler cmdhandler,
	{
		// cmdHandler = cmdhandler ;
		//super(mcmds) ;
		belongTo = ms ;

		this.socket = socket;
		
		serInputs = socket.getInputStream();
		serOutputs = socket.getOutputStream();
		
		//dataProvider = dp ;
		increaseCount(this);
	}




	void closeTcpConn()
	{
		try
		{
			if (serInputs != null)
			{
				try
				{
					serInputs.close();
				}
				catch (Exception ee)
				{
				}
			}

			if (serOutputs != null)
			{
				try
				{
					serOutputs.close();
				}
				catch (Exception ee)
				{
				}
			}

			if (socket != null)
			{
				try
				{
					socket.close();
				}
				catch (Exception ee)
				{
				}
			}
		}
		finally
		{
			socket = null;
			decreaseCount(this);
		}
	}
	
	
	//static final int REQ_ST_0 = 0 ;
	//static final int REQ_ST_RECV = 1 ;
	//static final int REQ_ST_0 = 2 ;
	//static final int REQ_ST_0 = 0 ;
	
	private void delay(int ms)
	{
		try
		{
			Thread.sleep(ms) ;
		}
		catch(Exception ee)
		{}
	}
	
	/**
	 * ���ݶ������������ݣ���÷��ؽ��
	 * @param reqbs
	 * @return
	 */
	private byte[] onReadReqAndResp(byte[] reqbs,int[] parseleft)
	{
		ModbusCmd mc = ModbusCmd.parseRequest(reqbs,parseleft) ;
		if(mc==null)
			return null ;
		
		if(mc instanceof ModbusCmdReadBits)
		{
			ModbusCmdReadBits mcb =(ModbusCmdReadBits)mc ;
			return onReqAndRespBits(mcb);
		}
		else if(mc instanceof ModbusCmdReadWords)
		{
			return onReqAndRespWords((ModbusCmdReadWords)mc) ;
		}
		
		return null ;
	}

	private byte[] onReqAndRespBits(ModbusCmdReadBits mcb)
	{
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		int req_num = mcb.getRegNum() ;
		boolean[] resp = new boolean[req_num] ;
		for(int i=0;i<req_num;i++)
			resp[i] = false;
		
		for(MSlaveDataProvider dp:belongTo.getBitDataProviders())
		{
			if(dp.getDevAddr()!=devid)
				continue ;
			
			int dp_regidx = dp.getRegIdx() ;
			int dp_regnum = dp.getRegNum() ;
			if(req_idx+req_num<=dp_regidx)
				continue ;
			if(req_idx>dp_regidx+dp_regnum)
				continue ;
			
			MSlaveDataProvider.SlaveData sd = dp.getSlaveData() ;
			if(sd==null || !(sd instanceof MSlaveDataProvider.BoolDatas))
			{
				continue ;
			}
			MSlaveDataProvider.BoolDatas msb = (MSlaveDataProvider.BoolDatas)sd ;
			//��ô�provider��regidx=0��ʼ������
			boolean[] bs = msb.getBoolUsingDatas() ;
			if(bs==null)
				continue ;
			
			if(req_idx<dp_regidx)
			{
				if(req_idx+req_num<dp_regidx+bs.length)
					System.arraycopy(bs, 0, resp, dp_regidx-req_idx, req_num-(dp_regidx-req_idx)) ;
				else
					System.arraycopy(bs, 0, resp, dp_regidx-req_idx, bs.length) ;
			}
			else
			{
				if(req_idx+req_num<dp_regidx+bs.length)
					System.arraycopy(bs,req_idx-dp_regidx, resp, 0, req_num) ;
				else
					System.arraycopy(bs,req_idx-dp_regidx, resp, 0, bs.length-(req_idx-dp_regidx)) ;
			}
		}
		
		return ModbusCmdReadBits.createResp(devid,mcb.getFC(),resp);
	}
	
	
	private byte[] onReqAndRespWords(ModbusCmdReadWords mcb)
	{
		
		short devid = mcb.getDevAddr();
		
		int req_idx = mcb.getRegAddr() ;
		int req_num = mcb.getRegNum() ;
		
		
		short[] resp = new short[req_num] ;
		for(int i=0;i<req_num;i++)
			resp[i] = 0;
		
		for(MSlaveDataProvider dp:belongTo.getWordDataProviders())
		{
			if(dp.getDevAddr()!=devid)
				continue ;
			
			int dp_regidx = dp.getRegIdx() ;
			int dp_regnum = dp.getRegNum() ;
			if(req_idx+req_num<=dp_regidx)
				continue ;
			if(req_idx>dp_regidx+dp_regnum)
				continue ;
			
			MSlaveDataProvider.SlaveData sd = dp.getSlaveData() ;
			if(sd==null || !(sd instanceof MSlaveDataProvider.SlaveDataWord))
			{
				continue ;
			}
			MSlaveDataProvider.SlaveDataWord msb = (MSlaveDataProvider.SlaveDataWord)sd ;
			//��ô�provider��regidx=0��ʼ������
			short[] bs = msb.getInt16UsingDatas() ;
			if(bs==null)
				continue ;
			
			if(req_idx<dp_regidx)
			{
				if(req_idx+req_num<dp_regidx+bs.length)
					System.arraycopy(bs, 0, resp, dp_regidx-req_idx, req_num-(dp_regidx-req_idx)) ;
				else
					System.arraycopy(bs, 0, resp, dp_regidx-req_idx, bs.length) ;
			}
			else
			{
				if(req_idx+req_num<dp_regidx+bs.length)
					System.arraycopy(bs,req_idx-dp_regidx, resp, 0, req_num) ;
				else
					System.arraycopy(bs,req_idx-dp_regidx, resp, 0, bs.length-(req_idx-dp_regidx)) ;
			}
		}
		
		return ModbusCmdReadWords.createResp(devid,mcb.getFC(),resp);
	}
	
	
	static final int BUF_LEN = 255; 

	public void run()
	{
		try
		{
			// clientInfo.devId = "no_id" ;
			System.out.println(">>modbus runner="+this.getClass().getCanonicalName()+" on port="+socket.getRemoteSocketAddress());

			//SerialPort sp = null;
			//byte[] rdata = new byte[255];
			                         
			//boolean b_in_recv = false;
			int last_dlen = 0 ;
			long last_dt = -1 ;
			
			long last_no_dt = System.currentTimeMillis() ;
			
			byte[] buf = new byte[BUF_LEN] ;
			int len = 0 ;
			
			while (bRun)
			{//ͨ��ʱ������ȡmodbusָ��ԭʼ����
				delay(1) ;
				
				
				if(last_dlen==0)
				{//no data,not in recv
					//System.out.println("avlen="+serInputs.available()) ;
					
//					int c = serInputs.read(buf,len,BUF_LEN-len) ;
//					if(c>0)
//						len += c ;
//					System.out.println("rlen="+len) ;
					if(serInputs.available()<=0)
					{
						delay(5) ;
						if(System.currentTimeMillis()-last_no_dt>5000)
						{
							last_no_dt = System.currentTimeMillis() ;
							socket.sendUrgentData(0) ;
						}
						else
						{
							
						}
					}
					else
					{
						last_dlen = serInputs.available() ;
						last_dt = System.currentTimeMillis() ;
					}
					
					continue;
				}
				
				//recv
				if(serInputs.available()>last_dlen)
				{
					last_dlen = serInputs.available() ;
					last_dt = System.currentTimeMillis() ;
					continue ;
				}
				
				//check recv end
				if(System.currentTimeMillis()-last_dt<10)
				{//recv not end
					continue ;
				}
				
				int rlen = last_dlen ;
				try
				{
					//recv end
					if(last_dlen>255)
					{//err data
						serInputs.skip(last_dlen) ;
						continue ;
					}
				}
				finally
				{
					last_dlen = 0 ;
					last_dt = 0 ;
				}
				
				byte[] rdata = new byte[rlen] ;
				serInputs.read(rdata) ;
				
				//System.out.println("1 readlen="+rlen+"  "+Convert.byteArray2HexStr(rdata)) ;
				//�������󲢷��ؽ��
				long st = System.currentTimeMillis() ;
				
				int[] pl = new int[1] ;
				pl[0] = 0 ;
				
				do
				{
					if(pl[0]<0)
						break ;
					
					if(pl[0]>0)
					{
						byte[] crbs = new byte[rdata.length-pl[0]] ;
						System.arraycopy(rdata, pl[0], crbs, 0, crbs.length) ;
						rdata = crbs ;
					}
					
					byte[] respbs = onReadReqAndResp(rdata,pl) ;
					
					//System.out.println("2 on req resp cost="+(System.currentTimeMillis()-st)+" replen="+respbs.length) ;
					if(respbs!=null)
					{
						//System.out.println("3 resp len="+respbs.length+"  "+Convert.byteArray2HexStr(respbs)) ;
						//�������󲢷��ؽ��
						serOutputs.write(respbs) ;
						serOutputs.flush() ;
					}
				}
				while(pl[0]>=0) ;
			}// end of while
		}
		
		catch (Throwable e)
		{
			//e.printStackTrace();
			log.error("MSlaveTcpConn Broken:" + e.getMessage());

			//System.out.println("MSlaveTcpConn Broken:" + e.getMessage());
			// close() ;
		}
		finally
		{
			thread = null;
			bRun = false;
			onRunnerStopped();
		}
	}
	

	synchronized void startRunner()
	{
		if (thread != null)
			return;

		bRun = true;
		thread = new Thread(this, "MSlaveRunner");
		thread.start();
	}
	

	public void start() throws Exception
	{
		startRunner();
	}


	
	
	synchronized protected void stopRunner(boolean interrupt)
	{
		Thread th = thread;
		if (th == null)
			return;

		if (interrupt)
		{
			th.interrupt();
			thread = null;
		}
		
		onRunnerStopped();//closeCom();
	}

	
	public void stopForce()
	{
		stopRunner(true) ;
	}
	
//	protected boolean checkEnd()
//	{
//		return tcpClient.isClosed();
//	}
	
	protected boolean checkEnd(boolean bhalt)
	{
		if(socket.isClosed())
			return true ;
		
		if(bhalt)
		{//�ж�socket�Ƿ������Ͽ�
		    try
		    {
		    	socket.sendUrgentData(0xFF);  
		    }
		    catch(Exception ex)
		    {
		    	try
		    	{
		    		socket.close() ;
		    	}
		    	catch(Exception sex){}
		    	
		        return true ;
		    }
		}
		
		return false;
	}
	
	protected InputStream getInputStream()
	{
		return serInputs ;
	}
	
	protected OutputStream getOutputStream()
	{
		return serOutputs ;
	}
	
	
	protected void onRunnerStopped()
	{
		closeTcpConn();
	}
	
	/**
	 * �ж��Ƿ���������
	 * @return
	 */
	public boolean isRunningOk()
	{
		return socket!=null ;
	}
	
	/**
	 * ��ǰ����״����Ϣ��������в��������򷵻�����
	 * @return
	 */
	public String getRunningInfo()
	{
		if(isRunningOk())
			return "ok" ;
		else
			return "tcp error" ;
	}
}
