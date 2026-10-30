package org.iottree.driver.zigbee.zspec.zdo_def;

/**
 * Bits:
 * - [primaryTrustCenter: 1]
 * - [backupTrustCenter: 1]
 * - [deprecated1: 1]
 * - [deprecated2: 1]
 * - [deprecated3: 1]
 * - [deprecated4: 1]
 * - [networkManager: 1]
 * - [reserved1: 1]
 * - [reserved2: 1]
 * - [stackComplianceRevision: 7]
 */
public class ServerMask
{
	public int primaryTrustCenter;
	public int backupTrustCenter;
	public int deprecated1;
	public int deprecated2;
	public int deprecated3;
	public int deprecated4;
	public int networkManager;
	public int reserved1;
	public int reserved2;
	/**
	 * Indicate the Revision of the Zigbee Pro Core specification that the running stack is implemented to.
	 * Prior to Revision 21 of the specification these bits were reserved and thus set to 0.
	 * A stack that is compliant to Revision 23 would set these bits to 23 (0010111b).
	 * A stack SHALL indicate the Revision of the specification it is compliant to by setting these bits.
	 */
	public int stackComplianceRevision;
	
	
	public int toMaskValue()
	{
	    return (
	        (this.primaryTrustCenter & 0x01) |
	        ((this.backupTrustCenter << 1) & 0x02) |
	        ((this.deprecated1 << 2) & 0x04) |
	        ((this.deprecated2 << 3) & 0x08) |
	        ((this.deprecated3 << 4) & 0x10) |
	        ((this.deprecated4 << 5) & 0x20) |
	        ((this.networkManager << 6) & 0x40) |
	        ((this.reserved1 << 7) & 0x80) |
	        ((this.reserved2 << 8) & 0x100) |
	        ((this.stackComplianceRevision << 9) & 0xfe00)
	    );
	};
	
	/**
	 * Get the values for the bitmap `Server Mask Field` as per spec.
	 * Given value is assumed to be a proper 2-byte length.
	 * @param serverMask
	 * @returns
	 */
	public static ServerMask getServerMask(int serverMask)
	{
		ServerMask sm = new ServerMask() ;
		sm.primaryTrustCenter=serverMask & 0x01;
		sm.backupTrustCenter=(serverMask & 0x02) >> 1;
		sm.deprecated1=(serverMask & 0x04) >> 2;
		sm.deprecated2=(serverMask & 0x08) >> 3;
		sm.deprecated3=(serverMask & 0x10) >> 4;
		sm.deprecated4=(serverMask & 0x20) >> 5;
		sm.networkManager=(serverMask & 0x40) >> 6;
		sm.reserved1=(serverMask & 0x80) >> 7;
		sm.reserved2=(serverMask & 0x100) >> 8;
		sm.stackComplianceRevision=(serverMask & 0xfe00) >> 9;
	    return sm;
	};
}
