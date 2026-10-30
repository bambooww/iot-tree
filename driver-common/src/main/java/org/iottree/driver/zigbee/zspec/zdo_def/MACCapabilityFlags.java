package org.iottree.driver.zigbee.zspec.zdo_def;

/**
 * Bits:
 * - [alternatePANCoordinator: 1]
 * - [deviceType: 1]
 * - [powerSource: 1]
 * - [rxOnWhenIdle: 1]
 * - [reserved1: 1]
 * - [reserved2: 1]
 * - [securityCapability: 1]
 * - [securityCapability: 1]
 */
public class MACCapabilityFlags
{
	/**
     * The alternate PAN coordinator sub-field is one bit in length and shall be set to 1 if this node is capable of becoming a PAN coordinator.
     * Otherwise, the alternative PAN coordinator sub-field shall be set to 0.
     */
    public int alternatePANCoordinator;
    /**
     * The device type sub-field is one bit in length and shall be set to 1 if this node is a full function device (FFD).
     * Otherwise, the device type sub-field shall be set to 0, indicating a reduced function device (RFD).
     */
    public int  deviceType;
    /**
     * The power source sub-field is one bit in length and shall be set to 1 if the current power source is mains power.
     * Otherwise, the power source sub-field shall be set to 0.
     * This information is derived from the node current power source field of the node power descriptor.
     */
    public int powerSource;
    /**
     * The receiver on when idle sub-field is one bit in length and shall be set to 1 if the device does not disable its receiver to
     * conserve power during idle periods.
     * Otherwise, the receiver on when idle sub-field shall be set to 0 (see also section 2.3.2.4.)
     */
    public int rxOnWhenIdle;
    public int reserved1;
    public int reserved2;
    /**
     * The security capability sub-field is one bit in length and shall be set to 1 if the device is capable of sending and receiving
     * frames secured using the security suite specified in [B1].
     * Otherwise, the security capability sub-field shall be set to 0.
     */
    public int securityCapability;
    /** The allocate address sub-field is one bit in length and shall be set to 0 or 1. */
    public int  allocateAddress;
    
    /**
     * Get the values for the bitmap `Mac Capability Flags Field` as per spec.
     * Given value is assumed to be a proper 1-byte length.
     * @param capabilities
     * @returns
     */
    public static MACCapabilityFlags getMacCapFlags(int capabilities)
    {
    	MACCapabilityFlags r = new MACCapabilityFlags() ;
    	r.alternatePANCoordinator= capabilities & 0x01;
    	r.deviceType= (capabilities & 0x02) >> 1;
    	r.powerSource= (capabilities & 0x04) >> 2;
    	r.rxOnWhenIdle=(capabilities & 0x08) >> 3;
    	r.reserved1= (capabilities & 0x10) >> 4;
    	r.reserved2= (capabilities & 0x20) >> 5;
    	r.securityCapability=(capabilities & 0x40) >> 6;
    	r.allocateAddress= (capabilities & 0x80) >> 7;
        return r ;
    };
}
