package org.iottree.driver.zigbee.zspec.zdo_def;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOConsts;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;

public interface TlvLocal extends Tlv
{
	//-- Local TLVs

	/**
	 * NOTE: The Maximum Transmission Unit (MTU) of the underlying message will limit the maximum range of this field.
	 */
	public static class ClearAllBindingsReqEUI64TLV implements TlvLocal
	{
	    /** A list of EUI64 that SHALL trigger corresponding bindings to be deleted. */
		public List<String> eui64List ;//: Eui64[];
	};

	public static class BeaconSurveyConfigurationTLV implements TlvLocal
	{
	    /**
	     * The list of channels and pages over which the scan is to be done.
	     * For more information on the Channel List structure see section 3.2.2.2.1.
	     */
		public List<Integer> scanChannelList;
	    /**
	     * - 0 Active or Enhanced Scan This bit determines whether to do an Active Scan or Enhanced Active Scan.
	     *   When the bit is set to 1 it indicates an Enhanced Active Scan.
	     *   And in case of Enhanced Active scan EBR shall be sent with EPID filter instead of PJOIN filter.
	     * - 1 – 7 Reserved -
	     */
		public int configurationBitmask;
	};

	public static class Curve25519PublicPointTLV implements TlvLocal
	{
	    /** This indicates the EUI64 of the device that generated the public point. */
	    public String eui64; //: Eui64;
	    /** The 32-byte Curve public point. */
	    public byte[] publicPoint; //: Buffer;
	};

	public static class AuthenticationTokenIdTLV implements TlvLocal
	{
	    /** The Global TLV Type Tag ID being requested for an authentication token. */
		public  int tlvTypeTagId ;//: number;
	};

	public static class TargetIEEEAddressTLV implements TlvLocal
	{
	    /** Extended address of the device whose security level is requested. */
		public  String ieee ; //: Eui64;
	};

	public static class SelectedKeyNegotiationMethodTLV implements TlvLocal
	{
	    /**
	     * The enumeration of the key negotiation method the sender is requesting to use in key negotiation.
	     */
		public SelectedKeyNegotiationProtocol protocol;
	    /**
	     * The enumeration indicating the pre-shared secret that the sending device is requesting to be used in the key negotiation.
	     */
		public SelectedPreSharedSecret presharedSecret;
	    /** The value of the EUI64 of the device sending the message. This field SHALL always be present. */
		public String sendingDeviceEui64 ; //: Eui64;
	};

	public static class DeviceEUI64ListTLV implements TlvLocal
	{
	    /** A list of EUI64 that shall trigger decommissioning operations. Count: [0x00-0xFF] */
	    public List<String> eui64List ; //: Eui64[];
	};

	public static class APSFrameCounterChallengeTLV implements TlvLocal
	{
	    /** The EUI64 of the device that generated the frame. */
	    public String senderEui64 ; //: Eui64;
	    /**
	     * A randomly generated 64-bit value sent to a device to prove they have the link key.
	     * This allows the initiator to detect replayed challenge response frames.
	     */
	    public byte[] challengeValue ; //: Buffer;
	};

	public static class APSFrameCounterResponseTLV implements TlvLocal
	{
	    /** The EUI64 of the device that is responding to the Security_Challenge_req with its own challenge. */
		public String responderEui64 ; //: Eui64;
	    /** A randomly generated 64-bit value previously received in the APSFrameCounterChallengeTLV. */
		public byte[] receivedChallengeValue ; //: Buffer;
	    /** The current outgoing APS security frame counter held by the Responder EUI64 device. */
		public long apsFrameCounter ; //: number;
	    /**
	     * The AES-CCM-128 outgoing frame counter used to generate the MIC over the octet sequence
	     * { tag || length || responder EUI-64 || received challenge value || APS frame counter }
	     * using the special nonce and AES-128 key for frame counter synchronization.
	     */
		public long challengeSecurityFrameCounter; //: number;
	    /**
	     * The AES-128-CCM 64-bit MIC (security level 2) on all previous fields of this TLV,
	     * excluding the challenge security frame counter, including Tag ID and length fields.
	     */
		public byte[] mic ; //: Buffer;
	};

	public static class BeaconSurveyResultsTLV implements TlvLocal
	{
	    /** The total number of IEEE Std 802.15.4 beacons received during the scan. */
		public int totalBeaconsReceived;
	    /** The total number of Zigbee Network beacons where the Extended PAN ID matches the local device’s nwkExtendedPanId. */
    	public int onNetworkBeacons;
    	    /**
    	     * The total number of Zigbee Network beacons where the Extended PAN ID matches and the Zigbee Beacon payload indicates
    	     * End Device Capacity = TRUE.
    	     */
    	public int potentialParentBeacons;
    	    /**
    	     * The total number of IEEE Std 802.15.4 beacons from other Zigbee networks or other IEEE Std 802.15.4 networks.
    	     * Other Zigbee network beacons are defined as when the Extended PAN ID does not match the local Extended PAN ID.
    	     */
    	public int otherNetworkBeacons;
	};

	public static class PotentialParentsTLV implements TlvLocal
	{
	    /** The short address that is the current parent for the device. For a router or coordinator this value SHALL be set to 0xFFFF. */
		public int currentParentNwkAddress;//: number;
	    /** The value of the LQA of the current parent. */
	public int currentParentLQA; //: number;
	    /**
	     * This is the count of additional potential parent short addresses and their associated LQA.
	     * If there are no other potential parents this SHALL indicate 0. This value SHALL not be greater than 5.
	     */
	public int entryCount ; //: number;
	
	 public List<PotentialParent>   potentialParents;
	};

	public static class DeviceAuthenticationLevelTLV implements TlvLocal
	{
	    /** 64-bit address for the node that is being inquired about. */
		public String remoteNodeIeee ; //: Eui64;
	    /** This indicates the joining method that was used when the device joined the network. */
		public InitialJoinMethod initialJoinMethod;
	    /** This indicates what Link Key update method was used to create the current active Link Key. */
		public ActiveLinkKeyType activeLinkKeyType;
	};

	public static class ProcessingStatusTLV implements TlvLocal
	{
	    /**
	     * indicate the number of Tag ID and Processing Status pairs are present in the full TLV.
	     * The count may be zero, indicating that there were no known TLVs in the previous message that could be processed.
	     */
	   public int count;
	   
	   public List<TagProcessStatus> tlvs ;
//	    tlvs: {
//	        /** Indicate a previously received TLV tag ID */
//	        tagId: number;
//	        /** The associated status of whether it is processed */
//	        processingStatus: Status.SUCCESS | Status.INV_REQUESTTYPE | Status.NOT_SUPPORTED;
//	    }[];
	};
	
	
	public static BeaconSurveyResultsTLV readBeaconSurveyResultsTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readBeaconSurveyResultsTLV with length=${length}`, NS);
        if (length != 4) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected 4.");
        }
        BeaconSurveyResultsTLV r = new BeaconSurveyResultsTLV() ;
        r.totalBeaconsReceived = buf.readUInt8();
        r.onNetworkBeacons = buf.readUInt8();
        r.potentialParentBeacons = buf.readUInt8();
        r.otherNetworkBeacons = buf.readUInt8();

        return r;
    }
	
	public static PotentialParentsTLV readPotentialParentsTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readPotentialParentsTLV with length=${length}`, NS);
		if (length != 4) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected at least 4.");
        }
		PotentialParentsTLV r = new PotentialParentsTLV() ;
        r.currentParentNwkAddress = buf.readUInt16();
        r.currentParentLQA = buf.readUInt8();
        // [0x00 - 0x05]
        r.entryCount = buf.readUInt8();

        if (length != 4 + r.entryCount * 3) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected "+(4 + r.entryCount * 3));
        }

        ArrayList<PotentialParent> potentialParents = new ArrayList<>();
        r.potentialParents = potentialParents ;
        
        for (int i = 0; i < r.entryCount; i ++) {
        	PotentialParent pp = new PotentialParent() ;
            pp.nwkAddress = buf.readUInt16();
            pp.lqa = buf.readUInt8();
            potentialParents.add(pp);
        }

        return r; 
    }
	
	public static Curve25519PublicPointTLV readCurve25519PublicPointTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readCurve25519PublicPointTLV with length=${length}`, NS);
        if (length != ZSpecConsts.EUI64_SIZE + ZDOConsts.CURVE_PUBLIC_POINT_SIZE) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected "+(ZSpecConsts.EUI64_SIZE + ZDOConsts.CURVE_PUBLIC_POINT_SIZE));
        }
        Curve25519PublicPointTLV r = new Curve25519PublicPointTLV() ;
        r.eui64 = buf.readIEEEAddr();
        r.publicPoint = buf.readBuf(ZDOConsts.CURVE_PUBLIC_POINT_SIZE);

        return r;
    }
	
	public static DeviceAuthenticationLevelTLV readDeviceAuthenticationLevelTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readDeviceAuthenticationLevelTLV with length=${length}`, NS);
        if (length != 10) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '${length}', expected 10.");
        }

        DeviceAuthenticationLevelTLV r = new DeviceAuthenticationLevelTLV() ;
        r. remoteNodeIeee = buf.readIEEEAddr();
        r. initialJoinMethod = InitialJoinMethod.valOfInt(buf.readUInt8());
        r. activeLinkKeyType = ActiveLinkKeyType.valOfInt(buf.readUInt8());

        return r;
    }
	
	public static ProcessingStatusTLV readProcessingStatusTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readProcessingStatusTLV with length=${length}`, NS);
        int count = buf.readUInt8();

        if (length != 1 + count * 2) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected "+(1 + count * 2));
        }
        ProcessingStatusTLV r = new ProcessingStatusTLV() ;
        r.count = count ;
        r.tlvs = new ArrayList<>() ;

        for (int i = 0; i < count; i++) {
        	TagProcessStatus tps = new TagProcessStatus() ;
            tps.tagId = buf.readUInt8();
            tps.processingStatus = buf.readUInt8();
            r.tlvs.add(tps);
        }
        return r;
    }
	
	public static  APSFrameCounterResponseTLV readAPSFrameCounterResponseTLV(ZDOBuffer buf,int length)
	{
        //logger.debug(`readAPSFrameCounterResponseTLV with length=${length}`, NS);
        if (length != 32) {
            throw new IllegalArgumentException("Malformed TLV. Invalid length '"+length+"', expected 32.");
        }

        APSFrameCounterResponseTLV r = new APSFrameCounterResponseTLV() ;
        r.responderEui64 = buf.readIEEEAddr();
        r.receivedChallengeValue = buf.readBuf(ZDOConsts.CHALLENGE_VALUE_SIZE);
        r.apsFrameCounter = buf.readUInt32();
        r.challengeSecurityFrameCounter = buf.readUInt32();
        r.mic = buf.readBuf(8);

        return r ;
    }
}
