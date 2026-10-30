package org.iottree.driver.zigbee.ember.util;

public class EzspError extends Exception
{
	EzspStatus code ;
	
	public EzspError(EzspStatus code)
	{
		this.code = code ;
	}
	
	public EzspStatus getCode()
	{
		return this.code ;
	}
}
