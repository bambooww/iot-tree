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
		Object r = super.JS_get(key) ;
		if(r!=null)
			return r ;
		switch(key)
		{
		case "_regidx":
			return regIdx ;
		default:
			return null ;
		}
	}

	public List<String> JS_names()
	{
		List<String> rets = super.JS_names() ;
		rets.add("_regidx");
		return rets ;
	}
	
}
