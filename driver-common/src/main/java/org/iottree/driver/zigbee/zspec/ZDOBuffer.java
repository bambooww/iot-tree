package org.iottree.driver.zigbee.zspec;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.zspec.zdo.*;
import org.iottree.driver.zigbee.zspec.zdo_def.GlobalTLV;
import org.iottree.driver.zigbee.zspec.zdo_def.MACCapabilityFlags;
import org.iottree.driver.zigbee.zspec.zdo_def.ServerMask;
import org.iottree.driver.zigbee.zspec.zdo_def.Tlv;
import org.iottree.driver.zigbee.zspec.zdo_def.TlvTag;

public class ZDOBuffer extends DataBuffer
{
	static final int MAX_BUFFER_SIZE = 255;
	
	static ILogger log = LoggerManager.getLogger(ZDOBuffer.class) ;
	
	private ZDOBuffer()
	{
	}
	
	public ZDOBuffer(int buflen,int init_pos)
	{
		super(buflen,init_pos) ;
	}

	public ZDOBuffer(byte[] bs, int offset, int len)
	{
		super(bs, offset, len);
	}

	public static ZDOBuffer alloc(int len)
	{
		ZDOBuffer r = new ZDOBuffer();
		r.buf = new byte[len];
		return r;
	}
	
	public static ZDOBuffer allocMax()
	{
		return alloc(MAX_BUFFER_SIZE) ;
	}
	
	public ZDOStatus readZDOStatus()
	{
		int v = this.readUInt8() ;
		return ZDOStatus.valOfInt(v) ;
	}
	
	public void writeGlobalTLVs(List<TlvTag> tlvs)
	{
		if(tlvs==null)
			return ;
		
        for (TlvTag tlv : tlvs) {
            this.writeGlobalTLV(tlv);
        }
    }
	
	public void writeGlobalTLV(TlvTag tlv)
	{
        this.writeUInt8(tlv.tagId);
        this.writeUInt8(tlv.len - 1); // remove offset (spec quirk...)

        GlobalTLV gtvl = GlobalTLV.valOfInt(tlv.tagId) ;
        if(gtvl==null)
        	throw new IllegalArgumentException("not Global TLV");
        switch (gtvl) {
            case MANUFACTURER_SPECIFIC: {
                this.writeManufacturerSpecificGlobalTLV((Tlv.ManufacturerSpecificGlobalTLV)tlv.tlv );
                break;
            }
            case SUPPORTED_KEY_NEGOTIATION_METHODS: {
                this.writeSupportedKeyNegotiationMethodsGlobalTLV((Tlv.SupportedKeyNegotiationMethodsGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case PAN_ID_CONFLICT_REPORT: {
                this.writePanIdConflictReportGlobalTLV((Tlv.PanIdConflictReportGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case NEXT_PAN_ID_CHANGE: {
                this.writeNextPanIdChangeGlobalTLV((Tlv.NextPanIdChangeGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case NEXT_CHANNEL_CHANGE: {
                this.writeNextChannelChangeGlobalTLV((Tlv.NextChannelChangeGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case SYMMETRIC_PASSPHRASE: {
                this.writeSymmetricPassphraseGlobalTLV((Tlv.SymmetricPassphraseGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case ROUTER_INFORMATION: {
                this.writeRouterInformationGlobalTLV((Tlv.RouterInformationGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case FRAGMENTATION_PARAMETERS: {
                this.writeFragmentationParametersGlobalTLV((Tlv.FragmentationParametersGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case JOINER_ENCAPSULATION: {
                this.writeJoinerEncapsulationGlobalTLV((Tlv.JoinerEncapsulationGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case BEACON_APPENDIX_ENCAPSULATION: {
                this.writeBeaconAppendixEncapsulationGlobalTLV((Tlv.BeaconAppendixEncapsulationGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case CONFIGURATION_PARAMETERS: {
                this.writeConfigurationParametersGlobalTLV((Tlv.ConfigurationParametersGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            case DEVICE_CAPABILITY_EXTENSION: {
                this.writeDeviceCapabilityExtensionGlobalTLV((Tlv.DeviceCapabilityExtensionGlobalTLV)tlv.tlv ); //(tlv.tlv as );
                break;
            }
            default: {
                throw new IllegalArgumentException("NOT_SUPPORTED");
            }
        }
    }
	
	public Tlv readGlobalTLV(int tagId, int length)
	{
		GlobalTLV gtvl = GlobalTLV.valOfInt(tagId) ;
        if(gtvl==null)
        	throw new IllegalArgumentException("not Global TLV");
        
        switch (gtvl) {
            case MANUFACTURER_SPECIFIC: {
                return this.readManufacturerSpecificGlobalTLV(length);
            }
            case SUPPORTED_KEY_NEGOTIATION_METHODS: {
                return this.readSupportedKeyNegotiationMethodsGlobalTLV(length);
            }
            case PAN_ID_CONFLICT_REPORT: {
                return this.readPanIdConflictReportGlobalTLV(length);
            }
            case NEXT_PAN_ID_CHANGE: {
                return this.readNextPanIdChangeGlobalTLV(length);
            }
            case NEXT_CHANNEL_CHANGE: {
                return this.readNextChannelChangeGlobalTLV(length);
            }
            case SYMMETRIC_PASSPHRASE: {
                return this.readSymmetricPassphraseGlobalTLV(length);
            }
            case ROUTER_INFORMATION: {
                return this.readRouterInformationGlobalTLV(length);
            }
            case FRAGMENTATION_PARAMETERS: {
                return this.readFragmentationParametersGlobalTLV(length);
            }
            case JOINER_ENCAPSULATION: {
                return this.readJoinerEncapsulationGlobalTLV(length);
            }
            case BEACON_APPENDIX_ENCAPSULATION: {
                return this.readBeaconAppendixEncapsulationGlobalTLV(length);
            }
            case CONFIGURATION_PARAMETERS: {
                return this.readConfigurationParametersGlobalTLV(length);
            }
            case DEVICE_CAPABILITY_EXTENSION: {
                return this.readDeviceCapabilityExtensionGlobalTLV(length);
            }
            default: {
                // validation: unknown tag shall be ignored
                return null;
            }
        }
    }
	
	//-- GLOBAL TLVS

    private void writeManufacturerSpecificGlobalTLV(Tlv.ManufacturerSpecificGlobalTLV tlv) {
        this.writeUInt16(tlv.zigbeeManufacturerId);
        this.writeBuf(tlv.additionalData,0, tlv.additionalData.length);
    }

    private Tlv.ManufacturerSpecificGlobalTLV readManufacturerSpecificGlobalTLV(int length) {
        //logger.debug(`readManufacturerSpecificGlobalTLV with length=${length}`, NS);
        if (length < 2) {
            throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }
        Tlv.ManufacturerSpecificGlobalTLV r = new Tlv.ManufacturerSpecificGlobalTLV() ;
        r. zigbeeManufacturerId = this.readUInt16();
        r. additionalData = this.readBuf(length - 2);
        return r;
    }

    private void writeSupportedKeyNegotiationMethodsGlobalTLV(Tlv.SupportedKeyNegotiationMethodsGlobalTLV tlv)
    {
        this.writeUInt8(tlv.keyNegotiationProtocolsBitmask);
        this.writeUInt8(tlv.preSharedSecretsBitmask);

        if (Convert.isNotNullEmpty(tlv.sourceDeviceEui64)) {
            this.writeIEEEAddr(tlv.sourceDeviceEui64);
        }
    }

    private Tlv.SupportedKeyNegotiationMethodsGlobalTLV readSupportedKeyNegotiationMethodsGlobalTLV(int length)
    {
        //logger.debug(`readSupportedKeyNegotiationMethodsGlobalTLV with length=${length}`, NS);
        if (length < 2) {
            throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }

        Tlv.SupportedKeyNegotiationMethodsGlobalTLV r = new Tlv.SupportedKeyNegotiationMethodsGlobalTLV() ;
        r. keyNegotiationProtocolsBitmask = this.readUInt8();
        r. preSharedSecretsBitmask = this.readUInt8();
        r. sourceDeviceEui64 = null;// SupportedKeyNegotiationMethodsGlobalTLV["sourceDeviceEui64"];

        if (length >= 2 + ZSpecConsts.EUI64_SIZE) {
            r.sourceDeviceEui64 = this.readIEEEAddr(); //.readIeeeAddr();
        }

        return r;
    }

    private void writePanIdConflictReportGlobalTLV(Tlv.PanIdConflictReportGlobalTLV tlv)
    {
        this.writeUInt16(tlv.nwkPanIdConflictCount);
    }

    private Tlv.PanIdConflictReportGlobalTLV readPanIdConflictReportGlobalTLV(int length)
    {
        //logger.debug(`readPanIdConflictReportGlobalTLV with length=${length}`, NS);
        if (length < 2) {
            throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }
        Tlv. PanIdConflictReportGlobalTLV r = new Tlv.PanIdConflictReportGlobalTLV() ;
        r. nwkPanIdConflictCount = this.readUInt16();

        return r ;
    }

    private void writeNextPanIdChangeGlobalTLV(Tlv.NextPanIdChangeGlobalTLV tlv)
    {
        this.writeUInt16(tlv.panId);
    }

    private Tlv.NextPanIdChangeGlobalTLV readNextPanIdChangeGlobalTLV(int length)
    {
        //logger.debug(`readNextPanIdChangeGlobalTLV with length=${length}`, NS);
        if (length < ZSpecConsts.PAN_ID_SIZE) {
        	 throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least "+ZSpecConsts.PAN_ID_SIZE);
        }
        Tlv.NextPanIdChangeGlobalTLV r = new Tlv.NextPanIdChangeGlobalTLV() ;
        r.panId = this.readUInt16();

        return r ;
    }

    private void writeNextChannelChangeGlobalTLV(Tlv.NextChannelChangeGlobalTLV tlv)
    {
        this.writeUInt32(tlv.channel);
    }

    private Tlv.NextChannelChangeGlobalTLV readNextChannelChangeGlobalTLV(int length)
    {
        // logger.debug(`readNextChannelChangeGlobalTLV with length=${length}`, NS);
        if (length < 4) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 4");
        }

        Tlv.NextChannelChangeGlobalTLV r = new Tlv.NextChannelChangeGlobalTLV() ;
        r.channel = this.readUInt32();

        return r ;
    }

    private void writeSymmetricPassphraseGlobalTLV(Tlv.SymmetricPassphraseGlobalTLV tlv)
    {
        this.writeBuf(tlv.passphrase,0, ZSpecConsts.DEFAULT_ENCRYPTION_KEY_SIZE);
    }

    private Tlv.SymmetricPassphraseGlobalTLV readSymmetricPassphraseGlobalTLV(int length)
    {
        //logger.debug(`readSymmetricPassphraseGlobalTLV with length=${length}`, NS);
        if (length < ZSpecConsts.DEFAULT_ENCRYPTION_KEY_SIZE) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least "+ZSpecConsts.DEFAULT_ENCRYPTION_KEY_SIZE);
        }
        Tlv.SymmetricPassphraseGlobalTLV r = new Tlv.SymmetricPassphraseGlobalTLV() ;
        r. passphrase = this.readBuf(ZSpecConsts.DEFAULT_ENCRYPTION_KEY_SIZE);

        return r ;
    }

    private void writeRouterInformationGlobalTLV(Tlv.RouterInformationGlobalTLV tlv) {
        this.writeUInt16(tlv.bitmask);
    }

    private Tlv.RouterInformationGlobalTLV readRouterInformationGlobalTLV(int length)
    {
        //logger.debug(`readRouterInformationGlobalTLV with length=${length}`, NS);
        if (length < 2) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }
        Tlv.RouterInformationGlobalTLV r = new Tlv.RouterInformationGlobalTLV() ;
        r.bitmask = this.readUInt16();
        return r ;
    }

    private void writeFragmentationParametersGlobalTLV(Tlv.FragmentationParametersGlobalTLV tlv) {
        this.writeUInt16(tlv.nwkAddress);

        if (tlv.fragmentationOptions !=null) {
            this.writeUInt8(tlv.fragmentationOptions);
        }

        if (tlv.maxIncomingTransferUnit !=null) {
            this.writeUInt16(tlv.maxIncomingTransferUnit);
        }
    }

    private Tlv.FragmentationParametersGlobalTLV readFragmentationParametersGlobalTLV(int length)
    {
        //logger.debug(`readFragmentationParametersGlobalTLV with length=${length}`, NS);
        if (length < 2) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }

        Tlv.FragmentationParametersGlobalTLV r = new Tlv.FragmentationParametersGlobalTLV() ;
        r. nwkAddress = this.readUInt16();
        //let fragmentationOptions: FragmentationParametersGlobalTLV["fragmentationOptions"];
        //let maxIncomingTransferUnit: FragmentationParametersGlobalTLV["maxIncomingTransferUnit"];

        if (length >= 3) {
            r.fragmentationOptions = this.readUInt8();
        }

        if (length >= 5) {
            r.maxIncomingTransferUnit = this.readUInt16();
        }

        return r ;
    }

    private void writeJoinerEncapsulationGlobalTLV(Tlv.JoinerEncapsulationGlobalTLV encapsulationTLV) {
        this.writeGlobalTLVs(encapsulationTLV.additionalTLVs);
    }

    private Tlv.JoinerEncapsulationGlobalTLV readJoinerEncapsulationGlobalTLV(int length)
    {
        //logger.debug(`readJoinerEncapsulationGlobalTLV with length=${length}`, NS);
        // at least the length of tagId+length for first encapsulated tlv, doesn't make sense otherwise
    	if (length < 2) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }

    	byte[] bs = this.readBuf(length) ;
    	Tlv.JoinerEncapsulationGlobalTLV r = new Tlv.JoinerEncapsulationGlobalTLV() ;
    	ZDOBuffer  encapsulation_buf = new ZDOBuffer(bs,0,bs.length);
        r. additionalTLVs = encapsulation_buf.readTLVs(null, true);

        return r;
    }

    private void writeBeaconAppendixEncapsulationGlobalTLV(Tlv.BeaconAppendixEncapsulationGlobalTLV encapsulationTLV)
    {
        this.writeGlobalTLVs(encapsulationTLV.additionalTLVs);
    }

    private Tlv.BeaconAppendixEncapsulationGlobalTLV readBeaconAppendixEncapsulationGlobalTLV(int length)
    {
        //logger.debug(`readBeaconAppendixEncapsulationGlobalTLV with length=${length}`, NS);
        // at least the length of tagId+length for first encapsulated tlv, doesn't make sense otherwise
        if (length < 2) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }

        byte[] bs = this.readBuf(length) ;
        Tlv.BeaconAppendixEncapsulationGlobalTLV r = new Tlv.BeaconAppendixEncapsulationGlobalTLV() ;
        ZDOBuffer  encapsulation_buf = new ZDOBuffer(bs,0,bs.length);
        // Global: SupportedKeyNegotiationMethodsGlobalTLV
        // Global: FragmentationParametersGlobalTLV
        r. additionalTLVs = encapsulation_buf.readTLVs(null, true);

        return r ;
    }

    private void writeConfigurationParametersGlobalTLV(Tlv.ConfigurationParametersGlobalTLV configurationParameters) {
        this.writeUInt16(configurationParameters.configurationParameters);
    }

    private Tlv.ConfigurationParametersGlobalTLV readConfigurationParametersGlobalTLV(int length) 
    {
        //logger.debug(`readConfigurationParametersGlobalTLV with length=${length}`, NS);
    	if (length < 2) {
        	throw new IllegalArgumentException("TLV Invalid length "+length+", expected at least 2.");
        }
    	Tlv.ConfigurationParametersGlobalTLV r = new Tlv.ConfigurationParametersGlobalTLV() ;
        r. configurationParameters = this.readUInt16();

        return r ;
    }

    private void writeDeviceCapabilityExtensionGlobalTLV(Tlv.DeviceCapabilityExtensionGlobalTLV tlv) {
        this.writeBuf(tlv.data,0, tlv.data.length);
    }

    private Tlv.DeviceCapabilityExtensionGlobalTLV readDeviceCapabilityExtensionGlobalTLV(int length)
    {
        //logger.debug(`readDeviceCapabilityExtensionGlobalTLV with length=${length}`, NS);
    	Tlv.DeviceCapabilityExtensionGlobalTLV r = new Tlv.DeviceCapabilityExtensionGlobalTLV() ;
        r. data = this.readBuf(length);

        return r ;
    }
    
    /**
     * ANNEX I ZIGBEE TLV DEFINITIONS AND FORMAT
     *
     * Unknown tags => TLV ignored
     * Duplicate tags => reject message except for MANUFACTURER_SPECIFIC_GLOBAL_TLV
     * Malformed TLVs => reject message
     *
     * @param localTLVReaders Mapping of tagID to local TLV reader function
     * @param encapsulated Default false. If true, this is reading inside an encapsuled TLV (excludes further encapsulation)
     * @returns
     */
    public List<TlvTag> readTLVs(Map<Integer, Tlv.LocalTLVReader> localTLVReaders, boolean encapsulated) // = false)
    {
        LinkedHashMap<Integer,TlvTag> tagid2tlvs = new LinkedHashMap<>();

        while (this.hasMore())
        {
            int tagId = this.readUInt8();

            // validation: cannot have duplicate tagId, except MANUFACTURER_SPECIFIC_GLOBAL_TLV
            if (tagId != GlobalTLV.MANUFACTURER_SPECIFIC.getIntVal() && tagid2tlvs.containsKey(tagId))
            {
                throw new RuntimeException("Duplicate tag. Cannot have more than one of tagId="+tagId);
            }

            // validation: encapsulation TLV cannot contain another encapsulation TLV, outer considered malformed, reject message
            if (encapsulated && (tagId == GlobalTLV.BEACON_APPENDIX_ENCAPSULATION.getIntVal() || tagId == GlobalTLV.JOINER_ENCAPSULATION.getIntVal())) {
                throw new RuntimeException("Invalid nested encapsulation for tagId="+tagId);
            }

            int length = this.readUInt8() + 1; // add offset (spec quirk...)

            // validation: invalid if not at least ${length} bytes to read
            if (!this.isMoreBy(length)) {
                throw new RuntimeException("Malformed TLV. Invalid data length for tagId="+tagId+", expected "+length);
            }

            int nextTLVStart = this.getPosition() + length;
            // undefined == unknown tag
            Tlv tlv = null ;//: Tlv["tlv"] | undefined;

            if (tagId < GlobalTLV.MANUFACTURER_SPECIFIC.getIntVal()) {
                if (localTLVReaders!=null)
                {
                	Tlv.LocalTLVReader localTLVReader = localTLVReaders.get(tagId);

                    if (localTLVReader!=null) {
                        tlv = localTLVReader.read_tlv_local(this, length);
                        /* v8 ignore start */
                    } else {
                    	log.debug("Local TLV found tagId="+tagId+" but no reader given for it. Ignoring it.");
                    }
                    /* v8 ignore stop */
                    /* v8 ignore start */
                } else {
                	log.debug("Local TLV found tagId="+tagId+" but no reader available. Ignoring it.");
                }
                /* v8 ignore stop */
            } else {
                tlv = this.readGlobalTLV(tagId, length);
            }

            // validation: unknown tag shall be ignored
            if (tlv!=null) {
            	tagid2tlvs.put(tagId,new TlvTag(tagId,length,tlv));
            } else {
                log.debug("Unknown TLV tagId="+tagId+". Ignoring it.");
            }

            // ensure we're at the right position as dictated by the tlv length field, and not the tlv reader (should be the same if proper)
            this.setPosition(nextTLVStart);
        }

        ArrayList<TlvTag> rets = new ArrayList<>(tagid2tlvs.size()) ;
        rets.addAll(tagid2tlvs.values()) ;
        return rets;
    }
	
  //-- REQUESTS

//    public static byte[] buildRequest(boolean hasZdoMessageOverhead,ZDOCluster zdo) //, ...args: RequestMap[K])
//    {
//        ZDOBuffer buf = new ZDOBuffer(MAX_BUFFER_SIZE, hasZdoMessageOverhead ? ZDOConsts.ZDO_MESSAGE_OVERHEAD : 0);
//
//        zdo.packTo(buf);
//        
//        return buf.getWritten() ;
        
//        switch (clusterId) {
//            case ZdoClusterId.NETWORK_ADDRESS_REQUEST: {
//                return buffalo.buildNetworkAddressRequest(...(args as RequestMap[ZdoClusterId.NETWORK_ADDRESS_REQUEST]));
//            }
//
//            case ZdoClusterId.IEEE_ADDRESS_REQUEST: {
//                return buffalo.buildIeeeAddressRequest(...(args as RequestMap[ZdoClusterId.IEEE_ADDRESS_REQUEST]));
//            }
//
//            case ZdoClusterId.NODE_DESCRIPTOR_REQUEST: {
//                return buffalo.buildNodeDescriptorRequest(...(args as RequestMap[ZdoClusterId.NODE_DESCRIPTOR_REQUEST]));
//            }
//
//            case ZdoClusterId.POWER_DESCRIPTOR_REQUEST: {
//                return buffalo.buildPowerDescriptorRequest(...(args as RequestMap[ZdoClusterId.POWER_DESCRIPTOR_REQUEST]));
//            }
//
//            case ZdoClusterId.SIMPLE_DESCRIPTOR_REQUEST: {
//                return buffalo.buildSimpleDescriptorRequest(...(args as RequestMap[ZdoClusterId.SIMPLE_DESCRIPTOR_REQUEST]));
//            }
//
//            case ZdoClusterId.ACTIVE_ENDPOINTS_REQUEST: {
//                return buffalo.buildActiveEndpointsRequest(...(args as RequestMap[ZdoClusterId.ACTIVE_ENDPOINTS_REQUEST]));
//            }
//
//            case ZdoClusterId.MATCH_DESCRIPTORS_REQUEST: {
//                return buffalo.buildMatchDescriptorRequest(...(args as RequestMap[ZdoClusterId.MATCH_DESCRIPTORS_REQUEST]));
//            }
//
//            case ZdoClusterId.SYSTEM_SERVER_DISCOVERY_REQUEST: {
//                return buffalo.buildSystemServiceDiscoveryRequest(...(args as RequestMap[ZdoClusterId.SYSTEM_SERVER_DISCOVERY_REQUEST]));
//            }
//
//            case ZdoClusterId.PARENT_ANNOUNCE: {
//                return buffalo.buildParentAnnounce(...(args as RequestMap[ZdoClusterId.PARENT_ANNOUNCE]));
//            }
//
//            case ZdoClusterId.BIND_REQUEST: {
//                return buffalo.buildBindRequest(...(args as RequestMap[ZdoClusterId.BIND_REQUEST]));
//            }
//
//            case ZdoClusterId.UNBIND_REQUEST: {
//                return buffalo.buildUnbindRequest(...(args as RequestMap[ZdoClusterId.UNBIND_REQUEST]));
//            }
//
//            case ZdoClusterId.CLEAR_ALL_BINDINGS_REQUEST: {
//                return buffalo.buildClearAllBindingsRequest(...(args as RequestMap[ZdoClusterId.CLEAR_ALL_BINDINGS_REQUEST]));
//            }
//
//            case ZdoClusterId.LQI_TABLE_REQUEST: {
//                return buffalo.buildLqiTableRequest(...(args as RequestMap[ZdoClusterId.LQI_TABLE_REQUEST]));
//            }
//
//            case ZdoClusterId.ROUTING_TABLE_REQUEST: {
//                return buffalo.buildRoutingTableRequest(...(args as RequestMap[ZdoClusterId.ROUTING_TABLE_REQUEST]));
//            }
//
//            case ZdoClusterId.BINDING_TABLE_REQUEST: {
//                return buffalo.buildBindingTableRequest(...(args as RequestMap[ZdoClusterId.BINDING_TABLE_REQUEST]));
//            }
//
//            case ZdoClusterId.LEAVE_REQUEST: {
//                return buffalo.buildLeaveRequest(...(args as RequestMap[ZdoClusterId.LEAVE_REQUEST]));
//            }
//
//            case ZdoClusterId.PERMIT_JOINING_REQUEST: {
//                return buffalo.buildPermitJoining(...(args as RequestMap[ZdoClusterId.PERMIT_JOINING_REQUEST]));
//            }
//
//            case ZdoClusterId.NWK_UPDATE_REQUEST: {
//                return buffalo.buildNwkUpdateRequest(...(args as RequestMap[ZdoClusterId.NWK_UPDATE_REQUEST]));
//            }
//
//            case ZdoClusterId.NWK_ENHANCED_UPDATE_REQUEST: {
//                return buffalo.buildNwkEnhancedUpdateRequest(...(args as RequestMap[ZdoClusterId.NWK_ENHANCED_UPDATE_REQUEST]));
//            }
//
//            case ZdoClusterId.NWK_IEEE_JOINING_LIST_REQUEST: {
//                return buffalo.buildNwkIEEEJoiningListRequest(...(args as RequestMap[ZdoClusterId.NWK_IEEE_JOINING_LIST_REQUEST]));
//            }
//
//            case ZdoClusterId.NWK_BEACON_SURVEY_REQUEST: {
//                return buffalo.buildNwkBeaconSurveyRequest(...(args as RequestMap[ZdoClusterId.NWK_BEACON_SURVEY_REQUEST]));
//            }
//
//            case ZdoClusterId.START_KEY_NEGOTIATION_REQUEST: {
//                return buffalo.buildStartKeyNegotiationRequest(...(args as RequestMap[ZdoClusterId.START_KEY_NEGOTIATION_REQUEST]));
//            }
//
//            case ZdoClusterId.RETRIEVE_AUTHENTICATION_TOKEN_REQUEST: {
//                return buffalo.buildRetrieveAuthenticationTokenRequest(...(args as RequestMap[ZdoClusterId.RETRIEVE_AUTHENTICATION_TOKEN_REQUEST]));
//            }
//
//            case ZdoClusterId.GET_AUTHENTICATION_LEVEL_REQUEST: {
//                return buffalo.buildGetAuthenticationLevelRequest(...(args as RequestMap[ZdoClusterId.GET_AUTHENTICATION_LEVEL_REQUEST]));
//            }
//
//            case ZdoClusterId.SET_CONFIGURATION_REQUEST: {
//                return buffalo.buildSetConfigurationRequest(...(args as RequestMap[ZdoClusterId.SET_CONFIGURATION_REQUEST]));
//            }
//
//            case ZdoClusterId.GET_CONFIGURATION_REQUEST: {
//                return buffalo.buildGetConfigurationRequest(...(args as RequestMap[ZdoClusterId.GET_CONFIGURATION_REQUEST]));
//            }
//
//            case ZdoClusterId.START_KEY_UPDATE_REQUEST: {
//                return buffalo.buildStartKeyUpdateRequest(...(args as RequestMap[ZdoClusterId.START_KEY_UPDATE_REQUEST]));
//            }
//
//            case ZdoClusterId.DECOMMISSION_REQUEST: {
//                return buffalo.buildDecommissionRequest(...(args as RequestMap[ZdoClusterId.DECOMMISSION_REQUEST]));
//            }
//
//            case ZdoClusterId.CHALLENGE_REQUEST: {
//                return buffalo.buildChallengeRequest(...(args as RequestMap[ZdoClusterId.CHALLENGE_REQUEST]));
//            }
//
//            default: {
//                throw new Error(`Unsupported request building for cluster ID '${clusterId}'.`);
//            }
//    }
    
	/**
     * @see ClusterId.PERMIT_JOINING_REQUEST
     * @param duration A value of 0x00 disables joining. A value of 0xFF enables joining. Any other value enables joining for that number of seconds.
     * @param authentication Controls Trust Center authentication behavior.
     *   This field SHALL always have a value of 1, indicating a request to change the Trust Center policy.
     *   If a frame is received with a value of 0, it shall be treated as having a value of 1.
     */
    private byte[] buildPermitJoining(int duration,int authentication,List<TlvTag> tlvs)
    {
        this.writeUInt8(duration);
        this.writeUInt8(authentication);
        // BeaconAppendixEncapsulationGlobalTLV
        //   - SupportedKeyNegotiationMethodsGlobalTLV
        //   - FragmentationParametersGlobalTLV
        this.writeGlobalTLVs(tlvs);

        return this.getWritten();
    }

	/**
	 * @see ClusterId.NETWORK_ADDRESS_RESPONSE
	 */
	public AddrRsp readNwkAddrResp()
	{
		//if(cid!= && cid!=ZDOClusterId.IEEE_ADDRESS_RESPONSE)
			
		//ZDOStatus status = ZDOStatus.valOfInt(this.readUInt8());
		AddrRsp ret = new AddrRsp(ZDOClusterId.NETWORK_ADDRESS_RESPONSE);
		ret.unpackResp(this) ;
		return ret ;
		
//		if (status == ZDOStatus.SUCCESS)
//		{
//			ret.eui64 = this.readIEEEAddr();
//			ret.nwkAddr = this.readUInt16();
//			int assocDevCount = 0;
//			if (this.hasMore())
//			{
//				assocDevCount = this.readUInt8();
//				ret.startIdx = this.readUInt8();
//				ret.assocDevList = this.readListUInt16(assocDevCount);
//			}
//		}
//		return ret;
	}

	/**
	 * @see ClusterId.IEEE_ADDRESS_RESPONSE
	 */
	public AddrRsp readIEEEAddrResp()
	{
		AddrRsp ret = new AddrRsp(ZDOClusterId.IEEE_ADDRESS_RESPONSE);
		ret.unpackResp(this) ;
		return ret ;
	}
	
//	public List<TlvTag> readTLVs(Map<Integer, LocalTLVReader> localTLVReaders , encapsulated = false): Tlv[] {
//        ArrayList<TlvTag> tlvs = new ArrayList<>();
//
//        while (this.isMore()) {
//            int tagId = this.readUInt8();
//
//            // validation: cannot have duplicate tagId, except MANUFACTURER_SPECIFIC_GLOBAL_TLV
//            if (tagId != MANUFACTURER_SPECIFIC && tlvs.findIndex((tlv) => tlv.tagId === tagId) !== -1) {
//                throw new Exception("Duplicate tag. Cannot have more than one of tagId="+tagId);
//            }
//
//            // validation: encapsulation TLV cannot contain another encapsulation TLV, outer considered malformed, reject message
//            if (encapsulated && (tagId == BEACON_APPENDIX_ENCAPSULATION || tagId === JOINER_ENCAPSULATION)) {
//                throw new Exception("Invalid nested encapsulation for tagId="+tagId);
//            }
//
//            int length = this.readUInt8() + 1; // add offset (spec quirk...)
//
//            // validation: invalid if not at least ${length} bytes to read
//            if (!this.isMoreBy(length)) {
//                throw new Exception("Malformed TLV. Invalid data length for tagId="+tagId+", expected +"+length);
//            }
//
//            int nextTLVStart = this.getPosition() + length;
//            // undefined == unknown tag
//            let tlv: Tlv["tlv"] | undefined;
//
//            if (tagId < MANUFACTURER_SPECIFIC) {
//                if (localTLVReaders!=null) {
//                    const localTLVReader = localTLVReaders.get(tagId);
//
//                    if (localTLVReader) {
//                        tlv = localTLVReader.call(this, length);
//                        /* v8 ignore start */
//                    } else {
//                        logger.debug(`Local TLV found tagId=${tagId} but no reader given for it. Ignoring it.`, NS);
//                    }
//                    /* v8 ignore stop */
//                    /* v8 ignore start */
//                } else {
//                    logger.debug(`Local TLV found tagId=${tagId} but no reader available. Ignoring it.`, NS);
//                }
//                /* v8 ignore stop */
//            } else {
//                tlv = this.readGlobalTLV(tagId, length);
//            }
//
//            // validation: unknown tag shall be ignored
//            if (tlv) {
//                tlvs.push({
//                    tagId,
//                    length,
//                    tlv,
//                });
//            } else {
//                logger.debug(`Unknown TLV tagId=${tagId}. Ignoring it.`, NS);
//            }
//
//            // ensure we're at the right position as dictated by the tlv length field, and not the tlv reader (should be the same if proper)
//            this.setPosition(nextTLVStart);
//        }
//
//        return tlvs;
//    }

	/**
     * @see ClusterId.NODE_DESCRIPTOR_RESPONSE
     */
    public NodeDescResp readNodeDescResp()
    {
        // DEVICE_NOT_FOUND, INV_REQUESTTYPE, or NO_DESCRIPTOR
    	NodeDescResp ret = new NodeDescResp();
    	ret.unpackFrom(this);
    	return ret ;
    	
//    	ZDOStatus status = ZDOStatus.valOfInt(this.readUInt8());
//        if (status == ZDOStatus.SUCCESS) {
//            ret. nwkAddr = this.readUInt16();
//            // in bits: [logical type: 3] [deprecated: 1] [deprecated: 1] [fragmentation supported (R23): 1] [reserved/unused: 2]
//            int nodeDescByte1 = this.readUInt8() ;
//            ret.logicalType=nodeDescByte1 & 0x07;
//            
//            ret. serverMask = ServerMask.getServerMask(this.readUInt16());
//            
//            // in bits: [aps flags: 3] [frequency band: 5]
//            int nodeDescByte2 = this.readUInt8();
//            ret.fragmentationSupported=ret.serverMask.stackComplianceRevision >= 23 ? (nodeDescByte1 & 0x20) >> 5 == 1 : false;//undefined,
//            ret. capabilities = MACCapabilityFlags.getMacCapFlags(this.readUInt8());
//            ret. manufacturerCode = this.readUInt16();
//            ret. maxBufSize = this.readUInt8();
//            ret. maxIncTxSize = this.readUInt16();
//            
//            ret. maxOutTxSize = this.readUInt16();
//            ret. deprecated1 = this.readUInt8();
//            // Global: FragmentationParametersGlobalTLV
//            // TODO ret. tlvs: Tlv[] = this.readTLVs();
//
//        }
//
//        return ret;
    }
}
