package org.iottree.driver.zigbee.zstack.util;

public interface IPackable
{
	public byte[] packTo() ;
	
	public boolean unpackFrom(byte[] data,int offset,int len) ;
}
