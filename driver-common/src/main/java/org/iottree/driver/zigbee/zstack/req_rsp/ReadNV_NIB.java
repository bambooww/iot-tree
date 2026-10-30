package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.util.NVItemIDS;
import org.json.JSONObject;

/**
 typedef struct {
    byte SequenceNum;
    byte PassiveAckTimeout;
    byte MaxBroadcastRetries;
    byte MaxChildren;
    byte MaxDepth;
    byte MaxRouters;
    byte dummyNeighborTable;    // to make everything a byte!!
    byte BroadcastDeliveryTime;
    byte ReportConstantCost;
    byte RouteDiscRetries;
    byte dummyRoutingTable;     // to make everything a byte!!
    byte SecureAllFrames;
    byte SecurityLevel;
#if defined ( COMPATIBILITY_221 )
    byte nwkAllFresh;           // Obsolete - do not use
#endif
    byte SymLink;
    byte CapabilityFlags;
    uint16 TransactionPersistenceTime;
    byte nwkProtocolVersion;
    byte RouteDiscoveryTime;
    byte RouteExpiryTime;
    // non-settable
    uint16 nwkDevAddress;
    byte nwkLogicalChannel;
    uint16 nwkCoordAddress;
    byte nwkCoordExtAddress[Z_EXTADDR_LEN];  // 8 bytes
    uint16 nwkPanId;
    // Other global items - non-settable
    nwk_states_t nwkState;
    uint32 channelList;
    byte beaconOrder;
    byte superFrameOrder;
    byte scanDuration;
    byte battLifeExt;
    uint32 allocatedRouterAddresses;
    uint32 allocatedEndDeviceAddresses;
    byte nodeDepth;
    // Version 1
    byte updateID;
    byte nwkLinkStatusPeriod;
    byte nwkRouterAgeLimit;
    // ... 后续还有更多字段
    byte extendedPANID[Z_EXTADDR_LEN];  // 8 bytes, 可通过 osal_offsetof(nwkIB_t, extendedPANID) 定位
    // ... 可能还有更多字段直到总大小116字节
} nwkIB_t;
 
 * @author jason.zhu
 *
 */
public class ReadNV_NIB extends ReadNV
{
	public ReadNV_NIB()
	{
		super(NVItemIDS.ZCD_NV_NIB,0);
	}

	public int getKeySeqNum()
	{
		if(respNVReadExt==null)
			return -1 ;
		byte[] bs = respNVReadExt.getNVValue() ;
		if(bs.length<1)
			return -1 ;
		return bs[0] & 0xFF ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

}
