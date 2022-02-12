package org.iottree.driver.common.modbus.sim;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.sim.SimTag;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class SlaveTag extends SimTag
{
	SlaveDevSeg relatedSeg = null ;
	

	@data_val
	int regIdx = -1 ;
	
	
	public SlaveTag()
	{}
	
	public SlaveTag(String name,int regidx)
	{
		super(name) ;
		this.regIdx = regidx ;
	}

	public int getRegIdx()
	{
		return regIdx ;
	}
	
	
	public SlaveDevSeg getRelatedSeg()
	{
		return this.relatedSeg ;
	}

	@Override
	public Class<?> getValueTp()
	{
		if(this.relatedSeg.isBoolData())
			return Boolean.class ;
		else
			return Short.class ;
	}

	@Override
	public Object getValue()
	{
		return this.relatedSeg.getSlaveData(regIdx) ;
	}

	@Override
	public void setValue(Object val)
	{
		this.relatedSeg.setSlaveDataStr(regIdx, val.toString());
	}

	public Object JS_get(String key)
	{
		switch(key)
		{
		case "_regidx":
			return regIdx ;
		case "_value":
			return this.getValue() ;
		default:
			return null ;
		}
	}

	public List<Object> JS_names()
	{
		ArrayList<Object> rets = new ArrayList<>() ;
		rets.add("_regidx");
		rets.add("_value");
		return rets ;
	}
	
	public Class<?> JS_type(String key)
	{
		switch(key.toLowerCase())
		{
		case "_value":
			return this.getValueTp();
		default:
			break ;//do nothing
		}
		return null ;
	}
	
	public void JS_set(String key,Object v)
	{
		switch(key.toLowerCase())
		{
		case "_value":
			this.setValue(v);
			return;
		default:
			break ;//do nothing
		}
		
		super.JS_set(key, v);
	}
}
