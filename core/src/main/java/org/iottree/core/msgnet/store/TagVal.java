package org.iottree.core.msgnet.store;

import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;

public class TagVal extends JSObMap
{
	UATag tag;
	
	long ts ;
	
	Object val ;
	
	public TagVal()
	{
	}
	
	public TagVal(UATag tag,long ts,Object val)
	{
		this.tag = tag ;
		this.ts = ts ;
		this.val = val ;
	}

	public long getTimestamp()
	{
		return ts ;
	}
	
	public Object getVal()
	{
		return val ;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> jsp = super.JS_props();
		jsp.add(new JsProp("ts",null,Long.class,false,"timestamp",""));
		jsp.add(new JsProp("val",val,null,false,"val",""));
		return jsp ;
	}
	
	@Override
	public Object JS_get(String key)
	{
		Object v = super.JS_get(key);
		if(v!=null)
			return v ;
		
		switch(key)
		{
		case "ts":
			return this.ts ;
		case "val":
			return this.val ;
		}
		
		return null ;
	}
}
