package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.BindingTableEntry;
import org.iottree.driver.zigbee.ember.types.BindingType;
import org.iottree.driver.zigbee.ember.types.EmberGpAddr;
import org.iottree.driver.zigbee.ember.types.EmberGpAddrIEEE;
import org.iottree.driver.zigbee.ember.types.EmberGpAddrSrcID;
import org.iottree.driver.zigbee.ember.types.EmberGpAppId;
import org.iottree.driver.zigbee.ember.types.EmberInitSecState;
import org.iottree.driver.zigbee.ember.types.EmberJoinMethod;
import org.iottree.driver.zigbee.ember.types.EmberNodeType;
import org.iottree.driver.zigbee.ember.types.EmberZigbeeNetwork;
import org.iottree.driver.zigbee.ember.types.EmberZllAddrAssignment;
import org.iottree.driver.zigbee.ember.types.EmberZllDevInfoRecord;
import org.iottree.driver.zigbee.ember.types.EmberZllNetwork;
import org.iottree.driver.zigbee.ember.types.EmberZllSecAlgData;
import org.iottree.driver.zigbee.ember.types.EmberZllState;
import org.iottree.driver.zigbee.ember.types.NwkParams;
import org.iottree.driver.zigbee.ember.types.PerDeviceDutyCycle;
import org.iottree.driver.zigbee.ember.types.RxPacketInfo;
import org.iottree.driver.zigbee.ember.types.SecManAPSKeyMeta;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.iottree.driver.zigbee.ember.types.SecManDerivedKeyType;
import org.iottree.driver.zigbee.ember.types.SecManFlag;
import org.iottree.driver.zigbee.ember.types.SecManKeyType;
import org.iottree.driver.zigbee.ember.types.SecManNwkKeyInf;
import org.iottree.driver.zigbee.ember.util.SLStatus;

public class EZSPBuffer extends DataBuffer
{
	private EZSPBuffer() {}

	public EZSPBuffer(byte[] bs, int offset, int len)
	{
		super(bs, offset, len);
	}
	
	public static EZSPBuffer alloc(int len)
	{
		EZSPBuffer r = new EZSPBuffer() ;
		r.buf = new byte[len] ;
		return r ;
	}
	
	public ApsFrame readEmberApsFrame()
	{
		ApsFrame r = new ApsFrame() ;
		int rlen = r.unpack(buf, pos) ;
		pos += rlen ;
		return r ;
    }
	
	public RxPacketInfo readRxPacketInfo()
	{
		RxPacketInfo r = new RxPacketInfo() ;
		// 2 + 8 + 1
        r.senderShortAddr = this.readUInt16();
        r.senderIEEEAddr = this.readIEEEAddr();
        r.bindingIdx = this.readUInt8();
        r.addrIdx = this.readUInt8();
        r.lastHopLqi = this.readUInt8();
        r.lastHopRssi = this.readInt8(); // SDK: (int8_t)fetchInt8u();
        r.lastHopTimestamp = this.readUInt32();
        return r;
    }
	
	public SLStatus readStatus(int version)
    {
		return readStatus(version,true) ;
    }
	/**
     * EZSP switched to using SLStatus for command returns from version 14.
     * @param version EZSP protocol version in use
     * @param mapFromEmber If true, map from EmberStatus, otherwise map from EzspStatus
     * @returns EzspStatus, EmberStatus or SLStatus as SLStatus
     */
    public SLStatus readStatus(int version, boolean mapFromEmber)
    {
        if (version < 0x0e) {
           int status = this.readUInt8();

            // skip lookup if SUCCESS (always zero)
            if (status == SLStatus.OK.getIntVal()) {
                return SLStatus.OK;
            }

            if (mapFromEmber)
            {
            	SLStatus r = SLStatus.EMBER_TO_SL_STATUS_MAP.get(status);
    			if (r != null)
    				return r;
            }
            this.pos ++ ;
            // EzspStatus mapping to SLStatus is always same code
            return SLStatus.ZIGBEE_EZSP_ERROR;
        }

        int v = (int)this.readUInt32() ;
        this.pos += 4 ;
		SLStatus r = SLStatus.valOfInt(v);
		if (r != null)
		{
			return r ;
		}
		return SLStatus.ZIGBEE_EZSP_ERROR;
    }
    
    public EmberZigbeeNetwork readEmberZigbeeNetwork()
    {
    	EmberZigbeeNetwork r = new EmberZigbeeNetwork() ;
        r.channel = this.readUInt8();
        r.panId = this.readUInt16();
        r.extendedPanId = this.readExtendPanId();
        r.allowingJoin = this.readUInt8() != 0;
        r.stackProfile = this.readUInt8();
        r.nwkUpdateId = this.readUInt8();
        return r;
    }
    

	public byte[] readPayload()
	{
		int len = this.readUInt8() ;
		return this.readBuf(len) ;
	}
	
    
    public PerDeviceDutyCycle[] readPerDeviceDutyCycle()
    {
        int maxDevices = this.readUInt8();
        PerDeviceDutyCycle[]  arrayOfDeviceDutyCycles = new PerDeviceDutyCycle[maxDevices] ;

        for (int i = 0; i < maxDevices; i++) {
        	PerDeviceDutyCycle dc = new PerDeviceDutyCycle() ;
            dc. nodeId = this.readUInt16();
            dc. dutyCycleConsumed = this.readUInt16();
            arrayOfDeviceDutyCycles[i] = dc;
        }
        return arrayOfDeviceDutyCycles;
    }
    
    public BindingTableEntry readBindingTableEntry()
    {
    	BindingTableEntry r = new BindingTableEntry() ;
        r.type = BindingType.valOfInt(this.readUInt8());
        r.local = this.readUInt8();
        r.clusterId = this.readUInt16();
        r.remote = this.readUInt8();
        r.identifier = this.readIEEEAddr();
        r.networkIndex = this.readUInt8();

        return r;
    }
    
    public byte[] readEmberPublicKeyData() {
        byte[] contents = this.readBuf(EZSPConsts.EMBER_PUBLIC_KEY_SIZE);

        return contents;
    }
    
    public byte[] readEmberSmacData() {
        byte[] contents = this.readBufferCopy(EZSPConsts.EMBER_SMAC_SIZE);
        return contents;
    }
    
    //EmberPublicKey283k1Data
    public byte[] readEmberPublicKey283k1Data()  {
        byte[] contents = this.readBufferCopy(EZSPConsts.EMBER_PUBLIC_KEY_283K1_SIZE);

        return contents;
    }
    
    public EmberZllDevInfoRecord readEmberZllDevInfoRecord()
    {
    	EmberZllDevInfoRecord r = new EmberZllDevInfoRecord() ;
        r. ieeeAddress = this.readIEEEAddr() ;
        r. endpointId = this.readUInt8();
        r. profileId = this.readUInt16();
        r. deviceId = this.readUInt16();
        r. version = this.readUInt8();
        r. groupIdCount = this.readUInt8();

        return r;
    }
    
    public EmberZllAddrAssignment readEmberZllAddrAssignment() {
    	EmberZllAddrAssignment r = new EmberZllAddrAssignment() ;
        r. nodeId = this.readUInt16();
        r. freeNodeIdMin = this.readUInt16();
        r. freeNodeIdMax = this.readUInt16();
        r. groupIdMin = this.readUInt16();
        r. groupIdMax = this.readUInt16();
        r. freeGroupIdMin = this.readUInt16();
        r. freeGroupIdMax = this.readUInt16();

        return r;
    }
    
    public EmberZllNetwork readEmberZllNetwork()
    {
    	EmberZllNetwork r = new EmberZllNetwork() ;
        r. zigbeeNetwork = this.readEmberZigbeeNetwork();
        r.  securityAlgorithm = this.readEmberZllSecAlgData();
        r.  eui64 = this.readIEEEAddr();
        r.  nodeId = this.readUInt16();
        r.  state = EmberZllState.valOfInt(this.readUInt16());
        r.  nodeType = EmberNodeType.valOfInt(this.readUInt8());
        r.  numberSubDevices = this.readUInt8();
        r.  totalGroupIdentifiers = this.readUInt8();
        r.  rssiCorrection = this.readUInt8();

        return r;
    }
    
    public EmberZllSecAlgData readEmberZllSecAlgData()
    {
    	EmberZllSecAlgData r = new EmberZllSecAlgData() ;
        r. transactionId = this.readUInt32();
        r. responseId = this.readUInt32();
        r. bitmask = this.readUInt16();

        return r;
    }
    
    public EmberGpAddr readEmberGpAddress()
    {
        int applicationId = this.readUInt8();
        if (applicationId == EmberGpAppId.SOURCE_ID.getIntVal())
        {
        	EmberGpAddrSrcID r = new EmberGpAddrSrcID() ;
            r.sourceId = this.readUInt32();
            this.readUInt32(); // filler
            r.endpoint = this.readUInt8();
            return r;
        }
        if (applicationId == EmberGpAppId.IEEE_ADDRESS.getIntVal())
        {
        	EmberGpAddrIEEE r = new EmberGpAddrIEEE() ;
            r. gpdIeeeAddress = this.readIEEEAddr() ;
            r. endpoint = this.readUInt8();
            return r;
        }
        throw new RuntimeException("Invalid GP applicationId "+applicationId);
    }
    
    
    public EmberInitSecState readEmberInitSecState()
    {
    	EmberInitSecState r = new EmberInitSecState() ;
        r. bitmask = this.readUInt16();
        r. preconfiguredKey = this.readEmberKeyData();
        r. networkKey = this.readEmberKeyData();
        r. networkKeySequenceNumber = this.readUInt8();
        r. preconfiguredTrustCenterEui64 = this.readIEEEAddr();

        return r;
    }
    
    public void writeEmberInitSecState(EmberInitSecState val)
    {
        this.writeUInt16(val.bitmask);
        this.writeEmberKeyData(val.preconfiguredKey);
        this.writeEmberKeyData(val.networkKey);
        this.writeUInt8(val.networkKeySequenceNumber);
        this.writeIEEEAddr(val.preconfiguredTrustCenterEui64);
    }

    public NwkParams readEmberNwkParams()
    {
    	NwkParams ret = new NwkParams() ;
    	ret.extPanId = this.readExtendPanId();//.readListUInt8(EXTENDED_PAN_ID_SIZE);
        ret.panId = this.readUInt16();
        ret.radio_tx_power= this.readUInt8();
        ret.radio_ch = this.readUInt8();
        ret.join_method = EmberJoinMethod.valOfInt(this.readUInt8());
        ret.nwk_short_addr= this.readUInt16();
        ret.nwk_updateid = this.readUInt8();
        ret.channels = this.readUInt32();
        return ret ;
    }
    

    public void writeEmberNwkParams(NwkParams val)
    {
        this.writeExtendPanId(val.extPanId);
        this.writeUInt16(val.panId);
        this.writeUInt8(val.radio_tx_power);
        this.writeUInt8(val.radio_ch);
        this.writeUInt8(val.join_method.getIntVal());
        this.writeUInt16(val.nwk_short_addr);
        this.writeUInt8(val.nwk_updateid);
        this.writeUInt32(val.channels);
    }
    
    public SecManNwkKeyInf readSecManNwkKeyInf()
    {
    	SecManNwkKeyInf ret = new SecManNwkKeyInf() ;
        ret.networkKeySet = this.readUInt8() != 0;
        ret.alternateNetworkKeySet = this.readUInt8() != 0;
        ret.networkKeySequenceNumber = this.readUInt8();
        ret.altNetworkKeySequenceNumber = this.readUInt8();
        ret.networkKeyFrameCounter = this.readUInt32();
        return ret ;
    }

    public void writeSecManNwkKeyInf(SecManNwkKeyInf val)
    {
        this.writeUInt8(val.networkKeySet ? 1 : 0);
        this.writeUInt8(val.alternateNetworkKeySet ? 1 : 0);
        this.writeUInt8(val.networkKeySequenceNumber);
        this.writeUInt8(val.altNetworkKeySequenceNumber);
        this.writeUInt32(val.networkKeyFrameCounter);
    }

    public SecManAPSKeyMeta readSecManAPSKeyMetadata() {
    	SecManAPSKeyMeta r = new SecManAPSKeyMeta() ;
        r.bitmask = this.readUInt16();
        r.outgoingFrameCounter = this.readUInt32();
        r.incomingFrameCounter = this.readUInt32();
        r.ttlInSeconds = this.readUInt16();
        return r;
    }
    
    public void writeSecManAPSKeyMetadata(SecManAPSKeyMeta val)
    {
        this.writeUInt16(val.bitmask);
        this.writeUInt32(val.outgoingFrameCounter);
        this.writeUInt32(val.incomingFrameCounter);
        this.writeUInt16(val.ttlInSeconds);
    }
    
    public SecManContext readSecManContext()
    {
    	SecManContext r = SecManContext.initContext() ;
        r.coreKeyType = SecManKeyType.valOfInt(this.readUInt8());
        r.keyIndex = this.readUInt8();
        r.derivedType = SecManDerivedKeyType.valOfInt(this.readUInt16());
        r.eui64 = this.readIEEEAddr();
        r.multiNetworkIndex = this.readUInt8();
        r.flags = SecManFlag.valOfInt(this.readUInt8());
        r.psaKeyAlgPermission = this.readUInt32();

        return r;
    }

    public void writeSecManContext(SecManContext val)
    {
        this.writeUInt8(val.coreKeyType.getIntVal());
        this.writeUInt8(val.keyIndex);
        this.writeUInt16(val.derivedType.getIntVal());
        this.writeIEEEAddr(val.eui64);
        this.writeUInt8(val.multiNetworkIndex);
        this.writeUInt8(val.flags.getIntVal());
        this.writeUInt32(val.psaKeyAlgPermission);
    }

//    public EmberZigbeeNetwork readEmberZigbeeNetwork()
//    {
//    	EmberZigbeeNetwork r = new EmberZigbeeNetwork() ;
//        r.channel = this.readUInt8();
//        r. panId = this.readUInt16();
//        r. extendedPanId = this.readExtendPanId();//.readListUInt8(EXTENDED_PAN_ID_SIZE);
//        r. allowingJoin = this.readUInt8() != 0;
//        r. stackProfile = this.readUInt8();
//        r. nwkUpdateId = this.readUInt8();
//
//        return r;
//    }
}
