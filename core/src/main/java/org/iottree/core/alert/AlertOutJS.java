package org.iottree.core.alert;

import org.iottree.core.basic.ValAlert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class AlertOutJS extends AlertOut
{
	public static final String TP = "js" ;
	
	@data_val(param_name = "js")
	String jsCode = null ;
	
	@Override
	public String getOutTp()
	{
		return TP;
	}
	
	@Override
	public String getOutTpTitle()
	{
		return "JS";
	}


	public String getJsCode()
	{
		if(jsCode==null)
			return "" ;
		
		return jsCode;
	}
	
	@Override
	public void sendAlert(ValAlert alert,long alert_time_ms,String alert_title,String alert_msg,int alert_level,String alert_color)
	{
		
	}
}
