package org.iottree.driver.zigbee.ember;

import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.ember.ASHFrmData;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EZSPWaiter;
import org.iottree.driver.zigbee.ember.EmberCoor;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.*;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterIDS;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLManager;
import org.iottree.driver.zigbee.zcl.defs.ManufacturerCode;
import org.iottree.driver.zigbee.zspec.ZDOConsts;
import org.iottree.driver.zigbee.zspec.ZSController;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;
import org.iottree.driver.zigbee.zspec.event.IncomeMsgDefEvt;
import org.iottree.driver.zigbee.zspec.event.PermitJoinEvt;
import org.iottree.driver.zigbee.zspec.event.ZDORspEvt;

/**
 * 统一处理EmberCoor中的onXXX消息 EmberCoor中的，各种回调消息
 * 
 * 此handle处理处理 synReqRsp和synWaiter之外的其他所有 Callback 消息
 * @author jason.zhu
 */
public class EZSPWaiterEvt extends EZSPWaiter
{
	static ILogger log = LoggerManager.getLogger(EZSPWaiterEvt.class) ;
	
	EmberManager mgr ;
	
	// EmberEvtHandle evtHandle ;
	
	EzspFrameID ezspFrmID = null  ;
	
	//String evtName ;
	
	public EZSPWaiterEvt(EmberManager mgr,EzspFrameID ezsp_fid,long to_ms)
	{
		super(to_ms);
		this.mgr = mgr;
		this.ezspFrmID = ezsp_fid ;
		//this.evtName = evt_name ;
		
		//this.evtHandle = this.emgr.getEvtHandle() ;
	}
	

//	private ILogger getLog()
//	{
//		if(log!=null)
//			return log ;
//		
//		log = LoggerManager.getLogger("EmberEventCB_" + mgr.getBelongToCh().getName());
//		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
//		return log ;
//	}
	
	public EzspFrameID getWaitEzspFrameID()
	{
		return this.ezspFrmID ;
	}
	
//	public String getEvtName()
//	{
//		return this.evtName ;
//	}
	
	@Override
	public final boolean RT_chkRsp(ASHFrmData fd,EZSPRsp rsp)
	{
		int frm_id = fd.getEZSPFrmIDUnpacked() ;
		if(ezspFrmID!=null && frm_id!=ezspFrmID.getIntVal())
			return false;
		EZSPBuffer databuf = fd.getEZSPFrmBufUnpacked() ;
		return this.RT_onEZSPFrmCallback(frm_id, databuf, rsp) ;
	}
	
	public final void emit(String evt_n, ZSController.IEvent evt_v)
	{
		this.mgr.getCoor().RT_fireEvent(evt_n, evt_v);
	}
	
	final boolean RT_onEZSPFrmCallback(int frm_id, EZSPBuffer databuf, EZSPRsp frm)
	{
		EzspFrameID fid = EzspFrameID.valOfInt(frm_id);
		if (fid == null)
		{
			log.warn("unknown callback frame id=" + frm_id);
			return false;
		}
		int ver = this.mgr.getEZSPVersion();
		switch (fid)
		{
		case NO_CALLBACKS:
		{
			log.debug("no callback called");
			break;
		}
		case STACK_TOKEN_CHANGED_HANDLER:
		{
			int tokenAddress = databuf.readUInt16();
			log.debug("ezspStackTokenChangedHandler: tokenAddress=" + tokenAddress);
			break;
		}
		case TIMER_HANDLER:
		{
			int timerId = databuf.readUInt8();
			log.debug("ezspTimerHandler: timerId=" + timerId);
			break;
		}
		case COUNTER_ROLLOVER_HANDLER:
		{
			EmberCounterType ect = EmberCounterType.valOfInt(databuf.readUInt8());
			if (log.isDebugEnabled())
				log.debug("ezspCounterRolloverHandler: type=" + ect);
			log.info("NCP Counter " + ect + " rolled over");
			break;
		}
		case MUX_INVALID_RX_HANDLER:
		{
			if (ver >= 0x11)
			{
				int newRxChannel = databuf.readUInt8();
				int oldRxChannel = databuf.readUInt8();
				if (log.isDebugEnabled())
					log.debug(
							"ezspMuxInvalidRxHandler: newRxChannel=" + newRxChannel + " oldRxChannel=" + oldRxChannel);
			}
			break;
		}
		case CUSTOM_FRAME_HANDLER:
		{
			byte[] payload = databuf.readPayload();
			if (log.isDebugEnabled())
				log.debug("ezspCustomFrameHandler: payload=" + Convert.byteArray2HexStr(payload, "-"));
			break;
		}
		case STACK_STATUS_HANDLER:
		{
			SLStatus status = databuf.readStatus(ver);
			if (log.isDebugEnabled())
				log.debug("ezspStackStatusHandler: status=" + status);
			return this.ezspStackStatusHandler(status);
			//this.mgr.getCoor().RT_fireEvent(ZSController.STACK_STATUS, new PermitJoinEvt(status.getIntVal()));
		}
		case ENERGY_SCAN_RESULT_HANDLER:
		{
			int channel = databuf.readUInt8();
			int maxRssiValue = databuf.readInt8();
			if (log.isDebugEnabled())
				log.debug("ezspEnergyScanResultHandler: channel=" + channel + " maxRssiValue=" + maxRssiValue);
			log.info("Energy scan for channel " + channel + " reports max RSSI value at " + maxRssiValue + " dBm.");
			break;
		}
		case NETWORK_FOUND_HANDLER:
		{
			EmberZigbeeNetwork networkFound = databuf.readEmberZigbeeNetwork();
			int lastHopLqi = databuf.readUInt8();
			int lastHopRssi = databuf.readInt8();
			if (log.isDebugEnabled())
				log.debug("ezspNetworkFoundHandler: networkFound=" + networkFound.toJO() + " lastHopLqi=" + lastHopLqi
						+ " lastHopRssi=" + lastHopRssi);
			break;
		}
		case SCAN_COMPLETE_HANDLER:
		{
			int channel = databuf.readUInt8();
			SLStatus status = databuf.readStatus(ver);
			if (log.isDebugEnabled())
				log.debug("ezspScanCompleteHandler: channel=" + channel + " status=" + status);
			break;
		}
		case UNUSED_PAN_ID_FOUND_HANDLER:
		{
			int panId = databuf.readUInt16();
			int channel = databuf.readUInt8();
			if (log.isDebugEnabled())
				log.debug("ezspUnusedPanIdFoundHandler: panId=" + panId + " channel=" + channel);
			break;
		}
		case CHILD_JOIN_HANDLER:
		{
			int index = databuf.readUInt8();
			boolean joining = databuf.readUInt8() != 0;
			int childId = databuf.readUInt16();
			String childEui64 = databuf.readIEEEAddr();
			EmberNodeType childType = EmberNodeType.valOfInt(databuf.readUInt8());
			if (log.isDebugEnabled())
				log.debug("ezspChildJoinHandler: index=" + index + " joining=" + joining + " childId=" + childId
						+ " childEui64=" + childEui64 + " childType=" + childType);
			break;
		}
		case DUTY_CYCLE_HANDLER:
		{
			int channelPage = databuf.readUInt8();
			int channel = databuf.readUInt8();
			DutyCycleState state = DutyCycleState.valOfInt(databuf.readUInt8());
			int totalDevices = databuf.readUInt8();
			PerDeviceDutyCycle[] arrayOfDeviceDutyCycles = databuf.readPerDeviceDutyCycle();
			if (log.isDebugEnabled())
				log.debug("ezspDutyCycleHandler: channelPage=" + channelPage + " channel=" + channel + " state=" + state
						+ " totalDevices=" + totalDevices + " arrayOfDeviceDutyCycles=" + arrayOfDeviceDutyCycles);
			break;
		}
		case REMOTE_SET_BINDING_HANDLER:
		{
			BindingTableEntry entry = databuf.readBindingTableEntry();
			int index = databuf.readUInt8();
			SLStatus policyDecision = databuf.readStatus(ver);
			if (log.isDebugEnabled())
				log.debug("ezspRemoteSetBindingHandler: entry=" + entry.toJO() + " index=" + index + " policyDecision="
						+ policyDecision);
			break;
		}
		case REMOTE_DELETE_BINDING_HANDLER:
		{
			int index = databuf.readUInt8();
			SLStatus policyDecision = databuf.readStatus(ver);
			if (log.isDebugEnabled())
				log.debug("ezspRemoteDeleteBindingHandler: index=" + index + " policyDecision=" + policyDecision);
			break;
		}
		case MESSAGE_SENT_HANDLER:
		{
			if (ver < 0x0e)
			{
				OutgoingMsgType type = OutgoingMsgType.valOfInt(databuf.readUInt8());
				int indexOrDestination = databuf.readUInt16();
				ApsFrame apsFrame = databuf.readEmberApsFrame();
				int messageTag = databuf.readUInt8();
				SLStatus status = databuf.readStatus(ver);
				// EzspPolicyId.MESSAGE_CONTENTS_IN_CALLBACK_POLICY set to
				// messageTag only, so skip parsing entirely
				// const messageContents = databuf.readPayload();

				this.ezspMessageSentHandler(status, type, indexOrDestination, apsFrame, messageTag, null);
			}
			else
			{
				SLStatus status = databuf.readStatus(ver);
				OutgoingMsgType type = OutgoingMsgType.valOfInt(databuf.readUInt8());
				int indexOrDestination = databuf.readUInt16();
				ApsFrame apsFrame = databuf.readEmberApsFrame();
				int messageTag = databuf.readUInt16();
				// EzspPolicyId.MESSAGE_CONTENTS_IN_CALLBACK_POLICY set to
				// messageTag only, so skip parsing entirely
				// const messageContents = databuf.readPayload();

				this.ezspMessageSentHandler(status, type, indexOrDestination, apsFrame, messageTag, null);
			}
			break;
		}
		case POLL_COMPLETE_HANDLER:
		{
			SLStatus status = databuf.readStatus(ver);
			if (log.isDebugEnabled())
				log.debug("ezspPollCompleteHandler: status=" + status);
			break;
		}
		case POLL_HANDLER:
		{
			int childId = databuf.readUInt16();
			boolean transmitExpected = databuf.readUInt8() != 0;
			if (log.isDebugEnabled())
				log.debug("ezspPollHandler:  childId=" + childId + " transmitExpected=" + transmitExpected);
			break;
		}
		case INCOMING_MESSAGE_HANDLER:
		{
			if (mgr.getEZSPVersion() < 0x0e)
			{
				RxPacketInfo packetInfo = new RxPacketInfo();
				IncomeMsgTp type = IncomeMsgTp.valOfInt(databuf.readUInt8());
				ApsFrame apsFrame = databuf.readEmberApsFrame();
				packetInfo.lastHopLqi = databuf.readUInt8();
				packetInfo.lastHopRssi = databuf.readInt8();
				packetInfo.senderShortAddr = databuf.readUInt16();
				packetInfo.bindingIdx = databuf.readUInt8();
				packetInfo.addrIdx = databuf.readUInt8();

				byte[] msg_pld = databuf.readPayload();
				this.ezspIncomeMsgHandle(type, apsFrame, packetInfo, msg_pld);
			}
			else
			{
				IncomeMsgTp type = IncomeMsgTp.valOfInt(databuf.readUInt8());
				ApsFrame apsFrame = databuf.readEmberApsFrame();
				RxPacketInfo packetInfo = databuf.readRxPacketInfo();
				byte[] msg_pld = databuf.readPayload();
				this.ezspIncomeMsgHandle(type, apsFrame, packetInfo, msg_pld);
			}
			break;
		}
		case INCOMING_MANY_TO_ONE_ROUTE_REQUEST_HANDLER: {
            int source = databuf.readUInt16();
            String ieee = databuf.readIEEEAddr() ;
            int cost = databuf.readUInt8();
            if(log.isDebugEnabled())
            	log.debug("ezspIncomingManyToOneRouteRequestHandler: source="+source+" ieee="+ieee+" cost="+cost);
            break;
        }
        case INCOMING_ROUTE_ERROR_HANDLER: {
            SLStatus status = databuf.readStatus(ver);
            int target = databuf.readUInt16();
            if(log.isDebugEnabled())
            	log.debug("ezspIncomingRouteErrorHandler: status="+status+" target="+target);
            break;
        }
        case INCOMING_NETWORK_STATUS_HANDLER: {
        	StackError errorCode = StackError.valOfInt(databuf.readUInt8());
            int target = databuf.readUInt16();
            if(log.isDebugEnabled())
            	log.debug("ezspIncomingNetworkStatusHandler: errorCode="+errorCode+" target="+target);
            break;
        }
        case INCOMING_ROUTE_RECORD_HANDLER: {
            int source = databuf.readUInt16();
            String sourceEui = databuf.readIEEEAddr();
            int lastHopLqi = databuf.readUInt8();
            int lastHopRssi = databuf.readInt8();
            int relayCount = databuf.readUInt8();
            List<Integer> relayList = databuf.readListUInt16(relayCount); //databuf.readListUInt8(relayCount * 2);
            if(log.isDebugEnabled())
            	log.debug("ezspIncomingRouteRecordHandler: source="+source+" sourceEui="+sourceEui+" lastHopLqi="+lastHopLqi+" lastHopRssi="+lastHopRssi+" relayCount="+relayCount+" relayList="+relayList);
            break;
        }
        case ID_CONFLICT_HANDLER: {
            int id = databuf.readUInt16();
            if(log.isDebugEnabled())
            	log.debug("ezspIdConflictHandler: id="+id);
            log.warn("An ID conflict was detected for network address '"+id+"'. Corresponding devices kicked from the network.");
            break;
        }
        case MAC_PASSTHROUGH_MESSAGE_HANDLER: {
            if (ver < 0x0e) {
            	MacPassthroughType messageType = MacPassthroughType.valOfInt(databuf.readUInt8());
                int lastHopLqi = databuf.readUInt8();
                int lastHopRssi = databuf.readInt8();
                RxPacketInfo packetInfo = new RxPacketInfo() ;
                packetInfo.senderShortAddr = ZSpecConsts.NULL_NODE_ID;
                packetInfo.senderIEEEAddr = null;
                packetInfo.bindingIdx = ZSpecConsts.NULL_BINDING;
                packetInfo.addrIdx = 0xff ;
                packetInfo.lastHopLqi =lastHopLqi;
                packetInfo.lastHopRssi =lastHopRssi;
                packetInfo.lastHopTimestamp= 0 ;
                byte[] msg_cont = databuf.readPayload();
                if(log.isDebugEnabled())
                	log.debug("ezspMacPassthroughMessageHandler: messageType="+messageType+" packetInfo="+packetInfo.toJO()+" pld="+Convert.byteArray2HexStr(msg_cont, "-"));
            } else {
            	MacPassthroughType messageType = MacPassthroughType.valOfInt(databuf.readUInt8());
            	RxPacketInfo packetInfo = databuf.readRxPacketInfo() ;
            	byte[] msg_cont = databuf.readPayload();
            	if(log.isDebugEnabled())
                	log.debug("ezspMacPassthroughMessageHandler: messageType="+messageType+" packetInfo="+packetInfo.toJO()+" pld="+Convert.byteArray2HexStr(msg_cont, "-"));
            }
            break;
        }
        case MAC_FILTER_MATCH_MESSAGE_HANDLER: {
            if (ver < 0x0e) {
            	int filterIndexMatch = databuf.readUInt8();
            	MacPassthroughType legacyPassthroughType = MacPassthroughType.valOfInt(databuf.readUInt8());
            	int lastHopLqi = databuf.readUInt8();
            	int lastHopRssi = databuf.readInt8();
            	RxPacketInfo packetInfo = new RxPacketInfo() ;
            	packetInfo.senderShortAddr = ZSpecConsts.NULL_NODE_ID;
            	packetInfo.senderIEEEAddr = null;
            	packetInfo.bindingIdx = ZSpecConsts.NULL_BINDING;
            	packetInfo.addrIdx = 0xff ;
            	packetInfo.lastHopLqi =lastHopLqi;
                packetInfo.lastHopRssi =lastHopRssi;
                packetInfo.lastHopTimestamp= 0 ;
                byte[] msg_cont = databuf.readPayload();
                this.ezspMacFilterMatchMessageHandler(filterIndexMatch, legacyPassthroughType, packetInfo, msg_cont);
            } else if (ver < 0x12) {
            	int filterIndexMatch = databuf.readUInt8();
            	MacPassthroughType legacyPassthroughType = MacPassthroughType.valOfInt(databuf.readUInt8());
            	RxPacketInfo packetInfo = databuf.readRxPacketInfo();
            	byte[] msg_cont = databuf.readPayload();
                this.ezspMacFilterMatchMessageHandler(filterIndexMatch, legacyPassthroughType, packetInfo, msg_cont);
            } else {
            	int filterValueMatch = databuf.readUInt16();
            	MacPassthroughType legacyPassthroughType = MacPassthroughType.valOfInt(databuf.readUInt8());
            	RxPacketInfo packetInfo = databuf.readRxPacketInfo();
            	byte[] msg_cont = databuf.readPayload();
                this.ezspMacFilterMatchMessageHandler(filterValueMatch, legacyPassthroughType, packetInfo, msg_cont);
            }
            break;
        }
        case RAW_TRANSMIT_COMPLETE_HANDLER: {
            if (ver < 0x0e) {
                SLStatus status = databuf.readStatus(ver);
                if(log.isDebugEnabled())
                	log.debug("ezspRawTransmitCompleteHandler: messageContents=null status="+status) ;
            } else {
                byte[] msg_cont = databuf.readPayload();
                SLStatus status = databuf.readStatus(ver);
                if(log.isDebugEnabled())
                	log.debug("ezspRawTransmitCompleteHandler: messageContents="+Convert.byteArray2HexStr(msg_cont, "-")+" status="+status) ;
            }
            break;
        }
        case SWITCH_NETWORK_KEY_HANDLER: {
            int sequenceNumber = databuf.readUInt8();
            if(log.isDebugEnabled())
            	log.debug("ezspSwitchNetworkKeyHandler: sequenceNumber="+sequenceNumber);
            break;
        }
        case ZIGBEE_KEY_ESTABLISHMENT_HANDLER: {
            String partner = databuf.readIEEEAddr() ;
            KeyStatus status  = KeyStatus.valOfInt(databuf.readUInt8());
            if(log.isDebugEnabled())
            	log.debug("ezspZigbeeKeyEstablishmentHandler: partner="+partner+" status="+status);
            break;
        }
        case TRUST_CENTER_JOIN_HANDLER: {
            int newNodeId = databuf.readUInt16();
            String newNodeEui64 = databuf.readIEEEAddr();
            EmberDeviceUpdate status = EmberDeviceUpdate.valOfInt(databuf.readUInt8());
            EmberJoinDecision policyDecision = EmberJoinDecision.valOfInt(databuf.readUInt8());
            int parentOfNewNodeId = databuf.readUInt16();
            this.ezspTrustCenterJoinHandler(newNodeId, newNodeEui64, status, policyDecision, parentOfNewNodeId);
            break;
        }
        case GENERATE_CBKE_KEYS_HANDLER: {
            SLStatus status = databuf.readStatus(ver);
            byte[] ephemeralPublicKey = databuf.readEmberPublicKeyData();
            if(log.isDebugEnabled())
            	log.debug("ezspGenerateCbkeKeysHandler: status="+status+" ephemeralPublicKey="+Convert.byteArray2HexStr(ephemeralPublicKey, "-"));
            break;
        }
        case CALCULATE_SMACS_HANDLER: {
        	SLStatus status = databuf.readStatus(ver);
            byte[] initiatorSmac = databuf.readEmberSmacData();
            byte[] responderSmac = databuf.readEmberSmacData();
            if(log.isDebugEnabled())
            	log.debug("ezspCalculateSmacsHandler: status="+status+" initiatorSmac="+Convert.byteArray2HexStr(initiatorSmac, "-")+" responderSmac="+Convert.byteArray2HexStr(responderSmac, "-"));
            break;
        }
        case GENERATE_CBKE_KEYS_HANDLER283K1: {
        	SLStatus status = databuf.readStatus(ver);
            byte[] ephemeralPublicKey = databuf.readEmberPublicKey283k1Data();
            if(log.isDebugEnabled())
            	log.debug("ezspGenerateCbkeKeysHandler283k1(): status="+status+" ephemeralPublicKey="+Convert.byteArray2HexStr(ephemeralPublicKey, "-"));
            break;
        }
        case CALCULATE_SMACS_HANDLER283K1: {
        	SLStatus status = databuf.readStatus(ver);
            byte[] initiatorSmac = databuf.readEmberSmacData();
            byte[]  responderSmac = databuf.readEmberSmacData();
            if(log.isDebugEnabled())
            	log.debug("ezspCalculateSmacsHandler283k1(): status="+status+" initiatorSmac="+Convert.byteArray2HexStr(initiatorSmac, "-")+" responderSmac="+Convert.byteArray2HexStr(responderSmac, "-"));
            break;
        }
        case DSA_SIGN_HANDLER: {
            SLStatus status = databuf.readStatus(ver);
            byte[] messageContents = databuf.readPayload();
            if(log.isDebugEnabled())
            	log.debug("ezspDsaSignHandler: status="+status+" messageContents="+Convert.byteArray2HexStr(messageContents, "-"));
            break;
        }
        case DSA_VERIFY_HANDLER: {
        	SLStatus status = databuf.readStatus(ver);
        	if(log.isDebugEnabled())
        		log.debug("ezspDsaVerifyHandler: status="+status);
            break;
        }
        case MFGLIB_RX_HANDLER: {
            int linkQuality = databuf.readUInt8();
            int rssi = databuf.readInt8();
            byte[] packetContents = databuf.readPayload();
            if(log.isDebugEnabled())
            	log.debug("ezspMfglibRxHandler: linkQuality="+linkQuality+" rssi="+rssi+" packetContents="+Convert.byteArray2HexStr(packetContents, "-"));
            break;
        }
        case INCOMING_BOOTLOAD_MESSAGE_HANDLER: {
            if (ver < 0x0e) {
                String longId = databuf.readIEEEAddr();
                int lastHopLqi = databuf.readUInt8();
                int lastHopRssi = databuf.readInt8();
                RxPacketInfo packetInfo =new RxPacketInfo();
                packetInfo.senderShortAddr = ZSpecConsts.NULL_NODE_ID;
                packetInfo.senderIEEEAddr = null;//ZSpec.BLANK_EUI64,
                packetInfo.bindingIdx= ZSpecConsts.NULL_BINDING;
                packetInfo.addrIdx = 0xff;
                packetInfo.lastHopLqi = lastHopLqi;
                packetInfo.lastHopRssi = lastHopRssi;
                packetInfo.lastHopTimestamp= 0;
                byte[] messageContents = databuf.readPayload();
                if(log.isDebugEnabled())
                	log.debug("ezspIncomingBootloadMessageHandler: longId="+longId+" packetInfo="+packetInfo.toJO()+" messageContents="+Convert.byteArray2HexStr(messageContents, "-"));
            } else {
                String longId = databuf.readIEEEAddr() ;
                RxPacketInfo packetInfo = databuf.readRxPacketInfo();
                byte[] messageContents = databuf.readPayload();
                if(log.isDebugEnabled())
                	log.debug("ezspIncomingBootloadMessageHandler: longId="+longId+" packetInfo="+packetInfo.toJO()+" messageContents="+Convert.byteArray2HexStr(messageContents, "-"));
            }
            break;
        }
        case BOOTLOAD_TRANSMIT_COMPLETE_HANDLER: {
            SLStatus status = databuf.readStatus(ver);
            byte[] messageContents = databuf.readPayload();
            if(log.isDebugEnabled())
            	log.debug("ezspBootloadTransmitCompleteHandler: status="+status+" messageContents="+Convert.byteArray2HexStr(messageContents, "-"));
            break;
        }
        case INCOMING_MFG_TEST_MESSAGE_HANDLER: {
            int messageType = databuf.readUInt8();
            byte[] messageContents = databuf.readPayload();
            if(log.isDebugEnabled())
            	log.debug("ezspIncomingMfgTestMessageHandler: messageType="+messageType+" messageContents="+Convert.byteArray2HexStr(messageContents, "-"));
            break;
        }
        case ZLL_NETWORK_FOUND_HANDLER: {
            if (ver < 0x0e) {
            	EmberZllNetwork networkInfo = databuf.readEmberZllNetwork();
                boolean isDeviceInfoNull = databuf.readUInt8() != 0;
                EmberZllDevInfoRecord deviceInfo = databuf.readEmberZllDevInfoRecord();
                int lastHopLqi = databuf.readUInt8();
                int lastHopRssi = databuf.readInt8();
                RxPacketInfo packetInfo = new RxPacketInfo();
                packetInfo.senderShortAddr = ZSpecConsts.NULL_NODE_ID;
                packetInfo.senderIEEEAddr = null;
                packetInfo.bindingIdx= ZSpecConsts.NULL_BINDING;
                packetInfo.addrIdx = 0xff;
                packetInfo.lastHopLqi =   lastHopLqi;
                packetInfo.lastHopRssi = lastHopRssi ;
                packetInfo.lastHopTimestamp= 0;
                if(log.isDebugEnabled())
                	log.debug("ezspZllNetworkFoundHandler: networkInfo="+networkInfo.toJO()+" isDeviceInfoNull="+isDeviceInfoNull+" deviceInfo="+deviceInfo+" packetInfo="+packetInfo.toJO()) ;
            } else {
            	EmberZllNetwork networkInfo = databuf.readEmberZllNetwork();
                boolean isDeviceInfoNull = databuf.readUInt8() != 0;
                EmberZllDevInfoRecord deviceInfo = databuf.readEmberZllDevInfoRecord();
                RxPacketInfo packetInfo = databuf.readRxPacketInfo();
                if(log.isDebugEnabled())
                	log.debug("ezspZllNetworkFoundHandler: networkInfo="+networkInfo.toJO()+" isDeviceInfoNull="+isDeviceInfoNull+" deviceInfo="+deviceInfo+" packetInfo="+packetInfo.toJO()) ;
            }
            break;
        }
        case ZLL_SCAN_COMPLETE_HANDLER: {
            SLStatus status = databuf.readStatus(ver);
            if(log.isDebugEnabled())
            	log.debug("ezspZllScanCompleteHandler: status="+status);
            break;
        }
        case ZLL_ADDRESS_ASSIGNMENT_HANDLER: {
            if (ver < 0x0e) {
            	EmberZllAddrAssignment addressInfo = databuf.readEmberZllAddrAssignment();
                int lastHopLqi = databuf.readUInt8();
                int lastHopRssi = databuf.readInt8();
                RxPacketInfo packetInfo = new RxPacketInfo();
                packetInfo.senderShortAddr = ZSpecConsts.NULL_NODE_ID;
                packetInfo.senderIEEEAddr=null;
                packetInfo.bindingIdx = ZSpecConsts.NULL_BINDING;
                packetInfo.addrIdx = 0xff;
                packetInfo.lastHopLqi = lastHopLqi;
                packetInfo.lastHopRssi = lastHopRssi ;
                packetInfo.lastHopTimestamp = 0;
                if(log.isDebugEnabled())
                	log.debug("ezspZllAddressAssignmentHandler: addressInfo="+addressInfo.toJO()+" packetInfo="+packetInfo.toJO());
            } else {
            	EmberZllAddrAssignment addressInfo = databuf.readEmberZllAddrAssignment();
            	RxPacketInfo packetInfo = databuf.readRxPacketInfo();
                if(log.isDebugEnabled())
                	log.debug("ezspZllAddressAssignmentHandler: addressInfo="+addressInfo.toJO()+" packetInfo="+packetInfo.toJO());
            }
            break;
        }
        case ZLL_TOUCH_LINK_TARGET_HANDLER: {
        	EmberZllNetwork networkInfo = databuf.readEmberZllNetwork();
        	if(log.isDebugEnabled())
        		log.debug("ezspZllTouchLinkTargetHandler: networkInfo="+networkInfo.toJO());
            break;
        }
        case D_GP_SENT_HANDLER: {
        	SLStatus status = databuf.readStatus(ver);
            int gpepHandle = databuf.readUInt8();
            if(log.isDebugEnabled())
            	log.debug("ezspDGpSentHandler: status="+status+" gpepHandle="+gpepHandle);
            break;
        }
        case GPEP_INCOMING_MESSAGE_HANDLER: {
        	EmberGPStatus gpStatus =EmberGPStatus.valOfInt(databuf.readUInt8());
        	int gpdLink = databuf.readUInt8();
            int sequenceNumber = databuf.readUInt8();
            EmberGpAddr addr = databuf.readEmberGpAddress();
            EmberGpSecurityLevel gpdfSecurityLevel = EmberGpSecurityLevel.valOfInt(databuf.readUInt8());
            EmberGpKeyType gpdfSecurityKeyType = EmberGpKeyType.valOfInt(databuf.readUInt8());
            boolean autoCommissioning = databuf.readUInt8() != 0;
            int bidirectionalInfo = databuf.readUInt8();
            long gpdSecurityFrameCounter = databuf.readUInt32();
            int gpdCommandId = databuf.readUInt8();
            long mic = databuf.readUInt32();
            int proxyTableIndex = databuf.readUInt8();
            byte[] gpdCommandPayload = databuf.readPayload();
            RxPacketInfo packetInfo;
            if (ver < 0x10) {
            	packetInfo = new RxPacketInfo();
                packetInfo.senderShortAddr= ZSpecConsts.NULL_NODE_ID ;
                packetInfo.senderIEEEAddr = null ;
                packetInfo.bindingIdx = ZSpecConsts.NULL_BINDING ;
                packetInfo.addrIdx = 0xff ;
                packetInfo.lastHopLqi=gpdLink; // technically not accurate (this is rssi + lqi[0..3] map)
                packetInfo.lastHopRssi= 0;
                packetInfo.lastHopTimestamp= 0;
            } else {
                packetInfo = databuf.readRxPacketInfo();
            }

            this.ezspGpepIncomingMessageHandler(gpStatus,gpdLink,sequenceNumber,addr,
                gpdfSecurityLevel,gpdfSecurityKeyType,autoCommissioning,bidirectionalInfo,
                gpdSecurityFrameCounter,gpdCommandId,mic,proxyTableIndex,
                gpdCommandPayload,packetInfo);
            break;
        }
        default:
        	if(log.isDebugEnabled())
        		log.debug("unknown ezsp frame id"+fid);
		}
		
		return false;
	}

	private void ezspIncomeMsgHandle(IncomeMsgTp type, ApsFrame aspf, RxPacketInfo pk_inf, byte[] pld)
	{
		if (log.isDebugEnabled())
			log.debug("ezspIncomingMessageHandler: type=" + type + " apsFrame=" + aspf.toJO() + "packetInfo:"
					+ pk_inf.toJO() + " msg_cont=" + Convert.byteArray2HexStr(pld, "-"));

		if (aspf.profileId == ZDOConsts.ZDO_PROFILE_ID)
		{
			ZDORspEvt rspevt = new ZDORspEvt() ;
			rspevt.clusterId = aspf.clusterId ;
			rspevt.pld = pld ;
			if (log.isDebugEnabled())
				log.debug("ZDO_RSP <<<<<<<<<") ;
//			const result = Zdo.Buffalo.readResponse(hasZdoMessageOverhead, aspf.clusterId, pld);
//
//            if (aspf.clusterId == ZDOClusterId.NETWORK_ADDRESS_RESPONSE.getIntVal()) {
//            	
//            }
			this.emit(ZSController.ZDO_RSP, rspevt);
		}
		else if (aspf.profileId == ZSpecConsts.HA_PROFILE_ID || aspf.profileId == ZSpecConsts.WILDCARD_PROFILE_ID
				|| (aspf.profileId == ZSpecConsts.GP_PROFILE_ID && type != IncomeMsgTp.BROADCAST_LOOPBACK) ||
				// Shelly custom Clusters require a special profile ID
				aspf.profileId == ZSpecConsts.CUSTOM_SHELLY_PROFILE_ID ||
				// Control4 custom Clusters require a special profile ID
				aspf.profileId == ZSpecConsts.CUSTOM_CONTROL4_PROFILE_ID)
		{
			ZCLFrame zcl_f = null;
			if(pld!=null)
			{
				StringBuilder failedr = new StringBuilder() ;
				zcl_f = ZCLManager.unpackedFrame(pld, failedr) ;
			}
			boolean b_bcast = (type == IncomeMsgTp.BROADCAST) || (type == IncomeMsgTp.BROADCAST_LOOPBACK) ;
			IncomeMsgDefEvt inmsg = new IncomeMsgDefEvt(aspf.groupId,aspf.clusterId,aspf.src_ep,aspf.dst_ep,
					pk_inf.senderShortAddr,b_bcast,pk_inf.lastHopLqi,zcl_f) ;
		
			this.emit(ZSController.INCOME_MSG, inmsg);
		}
	}

	/**
     * Callback
     * A callback invoked by the Zigbee GP stack when a GPDF is received.
     * @param status The status of the GPDF receive.
     * @param gpdLink uint8_t The gpdLink value of the received GPDF.
     * @param sequenceNumber uint8_t The GPDF sequence number.
     * @param addr EmberGpAddress *The address of the source GPD.
     * @param gpdfSecurityLevel The security level of the received GPDF.
     * @param gpdfSecurityKeyType The securityKeyType used to decrypt/authenticate the incoming GPDF.
     * @param autoCommissioning Whether the incoming GPDF had the auto-commissioning bit set.
     * @param bidirectionalInfo uint8_t Bidirectional information represented in bitfields,
     *        where bit0 holds the rxAfterTx of incoming gpdf and bit1 holds if tx queue is available for outgoing gpdf.
     * @param gpdSecurityFrameCounter uint32_t The security frame counter of the incoming GPDF.
     * @param gpdCommandId uint8_t The gpdCommandId of the incoming GPDF.
     * @param mic uint32_t The received MIC of the GPDF.
     * @param proxyTableIndex uint8_tThe proxy table index of the corresponding proxy table entry to the incoming GPDF.
     * @param gpdCommandPayload uint8_t * The GPD command payload.
     * @param packetInfo Rx packet information.
     */
    private void ezspGpepIncomingMessageHandler(EmberGPStatus status,
        int gpdLink,int sequenceNumber,EmberGpAddr addr,EmberGpSecurityLevel gpdfSecurityLevel ,
        EmberGpKeyType gpdfSecurityKeyType ,boolean autoCommissioning,int bidirectionalInfo,
        long gpdSecurityFrameCounter,int gpdCommandId,long mic,int proxyTableIndex,
        byte[] gpdCommandPayload,RxPacketInfo packetInfo)
    {
    	if(log.isDebugEnabled())
    	{
    		log.debug("ezspGpepIncomingMessageHandler: status="+status+" gpdLink="+gpdLink +
                    "sequenceNumber="+sequenceNumber+" addr="+addr.toJ()+" gpdfSecurityLevel="+gpdfSecurityLevel +
                    "gpdfSecurityKeyType="+gpdfSecurityKeyType+" autoCommissioning="+autoCommissioning +
                    "bidirectionalInfo="+bidirectionalInfo+" gpdSecurityFrameCounter="+gpdSecurityFrameCounter+" gpdCommandId="+gpdCommandId +
                    "mic="+mic+" proxyTableIndex="+proxyTableIndex+" gpdCommandPayload="+gpdCommandPayload+" packetInfo="+packetInfo);
    	}
    	
        if (addr instanceof EmberGpAddrIEEE) //(addr.applicationId == EmberGpAppId.IEEE_ADDRESS) {
        {
            // XXX: don't bother parsing for upstream for now, since it will be rejected
            log.error("[GP] Received IEEE address type in message. Support not implemented upstream. Dropping.");
            return;
        }
        EmberGpAddrSrcID addr_src = (EmberGpAddrSrcID)addr ;
        
        ZCLCluster cc = ZCLClusterManager.getInstance().getCluserByID(ZCLClusterIDS.greenPower) ;
        ZCLCmd cmd_notification = cc.getCmdByName("notification") ;
        int commandIdentifier = cmd_notification.getID() ;//Clusters.greenPower.commands.notification.ID;
        int options = 0;
        boolean isCommissioning = gpdCommandId == 0xe0;

        if (isCommissioning) {
            if (gpdCommandPayload==null || gpdCommandPayload.length<=0) {
                // XXX: seem to be receiving duplicate commissioningNotification from some devices, second one with empty payload?
                //      this will mess with the process no doubt, so dropping them
                log.debug("[GP] Received commissioning notification with empty payload. Dropping.");
                return;
            }

            ZCLCmd cmd = cc.getCmdByName("commissioningNotification") ;
            commandIdentifier = cmd.getID();// Clusters.greenPower.commands.commissioningNotification.ID;
            options =
                (addr.applicationId.getIntVal() & 0x7) | ((bidirectionalInfo & 0x1) << 3) | ((gpdfSecurityLevel.getIntVal() & 0x3) << 4) | ((gpdfSecurityKeyType.getIntVal() & 0x7) << 6);
        } else {
            options =
                (addr.applicationId.getIntVal() & 0x7) |
                ((gpdfSecurityLevel.getIntVal() & 0x3) << 6) |
                ((gpdfSecurityKeyType.getIntVal() & 0x7) << 8) |
                ((bidirectionalInfo & 0x1) << 11);
        }

        ApsFrame apsFrame = new ApsFrame();
        apsFrame.profileId =  ZSpecConsts.GP_PROFILE_ID;
    		apsFrame.clusterId = ZCLClusterIDS.greenPower ; // Zcl.Clusters.greenPower.ID;
    		apsFrame.src_ep = ZSpecConsts.GP_ENDPOINT;
    		apsFrame.dst_ep = ZSpecConsts.GP_ENDPOINT;
    		apsFrame.options= 0; // not used
    		apsFrame.groupId= ZSpecConsts.GP_GROUP_ID;
    		apsFrame.sequence= 0; // not used
        // this stuff is already parsed by EmberZNet stack, but Z2M expects the full buffer, so combine it back
        int offset = 0;
        DataBuffer msg_ct = DataBuffer.alloc(15 + gpdCommandPayload.length);
        msg_ct.writeUInt8(0b00000001); // frameControl: FrameType.SPECIFIC + Direction.CLIENT_TO_SERVER + disableDefaultResponse=false
        msg_ct.writeUInt8(sequenceNumber);
        msg_ct.writeUInt8(commandIdentifier); // commandIdentifier
        msg_ct.writeUInt16(options); //.writeUInt16LE(options, offset);
        msg_ct.writeUInt32(addr_src.sourceId); //.writeUInt32LE(addr.sourceId, offset);
        msg_ct.writeUInt32(gpdSecurityFrameCounter); //writeUInt32LE(gpdSecurityFrameCounter, offset);
        msg_ct.writeUInt8(gpdCommandId);
        msg_ct.writeUInt8(gpdCommandPayload.length);
        //offset = gpdCommandPayload.copy(messageContents, offset);
        msg_ct.writeBuf(gpdCommandPayload, 0, gpdCommandPayload.length);

        // upstream will use COORDINATOR_ADDRESS if GPP not present
        // TODO wait to impl
        // this.emit(ZSController.INCOME_MSG, IncomeMsgTp.UNICAST, apsFrame, packetInfo.lastHopLqi, addr_src.sourceId & 0xffff, msg_ct);
    }
    
	/**
     * Callback
     * The NCP used the trust center behavior policy to decide whether to allow a
     * new node to join the network. The Host cannot change the current decision,
     * but it can change the policy for future decisions using the setPolicy
     * command.
     * @param newNodeId The Node Id of the node whose status changed
     * @param newNodeEui64 The EUI64 of the node whose status changed.
     * @param status The status of the node: Secure Join/Rejoin, Unsecure Join/Rejoin, Device left.
     * @param policyDecision An EmberJoinDecision reflecting the decision made.
     * @param parentOfNewNodeId The parent of the node whose status has changed.
     */
    private void ezspTrustCenterJoinHandler(int newNodeId,String newNodeEui64,
    		EmberDeviceUpdate status,EmberJoinDecision policyDecision ,int parentOfNewNodeId)
    {
    	if(log.isDebugEnabled())
    		log.debug("ezspTrustCenterJoinHandler: newNodeId="+newNodeId+" newNodeEui64="+newNodeEui64 +
                "status="+status+" policyDecision="+policyDecision+" parentOfNewNodeId="+parentOfNewNodeId);
        // NOTE: this is mostly just passing stuff up to Z2M, so use only one emit for all, let adapter do the rest, no parsing needed
    	// TODO wait to impl
        //this.emit(ZSController.TRUST_CENTER_JOIN, newNodeId, newNodeEui64, status, policyDecision, parentOfNewNodeId);
    }
    
	/**
	 * Callback A callback indicating the stack has completed sending a message.
	 * 
	 * @param status
	 *            - SL_STATUS_OK if an ACK was received from the destination -
	 *            SL_STATUS_ZIGBEE_DELIVERY_FAILED if no ACK was received.
	 * @param type
	 *            The type of message sent.
	 * @param indexOrDestination
	 *            uint16_t The destination to which the message was sent, for
	 *            direct unicasts, or the address table or binding index for
	 *            other unicasts. The value is unspecified for multicasts and
	 *            broadcasts.
	 * @param apsFrame
	 *            EmberApsFrame * The APS frame for the message.
	 * @param messageTag
	 *            uint8_t The value supplied by the Host in the ezspSendUnicast,
	 *            ezspSendBroadcast or ezspSendMulticast command.
	 * @param messageContents
	 *            uint8_t * The unicast message supplied by the Host. The
	 *            message contents are only included here if the decision for
	 *            the messageContentsInCallback policy is
	 *            messageTagAndContentsInCallback.
	 */
	private void ezspMessageSentHandler(SLStatus status, OutgoingMsgType msgtp, int indexOrDestination,
			ApsFrame apsFrame, int messageTag, byte[] msg_content)
	{
		if (log.isDebugEnabled())
			log.debug("ezspMessageSentHandler: status=" + status + " type=" + msgtp + " indexOrDestination="
					+ indexOrDestination + " apsFrame=" + apsFrame.toJO() + " messageTag=" + messageTag + "msg_cont="
					+ Convert.byteArray2HexStr(msg_content, "-"));

		// TODO wait to impl
		//this.emit(ZSController.MSG_SENT, msg_content);
	}
	
	
	/**
     * Callback
     * A callback invoked by the EmberZNet stack when a raw MAC message that has
     * matched one of the application's configured MAC filters.
     * @param filterValueMatch uint16_t The value of the filter that was matched.
     *   - < v18: uint8_t The index of the filter that was matched.
     * @param legacyPassthroughType The type of MAC passthrough message received.
     * @param packetInfo Information about the incoming packet.
     * @param messageContents uint8_t * The raw message that was received.
     */
    private void ezspMacFilterMatchMessageHandler(int filterValueMatch,MacPassthroughType legacyPassthroughType,
        RxPacketInfo packetInfo,byte[] msg_cont)
    {
        if(log.isDebugEnabled())
        	log.debug("ezspMacFilterMatchMessageHandler: filterValueMatch="+filterValueMatch+" legacyPassthroughType="+legacyPassthroughType+
        			"packetInfo="+packetInfo.toJO()+" msg_cont="+Convert.byteArray2HexStr(msg_cont, "-")) ;

        // TODO: needs triple-checking, this is only valid for InterPAN messages
        EZSPBuffer msgBuffalo = new EZSPBuffer(msg_cont, 0,msg_cont.length);

        int macFrameControl = msgBuffalo.readUInt16() & ~EmberConsts.MAC_ACK_REQUIRED;
        msgBuffalo.readUInt8(); // sequence, unused
        msgBuffalo.readUInt16(); // destPanId, unused

        if (macFrameControl == EmberConsts.LONG_DEST_FRAME_CONTROL) {
            msgBuffalo.readIEEEAddr(); // destAddress, unused
        } else if (macFrameControl == EmberConsts.SHORT_DEST_FRAME_CONTROL) {
            msgBuffalo.readUInt16(); // destAddress, unused
        } else {
            log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN macFrameControl '"+macFrameControl+"'.");
            return;
        }

        int sourcePanId = msgBuffalo.readUInt16();
        String src_addr = msgBuffalo.readIEEEAddr() ;

        // Now that we know the correct MAC length, verify the interpan frame is the correct length.
        int remainingLength = msgBuffalo.getLeftLen() ;

        if (remainingLength < EmberConsts.STUB_NWK_SIZE + EmberConsts.MIN_STUB_APS_SIZE) {
            log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN length '"+remainingLength+"'.");
            return;
        }

        int nwkFrameControl = msgBuffalo.readUInt16();
        remainingLength -= 2; // read 2 more bytes before APS stuff

        if (nwkFrameControl != EmberConsts.STUB_NWK_FRAME_CONTROL) {
            log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN nwkFrameControl '"+nwkFrameControl+"'.");
            return;
        }

        int apsFrameControl = msgBuffalo.readUInt8();

        if (
            (apsFrameControl & ~EmberConsts.INTERPAN_APS_FRAME_DELIVERY_MODE_MASK & ~EmberConsts.INTERPAN_APS_FRAME_SECURITY) !=
            		EmberConsts.INTERPAN_APS_FRAME_CONTROL_NO_DELIVERY_MODE
        ) {
            log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN apsFrameControl '"+apsFrameControl+"'.");
            return;
        }

        InterpanMsgType messageType = InterpanMsgType.valOfInt(apsFrameControl & EmberConsts.INTERPAN_APS_FRAME_DELIVERY_MODE_MASK);
        int groupId = 0; // XXX: looks fine from z2m code?

        switch (messageType) {
            case UNICAST:
            case BROADCAST: {
                if (remainingLength < EmberConsts.INTERPAN_APS_UNICAST_BROADCAST_SIZE) {
                    log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN length '"+remainingLength+"'.");
                    return;
                }
                break;
            }
            case MULTICAST: {
                if (remainingLength < EmberConsts.INTERPAN_APS_MULTICAST_SIZE) {
                    log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN length '"+remainingLength+"'.");
                    return;
                }

                groupId = msgBuffalo.readUInt16();
                break;
            }
            default: {
                log.debug("ezspMacFilterMatchMessageHandler INVALID InterPAN messageType '"+messageType+"'.");
                return;
            }
        }

        int clusterId = msgBuffalo.readUInt16();
        int profileId = msgBuffalo.readUInt16();
        byte[] payload = msgBuffalo.readRest();

        if (profileId == ZSpecConsts.TOUCHLINK_PROFILE_ID && clusterId == ZCLClusterIDS.touchlink)
        {
        	// TODO wait to impl
            // this.emit(ZSController.TOUCHLINK_MSG, sourcePanId, src_addr, groupId, packetInfo.lastHopLqi, payload);
        }
    }

	public boolean ezspStackStatusHandler(SLStatus status)
	{
		if(log.isDebugEnabled())
			log.debug("ezspStackStatusHandler: status="+status);

		mgr.getCoor().clearNwkCache();

        switch (status) {
            case NETWORK_UP: {
                //this.oneWaitress.resolveEvent(OneWaitressEvents.STACK_STATUS_NETWORK_UP);
                log.info("[STACK STATUS] Network up.");
                break;
            }
            case NETWORK_DOWN: {
                //this.oneWaitress.resolveEvent(OneWaitressEvents.STACK_STATUS_NETWORK_DOWN);
                log.info("[STACK STATUS] Network down.");
                break;
            }
            case ZIGBEE_NETWORK_OPENED: {
                //this.oneWaitress.resolveEvent(OneWaitressEvents.STACK_STATUS_NETWORK_OPENED);
                log.info("[STACK STATUS] Network opened.");
                break;
            }
            case ZIGBEE_NETWORK_CLOSED: {
                //this.oneWaitress.resolveEvent(OneWaitressEvents.STACK_STATUS_NETWORK_CLOSED);
                log.info("[STACK STATUS] Network closed.");

//                if (this.manufacturerCode != ManufacturerCode.SILICON_LABORATORIES) {
//                   // await this.queue.execute<void>(async () => {
//                        log.debug("[WORKAROUND] Reverting coordinator manufacturer code to default.");
//                        this.mgr.getCoor().getEZSP().ezspSetManufacturerCode(DEFAULT_MANUFACTURER_CODE);
//
//                        this.manufacturerCode = DEFAULT_MANUFACTURER_CODE;
//                  //  });
//                }

                break;
            }
            case ZIGBEE_CHANNEL_CHANGED: {
                // invalidate cache
//            	this.mgr.getCoor().getNwkCache().params.radio_ch = EmberConsts.INVALID_RADIO_CHANNEL;
                log.info("[STACK STATUS] Channel changed.");
                break;
            }
            default: {
                log.debug("[STACK STATUS] "+status);
                break;
            }
        }
        //this.emit("stackStatus", status);
		return false;
    }
}
