package org.iottree.core.alert;

import org.iottree.core.UAPrj;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public abstract class AlertOut
{
	public static final String[] TPS = new String[] {"ui","js"} ;
	public static final String[] TP_TITLES = new String[] {"UI","JS"} ;
	
	static AlertOut newInsByTp(String tp)
	{
		AlertOut ao = null ;
		switch(tp)
		{
		case AlertOutJS.TP:
			ao = new AlertOutJS() ;
			break ;
		case AlertOutUI.TP:
			ao = new AlertOutUI() ;
			break ;
		}
		return ao ;
	}
	
	@data_val
	String id = null ;
	
//	@data_val(param_name = "n")
//	String name = null ;
	
	@data_val(param_name = "t")
	String title = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	transient UAPrj prj = null ;
	
	public AlertOut()
	{
		this.id = CompressUUID.createNewId() ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
//	public String getName()
//	{
//		return this.name ;
//	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public abstract String getOutTp() ;
	
	public abstract String getOutTpTitle() ;
	
	
	public abstract void sendAlert(ValAlert alert,long alert_time_ms,String alert_title,String alert_msg,int alert_level,String alert_color) ;
	
	public JSONObject toJO() // throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		jo.put("tpt", this.getOutTpTitle()) ;
		return jo ;
	}
}
