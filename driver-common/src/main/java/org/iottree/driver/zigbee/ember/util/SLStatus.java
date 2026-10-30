package org.iottree.driver.zigbee.ember.util;

import java.util.Map;

public enum SLStatus
{
	    // -----------------------------------------------------------------------------
	    // Generic Errors

	    /** No error. */
	    OK(0x0000),
	    /** Generic error. */
	    FAIL(0x0001),

	    // -----------------------------------------------------------------------------
	    // State Errors

	    /** Generic invalid state error. */
	    INVALID_STATE(0x0002),
	    /** Module is not ready for requested operation. */
	    NOT_READY(0x0003),
	    /** Module is busy and cannot carry out requested operation. */
	    BUSY(0x0004),
	    /** Operation is in progress and not yet complete (pass or fail). */
	    IN_PROGRESS(0x0005),
	    /** Operation aborted. */
	    ABORT(0x0006),
	    /** Operation timed out. */
	    TIMEOUT(0x0007),
	    /** Operation not allowed per permissions. */
	    PERMISSION(0x0008),
	    /** Non-blocking operation would block. */
	    WOULD_BLOCK(0x0009),
	    /** Operation/module is Idle), cannot carry requested operation. */
	    IDLE(0x000a),
	    /** Operation cannot be done while construct is waiting. */
	    IS_WAITING(0x000b),
	    /** No task/construct waiting/pending for that action/event. */
	    NONE_WAITING(0x000c),
	    /** Operation cannot be done while construct is suspended. */
	    SUSPENDED(0x000d),
	    /** Feature not available due to software configuration. */
	    NOT_AVAILABLE(0x000e),
	    /** Feature not supported. */
	    NOT_SUPPORTED(0x000f),
	    /** Initialization failed. */
	    INITIALIZATION(0x0010),
	    /** Module has not been initialized. */
	    NOT_INITIALIZED(0x0011),
	    /** Module has already been initialized. */
	    ALREADY_INITIALIZED(0x0012),
	    /** Object/construct has been deleted. */
	    DELETED(0x0013),
	    /** Illegal call from ISR. */
	    ISR(0x0014),
	    /** Illegal call because network is up. */
	    NETWORK_UP(0x0015),
	    /** Illegal call because network is down. */
	    NETWORK_DOWN(0x0016),
	    /** Failure due to not being joined in a network. */
	    NOT_JOINED(0x0017),
	    /** Invalid operation as there are no beacons. */
	    NO_BEACONS(0x0018),

	    // -----------------------------------------------------------------------------
	    // Allocation/ownership Errors

	    /** Generic allocation error. */
	    ALLOCATION_FAILED(0x0019),
	    /** No more resource available to perform the operation. */
	    NO_MORE_RESOURCE(0x001a),
	    /** Item/list/queue is empty. */
	    EMPTY(0x001b),
	    /** Item/list/queue is full. */
	    FULL(0x001c),
	    /** Item would overflow. */
	    WOULD_OVERFLOW(0x001d),
	    /** Item/list/queue has been overflowed. */
	    HAS_OVERFLOWED(0x001e),
	    /** Generic ownership error. */
	    OWNERSHIP(0x001f),
	    /** Already/still owning resource. */
	    IS_OWNER(0x0020),

	    // -----------------------------------------------------------------------------
	    // Invalid Parameters Errors

	    /** Generic invalid argument or consequence of invalid argument. */
	    INVALID_PARAMETER(0x0021),
	    /** Invalid null pointer received as argument. */
	    NULL_POINTER(0x0022),
	    /** Invalid configuration provided. */
	    INVALID_CONFIGURATION(0x0023),
	    /** Invalid mode. */
	    INVALID_MODE(0x0024),
	    /** Invalid handle. */
	    INVALID_HANDLE(0x0025),
	    /** Invalid type for operation. */
	    INVALID_TYPE(0x0026),
	    /** Invalid index. */
	    INVALID_INDEX(0x0027),
	    /** Invalid range. */
	    INVALID_RANGE(0x0028),
	    /** Invalid key. */
	    INVALID_KEY(0x0029),
	    /** Invalid credentials. */
	    INVALID_CREDENTIALS(0x002a),
	    /** Invalid count. */
	    INVALID_COUNT(0x002b),
	    /** Invalid signature / verification failed. */
	    INVALID_SIGNATURE(0x002c),
	    /** Item could not be found. */
	    NOT_FOUND(0x002d),
	    /** Item already exists. */
	    ALREADY_EXISTS(0x002e),

	    // -----------------------------------------------------------------------------
	    // IO/Communication Errors

	    /** Generic I/O failure. */
	    IO(0x002f),
	    /** I/O failure due to timeout. */
	    IO_TIMEOUT(0x0030),
	    /** Generic transmission error. */
	    TRANSMIT(0x0031),
	    /** Transmit underflowed. */
	    TRANSMIT_UNDERFLOW(0x0032),
	    /** Transmit is incomplete. */
	    TRANSMIT_INCOMPLETE(0x0033),
	    /** Transmit is busy. */
	    TRANSMIT_BUSY(0x0034),
	    /** Generic reception error. */
	    RECEIVE(0x0035),
	    /** Failed to read on/via given object. */
	    OBJECT_READ(0x0036),
	    /** Failed to write on/via given object. */
	    OBJECT_WRITE(0x0037),
	    /** Message is too long. */
	    MESSAGE_TOO_LONG(0x0038),

	    // -----------------------------------------------------------------------------
	    // EEPROM/Flash Errors

	    /** EEPROM MFG version mismatch. */
	    EEPROM_MFG_VERSION_MISMATCH(0x0039),
	    /** EEPROM Stack version mismatch. */
	    EEPROM_STACK_VERSION_MISMATCH(0x003a),
	    /** Flash write is inhibited. */
	    FLASH_WRITE_INHIBITED(0x003b),
	    /** Flash verification failed. */
	    FLASH_VERIFY_FAILED(0x003c),
	    /** Flash programming failed. */
	    FLASH_PROGRAM_FAILED(0x003d),
	    /** Flash erase failed. */
	    FLASH_ERASE_FAILED(0x003e),

	    // -----------------------------------------------------------------------------
	    // MAC Errors

	    /** MAC no data. */
	    MAC_NO_DATA(0x003f),
	    /** MAC no ACK received. */
	    MAC_NO_ACK_RECEIVED(0x0040),
	    /** MAC indirect timeout. */
	    MAC_INDIRECT_TIMEOUT(0x0041),
	    /** MAC unknown header type. */
	    MAC_UNKNOWN_HEADER_TYPE(0x0042),
	    /** MAC ACK unknown header type. */
	    MAC_ACK_HEADER_TYPE(0x0043),
	    /** MAC command transmit failure. */
	    MAC_COMMAND_TRANSMIT_FAILURE(0x0044),

	    // -----------------------------------------------------------------------------
	    // CLI_STORAGE Errors

	    /** Error in open NVM */
	    CLI_STORAGE_NVM_OPEN_ERROR(0x0045),

	    // -----------------------------------------------------------------------------
	    // Security status codes

	    /** Image checksum is not valid. */
	    SECURITY_IMAGE_CHECKSUM_ERROR(0x0046),
	    /** Decryption failed */
	    SECURITY_DECRYPT_ERROR(0x0047),

	    // -----------------------------------------------------------------------------
	    // Command status codes

	    /** Command was not recognized */
	    COMMAND_IS_INVALID(0x0048),
	    /** Command or parameter maximum length exceeded */
	    COMMAND_TOO_LONG(0x0049),
	    /** Data received does not form a complete command */
	    COMMAND_INCOMPLETE(0x004a),

	    // -----------------------------------------------------------------------------
	    // Misc Errors

	    /** Bus error), e.g. invalid DMA address */
	    BUS_ERROR(0x004b),

	    // -----------------------------------------------------------------------------
	    // Unified MAC Errors

	    /** CCA failure. */
	    CCA_FAILURE(0x004c),

	    // -----------------------------------------------------------------------------
	    // Scan errors

	    /** MAC scanning. */
	    MAC_SCANNING(0x004d),
	    /** MAC incorrect scan type. */
	    MAC_INCORRECT_SCAN_TYPE(0x004e),
	    /** Invalid channel mask. */
	    INVALID_CHANNEL_MASK(0x004f),
	    /** Bad scan duration. */
	    BAD_SCAN_DURATION(0x0050),

	    // -----------------------------------------------------------------------------
	    // MAC transmit related status

	    /** The MAC transmit queue is full */
	    MAC_TRANSMIT_QUEUE_FULL(0x0053),
	    /**
	     * The transmit attempt failed because the radio scheduler could not find a slot to transmit this packet in or
	     * a higher priority event interrupted it
	     */
	    TRANSMIT_SCHEDULER_FAIL(0x0054),
	    /** An unsupported channel setting was specified */
	    TRANSMIT_INVALID_CHANNEL(0x0055),
	    /** An unsupported power setting was specified */
	    TRANSMIT_INVALID_POWER(0x0056),
	    /** The expected ACK was received after the last transmission */
	    TRANSMIT_ACK_RECEIVED(0x0057),
	    /**
	     * The transmit attempt was blocked from going over the air.
	     * Typically this is due to the Radio Hold Off (RHO) or Coexistence plugins as they can prevent transmits based on external signals.
	     */
	    TRANSMIT_BLOCKED(0x0058),

	    // -----------------------------------------------------------------------------
	    // NVM3 specific errors

	    /** The initialization was aborted as the NVM3 instance is not aligned properly in memory */
	    NVM3_ALIGNMENT_INVALID(0x0059),
	    /** The initialization was aborted as the size of the NVM3 instance is too small */
	    NVM3_SIZE_TOO_SMALL(0x005a),
	    /** The initialization was aborted as the NVM3 page size is not supported */
	    NVM3_PAGE_SIZE_NOT_SUPPORTED(0x005b),
	    /** The application that there was an error initializing some of the tokens */
	    NVM3_TOKEN_INIT_FAILED(0x005c),
	    /** The initialization was aborted as the NVM3 instance was already opened with other parameters */
	    NVM3_OPENED_WITH_OTHER_PARAMETERS(0x005d),

	    // -----------------------------------------------------------------------------
	    // MVP Driver and MVP Math status codes

	    /** Critical fault */
	    COMPUTE_DRIVER_FAULT(0x1501),
	    /** ALU operation output NaN */
	    COMPUTE_DRIVER_ALU_NAN(0x1502),
	    /** ALU numeric overflow */
	    COMPUTE_DRIVER_ALU_OVERFLOW(0x1503),
	    /** ALU numeric underflow */
	    COMPUTE_DRIVER_ALU_UNDERFLOW(0x1504),
	    /** Overflow during array store */
	    COMPUTE_DRIVER_STORE_CONVERSION_OVERFLOW(0x1505),
	    /** Underflow during array store conversion */
	    COMPUTE_DRIVER_STORE_CONVERSION_UNDERFLOW(0x1506),
	    /** Infinity encountered during array store conversion */
	    COMPUTE_DRIVER_STORE_CONVERSION_INFINITY(0x1507),
	    /** NaN encountered during array store conversion */
	    COMPUTE_DRIVER_STORE_CONVERSION_NAN(0x1508),

	    /** MATH NaN encountered */
	    COMPUTE_MATH_NAN(0x1512),
	    /** MATH Infinity encountered */
	    COMPUTE_MATH_INFINITY(0x1513),
	    /** MATH numeric overflow */
	    COMPUTE_MATH_OVERFLOW(0x1514),
	    /** MATH numeric underflow */
	    COMPUTE_MATH_UNDERFLOW(0x1515),

	    // -----------------------------------------------------------------------------
	    // Zigbee status codes

	    /** Packet is dropped by packet-handoff callbacks */
	    ZIGBEE_PACKET_HANDOFF_DROPPED(0x0c01),
	    /** The APS layer attempted to send or deliver a message and failed */
	    ZIGBEE_DELIVERY_FAILED(0x0c02),
	    /** The maximum number of in-flight messages ::EMBER_APS_UNICAST_MESSAGE_COUNT has been reached */
	    ZIGBEE_MAX_MESSAGE_LIMIT_REACHED(0x0c03),
	    /** The application is trying to delete or overwrite a binding that is in use */
	    ZIGBEE_BINDING_IS_ACTIVE(0x0c04),
	    /** The application is trying to overwrite an address table entry that is in use */
	    ZIGBEE_ADDRESS_TABLE_ENTRY_IS_ACTIVE(0x0c05),
	    /** After moving), a mobile node's attempt to re-establish contact with the network failed */
	    ZIGBEE_MOVE_FAILED(0x0c06),
	    /** The local node ID has changed. The application can get the new node ID by calling ::sl_zigbee_get_node_id() */
	    ZIGBEE_NODE_ID_CHANGED(0x0c07),
	    /** The chosen security level is not supported by the stack */
	    ZIGBEE_INVALID_SECURITY_LEVEL(0x0c08),
	    /** An error occurred when trying to encrypt at the APS Level */
	    ZIGBEE_IEEE_ADDRESS_DISCOVERY_IN_PROGRESS(0x0c09),
	    /** An error occurred when trying to encrypt at the APS Level */
	    ZIGBEE_APS_ENCRYPTION_ERROR(0x0c0a),
	    /** There was an attempt to form or join a network with security without calling ::sl_zigbee_set_initial_security_state() first */
	    ZIGBEE_SECURITY_STATE_NOT_SET(0x0c0b),
	    /**
	     * There was an attempt to broadcast a key switch too quickly after broadcasting the next network key.
	     * The Trust Center must wait at least a period equal to the broadcast timeout so that all routers have a chance
	     * to receive the broadcast of the new network key
	     */
	    ZIGBEE_TOO_SOON_FOR_SWITCH_KEY(0x0c0c),
	    /** The received signature corresponding to the message that was passed to the CBKE Library failed verification and is not valid */
	    ZIGBEE_SIGNATURE_VERIFY_FAILURE(0x0c0d),
	    /** The message could not be sent because the link key corresponding to the destination is not authorized for use in APS data messages */
	    ZIGBEE_KEY_NOT_AUTHORIZED(0x0c0e),
	    /** The application tried to use a binding that has been remotely modified and the change has not yet been reported to the application */
	    ZIGBEE_BINDING_HAS_CHANGED(0x0c0f),
	    /** The EUI of the Trust center has changed due to a successful rejoin after TC Swapout */
	    ZIGBEE_TRUST_CENTER_SWAP_EUI_HAS_CHANGED(0x0c10),
	    /** A Trust Center Swapout Rejoin has occurred without the EUI of the TC changing */
	    ZIGBEE_TRUST_CENTER_SWAP_EUI_HAS_NOT_CHANGED(0x0c11),
	    /** An attempt to generate random bytes failed because of insufficient random data from the radio */
	    ZIGBEE_INSUFFICIENT_RANDOM_DATA(0x0c12),
	    /** A Zigbee route error command frame was received indicating that a source routed message from this node failed en route */
	    ZIGBEE_SOURCE_ROUTE_FAILURE(0x0c13),
	    /** A Zigbee route error command frame was received indicating that a message sent to this node along a many-to-one route failed en route */
	    ZIGBEE_MANY_TO_ONE_ROUTE_FAILURE(0x0c14),
	    /** A critical and fatal error indicating that the version of the stack trying to run does not match with the chip it's running on */
	    ZIGBEE_STACK_AND_HARDWARE_MISMATCH(0x0c15),
	    /** The local PAN ID has changed. The application can get the new PAN ID by calling ::emberGetPanId() */
	    ZIGBEE_PAN_ID_CHANGED(0x0c16),
	    /** The channel has changed. */
	    ZIGBEE_CHANNEL_CHANGED(0x0c17),
	    /** The network has been opened for joining. */
	    ZIGBEE_NETWORK_OPENED(0x0c18),
	    /** The network has been closed for joining. */
	    ZIGBEE_NETWORK_CLOSED(0x0c19),
	    /**
	     * An attempt was made to join a Secured Network using a pre-configured key), but the Trust Center sent back a
	     * Network Key in-the-clear when an encrypted Network Key was required. (::EMBER_REQUIRE_ENCRYPTED_KEY)
	     */
	    ZIGBEE_RECEIVED_KEY_IN_THE_CLEAR(0x0c1a),
	    /** An attempt was made to join a Secured Network), but the device did not receive a Network Key. */
	    ZIGBEE_NO_NETWORK_KEY_RECEIVED(0x0c1b),
	    /** After a device joined a Secured Network), a Link Key was requested (::EMBER_GET_LINK_KEY_WHEN_JOINING) but no response was ever received. */
	    ZIGBEE_NO_LINK_KEY_RECEIVED(0x0c1c),
	    /**
	     * An attempt was made to join a Secured Network without a pre-configured key), but the Trust Center sent encrypted data using a
	     * pre-configured key.
	     */
	    ZIGBEE_PRECONFIGURED_KEY_REQUIRED(0x0c1d),
	    /** A Zigbee EZSP error has occured. Track the origin and corresponding EzspStatus for more info. */
	    ZIGBEE_EZSP_ERROR(0x0c1e),
	    /** Node ID discovery failed. */
	    ZIGBEE_ID_DISCOVERY_FAILED(0x0c1f),
	    /** Message was sent but no APS ACK received. */
	    ZIGBEE_NO_APS_ACK(0x0c20),
	    /** APS message was canceled. */
	    ZIGBEE_APS_MESSAGE_CANCELED(0x0c21),
	    /** Node ID discovery not enabled. */
	    ZIGBEE_ID_DISCOVERY_NOT_ENABLED(0x0c22),
	    /** Message was not sent), Node ID discovery is underway. */
	    ZIGBEE_ID_DISCOVERY_UNDERWAY(0x0c23),
	    /** The message was not sent because a route discovery is currently underway. There is no route to the target until the route discovery completes. */
	    ZIGBEE_SEND_UNICAST_ROUTE_DISCOVERY_UNDERWAY(0x0c24),
	    /** Radius is 0 or message has been dropped because route request failed or failed to submit message to tx queue. */
	    ZIGBEE_SEND_UNICAST_FAILURE(0x0c25),
	    /** No active route to the destination. */
	    ZIGBEE_SEND_UNICAST_NO_ROUTE(0x0c26),
	    /** Broadcast message timeout while waiting for sleepy children to poll. */
	    ZIGBEE_BROADCAST_TO_SLEEPY_CHILDREN_TIMEOUT(0x0c27),
	    /** Expected a neighbor to relay the message, but none did. */
	    ZIGBEE_BROADCAST_RELAY_FAILED (0x0c28);

	    private final int val;
		//private final String title;

		SLStatus(int val) //, String title)
		{
			this.val = val;
		}
		
		public int getIntVal()
		{
			return this.val ;
		}
		
		public boolean isOk()
		{
			return this==OK;
		}
//		public String getTitle()
//		{
//			return this.title ;
//		}
		
		public static SLStatus valOfInt(int v)
		{
			for(SLStatus st:SLStatus.values())
			{
				if(st.val==v)
					return st ;
			}
			return null ;
		}
		
		
		public static final Map<Integer,SLStatus> EMBER_TO_SL_STATUS_MAP = Map.ofEntries(
			   Map.entry(0x02 /*BAD_ARGUMENT*/, SLStatus.INVALID_PARAMETER),
			   Map.entry(0x18 /*NO_BUFFERS*/, SLStatus.ALLOCATION_FAILED),
			   Map.entry(0x19 /*PACKET_HANDOFF_DROP_PACKET*/, SLStatus.ZIGBEE_PACKET_HANDOFF_DROPPED),
			   Map.entry(0x66 /*DELIVERY_FAILED*/, SLStatus.ZIGBEE_DELIVERY_FAILED),
			   Map.entry(0x70 /*INVALID_CALL*/, SLStatus.INVALID_STATE),
			   Map.entry(0x72 /*MAX_MESSAGE_LIMIT_REACHED*/, SLStatus.ZIGBEE_MAX_MESSAGE_LIMIT_REACHED),
			   Map.entry(0x74 /*MESSAGE_TOO_LONG*/, SLStatus.MESSAGE_TOO_LONG),
			   Map.entry(0x75 /*BINDING_IS_ACTIVE*/, SLStatus.ZIGBEE_BINDING_IS_ACTIVE),
			   Map.entry(0x76 /*ADDRESS_TABLE_ENTRY_IS_ACTIVE*/, SLStatus.ZIGBEE_ADDRESS_TABLE_ENTRY_IS_ACTIVE),
			   Map.entry(0x90 /*NETWORK_UP*/, SLStatus.NETWORK_UP),
			   Map.entry(0x91 /*NETWORK_DOWN*/, SLStatus.NETWORK_DOWN),
			   Map.entry(0x93 /*NOT_JOINED*/, SLStatus.NOT_JOINED),
			   Map.entry(0x95 /*INVALID_SECURITY_LEVEL*/, SLStatus.ZIGBEE_INVALID_SECURITY_LEVEL),
			   Map.entry(0x96 /*MOVE_FAILED*/, SLStatus.ZIGBEE_MOVE_FAILED),
			   Map.entry(0x99 /*NODE_ID_CHANGED*/, SLStatus.ZIGBEE_NODE_ID_CHANGED),
			   Map.entry(0x9a /*PAN_ID_CHANGED*/, SLStatus.ZIGBEE_PAN_ID_CHANGED),
			   Map.entry(0x9b /*CHANNEL_CHANGED*/, SLStatus.ZIGBEE_CHANNEL_CHANGED),
			   Map.entry(0x9c /*NETWORK_OPENED*/, SLStatus.ZIGBEE_NETWORK_OPENED),
			   Map.entry(0x9d /*NETWORK_CLOSED*/, SLStatus.ZIGBEE_NETWORK_CLOSED),
			   Map.entry(0xa1 /*NETWORK_BUSY*/, SLStatus.BUSY),
			   Map.entry(0xa4 /*BINDING_HAS_CHANGED*/, SLStatus.ZIGBEE_BINDING_HAS_CHANGED),
			   Map.entry(0xa5 /*INSUFFICIENT_RANDOM_DATA*/, SLStatus.ZIGBEE_INSUFFICIENT_RANDOM_DATA),
			   Map.entry(0xa6 /*APS_ENCRYPTION_ERROR*/, SLStatus.ZIGBEE_APS_ENCRYPTION_ERROR),
			   Map.entry(0xa9 /*SOURCE_ROUTE_FAILURE*/, SLStatus.ZIGBEE_SOURCE_ROUTE_FAILURE),
			   Map.entry(0xa8 /*SECURITY_STATE_NOT_SET*/, SLStatus.ZIGBEE_SECURITY_STATE_NOT_SET),
			   Map.entry(0xaa /*MANY_TO_ONE_ROUTE_FAILURE*/, SLStatus.ZIGBEE_MANY_TO_ONE_ROUTE_FAILURE),
			   Map.entry(0xac /*RECEIVED_KEY_IN_THE_CLEAR*/, SLStatus.ZIGBEE_RECEIVED_KEY_IN_THE_CLEAR),
			   Map.entry(0xad /*NO_NETWORK_KEY_RECEIVED*/, SLStatus.ZIGBEE_NO_NETWORK_KEY_RECEIVED),
			   Map.entry(0xae /*NO_LINK_KEY_RECEIVED*/, SLStatus.ZIGBEE_NO_LINK_KEY_RECEIVED),
			   Map.entry(0xaf /*PRECONFIGURED_KEY_REQUIRED*/, SLStatus.ZIGBEE_PRECONFIGURED_KEY_REQUIRED),
			   Map.entry(0xb0 /*STACK_AND_HARDWARE_MISMATCH*/, SLStatus.ZIGBEE_STACK_AND_HARDWARE_MISMATCH),
			   Map.entry(0xb5 /*LIBRARY_NOT_PRESENT*/, SLStatus.NOT_AVAILABLE),
			   Map.entry(0xb8 /*TOO_SOON_FOR_SWITCH_KEY*/, SLStatus.ZIGBEE_TOO_SOON_FOR_SWITCH_KEY),
			   Map.entry(0xb9 /*SIGNATURE_VERIFY_FAILURE*/, SLStatus.ZIGBEE_SIGNATURE_VERIFY_FAILURE),
			   Map.entry(0xbb /*KEY_NOT_AUTHORIZED*/, SLStatus.ZIGBEE_KEY_NOT_AUTHORIZED),
			   Map.entry(0xbc /*TRUST_CENTER_EUI_HAS_CHANGED*/, SLStatus.ZIGBEE_TRUST_CENTER_SWAP_EUI_HAS_CHANGED),
			   Map.entry(0xbe /*IEEE_ADDRESS_DISCOVERY_IN_PROGRESS*/, SLStatus.ZIGBEE_IEEE_ADDRESS_DISCOVERY_IN_PROGRESS),
			   Map.entry(0xbf /*TRUST_CENTER_SWAPPED_OUT_EUI_HAS_NOT_CHANGED*/, SLStatus.ZIGBEE_TRUST_CENTER_SWAP_EUI_HAS_NOT_CHANGED)
			   );
}




