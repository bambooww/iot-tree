package org.iottree.driver.zigbee.ember.types;

public class EzspValueId
{
	/** The contents of the node data stack token. */
    public static final int TOKEN_STACK_NODE_DATA = 0x00;
    /** The types of MAC passthrough messages that the host wishes to receive. */
    public static final int MAC_PASSTHROUGH_FLAGS = 0x01;
    /**
     * The source address used to filter legacy EmberNet messages when the
     * EMBER_MAC_PASSTHROUGH_EMBERNET_SOURCE flag is set in MAC_PASSTHROUGH_FLAGS.
     */
    public static final int EMBERNET_PASSTHROUGH_SOURCE_ADDRESS = 0x02;
    /** The amount in bytes (max 2^16) of available general purpose heap memory. */
    		public static final int BUFFER_HEAP_FREE_SIZE = 0x03;
    /** Selects sending synchronous callbacks in ezsp-uart. */
    public static final int UART_SYNCH_CALLBACKS = 0x04;
    /**
     * The maximum incoming transfer size for the local node.
     * Default value is set to 82 and does not use fragmentation. Sets the value in Node Descriptor.
     * To set, this takes the input of a uint8 array of length 2 where you pass the lower byte at index 0 and upper byte at index 1.
     */
    public static final int MAXIMUM_INCOMING_TRANSFER_SIZE = 0x05;
    /**
     * The maximum outgoing transfer size for the local node.
     * Default value is set to 82 and does not use fragmentation. Sets the value in Node Descriptor.
     * To set, this takes the input of a uint8 array of length 2 where you pass the lower byte at index 0 and upper byte at index 1.
     */
    public static final int MAXIMUM_OUTGOING_TRANSFER_SIZE = 0x06;
    /** A bool indicating whether stack tokens are written to persistent storage as they change. */
    public static final int  STACK_TOKEN_WRITING = 0x07;
    /** A read-only value indicating whether the stack is currently performing a rejoin. */
    public static final int STACK_IS_PERFORMING_REJOIN = 0x08;
    /** A list of EmberMacFilterMatchData values. */
    public static final int MAC_FILTER_LIST = 0x09;
    /** The Ember Extended Security Bitmask. */
    public static final int EXTENDED_SECURITY_BITMASK = 0x0a;
    /** The node short ID. */
    public static final int NODE_SHORT_ID = 0x0b;
    /** The descriptor capability of the local node. Write only. */
    public static final int DESCRIPTOR_CAPABILITY = 0x0c;
    /** The stack device request sequence number of the local node. */
    public static final int STACK_DEVICE_REQUEST_SEQUENCE_NUMBER = 0x0d;
    /** Enable or disable radio hold-off. */
    public static final int RADIO_HOLD_OFF = 0x0e;
    /** The flags field associated with the endpoint data. */
    public static final int ENDPOINT_FLAGS = 0x0f;
    /** Enable/disable the Mfg security config key settings. */
    public static final int MFG_SECURITY_CONFIG = 0x10;
    /** Retrieves the version information from the stack on the NCP. */
    public static final int VERSION_INFO = 0x11;
    // NEXT_HOST_REJOIN_REASON              = 0x12,// v13-, unused
    /**
     * This is the reason that the last rejoin took place. This value may only be retrieved, not set.
     * The rejoin may have been initiated by the stack (NCP) or the application (host).
     * If a host initiated a rejoin the reason will be set by default to EMBER_REJOIN_DUE_TO_APP_EVENT_1.
     * If the application wishes to denote its own rejoin reasons it can do so by calling
     * ezspSetValue(EMBER_VALUE_HOST_REJOIN_REASON, EMBER_REJOIN_DUE_TO_APP_EVENT_X).
     * X is a number corresponding to one of the app events defined.
     * If the NCP initiated a rejoin it will record this value internally for retrieval by ezspGetValue(REAL_REJOIN_REASON).
     */
    public static final int LAST_REJOIN_REASON = 0x13;
    /** The next Zigbee sequence number. */
    public static final int NEXT_ZIGBEE_SEQUENCE_NUMBER = 0x14;
    /** CCA energy detect threshold for radio. */
    public static final int CCA_THRESHOLD = 0x15;
    /** The threshold value for a counter */
    public static final int SET_COUNTER_THRESHOLD = 0x17;
    /** Resets all counters thresholds to 0xFF */
    public static final int RESET_COUNTER_THRESHOLDS = 0x18;
    /** Clears all the counters */
    public static final int CLEAR_COUNTERS = 0x19;
    /** The node's new certificate signed by the CA. */
    public static final int CERTIFICATE_283K1 = 0x1a;
    /** The Certificate Authority's public key. */
    public static final int PUBLIC_KEY_283K1 = 0x1b;
    /** The node's new static private key. */
    public static final int PRIVATE_KEY_283K1 = 0x1c;
    // 0x1D?
    // 0x1E?
    // 0x1F?
    // 0x20?
    // 0x21?
    // 0x22?
    /** The NWK layer security frame counter value */
    public static final int NWK_FRAME_COUNTER = 0x23;
    /** The APS layer security frame counter value. Managed by the stack. Users should not set these unless doing backup and restore. */
    public static final int APS_FRAME_COUNTER = 0x24;
    /** Sets the device type to use on the next rejoin using device type */
    public static final int RETRY_DEVICE_TYPE = 0x25;
    // 0x26?
    // 0x27?
    // 0x28?
    /** Setting this byte enables R21 behavior on the NCP. */
    public static final int ENABLE_R21_BEHAVIOR = 0x29;
    /** Configure the antenna mode(0-don't switch,1-primary,2-secondary,3-TX antenna diversity). */
    public static final int ANTENNA_MODE = 0x30;
    /** Enable or disable packet traffic arbitration. */
    public static final int ENABLE_PTA = 0x31;
    /** Set packet traffic arbitration configuration options. */
    public static final int PTA_OPTIONS = 0x32;
    /** Configure manufacturing library options (0-non-CSMA transmits,1-CSMA transmits). To be used with Manufacturing Library. */
    public static final int MFGLIB_OPTIONS = 0x33;
    /**
     * Sets the flag to use either negotiated power by link power delta (LPD) or fixed power value provided by user
     * while forming/joining a network for packet transmissions on sub-ghz interface. This is mainly for testing purposes.
     */
    public static final int USE_NEGOTIATED_POWER_BY_LPD = 0x34;
    /** Set packet traffic arbitration PWM options. */
    public static final int PTA_PWM_OPTIONS = 0x35;
    /** Set packet traffic arbitration directional priority pulse width in microseconds. */
    public static final int PTA_DIRECTIONAL_PRIORITY_PULSE_WIDTH = 0x36;
    /** Set packet traffic arbitration phy select timeout(ms). */
    public static final int PTA_PHY_SELECT_TIMEOUT = 0x37;
    /** Configure the RX antenna mode: (0-do not switch; 1-primary; 2-secondary; 3-RX antenna diversity). */
    public static final int ANTENNA_RX_MODE = 0x38;
    /** Configure the timeout to wait for the network key before failing a join. Acceptable timeout range [3,255]. Value is in seconds. */
    public static final int NWK_KEY_TIMEOUT = 0x39;
    /**
     * The number of failed CSMA attempts due to failed CCA made by the MAC before continuing transmission with CCA disabled.
     * This is the same as calling the emberForceTxAfterFailedCca(uint8_t csmaAttempts) API. A value of 0 disables the feature.
     */
    public static final int FORCE_TX_AFTER_FAILED_CCA_ATTEMPTS = 0x3a;
    /**
     * The length of time, in seconds, that a trust center will store a transient link key that a device can use to join its network.
     * A transient key is added with a call to sl_zb_sec_man_import_transient_key. After the transient key is added,
     * it will be removed once this amount of time has passed. A joining device will not be able to use that key to join
     * until it is added again on the trust center.
     * The default value is 300 seconds (5 minutes).
     */
    public static final int TRANSIENT_KEY_TIMEOUT_S = 0x3b;
    /** Cumulative energy usage metric since the last value reset of the coulomb counter plugin. Setting this value will reset the coulomb counter. */
    public static final int COULOMB_COUNTER_USAGE = 0x3c;
    /**
     * When scanning, configure the maximum number of beacons to store in cache.
     * Each beacon consumes on average 32-bytes (+ buffer overhead) in RAM.
     */
    public static final int MAX_BEACONS_TO_STORE = 0x3d;
    /** Set the mask to filter out unacceptable child timeout options on a router. */
    public static final int END_DEVICE_TIMEOUT_OPTIONS_MASK = 0x3e;
    /** The end device keep-alive mode supported by the parent. */
    public static final int END_DEVICE_KEEP_ALIVE_SUPPORT_MODE = 0x3f;
    /**
     * Return the active radio config. Read only.
     * Values are 0: Default, 1: Antenna Diversity, 2: Co-Existence, 3: Antenna Diversity and Co-Existence.
     */
    public static final int ACTIVE_RADIO_CONFIG = 0x41;
    /** Return the number of seconds the network will remain open. A return value of 0 indicates that the network is closed. Read only. */
    public static final int NWK_OPEN_DURATION = 0x42;
    /**
     * Timeout in milliseconds to store entries in the transient device table.
     * If the devices are not authenticated before the timeout, the entry shall be purged
     */
    public static final int TRANSIENT_DEVICE_TIMEOUT = 0x43;
    /**
     * Return information about the key storage on an NCP.
     * Returns 0 if keys are in classic key storage, and 1 if they are located in PSA key storage. Read only.
     */
    public static final int KEY_STORAGE_VERSION = 0x44;
    /** Return activation state about TC Delayed Join on an NCP.  A return value of 0 indicates that the feature is not activated. */
    public static final int DELAYED_JOIN_ACTIVATION = 0x45;
    /**
     * v14+
     * The maximum number of NWK retries that will be attempted.
     */
    public static final int MAX_NWK_RETRIES = 0x46;
    /**
     * v14+
     * Policies for allowing/disallowing rejoins.
     */
    public static final int REJOIN_MODE = 0x47;
    /**
     * v17+
     * Controls whether devices must use an install code when joining.
     */
    public static final int  JOIN_USE_INSTALL_CODE_ENABLE = 0x48;
    /** v18+ The stack revision. */
    public static final int STACK_REVISION = 0x49;
    /**
     * v19+ (technically v9.1.1)
     * The extra milliseconds added to the APS retry and Fragmentation retry
     * timeout for known sleepy destinations.
     */
    public static final int SLEEPY_TARGET_MAC_TIMEOUT_INCREASE = 0x50;
}
