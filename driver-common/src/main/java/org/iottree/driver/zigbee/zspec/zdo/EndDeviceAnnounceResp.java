package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.ZSDevice;
import org.iottree.driver.zigbee.zspec.util.EndDeviceCap;
import org.iottree.driver.zigbee.zspec.util.MacCapabilitiesFlags;
import org.iottree.driver.zigbee.zspec.util.ShortAddr;

public class EndDeviceAnnounceResp extends ZDOResp
{
	public int nwk_addr ;
	
	public String ieee_addr ;
	
	public EndDeviceCap cap ; // MacCapabilitiesFlags mac_cap ;

	public EndDeviceAnnounceResp()
	{
	}
	
	public EndDeviceAnnounceResp(int nwk_addr,String ieee,EndDeviceCap mac_cap)
	{
		this.nwk_addr = nwk_addr ;
		this.ieee_addr = ieee ;
		this.cap = mac_cap ;
	}

	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.END_DEVICE_ANNOUNCE;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		throw new RuntimeException("no impl") ;
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		ZDOStatus status = ZDOStatus.valOfInt(buf.readUInt8());
        if (status == ZDOStatus.SUCCESS) {
        	this.nwk_addr = buf.readUInt16();
            // Length in bytes of the Simple Descriptor to follow. [0x00-0xff]
            this.ieee_addr = buf.readIEEEAddr() ;
            this.cap = new EndDeviceCap(buf.readUInt8()); // MacCapabilitiesFlags(buf.readUInt8());
        }
	}
	

	public boolean MGR_checkChanged(ZSDevice d)
	{
		ShortAddr d_addr = d.getShortAddr() ;
		if(d_addr==null)
			return true;
		if(this.nwk_addr!=d_addr.getAddrVal())
			return true ;
		
		if(d.getDeviceCap()==null)
			return true ;
		if(this.cap.getCapVal()!=d.getDeviceCap().getCapVal())
			return true ;
		return false;
	}
	
	

	@Override
	public ZDOStatus getStatus()
	{
		return null;
	}
}
