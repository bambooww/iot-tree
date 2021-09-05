package org.iottree.core.service;

import java.util.HashMap;

/**
 * IOT-Tree may embed some other server like mq,db etc
 * all these is abstracted as a service
 * 
 * @author jason.zhu
 *
 */
public abstract class AbstractService
{
	protected HashMap<String,String> confPms = null ;
	
	private boolean bEnable = false;
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;
	
	public abstract String getBrief() ;
	
	
	protected void initService(HashMap<String,String> pms) throws Exception
	{
		confPms = pms ;
		bEnable = "true".equalsIgnoreCase(pms.get("enable")) ;
	}
	
	public HashMap<String,String> getConfPMS()
	{
		if(confPms==null)
			return new HashMap<>() ;
		return confPms ;
	}
	
	public void setService(HashMap<String,String> pms) throws Exception
	{
		initService(pms) ;
		ServiceManager.getInstance().saveServiceConf(getName(), pms);
	}
	
	public boolean isEnable()
	{
		return bEnable ;
	}
	
	public abstract boolean startService() ;
	
	public abstract boolean stopService() ;
	
	public abstract boolean isRunning() ;
}
