package org.iottree.driver.zigbee.zspec.zdo_def;

public class TlvTag
{
	/** 1-byte - 0-63: Local, 64-255: Global */
    public int tagId;
    /**
     * The Length byte encodes the number of bytes in the value field -1.
     * This means that a TLV with length field of 3 is expected to contain 4 bytes of data in the value field.
     *
     * WARNING: This field is assumed to always include the +1 offset, and logic should do the appropriate reversal when writing the actual buffer.
     *
     * 1-byte
     */
    public int len;
    /** Size = ${length + 1} */
    public Tlv tlv ;
    
//        | ManufacturerSpecificGlobalTLV
//        | SupportedKeyNegotiationMethodsGlobalTLV
//        | PanIdConflictReportGlobalTLV
//        | NextPanIdChangeGlobalTLV
//        | NextChannelChangeGlobalTLV
//        | SymmetricPassphraseGlobalTLV
//        | RouterInformationGlobalTLV
//        | FragmentationParametersGlobalTLV
//        | JoinerEncapsulationGlobalTLV
//        | BeaconAppendixEncapsulationGlobalTLV
//        | ConfigurationParametersGlobalTLV
//        | DeviceCapabilityExtensionGlobalTLV
//        | LocalTLVType;
    
    public TlvTag()
    {}
    
    public TlvTag(int tagid,int len,Tlv tlv)
    {
    	this.tagId = tagid ;
    	this.len = len ;
    	this.tlv = tlv ;
    }
}
