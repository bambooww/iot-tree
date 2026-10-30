package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command handles the ZDO route discovery extension message.
 * 
 * @author jason.zhu
 */
public class ExtRouterDiscSREQ extends MsgData
{
	public ExtRouterDiscSREQ()
	{
		super(0x25,0x45) ;
	}
	
	/**
	 * 
	 */
	public ExtRouterDiscSREQ(int dst_addr,int option,int radius)
	{
		super(0x25,0x45) ;
		
		this.data = new byte[3] ;
		
		DataUtil.setUInt16(dst_addr, this.data, 0); //0-1
		this.data[2] = (byte)option ;
		this.data[3] = (byte)radius ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_ROUTE_DISC";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 4;
	}
	
	public ShortAddr getDstAddr()
	{
		return new ShortAddr(DataUtil.getUInt16(this.data, 0)) ;
	}
	
	/**
	 * Route options
	 * @return
	 */
	public int getOption()
	{
		return this.data[2] & 0xFF ;
	}
	
	/**
	 * Broadcast radius
	 * @return
	 */
	public int getRadius()
	{
		return this.data[3] & 0xFF ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " dst_addr="+this.getDstAddr()+" option="+this.getOption()+" radius="+this.getRadius();
	}
}
