package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.AddrMode;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command is generated to request a Bind
 * 
 * @author jason.zhu
 */
public class BindSREQ extends MsgData
{	
	public BindSREQ()
	{
		super(0x25,0x21) ;
	}
	/**
	 * This command is generated to inquire about the Node Descriptor information
	 *  of the destination device.
	 */
	public BindSREQ(int dst_addr,String src_ieee_addr,int src_ep,int clusterid,
			AddrMode dst_addr_mode,int bind_dst_addr16)
	{
		super(0x25,0x21) ;
		
		if(dst_addr_mode!=AddrMode.addr_16bit && AddrMode.group_addr!=dst_addr_mode)
			throw new IllegalArgumentException("addr mode must group or bit16") ;
		
		data = new byte[16] ;
		DataUtil.setUInt16(dst_addr, data, 0); // 0-1
		DataUtil.transStr2IEEEAddr(src_ieee_addr, data, 2) ; //2-9
		data[10] = (byte)src_ep ;
		DataUtil.setUInt16(clusterid, data, 11);  // 11-12
		data[13] = (byte)dst_addr_mode.getValInt() ;
		DataUtil.setUInt16(bind_dst_addr16, data, 14); // 14-15
	}
	
	public BindSREQ(int dst_addr,String src_ieee_addr,int src_ep,int clusterid,String ieee_addr,int dst_ep)
	{
		super(0x25,0x21) ;

		data = new byte[23] ;
		DataUtil.setUInt16(dst_addr, data, 0); // 0-1
		DataUtil.transStr2IEEEAddr(src_ieee_addr, data, 2) ; //2-9
		data[10] = (byte)src_ep ;
		DataUtil.setUInt16(clusterid, data, 11);  // 11-12
		data[13] = (byte)AddrMode.addr_64bit.getValInt() ;
		DataUtil.transStr2IEEEAddr(ieee_addr, data, 14); // 14-21
		data[22] = (byte)dst_ep ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_BIND_REQ";
	}
	@Override
	public int getDataLenMin()
	{
		return 14;
	}
	
	public int getDstAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ; //0-1
	}
	
	public String getSrcIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.data, 2) ; //2-9
	}
	
	public int getSrcEndpoint()
	{
		return this.data[10] & 0xFF ;
	}
	
	public int getClusterId()
	{
		return DataUtil.getUInt16(this.data, 11) ; //11-12
	}
	
	public AddrMode getDstAddrMode()
	{
		return AddrMode.valOfInt(this.data[13] & 0xFF) ;
	}
	
	public Object getBindDstAddr()
	{
		AddrMode am = this.getDstAddrMode() ;
		if(am==AddrMode.addr_64bit)
			return DataUtil.transIEEEAddr(this.data, 14) ; // 14-21
		return DataUtil.getUInt16(this.data, 14) ; //14-15
	}
	
	public Integer getDstEndpoint()
	{
		AddrMode am = this.getDstAddrMode() ;
		if(am==AddrMode.addr_64bit)
			return this.data[22] & 0xFF ;
		return null ; //
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		ret += " dst_addr="+DataUtil.transHex(getDstAddr())+" src_ieee="+getSrcIEEEAddr()+" src_ep="+this.getSrcEndpoint()
			+" clusterid="+this.getClusterId()+" dst_addr_mode="+this.getDstAddrMode()+" dst_addr="+this.getBindDstAddr();
		Integer dst_ep = this.getDstEndpoint() ;
		if(dst_ep!=null)
			ret += " dst_ep="+dst_ep ;
		return ret ;
	}
}
