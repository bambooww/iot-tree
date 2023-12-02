package org.iottree.core.alert;

import org.iottree.core.basic.ValAlert;
import org.iottree.core.util.xmldata.data_class;

@data_class
public class AlertOutUI  extends AlertOut
{
	public static final String TP = "ui" ;
	
	@Override
	public String getOutTp()
	{
		return TP;
	}

	@Override
	public String getOutTpTitle()
	{
		return "UI";
	}
	
	public void sendAlert(ValAlert alert,long alert_time_ms,String alert_title,String alert_msg,int alert_level,String alert_color)
	{
		
	}
}
