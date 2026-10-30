package org.iottree.driver.zigbee.ember.types;

public enum EmberCounterType
{
	/**
     * The MAC received a broadcast Data frame, Command frame, or Beacon.
     * - destinationNodeId: BROADCAST_ADDRESS or Data frames or sender node ID for Beacon frames
     * - data: not used
     */
    MAC_RX_BROADCAST(0),
    /**
     * The MAC transmitted a broadcast Data frame, Command frame or Beacon.
     * - destinationNodeId: BROADCAST_ADDRESS
     * - data: not used
     */
    MAC_TX_BROADCAST(1),
    /**
     * The MAC received a unicast Data or Command frame.
     * - destinationNodeId: MAC layer source or EMBER_UNKNOWN_NODE_ID if no 16-bit source node ID is present in the frame
     * - data: not used
     */
    MAC_RX_UNICAST(2),
    /**
     * The MAC successfully transmitted a unicast Data or Command frame.
     *   Note: Only frames with a 16-bit destination node ID are counted.
     * - destinationNodeId: MAC layer destination address
     * - data: not used
     */
    MAC_TX_UNICAST_SUCCESS(3),
    /**
     * The MAC retried a unicast Data or Command frame after initial Tx attempt.
     *   Note: CSMA-related failures are tracked separately via PHY_CCA_FAIL_COUNT.
     * - destinationNodeId: MAC layer destination or EMBER_UNKNOWN_NODE_ID if no 16-bit destination node ID is present in the frame
     * - data: number of retries (after initial Tx attempt) accumulated so far for this packet. (Should always be >0.)
     */
    MAC_TX_UNICAST_RETRY(4),
    /**
     * The MAC unsuccessfully transmitted a unicast Data or Command frame.
     *   Note: Only frames with a 16-bit destination node ID are counted.
     * - destinationNodeId: MAC layer destination address
     * - data: not used
     */
    MAC_TX_UNICAST_FAILED(5),
    /**
     * The APS layer received a data broadcast.
     * - destinationNodeId: sender's node ID
     * - data: not used
     */
    APS_DATA_RX_BROADCAST(6),
    /** The APS layer transmitted a data broadcast. */
    APS_DATA_TX_BROADCAST(7),
    /**
     * The APS layer received a data unicast.
     * - destinationNodeId: sender's node ID
     * - data: not used
     */
    APS_DATA_RX_UNICAST(8),
    /**
     * The APS layer successfully transmitted a data unicast.
     * - destinationNodeId: NWK destination address
     * - data: number of APS retries (>=0) consumed for this unicast.
     */
    APS_DATA_TX_UNICAST_SUCCESS(9),
    /**
     * The APS layer retried a unicast Data frame.
     * This is a placeholder and is not used by the @c ::emberCounterHandler() callback.
     * Instead, the number of APS retries are returned in the data parameter of the callback
     * for the @c ::APS_DATA_TX_UNICAST_SUCCESS and @c ::APS_DATA_TX_UNICAST_FAILED types.
     * However, our supplied Counters component code will attempt to collect this information
     * from the aforementioned counters and populate this counter.
     * Note that this counter's behavior differs from that of @c ::MAC_TX_UNICAST_RETRY .
     */
    APS_DATA_TX_UNICAST_RETRY(10),
    /**
     * The APS layer unsuccessfully transmitted a data unicast.
     * - destinationNodeId: NWK destination address
     * - data: number of APS retries (>=0) consumed for this unicast.
     */
    APS_DATA_TX_UNICAST_FAILED(11),
    /** The network layer successfully submitted a new route discovery to the MAC. */
    ROUTE_DISCOVERY_INITIATED(12),
    /** An entry was added to the neighbor table. */
    NEIGHBOR_ADDED(13),
    /** An entry was removed from the neighbor table. */
    NEIGHBOR_REMOVED(14),
    /** A neighbor table entry became stale because it had not been heard from. */
    NEIGHBOR_STALE(15),
    /**
     * A node joined or rejoined to the network via this node.
     * - destinationNodeId: node ID of child
     * - data: not used
     */
    JOIN_INDICATION(16),
    /**
     * An entry was removed from the child table.
     * - destinationNodeId: node ID of child
     * - data: not used
     */
    CHILD_REMOVED(17),
    /** EZSP-UART only. An overflow error occurred in the UART. */
    ASH_OVERFLOW_ERROR(18),
    /** EZSP-UART only. A framing error occurred in the UART. */
    ASH_FRAMING_ERROR(19),
    /** EZSP-UART only. An overrun error occurred in the UART. */
    ASH_OVERRUN_ERROR(20),
    /** A message was dropped at the Network layer because the NWK frame counter was not higher than the last message seen from that source. */
    NWK_FRAME_COUNTER_FAILURE(21),
    /**
     * A message was dropped at the APS layer because the APS frame counter was not higher than the last message seen from that source.
     * - destinationNodeId: node ID of MAC source that relayed the message
     * - data: not used
     */
    APS_FRAME_COUNTER_FAILURE(22),
    /** EZSP-UART only. An XOFF was transmitted by the UART. */
    ASH_XOFF(23),
    /**
     * An encrypted message was dropped by the APS layer because the sender's key has not been authenticated.
     * As a result, the key is not authorized for use in APS data messages.
     * - destinationNodeId: EMBER_NULL_NODE_ID
     * - data: APS key table index related to the sender
     */
    APS_LINK_KEY_NOT_AUTHORIZED(24),
    /**
     * A NWK encrypted message was received but dropped because decryption failed.
     * - destinationNodeId: sender of the dropped packet
     * - data: not used
     */
    NWK_DECRYPTION_FAILURE(25),
    /**
     * An APS encrypted message was received but dropped because decryption failed.
     * - destinationNodeId: sender of the dropped packet
     * - data: not used
     */
    APS_DECRYPTION_FAILURE(26),
    /**
     * The number of failures to allocate a set of linked packet buffers.
     * This doesn't necessarily mean that the packet buffer count was 0 at the time,
     * but that the number requested was greater than the number free.
     */
    ALLOCATE_PACKET_BUFFER_FAILURE(27),
    /**
     * The number of relayed unicast packets.
     * - destinationId: NWK layer destination address of relayed packet
     * - data: not used
     */
    RELAYED_UNICAST(28),
    /**
     * The number of times a packet was dropped due to reaching the preset PHY-to-MAC queue limit (sli_mac_phy_to_mac_queue_length).
     * The limit will determine how many messages are accepted by the PHY between calls to emberTick().
     * After that limit is reached, packets will be dropped. The counter records the number of dropped packets.
     *
     * NOTE: For each call to emberCounterHandler() there may be more than 1 packet that was dropped due to the limit reached.
     * The actual number of packets dropped will be returned in the 'data' parameter passed to that function.
     *
     * - destinationNodeId: not used
     * - data: number of dropped packets represented by this counter event
     * - phyIndex: present
     */
    PHY_TO_MAC_QUEUE_LIMIT_REACHED(29),
    /**
     * The number of times a packet was dropped due to the packet-validate library checking a packet
     * and rejecting it due to length or other formatting problems.
     * - destinationNodeId: not used
     * - data: type of validation condition that failed
     */
    PACKET_VALIDATE_LIBRARY_DROPPED_COUNT(30),
    /**
     * The number of times the NWK retry queue is full and a new message failed to be added.
     * - destinationNodeId; not used
     * - data: NWK retry queue size that has been exceeded
     */
    TYPE_NWK_RETRY_OVERFLOW(31),
    /**
     * The number of times the PHY layer was unable to transmit due to a failed CCA (Clear Channel Assessment) attempt.
     * See also: MAC_TX_UNICAST_RETRY.
     * - destinationNodeId: MAC layer destination or EMBER_UNKNOWN_NODE_ID if no 16-bit destination node ID is present in the frame
     * - data: not used
     */
    PHY_CCA_FAIL_COUNT(32),
    /** The number of times a NWK broadcast was dropped because the broadcast table was full. */
    BROADCAST_TABLE_FULL(33),
    /** The number of times a low-priority packet traffic arbitration request has been made. */
    PTA_LO_PRI_REQUESTED(34),
    /** The number of times a high-priority packet traffic arbitration request has been made. */
    PTA_HI_PRI_REQUESTED(35),
    /** The number of times a low-priority packet traffic arbitration request has been denied. */
    PTA_LO_PRI_DENIED(36),
    /** The number of times a high-priority packet traffic arbitration request has been denied. */
    PTA_HI_PRI_DENIED(37),
    /** The number of times a low-priority packet traffic arbitration transmission has been aborted. */
    PTA_LO_PRI_TX_ABORTED(38),
    /** The number of times a high-priority packet traffic arbitration transmission has been aborted. */
    PTA_HI_PRI_TX_ABORTED(39),
    /** The number of times an address conflict has caused node_id change, and an address conflict error is sent. */
    ADDRESS_CONFLICT_SENT(40),
    /** The number of times CSL failed to schedule Rx on target */
    CSL_RX_SCHEDULE_FAILED(41),
    /** A placeholder giving the number of Ember counter types. */
    COUNT(42);

	private final int val;

	EmberCounterType(int val)
	{
		this.val= val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberCounterType valOfInt(int v)
	{
		for (EmberCounterType st : EmberCounterType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
