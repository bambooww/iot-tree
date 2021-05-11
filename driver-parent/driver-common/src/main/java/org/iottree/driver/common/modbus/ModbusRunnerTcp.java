package org.iottree.driver.common.modbus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import java.net.*;


/**
 * 
 * @author Jason Zhu
 *
 */
public class ModbusRunnerTcp extends ModbusRunner
{
	static Object lockObj = new Object();
    static ArrayList<ModbusRunnerTcp> ALL_CLIENTS = new ArrayList<ModbusRunnerTcp>() ;
    
    static void increaseCount(ModbusRunnerTcp c)
    {
    	synchronized(lockObj)
    	{
    		ALL_CLIENTS.add(c) ;
    	}
    }
    
    static void decreaseCount(ModbusRunnerTcp c)
    {
    	//System.out.println("client disconn="+c) ;
    	synchronized(lockObj)
    	{
    		ALL_CLIENTS.remove(c);
    	}
    }
    
    
    public static int getClientConnCount()
    {
    	return ALL_CLIENTS.size() ;
    }
    
    public static ModbusRunnerTcp[] getAllClients()
    {
    	synchronized(lockObj)
    	{
    		ModbusRunnerTcp[] rets = new ModbusRunnerTcp[ALL_CLIENTS.size()];
    		ALL_CLIENTS.toArray(rets);
    		return rets;
    	}
    }
    
    
    public static void closeAllClients()
    {
    	for(ModbusRunnerTcp pcf:getAllClients())
    		pcf.stopForce() ;
    }
    
    public static List<ModbusRunnerTcp> getAllClientsList()
    {
    	synchronized(lockObj)
    	{
	    	List<ModbusRunnerTcp> rets = new ArrayList<ModbusRunnerTcp>() ;
	    	rets.addAll(ALL_CLIENTS);
	    	return rets ;
    	}
    }
    
    
    public static List<ModbusRunnerTcp> getClientsByRelatedObj(Object robj)
    {
    	ArrayList<ModbusRunnerTcp> rets = new ArrayList<ModbusRunnerTcp>() ;
    	for(ModbusRunnerTcp rt:ALL_CLIENTS)
    	{
    		if(robj.equals(rt.relatedObj))
    			rets.add(rt) ;
    	}
    	return rets ;
    }
    
    
    public static ModbusRunnerTcp getClientById(String clientid)
    {
    	for(ModbusRunnerTcp c:ALL_CLIENTS)
    	{
    		//if(clientid.equals(c.getClientInfo().devId))
    		//	return c ;
    	}
    	return null ;
    }
    
    static long TMP_ID_C = 0 ;
    
    synchronized static long newTmpId()
    {
    	TMP_ID_C++ ;
    	return TMP_ID_C ;
    }
    
    /**
     * ��Tcp����һ�������ƴ���ӿ�
     * @author Jason Zhu
     *
     */
    public static interface StHandler
    {
    	public boolean checkAuthOk(ModbusRunnerTcp mrt) ;
    	
    	public void onStarted(ModbusRunnerTcp mrt) ;
    	
    	//public boolean onAuthDataRecved(ModbusRunnerTcp mrt,int[] val) ;
    	
    	public void onDisconnected(ModbusRunnerTcp mrt) ;
    	
    	
    	public int getRecvTimeout() ;
    	
    	public int getRecvEndTimeout() ;
    	
    	public int getCmdInterval() ;
    	
    	public int getIgnoreErrCount() ;
    }

	Socket socket = null ;

	
	InputStream serInputs = null;

	OutputStream serOutputs = null;
	
	/**
	 * ���StHandler�������������ڲ����м�״̬�仯���п���
	 */
	transient Object relatedObj = null ;
	
	/**
	 * ����֧����֤���Ͽ�����֮��Ĵ������
	 */
	transient StHandler stH = null ;
	
	//transient Object belongToObj = null ;

	public ModbusRunnerTcp(String uid,Socket s,StHandler sth)
			throws Exception// IMCmdHandler cmdhandler,
	{
		// cmdHandler = cmdhandler ;
		super(uid) ;

		socket = s ;
		
		serInputs = socket.getInputStream() ;
		serOutputs = socket.getOutputStream() ;
		
		stH = sth ;
		
		int t = sth.getCmdInterval() ;
		if(t>0)
			this.setCmdIntervalMS(t) ;
		
		this.setIgnoreErrCount(sth.getIgnoreErrCount()) ;
		
		increaseCount(this) ;
	}

//	public Object getBelongTo()
//	{
//		return belongToObj ;
//	}
//	
//	public void setBelongTo(Object o)
//	{
//		belongToObj = o ;
//	}
	

	void closeConn()
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
			
			
		}
	}

	
	protected boolean checkReady() 
	{
		return socket!=null ;
	}
	
	public InputStream getInputStream()
	{
		return serInputs ;
	}
	
	public OutputStream getOutputStream()
	{
		return serOutputs ;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	transient long lastChkHalt = -1 ;
	protected boolean checkEnd(boolean bhalt)
	{
		if(socket.isClosed())
			return true ;
		
		if(bhalt)
		{//�ж�socket�Ƿ������Ͽ�
		    try
		    {//����Ĵ�����gprs����£����ܷ���������̫�󣬻������
		    	//��Ҫ��ʱ�����
		    	if(System.currentTimeMillis()-lastChkHalt>2000)
		    	{
		    		lastChkHalt = System.currentTimeMillis() ;
		    		socket.sendUrgentData(0xFF);
		    	}
		    	
		    	
		    	
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
//	public 
//	public String readInputLine() throws IOException
//	{
//		InputStreamReader sir = new InputStreamReader(serInputs) ;
//		BufferedReader br = new BufferedReader(sir) ;
//		//si
//		//serInputs.read(arg0)
//		return br.readLine() ;
//	}
	
	protected boolean beforeRunnerStart()
	{
		return true ;
	}
	
	protected void onRunnerStopped()
	{
		closeConn();
		
		decreaseCount(this) ;
		
		stH.onDisconnected(this) ;
	}
	
	
	public void dispose()
	{
		onRunnerStopped();
	}
	/**
	 * �ж��Ƿ���������
	 * @return
	 */
	public boolean isRunningOk()
	{
		//return checkReady() ;
		return bCmdRun;
	}
	
	/**
	 * ��ǰ����״����Ϣ��������в��������򷵻�����
	 * @return
	 */
	public String getRunningInfo()
	{
		return "" ;
	}
	
//	public boolean isAuthOk()
//	{
//		return (clientId>0) ;
//	}
	
	
	public void setRelatedObj(Object robj)
	{
		this.relatedObj = robj ;
	}
	
	public Object getRelatedObj()
	{
		return this.relatedObj ;
	}
	
	public void sendStr(String s) throws IOException
	{
		serOutputs.write(s.getBytes()) ;
	}
	
	public void run()
	{
		try
		{
			// clientInfo.devId = "no_id" ;
			//System.out.println(">>modbus tcp auth on port=["+socket.getLocalPort()+"]");
			//if(log.isDebugEnabled())
			//System.out.println(">>modbus tcp checkAuth on port=["+socket.getLocalPort()+"] clientid="+this.getRelatedObj());
			if(!stH.checkAuthOk(this))
				return ;
			
			//stH.onStarted(this)
			if(log.isDebugEnabled())
				log.debug(">>modbus tcp checkOk . readcmd num="+getReadCmds().size());
			
			
			super.run() ;
		}
		catch (Throwable e)
		{
			//
			if(log.isDebugEnabled())
			{
				log.debug("����ֹͣ����" + e.getMessage());
				e.printStackTrace();
			}

			// close() ;
		}
		finally
		{
			thread = null;
			bCmdRun = false;
			
			//if(log.isErrorEnabled())
			//	log.error("modbus runner tcp break");
			
			onRunnerStopped();
		}
	}

	public String toString()
	{
		if(socket==null)
			return "" ;
		
		return socket.getRemoteSocketAddress().toString() ;
	}
}