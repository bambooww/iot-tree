package org.iottree.core.basic;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

/**
 * define a value (may be for tag) value option
 * 
 * @author jason.zhu
 */
public abstract class ValOption
{
	private static ILogger log = LoggerManager.getLogger(ValOption.class) ;
	
	public abstract String getTP() ;
	
	public abstract String getOptTitle() ;
	
	public abstract String RT_getValOptTitle(Object val) ;
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("_tp",this.getTP());
		return jo ;
	}
	
	@Override
	public String toString()
	{
		return this.toJO().toString();
	}
	
	protected void fromJO(JSONObject jo)
	{}
	
	public static ValOption transFromJO(JSONObject jo)
	{
		String tp = jo.optString("_tp") ;
		if(Convert.isNullOrEmpty(tp))
			return null ;
		ValOption vo = null ;
		switch(tp)
		{
		case ValOptEnum.TP:
			vo = new ValOptEnum() ;
			break ;
		case ValOptRange.TP:
			vo = new ValOptRange() ;
			break ;
		default:
			return null ;
		}
		
		vo.fromJO(jo);
		return vo ;
	}
	
	public static ValOption parseValOption(String str)
	{
		if(Convert.isNullOrEmpty(str))
			return null ;
		try
		{
			JSONObject jo = new JSONObject(str) ;
			return transFromJO(jo) ;
		}
		catch(Exception eee)
		{
			if(log.isWarnEnabled())
				log.warn(eee);
			return null ;
		}
	}
}
