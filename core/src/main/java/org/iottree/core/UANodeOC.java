package org.iottree.core;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.json.JSONObject;

@data_class
public abstract class UANodeOC extends UANode implements IOCBox
{
	JSONObject ocUnitJSONObj = null ;
	
	public UANodeOC()
	{
		super();
	}
	
	public UANodeOC(String name,String title,String desc)
	{
		super(name,title,desc) ;
	}
	
	/**
	 * true node may has sub unit
	 * @return
	 */
	public boolean OC_supportSub()
	{
		return false;
	}
	
	public List<IOCBox> OC_getSubs()
	{
		return new ArrayList<>();
	}
	
	public JSONObject OC_getPropsJSON()
	{
		return ocUnitJSONObj;
	}
	
	public void OC_setPropsJSON(JSONObject jo)
	{
		ocUnitJSONObj = jo;
	}
	
	public void OCUnit_setProp(String pn,Object pv)
	{
		if(ocUnitJSONObj==null)
			ocUnitJSONObj = new JSONObject() ;
		ocUnitJSONObj.put(pn, pv);
	}
	

	

	public void afterXmlDataExtract(XmlData xd)
	{
		super.afterXmlDataExtract(xd);
		IOCBox.injectToXmlData(xd, this);
	}
	
	public void afterXmlDataInject(XmlData xd)
	{
		super.afterXmlDataInject(xd);
		IOCBox.extractFromXmlData(xd, this);
	}
}
