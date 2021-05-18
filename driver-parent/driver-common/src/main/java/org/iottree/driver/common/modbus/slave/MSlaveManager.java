package org.iottree.driver.common.modbus.slave;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;



public class MSlaveManager
{
	static Object locker = new Object() ;
	
	static MSlaveManager slaveMgr = null ;
	
	public static MSlaveManager getInstance()
	{
		if(slaveMgr!=null)
			return slaveMgr ;
		
		synchronized(locker)
		{
			if(slaveMgr!=null)
				return slaveMgr ;
			
			slaveMgr = new MSlaveManager() ;
			return slaveMgr ;
		}
	}
	
	
	ArrayList<MSlave> slaves = new ArrayList<MSlave>() ;
	
	Thread monThread = null;
	
	
	private MSlaveManager()
	{
		Element ele = Config.getConfElement("modbus") ;
		if(ele==null)
			return ;
		
		for(Element sele:Convert.getSubChildElement(ele, "slave"))
		{
			MSlave ms = null ;
			String t = sele.getAttribute("type");
			if("tcp_server".equals(t))
			{
				
				
				ms = new MSlaveTcpServer() ;
			}
			else if("tcp_client".equals(t))
			{
				
			}
			else if("com".equalsIgnoreCase(t))
			{
				
			}
			
			if(ms==null)
				continue ;
			
			ms.init(sele) ;
			
			slaves.add(ms) ;
		}
	}
	
	Runnable monRunner = new Runnable(){

		public void run()
		{
			while(true)
			{
				for(MSlave ms:slaves)
				{
					List<MSlaveDataProvider> bdps = ms.getBitDataProviders();
					if(bdps!=null)
					{
						for(MSlaveDataProvider dp:bdps)
							dp.pulseAcquireData();
					}
					
					bdps = ms.getWordDataProviders();
					if(bdps!=null)
					{
						for(MSlaveDataProvider dp:bdps)
							dp.pulseAcquireData();
					}
				}
				//
				try
				{
					Thread.sleep(50);
				}
				catch (Exception ee)
				{
				}
			}
		}};
	
	/**
	 * �����е�slave�����������
	 *
	 */
	public void start()
	{
		for(MSlave ms:slaves)
		{
			ms.start() ;
		}
		
		monThread = new Thread(monRunner,"mslave_mon") ;
        monThread.start() ;
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		for(MSlave ms:slaves)
		{
			ms.stop() ;
		}
		
		Thread st = monThread;
    	if(st!=null)
    	{//stop recv conn
    		st.interrupt() ;
    		monThread = null ;
    	}
	}
	
	public static void main(String[] args) throws Exception
    {
		MSlaveManager pm = new MSlaveManager();
		
    	String inputLine;
		BufferedReader in = new BufferedReader(
			new InputStreamReader(
			System.in));
		
		String sDevId = null ;
		
		System.out.print(">") ;

		while ( (inputLine = in.readLine()) != null)
		{
			try
			{
				
				StringTokenizer st = new StringTokenizer(inputLine, " ", false);
				String cmds[] = new String[st.countTokens()];
				for (int i = 0; i < cmds.length; i++)
				{
					cmds[i] = st.nextToken();
				}
				if(cmds.length<=0)
					continue ;
				
				if ("start".equals(cmds[0]))
				{
					pm.start() ;
				}
				else if("stop".equals(cmds[0]))
				{
					pm.stop() ;
				}
				else if("ls".equals(cmds[0]))
				{
					for(MSlave pp:pm.slaves)
					{
						System.out.println(pp);
					}
				}
				else if("exit".equals(cmds[0]))
				{
					break ;
				}
			}
			catch (Exception _e)
			{
				_e.printStackTrace();
			}
			finally
			{
				if(sDevId!=null)
					System.out.print(sDevId) ;
				System.out.print(">") ;
			}
		}
		
		System.exit(0);
    }
}
