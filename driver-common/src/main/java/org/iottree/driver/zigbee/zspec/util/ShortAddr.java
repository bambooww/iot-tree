package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.DataUtil;

/**
 * 2bytes addr
 * Dst Addr or Src Addr
 * 
 * @author jason.zhu
 */
public class ShortAddr
{
	public static final int BROADCAST_ACTIVE = 0xFFFD ; // 所有非睡眠设备（广播）
	
	public static final int BROADCAST_ALL = 0xFFFD ; // 所有设备（广播）
	
	public static final int COORDINATOR = 0x0000 ; //协调器

	int addr ;
	
	public ShortAddr(int v)
	{
		this.addr = v ;
	}
	
	public ShortAddr(byte[] bs,int offset)
	{
		this.addr = DataUtil.getUInt16(bs, offset) ;
	}
	
	public int getAddrVal()
	{
		return this.addr ;
	}
	
	public String toHexStr()
	{
		return DataUtil.transHexAddr(this.addr) ;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ShortAddr))
			return false;
		return this.addr == ((ShortAddr)obj).addr ;
	}
	
	public String toString()
	{
		return this.toHexStr() ;
	}
}
