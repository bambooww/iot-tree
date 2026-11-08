package org.iottree.driver.zigbee.zspec.zdo;


import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.util.PowerMode;


public class PowerDescResp  extends ZDOResp
{
	public ZDOStatus status ;
	
	public int nwk_addr ;
	
	public int byte1 ;
	
	public int byte2 ;
	

	public PowerDescResp()
	{
	}

	@Override
	public ZDOStatus getStatus()
	{
		return status;
	}
	
	/**
     * - 0000 == receiver sync'ed with receiver on when idle subfield of the node descriptor
     * - 0001 == receiver comes on periodically as defined by the node power descriptor
     * - 0010 == receiver comes on when stimulated, for example, by a user pressing a button
     * - 0011-1111 reserved
     */
	public PowerMode getCurPowerMode()
	{
		return PowerMode.valOfInt(byte1 & 0x0F) ;
	}
	
	/**
     * Bits:
     * - 0 == constants (mains) power
     * - 1 == rechargeable battery
     * - 2 == disposable battery
     * - 3 == reserved
     */
	public int getAvailPowerSrc()
	{
		return (byte1>>4) & 0x0F ;
	}
	
	/**
     * Bits:
     * - 0 == constants (mains) power
     * - 1 == rechargeable battery
     * - 2 == disposable battery
     * - 3 == reserved
     */
	public int getCurPowerSrc()
	{
		return byte2 & 0x0F ;
	}
	
	/**
     * - 0000 == critical
     * - 0100 == 33%
     * - 1000 == 66%
     * - 1100 == 100%
     */
	public int getCurPowerSrcLvl()
	{
		return (byte2>>4) & 0x0F ;
	}

	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.POWER_DESCRIPTOR_RESPONSE;
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
            this.byte1 = buf.readUInt8();
            this.byte2 = buf.readUInt8();
        }
	}

}
