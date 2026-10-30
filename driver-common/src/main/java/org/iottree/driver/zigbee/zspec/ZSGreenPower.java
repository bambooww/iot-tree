package org.iottree.driver.zigbee.zspec;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ZSGreenPower
{
	public static enum ZigbeeNWKGPAppId
	{
	    Default(0x00),Lped(0x01),Zgp(0x02);
		
	    public final int val;
	    ZigbeeNWKGPAppId(int val){this.val =val;}
	    public static ZigbeeNWKGPAppId valOfInt(int v)
		{
			for (ZigbeeNWKGPAppId st : ZigbeeNWKGPAppId.values())
				if (st.val == v)
					return st;
			return null;
		}
	}

	public static enum ZigbeeNWKGPSecurityLevel {
	    /** No Security  */
	    No(0x00),
	    /** Reserved?  */
	    OneLsb(0x01),
	    /** 4 Byte Frame Counter and 4 Byte MIC */
	    Full(0x02),
	    /** 4 Byte Frame Counter and 4 Byte MIC with encryption */
	    FullEncr(0x03);
		
	    public final int val;
	    ZigbeeNWKGPSecurityLevel(int val){this.val =val;}
	    public static ZigbeeNWKGPSecurityLevel valOfInt(int v)
		{
			for (ZigbeeNWKGPSecurityLevel st : ZigbeeNWKGPSecurityLevel.values())
				if (st.val == v)
					return st;
			return null;
		}
	}

	public static  enum ZigbeeNWKGPSecurityKeyType {
	    NoKey(0x00),
	    ZbNwkKey(0x01),
	    GpdGroupKey(0x02),
	    NwkKeyDerivedGpdKeyGroupKey(0x03),
	    PreconfiguredIndividualGpdKey(0x04),
	    DerivedIndividualGpdKey(0x07);
	    
	    public final int val;
	    ZigbeeNWKGPSecurityKeyType(int val){this.val =val;}
	    public static ZigbeeNWKGPSecurityKeyType valOfInt(int v)
		{
			for (ZigbeeNWKGPSecurityKeyType st : ZigbeeNWKGPSecurityKeyType.values())
				if (st.val == v)
					return st;
			return null;
		}
	}

	public static   enum GPCommunicationMode {
			FullUnicast(0),
	    GroupcastToDgroupId(1),
	    GroupcastToPrecommissionedGroupId(2),
	    LightweightUnicast(3);
	    
	    public final int val;
	    GPCommunicationMode(int val){this.val =val;}
	    public static GPCommunicationMode valOfInt(int v)
		{
			for (GPCommunicationMode st : GPCommunicationMode.values())
				if (st.val == v)
					return st;
			return null;
		}
	}

	public static class PairingOptions
	{
		public ZigbeeNWKGPAppId appId ;
		public boolean addSink;
		public boolean removeGpd;
		public GPCommunicationMode communicationMode;
	    public boolean gpdFixed;
	    public boolean gpdMacSeqNumCapabilities;
	    public ZigbeeNWKGPSecurityLevel securityLevel ;
	    public ZigbeeNWKGPSecurityKeyType securityKeyType ;
	    public boolean gpdSecurityFrameCounterPresent;
	    public boolean gpdSecurityKeyPresent;
	    public boolean assignedAliasPresent;
	    public boolean groupcastRadiusPresent;
	}

	public static class CommissioningModeOptions 
	{
		public int action;
	    public boolean  commissioningWindowPresent ;
	    /** Bits: 0: On first Pairing success | 1: On GP Proxy Commissioning Mode (exit) */
	    public int exitMode;
	    /** should always be always false in current spec (1.1.2) */
	    public boolean channelPresent;
	    public boolean unicastCommunication;
	};

	/** @see Zcl.Clusters.greenPower.commandsResponse.pairing */
	public static class PairingPayload
	{
	    public int options ;//: number;
	    public int srcID ; //?: number;
	    public String gpdIEEEAddr ; //?: string;
	    public int gpdEndpoint ; //?: number;
	    public String sinkIEEEAddr ; //?: string;
	    public int sinkNwkAddr ; //?: number;
	    public int sinkGroupID ; //?: number;
	    public int deviceID ; //?: number;
	    public int frameCounter ; //?: number;
	    public String gpdKey ; //?: Buffer;
	    public int assignedAlias ; //?: number;
	    public int groupcastRadius ; //?: number;
	};

//	/** @see Zcl.Clusters.greenPower.commandsResponse.response */
//	public static class  ResponsePayload<T extends GpdCommissioningReply | GpdChannelConfiguration> = {
//	    options: number;
//	    tempMaster: number;
//	    tempMasterTx: number;
//	    srcID?: number;
//	    gpdIEEEAddr?: string;
//	    gpdEndpoint?: number;
//	    gpdCmd: number;
//	    gpdPayload: T;
//	};

//	interface GreenPowerEventMap {
//		GreenPowerDeviceJoinedPayload[] deviceJoined: [payload: ];
//	    deviceLeave: [sourceID: number];
//	}
	
	private ZSCoor coor;

	ZSGreenPower(ZSCoor coor)
	{
		this.coor = coor;
	}

	public static String sourceIdToIeeeAddress(long sourceId)
	{
		return String.format("0x%016x", sourceId);
	}

	/**
	 * encryptSecurityKey 的 Java 实现 nonce 布局: sourceID(4) + sourceID(4) +
	 * sourceID(4) + 0x05(1)
	 */
	public byte[] encryptSecurityKey(long sourceID, byte[] securityKey) throws Exception
	{
		ByteBuffer nonceBuf = ByteBuffer.allocate(13).order(ByteOrder.LITTLE_ENDIAN);
		nonceBuf.putInt((int) (sourceID & 0xFFFFFFFFL));
		nonceBuf.putInt((int) (sourceID & 0xFFFFFFFFL));
		nonceBuf.putInt((int) (sourceID & 0xFFFFFFFFL));
		nonceBuf.put((byte) 0x05);
		byte[] nonce = nonceBuf.array();

		Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding", "BC");
		SecretKeySpec keySpec = new SecretKeySpec(ZSpecConsts.INTEROPERABILITY_LINK_KEY, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(128, nonce));
		return cipher.doFinal(securityKey);
	}

	/**
	 * decryptPayload 的 Java 实现 nonce 布局: sourceID(4) + sourceID(4) +
	 * frameCounter(4) + 0x05(1) M=4 (4字节 authTag)
	 */
	public byte[] decryptPayload(long sourceID, long frameCounter, byte[] securityKey, byte[] payload) throws Exception
	{
		ByteBuffer nonceBuf = ByteBuffer.allocate(13).order(ByteOrder.LITTLE_ENDIAN);
		nonceBuf.putInt((int) (sourceID & 0xFFFFFFFFL));
		nonceBuf.putInt((int) (sourceID & 0xFFFFFFFFL));
		nonceBuf.putInt((int) frameCounter);
		nonceBuf.put((byte) 0x05);
		byte[] nonce = nonceBuf.array();

		Cipher cipher = Cipher.getInstance("AES/CCM/NoPadding", "BC");
		SecretKeySpec keySpec = new SecretKeySpec(securityKey, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(32, nonce));

		return cipher.doFinal(payload);
	}
	

    public static int encodePairingOptions(PairingOptions options)
    {
    	int ret = options.appId.val & 0x7 |
    			(((options.addSink ? 1 : 0) << 3) & 0x8) |
                (((options.removeGpd ? 1 : 0) << 4) & 0x10) |
                ((options.communicationMode.val << 5) & 0x60) |
                (((options.gpdFixed ? 1 : 0) << 7) & 0x80) |
                (((options.gpdMacSeqNumCapabilities ? 1 : 0) << 8) & 0x100) |
                ((options.securityLevel.val << 9) & 0x600) |
                ((options.securityKeyType.val << 11) & 0x3800) |
                (((options.gpdSecurityFrameCounterPresent ? 1 : 0) << 14) & 0x4000) |
                (((options.gpdSecurityKeyPresent ? 1 : 0) << 15) & 0x8000) |
                (((options.assignedAliasPresent ? 1 : 0) << 16) & 0x10000) |
                (((options.groupcastRadiusPresent ? 1 : 0) << 17) & 0x20000) ;
                // bits 18..23 reserved ;
        return ret;
    }
    
    public static PairingOptions decodePairingOptions(int opt)
    {
    	PairingOptions ret = new PairingOptions() ;
        
		ret.appId = ZigbeeNWKGPAppId.valOfInt(opt & 0x7);
		ret.addSink = ((opt & 0x8) >> 3) > 0;
		ret.removeGpd = ((opt & 0x10) >> 4) > 0;
		ret.communicationMode = GPCommunicationMode.valOfInt((opt & 0x60) >> 5);
		ret.gpdFixed = ((opt & 0x80) >> 7) > 0;
		ret.gpdMacSeqNumCapabilities = ((opt & 0x100) >> 8) > 0;
		ret.securityLevel = ZigbeeNWKGPSecurityLevel.valOfInt((opt & 0x600) >> 9);
		ret.securityKeyType = ZigbeeNWKGPSecurityKeyType.valOfInt((opt & 0x3800) >> 11);
		ret.gpdSecurityFrameCounterPresent = ((opt & 0x4000) >> 14) > 0;
		ret.gpdSecurityKeyPresent = ((opt & 0x8000) >> 15) > 0;
		ret.assignedAliasPresent = ((opt & 0x10000) >> 16) > 0;
		ret.groupcastRadiusPresent = ((opt & 0x20000) >> 17) > 0;
		// bits 18..23 reserved
        return ret ;
    }
    
    
    /** see 14-0563-19 A.3.3.5.2 */
//    private async sendPairingCommand(
//        options: PairingOptions,
//        payload: PairingPayload,
//        gppNwkAddr: number | undefined,
//    ): Promise<AdapterEvents.ZclPayload | undefined> {
//        payload.options = GreenPower.encodePairingOptions(options);
//        logger.debug(
//            `[PAIRING] srcID=${payload.srcID} gpp=${gppNwkAddr ?? "NO"} options=${payload.options} (addSink=${options.addSink} commMode=${options.communicationMode})`,
//            NS,
//        );
//
//        // Set sink address based on communication mode
//        switch (options.communicationMode) {
//            case GPCommunicationMode.GroupcastToPrecommissionedGroupId:
//            case GPCommunicationMode.GroupcastToDgroupId: {
//                payload.sinkGroupID = GP_GROUP_ID;
//                break;
//            }
//            /* v8 ignore next */
//            case GPCommunicationMode.FullUnicast:
//            case GPCommunicationMode.LightweightUnicast: {
//                payload.sinkIEEEAddr = await this.adapter.getCoordinatorIEEE();
//                payload.sinkNwkAddr = COORDINATOR_ADDRESS;
//                break;
//            }
//        }
//
//        const replyFrame = Zcl.Frame.create(
//            Zcl.FrameType.SPECIFIC,
//            Zcl.Direction.SERVER_TO_CLIENT,
//            true,
//            undefined,
//            zclTransactionSequenceNumber.next(),
//            "pairing",
//            "greenPower",
//            payload,
//            {},
//        );
//
//        if (options.communicationMode !== GPCommunicationMode.LightweightUnicast) {
//            await this.adapter.sendZclFrameToAll(GP_ENDPOINT, replyFrame, GP_ENDPOINT, BroadcastAddress.RX_ON_WHEN_IDLE);
//            return;
//        }
//
//        const device = Device.byNetworkAddress(gppNwkAddr ?? /* v8 ignore next */ COORDINATOR_ADDRESS);
//        assert(device, "Failed to find green power proxy device");
//
//        return await this.adapter.sendZclFrameToEndpoint(
//            device.ieeeAddr,
//            device.networkAddress,
//            GP_ENDPOINT,
//            replyFrame,
//            10000,
//            false,
//            false,
//            GP_ENDPOINT,
//        );
//    }
}
