package org.iottree.core.alert;

import org.iottree.core.basic.ValEvent;
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
	
	public void sendAlert(String uid,AlertItem ai)
	{
		
	}
}
