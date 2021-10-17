package org.iottree.core.basic;

import javax.script.ScriptException;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class ValTransJS extends ValTranser
{
	public static final String NAME = "js";
	
	
	String jsStr = null ;
	
	@Override
	public String getName()
	{
		return NAME;
	}
	@Override
	public String getTitle()
	{
		return "JS";
	}
	
	public String getJsStr()
	{
		return jsStr ;
	}
	
	@Override
	public Object transVal(Object v) throws Exception
	{
		//Number inval = (Number)v ;
		UACodeItem ci = getCodeItem() ;
		if(ci==null)
			throw new Exception("no js code item ") ;
		
		return ci.runCodeFunc(this.getBelongTo(),v);
	}
	
	private transient UACodeItem codeItem = null ;
	
	//private transient UAVal midVal = null ;
	
	
	
	public UACodeItem getCodeItem() throws ScriptException
	{
		if(codeItem!=null)
			return codeItem;
		if(Convert.isNullOrEmpty(jsStr))
			return null ;
		
		UAContext cxt = this.getBelongTo().CXT_getBelongToCxt();
		if(cxt== null)
			return null;
		
		codeItem = new UACodeItem("","{"+this.jsStr+"}")  ;
		codeItem.initItem(cxt, "$tag","$input") ;
		return codeItem;
	}
	
	@Override
	public JSONObject toTransJO()
	{
		JSONObject ret = super.toTransJO();
		
		if(jsStr!=null)
			ret.put("js", jsStr);
		return ret;
	}
	
	@Override
	public boolean fromTransJO(JSONObject m)
	{
		boolean r = super.fromTransJO(m);
		this.jsStr = m.optString("js", null) ;
		return r;
	}
}