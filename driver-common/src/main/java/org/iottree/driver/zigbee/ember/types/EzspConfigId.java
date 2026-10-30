package org.iottree.driver.zigbee.ember.types;

/** Identifies a configuration value. uint8_t */
public enum EzspConfigId
{
	/**
	 * The NCP no longer supports configuration of packet buffer heap at runtime
	 * using this parameter. Packet buffers heap space must be configured using
	 * the EMBER_PACKET_BUFFER_COUNT macro when building the NCP project.
	 */
	PACKET_BUFFER_HEAP_SIZE(0x01),
	/**
	 * The maximum number of router neighbors the stack can keep track of. A
	 * neighbor is a node within radio range.
	 */
	NEIGHBOR_TABLE_SIZE(0x02),
	/**
	 * The maximum number of APS retried messages the stack can be transmitting
	 * at any time.
	 */
	APS_UNICAST_MESSAGE_COUNT(0x03),
	/**
	 * The maximum number of non-volatile bindings supported by the stack.
	 */
	BINDING_TABLE_SIZE(0x04),
	/**
	 * The maximum number of EUI64 to network address associations that the
	 * stack can maintain for the application. (Note, the total number of such
	 * address associations maintained by the NCP is the sum of the value of
	 * this setting and the value of ::TRUST_CENTER_ADDRESS_CACHE_SIZE.
	 */
	ADDRESS_TABLE_SIZE(0x05),
	/**
	 * The maximum number of multicast groups that the device may be a member
	 * of.
	 */
	MULTICAST_TABLE_SIZE(0x06),
	/**
	 * The maximum number of destinations to which a node can route messages.
	 * This includes both messages originating at this node and those relayed
	 * for others.
	 */
	ROUTE_TABLE_SIZE(0x07),
	/**
	 * The number of simultaneous route discoveries that a node will support.
	 */
	DISCOVERY_TABLE_SIZE(0x08),
	// 0x0A?
	// 0x0B?
	/**
	 * Specifies the stack profile.
	 */
	STACK_PROFILE(0x0c),
	/**
	 * The security level used for security at the MAC and network layers. The
	 * supported values are 0 (no security) and 5 (payload is encrypted and a
	 * four-byte MIC is used for authentication).
	 */
	SECURITY_LEVEL(0x0d),
	// 0x0E?
	// 0x0F?
	/**
	 * The maximum number of hops for a message.
	 */
	MAX_HOPS(0x10),
	/**
	 * The maximum number of end device children that a router will support.
	 */
	MAX_END_DEVICE_CHILDREN(0x11),
	/**
	 * The maximum amount of time that the MAC will hold a message for indirect
	 * transmission to a child.
	 */
	INDIRECT_TRANSMISSION_TIMEOUT(0x12),
	/**
	 * The maximum amount of time that an end device child can wait between
	 * polls. If no poll is heard within this timeout, then the parent removes
	 * the end device from its tables. Value range 0-14. The timeout
	 * corresponding to a value of zero is 10 seconds. The timeout corresponding
	 * to a nonzero value N is 2^N minutes, ranging from 2^1(2 minutes to 2^14 =
	 * 16384 minutes.
	 */
	END_DEVICE_POLL_TIMEOUT(0x13),
	// 0x14?
	// 0x15?
	// 0x16?
	/**
	 * Enables boost power mode and/or the alternate transmitter output.
	 */
	TX_POWER_MODE(0x17),
	/**
	 * 0: Allow this node to relay messages. 1: Prevent this node from relaying
	 * messages.
	 */
	DISABLE_RELAY(0x18),
	/**
	 * The maximum number of EUI64 to network address associations that the
	 * Trust Center can maintain. These address cache entries are reserved for
	 * and reused by the Trust Center when processing device join/rejoin
	 * authentications. This cache size limits the number of overlapping joins
	 * the Trust Center can process within a narrow time window (e.g. two
	 * seconds), and thus should be set to the maximum number of near
	 * simultaneous joins the Trust Center is expected to accommodate. (Note,
	 * the total number of such address associations maintained by the NCP is
	 * the sum of the value of this setting and the value of
	 * ::ADDRESS_TABLE_SIZE.)
	 */
	TRUST_CENTER_ADDRESS_CACHE_SIZE(0x19),
	/**
	 * The size of the source route table.
	 */
	SOURCE_ROUTE_TABLE_SIZE(0x1a),
	// 0x1B?
	/**
	 * The number of blocks of a fragmented message that can be sent in a single
	 * window.
	 */
	FRAGMENT_WINDOW_SIZE(0x1c),
	/**
	 * The time the stack will wait (in milliseconds) between sending blocks of
	 * a fragmented message.
	 */
	FRAGMENT_DELAY_MS(0x1d),
	/**
	 * The size of the Key Table used for storing individual link keys (if the
	 * device is a Trust Center) or Application Link Keys (if the device is a
	 * normal node).
	 */
	KEY_TABLE_SIZE(0x1e),
	/**
	 * The APS ACK timeout value. The stack waits this amount of time between
	 * resends of APS retried messages.
	 */
	APS_ACK_TIMEOUT(0x1f),
	/**
	 * The duration of a beacon jitter, in the units used by the 15.4 scan
	 * parameter (((1 << duration) + 1) * 15ms), when responding to a beacon
	 * request.
	 */
	BEACON_JITTER_DURATION(0x20),
	// 0x21?
	/**
	 * The number of PAN id conflict reports that must be received by the
	 * network manager within one minute to trigger a PAN id change.
	 */
	PAN_ID_CONFLICT_REPORT_THRESHOLD(0x22),
	/**
	 * The timeout value in minutes for how long the Trust Center or a normal
	 * node waits for the Zigbee Request Key to complete. On the Trust Center
	 * this controls whether or not the device buffers the request, waiting for
	 * a matching pair of Zigbee Request Key. If the value is non-zero, the
	 * Trust Center buffers and waits for that amount of time. If the value is
	 * zero, the Trust Center does not buffer the request and immediately
	 * responds to the request. Zero is the most compliant behavior.
	 */
	REQUEST_KEY_TIMEOUT(0x24),
	// 0x25?
	// 0x26?
	// 0x27?
	// 0x28?
	/**
	 * This value indicates the size of the runtime modifiable certificate
	 * table. Normally certificates are stored in MFG tokens but this table can
	 * be used to field upgrade devices with new Smart Energy certificates. This
	 * value cannot be set, it can only be queried.
	 */
	CERTIFICATE_TABLE_SIZE(0x29),
	/**
	 * This is a bitmask that controls which incoming ZDO request messages are
	 * passed to the application. The bits are defined in the
	 * EmberZdoConfigurationFlags enumeration. To see if the application is
	 * required to send a ZDO response in reply to an incoming message, the
	 * application must check the APS options bitfield within the
	 * incomingMessageHandler callback to see if the
	 * EMBER_APS_OPTION_ZDO_RESPONSE_REQUIRED flag is set.
	 */
	APPLICATION_ZDO_FLAGS(0x2a),
	/**
	 * The maximum number of broadcasts during a single broadcast timeout
	 * period.
	 */
	BROADCAST_TABLE_SIZE(0x2b),
	/** The size of the MAC filter list table. */
	MAC_FILTER_TABLE_SIZE(0x2c),
	/** The number of supported networks. */
	SUPPORTED_NETWORKS(0x2d),
	/**
	 * Whether multicasts are sent to the RxOnWhenIdle=true address (0xFFFD) or
	 * the sleepy broadcast address (0xFFFF). The RxOnWhenIdle=true address is
	 * the Zigbee compliant destination for multicasts.
	 */
	SEND_MULTICASTS_TO_SLEEPY_ADDRESS(0x2e),
	/** ZLL group address initial configuration. */
	ZLL_GROUP_ADDRESSES(0x2f),
	/** ZLL rssi threshold initial configuration. */
	ZLL_RSSI_THRESHOLD(0x30),
	// 0x31?
	// 0x32?
	/** Toggles the MTORR flow control in the stack. */
	MTORR_FLOW_CONTROL(0x33),
	/**
	 * Setting the retry queue size. Applies to all queues. Default value in the
	 * sample applications is 16.
	 */
	RETRY_QUEUE_SIZE(0x34),
	/**
	 * Setting the new broadcast entry threshold. The number
	 * (BROADCAST_TABLE_SIZE - NEW_BROADCAST_ENTRY_THRESHOLD) of broadcast table
	 * entries are reserved for relaying the broadcast messages originated on
	 * other devices. The local device will fail to originate a broadcast
	 * message after this threshold is reached. Setting this value to
	 * BROADCAST_TABLE_SIZE and greater will effectively kill this limitation.
	 */
	NEW_BROADCAST_ENTRY_THRESHOLD(0x35),
	/**
	 * The length of time, in seconds, that a trust center will store a
	 * transient link key that a device can use to join its network. A transient
	 * key is added with a call to emberAddTransientLinkKey. After the transient
	 * key is added, it will be removed once this amount of time has passed. A
	 * joining device will not be able to use that key to join until it is added
	 * again on the trust center. The default value is 300 seconds, i.e., 5
	 * minutes.
	 */
	TRANSIENT_KEY_TIMEOUT_S(0x36),
	/**
	 * The number of passive acknowledgements to record from neighbors before we
	 * stop re-transmitting broadcasts
	 */
	BROADCAST_MIN_ACKS_NEEDED(0x37),
	/**
	 * The length of time, in seconds, that a trust center will allow a Trust
	 * Center (insecure) rejoin for a device that is using the well-known link
	 * key. This timeout takes effect once rejoins using the well-known key has
	 * been allowed. This command updates the
	 * sli_zigbee_allow_tc_rejoins_using_well_known_key_timeout_sec value.
	 */
	TC_REJOINS_USING_WELL_KNOWN_KEY_TIMEOUT_S(0x38),
	/**
	 * Valid range of a CTUNE value is 0x0000-0x01FF. Higher order bits (0xFE00)
	 * of the 16-bit value are ignored.
	 */
	CTUNE_VALUE(0x39),
	// 0x3A?
	// 0x3B?
	// 0x3C?
	// 0x3D?
	// 0x3E?
	// 0x3F?
	/**
	 * To configure non trust center node to assume a concentrator type of the
	 * trust center it join to, until it receive many-to-one route request from
	 * the trust center. For the trust center node, concentrator type is
	 * configured from the concentrator plugin. The stack by default assumes
	 * trust center be a low RAM concentrator that make other devices send route
	 * record to the trust center even without receiving a many-to-one route
	 * request. The default concentrator type can be changed by setting
	 * appropriate EmberAssumeTrustCenterConcentratorType config value.
	 */
	ASSUME_TC_CONCENTRATOR_TYPE(0x40),
	/**
	 * This is green power proxy table size. This value is read-only and cannot
	 * be set at runtime
	 */
	GP_PROXY_TABLE_SIZE(0x41),
	/**
	 * This is green power sink table size. This value is read-only and cannot
	 * be set at runtime
	 */
	GP_SINK_TABLE_SIZE(0x42),
	/**
	 * v14+ This is the configuration advertised by the end device to the parent
	 * when joining/rejoining, either SL_ZIGBEE_END_DEVICE_CONFIG_NONE or
	 * SL_ZIGBEE_END_DEVICE_CONFIG_PERSIST_DATA_ON_PARENT.
	 */
	END_DEVICE_CONFIGURATION(0x43);

	private final int val;

	EzspConfigId(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspConfigId valOfInt(int v)
	{
		for (EzspConfigId st : EzspConfigId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
