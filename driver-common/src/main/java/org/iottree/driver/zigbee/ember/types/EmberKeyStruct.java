package org.iottree.driver.zigbee.ember.types;

/** This bitmask describes the presence of fields within the ::EmberKeyStruct. uint16_t */
public class EmberKeyStruct
{
	/** This indicates that the key has a sequence number associated with it. (i.e., a Network Key). */
    public static final int HAS_SEQUENCE_NUMBER = 0x0001;
    /** This indicates that the key has an outgoing frame counter and the corresponding value within the ::EmberKeyStruct has been populated.*/
    public static final int HAS_OUTGOING_FRAME_COUNTER = 0x0002;
    /** This indicates that the key has an incoming frame counter and the corresponding value within the ::EmberKeyStruct has been populated.*/
    public static final int HAS_INCOMING_FRAME_COUNTER = 0x0004;
    /**
     * This indicates that the key has an associated Partner EUI64 address and the corresponding value
     * within the ::EmberKeyStruct has been populated.
     */
    public static final int HAS_PARTNER_EUI64 = 0x0008;
    /**
     * This indicates the key is authorized for use in APS data messages.
     * If the key is not authorized for use in APS data messages it has not yet gone through a key agreement protocol, such as CBKE (i.e., ECC).
     */
    public static final int IS_AUTHORIZED = 0x0010;
    /**
     * This indicates that the partner associated with the link is a sleepy end device.
     * This bit is set automatically if the local device hears a device announce from the partner indicating it is not an 'RX on when idle' device.
     */
    public static final int PARTNER_IS_SLEEPY = 0x0020;
    /**
     * This indicates that the transient key which is being added is unconfirmed.
     * This bit is set when we add a transient key while the EmberTcLinkKeyRequestPolicy is EMBER_ALLOW_TC_LINK_KEY_REQUEST_AND_GENERATE_NEW_KEY
     */
    public static final int UNCONFIRMED_TRANSIENT = 0x0040;
    /** This indicates that the actual key data is stored in PSA, and the respective PSA ID is recorded in the psa_id field. */
    public static final int HAS_PSA_ID = 0x0080;
    /**
     * This indicates that the keyData field has valid data. On certain parts and depending on the security configuration,
     * keys may live in secure storage and are not exportable. In such cases, keyData will not house the actual key contents.
     */
    public static final int HAS_KEY_DATA = 0x0100;
    /**
     * This indicates that the key represents a Device Authentication Token and is not an encryption key.
     * The Authentication token is persisted for the lifetime of the device on the network and used to validate and update the device connection.
     * It is only removed when the device leaves or is decommissioned from the network
     */
    public static final int IS_AUTHENTICATION_TOKEN = 0x0200;
    /** This indicates that the key has been derived by the Dynamic Link Key feature. */
    public static final int DLK_DERIVED = 0x0400;
    /** This indicates that the device this key is being used to communicate with supports the APS frame counter synchronization procedure. */
    public static final int FC_SYNC_SUPPORTED = 0x0800;
}
