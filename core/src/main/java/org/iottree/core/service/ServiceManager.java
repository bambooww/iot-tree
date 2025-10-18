package org.iottree.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public class ServiceManager
{
	private static ILogger log = LoggerManager.getLogger(ServiceManager.class) ;
	
	private static ServiceManager inst  = null ;
	
	public static ServiceManager getInstance()
	{
		if(inst!=null)
			return inst ;
		
		synchronized(ServiceManager.class)
		{
			if(inst!=null)
				return inst ;
			
			inst = new ServiceManager() ;
			return inst ;
		}
	}
	
	private static String[] ALL_CNS = new String[] {
			"org.iottree.core.service.ServiceActiveMQ",
			"org.iottree.driver.opc.opcua.server.OpcUAService",
			"org.iottree.driver.bacnet.BACnetService"
	} ;
	
	private ArrayList<AbstractService> allServices = new ArrayList<>() ;
	
	private ServiceManager()
	{
		init();
	}
	
	private void init()
	{
		for(String cn:ALL_CNS)
		{
			try
			{
				Class<?> c = Class.forName(cn) ;
				if(c==null)
					continue ;
				AbstractService as = (AbstractService)c.getConstructor().newInstance() ;
				allServices.add(as) ;
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private HashMap<String,String> loadServiceConf(String servicename)
	{
		File f = new File(Config.getDataDirBase()+"service/"+servicename+".txt") ;
		if(!f.exists())
			return new HashMap<>() ;
		return Convert.loadPropFile(f) ;
	}
	
	public void saveServiceConf(String servicename,HashMap<String,String> pms) throws Exception
	{
		File f = new File(Config.getDataDirBase()+"service/"+servicename+".txt") ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		Convert.savePropFile(f, pms);
	}
	
	public List<AbstractService> listServices()
	{
		return allServices;
	}
	
	public AbstractService getService(String name)
	{
		for(AbstractService r : allServices)
		{
			if(name.equals(r.getName()))
				return r ;
		}
		return null ;
	}
	
	synchronized public void start()
	{
		for(AbstractService as : allServices)
		{
			try
			{
				HashMap<String,String> conf = this.loadServiceConf(as.getName()) ;
				as.initService(conf);
				if(as.isEnable())
				{
					StringBuilder failedr = new StringBuilder() ;
					if(!as.startService(failedr))
						log.error(failedr.toString());
				}
			}
			catch(Throwable ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	synchronized public void stop()
	{
		for(AbstractService as : allServices)
		{
			as.stopService() ;
		}
	}
}
