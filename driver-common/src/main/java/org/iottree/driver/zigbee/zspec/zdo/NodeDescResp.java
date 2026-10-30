package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.zdo_def.MACCapabilityFlags;
import org.iottree.driver.zigbee.zspec.zdo_def.ServerMask;

public class NodeDescResp extends ZDOResp
{

	/** NWK address for the request. */
    public int nwkAddr;
    /** 000 == Zigbee Coordinator, 001 == Zigbee Router,  010 === Zigbee End Device, 011-111 === Reserved */
    public int logicalType ; //: number;
    /** R23 and above (determined via other means if not). Indicates whether the device supports fragmentation at the APS layer. */
    public boolean fragmentationSupported ; //: boolean | undefined;
    /** Specifies the application support sub-layer capabilities of the node. Currently not supported, should be zero */
    public int apsFlags ; //: number;
    /**
     * Specifies the frequency bands that are supported by the underlying IEEE Std 802.15.4 radio(s) utilized by the node.
     * Bits:
     * - 0 = 868 – 868.6 MHz
     * - 1 = Reserved
     * - 2 = 902 – 928 MHz
     * - 3 = 2400 – 2483.5 MHz
     * - 4 = GB Smart Energy sub-GHz bands: (863-876MHz and 915-921MHz)
     */
    public int frequencyBand ; //: number;
    /** Specifies the node capabilities. */
    public MACCapabilityFlags capabilities;
    /** Specifies a manufacturer code that is allocated by the Connectivity Standards Alliance, relating the manufacturer to the device. */
    public int manufacturerCode;
    /**
     * Specifies the maximum size, in octets, of the network sub-layer data unit (NSDU) for this node.
     * This is the maximum size of data or commands passed to or from the application by the application support sub-layer,
     * before any fragmentation or re-assembly.
     * This field can be used as a high-level indication for network management.
     * Valid range of 0x00-0x7f.
     */
    public int maxBufSize;
    /**
     * Indicates the device's apsMaxSizeASDU AIB value.
     * Specifies the maximum size, in octets, of the application sub-layer data unit (ASDU) that can be transferred to this node
     * in one single message transfer.
     * This value can exceed the value of the node maximum buffer size field (see section 2.3.2.3.9) through the use of fragmentation.
     * Valid range of 0x0000-0x7fff.
     */
    public int maxIncTxSize;
    /** The system server capabilities of this node */
    public ServerMask serverMask ;
    /**
     * Specifies the maximum size, in octets, of the application sub-layer data unit (ASDU) that can be transferred
     * from this node in one single message transfer.
     * This value can exceed the value of the node 2777 maximum buffer size field (see section 2.3.2.3.9) through the use of fragmentation.
     * Valid range of 0x0000-0x7fff.
     */
    public int maxOutTxSize ;
    public int deprecated1;

	public NodeDescResp()
	{
	}

	@Override
	public ZDOStatus getStatus()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ZDOClusterId getZDOClusterId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		ZDOStatus status = ZDOStatus.valOfInt(buf.readUInt8());
        if (status == ZDOStatus.SUCCESS) {
            this.nwkAddr = buf.readUInt16();
            // in bits: [logical type: 3] [deprecated: 1] [deprecated: 1] [fragmentation supported (R23): 1] [reserved/unused: 2]
            int nodeDescByte1 = buf.readUInt8() ;
            this.logicalType=nodeDescByte1 & 0x07;
            
            this.serverMask = ServerMask.getServerMask(buf.readUInt16());
            
            // in bits: [aps flags: 3] [frequency band: 5]
            int nodeDescByte2 = buf.readUInt8();
            this.fragmentationSupported=this.serverMask.stackComplianceRevision >= 23 ? (nodeDescByte1 & 0x20) >> 5 == 1 : false;//undefined,
            this.capabilities = MACCapabilityFlags.getMacCapFlags(buf.readUInt8());
            this.manufacturerCode = buf.readUInt16();
            this.maxBufSize = buf.readUInt8();
            this.maxIncTxSize = buf.readUInt16();
            
            this.maxOutTxSize = buf.readUInt16();
            this.deprecated1 = buf.readUInt8();
            // Global: FragmentationParametersGlobalTLV
            // TODO ret. tlvs: Tlv[] = this.readTLVs();

//            result = {
//                nwkAddress,
//                
//                
//                ret.apsFlags: nodeDescByte2 & 0x07,
//                frequencyBand: (nodeDescByte2 & 0xf8) >> 3,
//                capabilities: macCapFlags,
//                manufacturerCode,
//                maxBufSize,
//                maxIncTxSize,
//                serverMask,
//                maxOutTxSize,
//                deprecated1,
//                tlvs,
//            };
        }
	}

}
