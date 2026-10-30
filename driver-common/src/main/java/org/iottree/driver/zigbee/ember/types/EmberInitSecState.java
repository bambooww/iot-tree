package org.iottree.driver.zigbee.ember.types;

/** This describes the Initial Security features and requirements that will be used when forming or joining the network.  */
public class EmberInitSecState
{
	/**
     * This bitmask enumerates which security features should be used and the presence of valid data within other elements of the
     * ::EmberInitialSecurityState data structure.  For more details, see the ::EmberInitialSecurityBitmask.
     * uint16_t
     */
    public int bitmask;
    /**
     * This is the pre-configured key that can be used by devices when joining the network if the Trust Center does not send
     * the initial security data in-the-clear.
     * For the Trust Center, it will be the global link key and <b>must</b> be set regardless of whether joining devices are
     * expected to have a pre-configured Link Key. This parameter will only be used if the EmberInitialSecurityState::bitmask
     * sets the bit indicating ::EMBER_HAVE_PRECONFIGURED_KEY.
     */
    public String preconfiguredKey; //: EmberKeyData;
    /**
     * This is the Network Key used when initially forming the network.
     * It must be set on the Trust Center and is not needed for devices joining the network.
     * This parameter will only be used if the EmberInitialSecurityState::bitmask sets the bit indicating ::EMBER_HAVE_NETWORK_KEY.
     */
    public String networkKey; //: EmberKeyData;
    /**
     * This is the sequence number associated with the network key. It must be set if the Network Key is set and is used to indicate
     * a particular of the network key for updating and switching.
     * This parameter will only be used if the ::EMBER_HAVE_NETWORK_KEY is set.
     * Generally, it should be set to 0 when forming the network; joining devices can ignore this value.
     * uint8_t
     * */
    public int networkKeySequenceNumber;//: number;
    /**
     * This is the long address of the trust center on the network that will be joined.
     * It is usually NOT set prior to joining the network and is learned during the joining message exchange.
     * This field is only examined if ::EMBER_HAVE_TRUST_CENTER_EUI64 is set in the EmberInitialSecurityState::bitmask.
     * Most devices should clear that bit and leave this field alone.
     * This field must be set when using commissioning mode.
     * It is required to be in little-endian format.
     */
    public String preconfiguredTrustCenterEui64; //: Eui64;
    
    public static final int STRUCT_LEN = 43;
}
