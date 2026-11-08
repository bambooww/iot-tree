package org.iottree.driver.zigbee.zspec;

import org.iottree.driver.zigbee.zspec.zdo.*;

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
	                return new AddrResp(clusterId).unpackResp(buf) ;
	            }
	            case NODE_DESCRIPTOR_RESPONSE: {
	                return new NodeDescResp().unpackResp(buf) ;
	            }
	            case POWER_DESCRIPTOR_RESPONSE: {
	                return new PowerDescResp().unpackResp(buf) ;
	            }
	            case SIMPLE_DESCRIPTOR_RESPONSE: {
	                return new SimpleDescResp().unpackResp(buf) ;
	            }
	            case ACTIVE_ENDPOINTS_RESPONSE: {
	                return new ActiveEndpointsResp().unpackResp(buf);
	            }
	            case MATCH_DESCRIPTORS_RESPONSE: {
	                return new MatchDescResp().unpackResp(buf);
	            }
	            case END_DEVICE_ANNOUNCE: {
	                return new EndDeviceAnnounceResp().unpackResp(buf);
	            }
	            case SYSTEM_SERVER_DISCOVERY_RESPONSE: {
	                return new SystemServerDiscResp().unpackResp(buf);
	            }
	            case PARENT_ANNOUNCE_RESPONSE: {
	                return new ParentAnnounceResp().unpackResp(buf) ;
	            }
	            case BIND_RESPONSE: {
	                return new CommonStatusResp(ZDOClusterId.BIND_RESPONSE).unpackResp(buf); 
	            }
	            case UNBIND_RESPONSE: {
	            	return new CommonStatusResp(ZDOClusterId.UNBIND_RESPONSE).unpackResp(buf);
	            }
	            case CLEAR_ALL_BINDINGS_RESPONSE: {
	            	return new CommonStatusResp(ZDOClusterId.CLEAR_ALL_BINDINGS_RESPONSE).unpackResp(buf);
	            }
	            case LQI_TABLE_RESPONSE: {
	                return new LQITableResp().unpackResp(buf) ;
	            }
	            case ROUTING_TABLE_RESPONSE: {
	                return new RoutingTableResp().unpackResp(buf) ;
	            }
	            case BINDING_TABLE_RESPONSE: {
	                return new BindingTableResp().unpackResp(buf) ;
	            }
	            case LEAVE_RESPONSE: {
	                return new CommonStatusResp(ZDOClusterId.LEAVE_RESPONSE).unpackResp(buf);
	            }
	            case PERMIT_JOINING_RESPONSE: {
	                return new CommonStatusResp(ZDOClusterId.PERMIT_JOINING_RESPONSE).unpackResp(buf); // ;buffalo.readPermitJoiningResponse() as ResponseMap[K];
	            }
	            case NWK_UPDATE_RESPONSE: {
	                return new NwkUpdateResp().unpackResp(buf);
	            }
	            case NWK_ENHANCED_UPDATE_RESPONSE: {
	                return new NwkEnhancedUpdateResp().unpackResp(buf);
	            }
	            case NWK_IEEE_JOINING_LIST_RESPONSE: {
	                return new NwkIEEEJoinListResp().unpackResp(buf);
	            }
	            case NWK_UNSOLICITED_ENHANCED_UPDATE_RESPONSE: {
	                return new NwkUnsolicEnhancedUpdateResp().unpackResp(buf) ;
	            }
	            case NWK_BEACON_SURVEY_RESPONSE: {
	                return new NwkBeaconSurveyResp().unpackResp(buf) ;
	            }
	            case START_KEY_NEGOTIATION_RESPONSE: {
	                return new StartKeyNegotiationResp().unpackResp(buf);
	            }
	            case RETRIEVE_AUTHENTICATION_TOKEN_RESPONSE: {
	                return new RetrieveAuthTokenResp().unpackResp(buf) ;
	            }
	            case GET_AUTHENTICATION_LEVEL_RESPONSE: {
	                return new GetAuthLevelResp().unpackResp(buf) ;
	            }
	            case SET_CONFIGURATION_RESPONSE: {
	                return new SetConfigResp().unpackResp(buf) ;
	            }
	            case GET_CONFIGURATION_RESPONSE: {
	                return new GetConfigResp().unpackResp(buf) ;
	            }
	            case START_KEY_UPDATE_RESPONSE: {
	                return new CommonStatusResp(ZDOClusterId.START_KEY_UPDATE_RESPONSE).unpackResp(buf);
	            }
	            case DECOMMISSION_RESPONSE: {
	            	return new CommonStatusResp(ZDOClusterId.DECOMMISSION_RESPONSE).unpackResp(buf);
	            }
	            case CHALLENGE_RESPONSE: {
	                return new ChallengeResp().unpackResp(buf) ;
	            }
	            default: {
	                throw new IllegalArgumentException("Unsupported response reading for cluster ID "+clusterId);
	            }
	        }
	    }
}
