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
	private boolean bEnable = false;
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;
	
	
	
	
	protected void initService(HashMap<String,String> pms) throws Exception
	{
		bEnable=true;//debut tmp
		
		if(pms==null)
			return ;
		bEnable = "true".equalsIgnoreCase(pms.get("enable")) ;
		
	}
	
	public boolean isEnable()
	{
		return bEnable ;
	}
	
	public abstract boolean startService() ;
	
	public abstract boolean stopService() ;
	
	public abstract boolean isRunning() ;
}
