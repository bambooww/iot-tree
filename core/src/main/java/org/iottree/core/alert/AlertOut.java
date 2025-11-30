package org.iottree.core.alert;

import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public abstract class AlertOut extends JSObMap
{
	public static final String[] TPS = new String[] {"js"};//{"ui","js"} ;
	public static final String[] TP_TITLES = new String[] {"JS"}; // {"UI","JS"} ;
	
	static AlertOut newInsByTp(String tp)
	{
		AlertOut ao = null ;
		switch(tp)
		{
		case AlertOutJS.TP:
			ao = new AlertOutJS() ;
			break ;
//		case AlertOutUI.TP:
//			ao = new AlertOutUI() ;
//			break ;
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
	
	public List<AlertHandler> getRelatedHandlers()
	{
		AlertManager am = AlertManager.getInstancePrjN(this.prj.getName()) ;
		//am.
		return null ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public abstract String getOutTp() ;
	
	public abstract String getOutTpTitle() ;
	
	
	public abstract void sendAlert(String uid,AlertItem ai) throws Exception;
	
	public JSONObject toJO() // throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		jo.put("tpt", this.getOutTpTitle()) ;
		return jo ;
	}
}
