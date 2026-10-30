package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.SendRsp;
import org.iottree.driver.zigbee.ember.req_rsp.MsgRR;
import org.iottree.driver.zigbee.ember.req_rsp.SetCommonRR;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.EmberApsOption;
import org.iottree.driver.zigbee.ember.types.EmberTransmitPriority;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;

public class EZSP
{
	public static final int MESSAGE_TAG_MASK = 0x7f;

	public static final int ZA_MAX_HOPS = 12;

	EmberManager emgr;

	private int version;
	// private readonly buffalo: EzspBuffalo;
	// /** The contents of the current EZSP frame. CAREFUL using this guy, it's
	// pre-allocated. */
	// private readonly frameContents: Buffer;
	// /** The total Length of the incoming frame */
	// private frameLength: number;
	// private readonly callbackBuffalo: EzspBuffalo;
	// /** The contents of the current EZSP frame. CAREFUL using this guy, it's
	// pre-allocated. */
	// private readonly callbackFrameContents: Buffer;
	// /** The total Length of the incoming frame */
	// private callbackFrameLength: number;
	//
	// private initialVersionSent: boolean;

	/** Sequence used for EZSP send() tagging. static uint8_t */
	private int sendSequence = 0;

	// private readonly queue: AsyncMutex;
	/**
	 * Awaiting response resolve/timer struct. undefined if not waiting for
	 * response.
	 */
	// private responseWaiter?: EzspWaiter;

	/** Counter for Queue Full errors */
	// public counterErrQueueFull: number;

	EZSP(EmberManager emgr)
	{
		this.emgr = emgr;

		sendSequence = 0; // start at 1 ;
	}

	/**
	 *
	 * @returns uint8_t
	 */
	private int nextSendSequence()
	{
		this.sendSequence = ++this.sendSequence & MESSAGE_TAG_MASK;
		return this.sendSequence;
	}

	/**
	 * Calls ezspSend${x} based on type and takes care of tagging message.
	 *
	 * Alias types expect `alias` & `sequence` params, along with
	 * `apsFrame.radius`.
	 *
	 * @param type
	 *            Specifies the outgoing message type.
	 * @param indexOrDestination
	 *            uint16_t Depending on the type of addressing used, this is
	 *            either the NodeId of the destination, an index into the
	 *            address table, or an index into the binding table. Unused for
	 *            multicast types. This must be one of the three Zigbee
	 *            broadcast addresses for broadcast.
	 * @param apsFrame
	 *            [IN/OUT] EmberApsFrame * The APS frame which is to be added to
	 *            the message. Sequence set in OUT as returned by ezspSend${x}
	 *            command
	 * @param message
	 *            uint8_t * Content of the message.
	 * @param alias
	 *            The alias source address
	 * @param sequence
	 *            uint8_t The alias sequence number
	 * @throws Exception
	 * @returns Result of the ezspSend${x} call or EmberStatus.INVALID_PARAMETER
	 *          if type not supported.
	 * @returns messageTag Tag used for ezspSend${x} command
	 */
	public SendRsp send(OutgoingMsgType type, int idx_or_dst, ApsFrame apsf, byte[] message, int alias, // :
																										// NodeId,
			int sequence // : number,
	) throws Exception // : Promise<[SLStatus, messageTag: number]>
	{
		// SLStatus status = SLStatus.INVALID_PARAMETER;
		// int apsSequence;
		int messageTag = nextSendSequence();
		int nwkRadius = ZA_MAX_HOPS;
		int nwkAlias = ZSpecConsts.NULL_NODE_ID; // : NodeId

		SendRsp send_rsp = null;
		switch (type)
		{
		case VIA_BINDING:
		case VIA_ADDRESS_TABLE:
		case DIRECT:
		{
			send_rsp = MsgRR.ezspSendUnicast(emgr, type, idx_or_dst, apsf, messageTag, message);
			break;
		}
		case MULTICAST:
		case MULTICAST_WITH_ALIAS:
		{
			if (type == OutgoingMsgType.MULTICAST_WITH_ALIAS
					|| (apsf.src_ep == ZSpecConsts.GP_ENDPOINT && apsf.dst_ep == ZSpecConsts.GP_ENDPOINT
							&& (apsf.options & EmberApsOption.USE_ALIAS_SEQUENCE_NUMBER.getIntVal()) > 0))
			{
				if (apsf.radius != null)
					nwkRadius = apsf.radius;// ?? nwkRadius;
				nwkAlias = alias;
			}

			send_rsp = MsgRR.ezspSendMulticast(emgr, apsf, nwkRadius, 0, // broadcast
																			// addr
					nwkAlias, sequence, messageTag, message);
			break;
		}
		case BROADCAST:
		case BROADCAST_WITH_ALIAS:
		{
			if (type == OutgoingMsgType.BROADCAST_WITH_ALIAS
					|| (apsf.src_ep == ZSpecConsts.GP_ENDPOINT && apsf.dst_ep == ZSpecConsts.GP_ENDPOINT
							&& (apsf.options & EmberApsOption.USE_ALIAS_SEQUENCE_NUMBER.getIntVal()) > 0))
			{
				if (apsf.radius != null)
					nwkRadius = apsf.radius;// ?? nwkRadius;
				nwkAlias = alias;
			}

			send_rsp = MsgRR.ezspSendBroadcast(emgr, nwkAlias, idx_or_dst, sequence, apsf, nwkRadius, messageTag,
					message);
			break;
		}
		}

		if (send_rsp != null)
		{
			apsf.sequence = send_rsp.getSeq();
		}

		// NOTE: match `~~~>` from adapter since this is just a wrapper for it
		// logger.debug(
		// `~~~> [SENT type=${EmberOutgoingMessageType[type]}
		// apsSequence=${apsSequence} messageTag=${messageTag}
		// status=${SLStatus[status]}]`,
		// NS,
		// );
		return send_rsp;
	}

	/**
	 * This call sets the radio channel in the stack and propagates the
	 * information to the hardware.
	 * 
	 * @param radioChannel
	 *            uint8_t The radio channel to be set.
	 * @throws Exception 
	 * @returns An SLStatus value indicating success or the reason for failure.
	 */
	public SLStatus setLogicalAndRadioChannel(int radioChannel) throws Exception
	{
		byte[] bs = new byte[] { (byte) radioChannel };
		Common_Cmd cmd = new Common_Cmd(emgr, EzspFrameID.SET_LOGICAL_AND_RADIO_CHANNEL, bs);
		SetCommonRR rr = new SetCommonRR(emgr, cmd);
		if (!emgr.RT_synReqRsp(rr, null))
			return null;
		return rr.getSLStatus();
	}
	
	/**
     * Transmits the given message without modification. The MAC header is assumed
     * to be configured in the message at the time this function is called.
     * @param messageContents uint8_t * The raw message.
     * @param priority uint8_t transmit priority.
     * @param useCca Should we enable CCA or not.
	 * @throws Exception 
     * @returns An SLStatus value indicating success or the reason for failure.
     */
	public SLStatus sendRawMessage(byte[] msg_cont, EmberTransmitPriority priority, boolean useCca) throws Exception
    {
		int len = msg_cont.length+3 ;
		EZSPBuffer buf = new EZSPBuffer(new byte[len],0,len) ;
		buf.writePayload(msg_cont);
		buf.writeUInt8(priority.getIntVal());
		buf.writeUInt8(useCca ? 1 : 0);
		byte[] bs = buf.getWritten() ;
		Common_Cmd cmd = new Common_Cmd(emgr, EzspFrameID.SEND_RAW_MESSAGE, bs);
		SetCommonRR rr = new SetCommonRR(emgr, cmd);
		if (!emgr.RT_synReqRsp(rr, null))
			return null;
		return rr.getSLStatus();
    }
}
