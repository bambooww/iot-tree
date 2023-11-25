package org.iottree.core.alert;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class AlertItem
{
	@data_val
	String id = null ;
	
	@data_val
	private String name = "" ;
	
	@data_val
	private String title = "" ;
	
	@data_val
	private String desc="" ;
	
	@data_val(param_name = "js_code")
	String jsCode = null ;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public String getJsCode()
	{
		return jsCode;
	}

	public void setJsCode(String jsCode)
	{
		this.jsCode = jsCode;
	}
	
	
}
