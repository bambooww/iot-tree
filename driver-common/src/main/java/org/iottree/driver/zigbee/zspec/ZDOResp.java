package org.iottree.driver.zigbee.zspec;

import org.iottree.driver.zigbee.zspec.zdo.AddrRsp;
import org.iottree.driver.zigbee.zspec.zdo.CommonStatusRsp;

public abstract class ZDOResp extends ZDOCluster
{
	public abstract ZDOStatus getStatus() ;
	

	public static ZDOResp parseResp(boolean hasZdoMessageOverhead,ZDOClusterId clusterId,
	        byte[] buffer) //throws Exception
	{
		int pos = hasZdoMessageOverhead ? ZDOConsts.ZDO_MESSAGE_OVERHEAD : 0;
	        ZDOBuffer buf = new ZDOBuffer(buffer, 0,buffer.length); // set pos to skip `transaction sequence number`
	        buf.setPosition(pos);
	        
	        switch (clusterId) {
	            case NETWORK_ADDRESS_RESPONSE:
	            case IEEE_ADDRESS_RESPONSE: {
	                return new AddrRsp(clusterId).unpackResp(buf) ;
	            }
//	            case NODE_DESCRIPTOR_RESPONSE: {
//	                return buffalo.readNodeDescriptorResponse() as ResponseMap[K];
//	            }
//	            case POWER_DESCRIPTOR_RESPONSE: {
//	                return buffalo.readPowerDescriptorResponse() as ResponseMap[K];
//	            }
//	            case SIMPLE_DESCRIPTOR_RESPONSE: {
//	                return buffalo.readSimpleDescriptorResponse() as ResponseMap[K];
//	            }
//	            case ACTIVE_ENDPOINTS_RESPONSE: {
//	                return buffalo.readActiveEndpointsResponse() as ResponseMap[K];
//	            }
//	            case MATCH_DESCRIPTORS_RESPONSE: {
//	                return buffalo.readMatchDescriptorsResponse() as ResponseMap[K];
//	            }
//	            case END_DEVICE_ANNOUNCE: {
//	                return buffalo.readEndDeviceAnnounce() as ResponseMap[K];
//	            }
//	            case SYSTEM_SERVER_DISCOVERY_RESPONSE: {
//	                return buffalo.readSystemServerDiscoveryResponse() as ResponseMap[K];
//	            }
//	            case PARENT_ANNOUNCE_RESPONSE: {
//	                return buffalo.readParentAnnounceResponse() as ResponseMap[K];
//	            }
//	            case BIND_RESPONSE: {
//	                return buffalo.readBindResponse() as ResponseMap[K];
//	            }
//	            case UNBIND_RESPONSE: {
//	                return buffalo.readUnbindResponse() as ResponseMap[K];
//	            }
//	            case CLEAR_ALL_BINDINGS_RESPONSE: {
//	                return buffalo.readClearAllBindingsResponse() as ResponseMap[K];
//	            }
//	            case LQI_TABLE_RESPONSE: {
//	                return buffalo.readLQITableResponse() as ResponseMap[K];
//	            }
//	            case ROUTING_TABLE_RESPONSE: {
//	                return buffalo.readRoutingTableResponse() as ResponseMap[K];
//	            }
//	            case BINDING_TABLE_RESPONSE: {
//	                return buffalo.readBindingTableResponse() as ResponseMap[K];
//	            }
//	            case LEAVE_RESPONSE: {
//	                return buffalo.readLeaveResponse() as ResponseMap[K];
//	            }
	            case PERMIT_JOINING_RESPONSE: {
	                return new CommonStatusRsp(ZDOClusterId.PERMIT_JOINING_RESPONSE).unpackResp(buf); // ;buffalo.readPermitJoiningResponse() as ResponseMap[K];
	            }
//	            case NWK_UPDATE_RESPONSE: {
//	                return buffalo.readNwkUpdateResponse() as ResponseMap[K];
//	            }
//	            case NWK_ENHANCED_UPDATE_RESPONSE: {
//	                return buffalo.readNwkEnhancedUpdateResponse() as ResponseMap[K];
//	            }
//	            case NWK_IEEE_JOINING_LIST_RESPONSE: {
//	                return buffalo.readNwkIEEEJoiningListResponse() as ResponseMap[K];
//	            }
//	            case NWK_UNSOLICITED_ENHANCED_UPDATE_RESPONSE: {
//	                return buffalo.readNwkUnsolicitedEnhancedUpdateResponse() as ResponseMap[K];
//	            }
//	            case NWK_BEACON_SURVEY_RESPONSE: {
//	                return buffalo.readNwkBeaconSurveyResponse() as ResponseMap[K];
//	            }
//	            case START_KEY_NEGOTIATION_RESPONSE: {
//	                return buffalo.readStartKeyNegotiationResponse() as ResponseMap[K];
//	            }
//	            case RETRIEVE_AUTHENTICATION_TOKEN_RESPONSE: {
//	                return buffalo.readRetrieveAuthenticationTokenResponse() as ResponseMap[K];
//	            }
//	            case GET_AUTHENTICATION_LEVEL_RESPONSE: {
//	                return buffalo.readGetAuthenticationLevelResponse() as ResponseMap[K];
//	            }
//	            case SET_CONFIGURATION_RESPONSE: {
//	                return buffalo.readSetConfigurationResponse() as ResponseMap[K];
//	            }
//	            case GET_CONFIGURATION_RESPONSE: {
//	                return buffalo.readGetConfigurationResponse() as ResponseMap[K];
//	            }
//	            case START_KEY_UPDATE_RESPONSE: {
//	                return buffalo.readStartKeyUpdateResponse() as ResponseMap[K];
//	            }
//	            case DECOMMISSION_RESPONSE: {
//	                return buffalo.readDecommissionResponse() as ResponseMap[K];
//	            }
//	            case CHALLENGE_RESPONSE: {
//	                return buffalo.readChallengeResponse() as ResponseMap[K];
//	            }
	            default: {
	                throw new IllegalArgumentException("Unsupported response reading for cluster ID "+clusterId);
	            }
	        }
	    }
}
