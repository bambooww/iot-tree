package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.GetConfigValCmd;
import org.iottree.driver.zigbee.ember.ezsp.GetConfigValRsp;
import org.iottree.driver.zigbee.ember.types.EzspConfigId;
import org.iottree.driver.zigbee.ember.types.ZdoConfigFlags;
import org.iottree.driver.zigbee.ember.util.SLStatus;

public class ConfRR
{

	public static boolean ezspSetConfigVal(EmberManager em, EzspConfigId confid, int val, StringBuilder failedr)
			throws Exception
	{
		SetConfigValRR gvrr = new SetConfigValRR(em, confid.getIntVal(), val);
		if (!em.RT_synReqRsp(gvrr, failedr))
			return false;
		SLStatus st = gvrr.scvRsp.getStatus();
		if(st==null || !st.isOk())
			return false;
		return true ;
	}
	
	public static boolean setConfigVal(EmberManager em, EzspConfigId confid, int val)
			throws Exception
	{
		return ezspSetConfigVal(em, confid, val, null) ;
	}
	
	public static Integer getConfigVal(EmberManager em, EzspConfigId confid)
			throws Exception
	{
		GetConfigValCmd cmd = new GetConfigValCmd(em,confid.getIntVal()) ;
		CommonRR<GetConfigValRsp> rr = new CommonRR<>(em, cmd,GetConfigValRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		GetConfigValRsp rsp = rr.getReturnRsp() ;
		if(rsp==null)
			return null ;
		SLStatus st = rsp.getStatus();
		if(!st.isOk())
			return null ;
		return rsp.getConfigV() ;
	}

	static class ConfVal
	{
		EzspConfigId conf_id;

		int val;

		ConfVal(EzspConfigId confid, int val)
		{
			this.conf_id = confid;
			this.val = val;
		}
	}

	private static ConfVal[] CONFIG_IDS_PRE_V9 = new ConfVal[] {
			new ConfVal(EzspConfigId.TC_REJOINS_USING_WELL_KNOWN_KEY_TIMEOUT_S, 90),
			new ConfVal(EzspConfigId.TRUST_CENTER_ADDRESS_CACHE_SIZE, 2),
			// [EzspConfigId.SUPPORTED_NETWORKS, 1],
			new ConfVal(EzspConfigId.FRAGMENT_DELAY_MS, 50),
			new ConfVal(EzspConfigId.PAN_ID_CONFLICT_REPORT_THRESHOLD, 2),
			// [EzspConfigId.SOURCE_ROUTE_TABLE_SIZE, 16],
			// [EzspConfigId.ADDRESS_TABLE_SIZE, 16],
			new ConfVal(EzspConfigId.APPLICATION_ZDO_FLAGS,
					ZdoConfigFlags.APP_HANDLES_UNSUPPORTED_ZDO_REQUESTS
							| ZdoConfigFlags.APP_RECEIVES_SUPPORTED_ZDO_REQUESTS),
			new ConfVal(EzspConfigId.INDIRECT_TRANSMISSION_TIMEOUT, 7680),
			new ConfVal(EzspConfigId.END_DEVICE_POLL_TIMEOUT, 14), new ConfVal(EzspConfigId.SECURITY_LEVEL, 5),
			new ConfVal(EzspConfigId.STACK_PROFILE, 2),
			// [EzspConfigId.TX_POWER_MODE, 3],
			new ConfVal(EzspConfigId.FRAGMENT_WINDOW_SIZE, 1),
			// [EzspConfigId.NEIGHBOR_TABLE_SIZE, 16],
			// [EzspConfigId.ROUTE_TABLE_SIZE, 16],
			// [EzspConfigId.BINDING_TABLE_SIZE, 32],
			// [EzspConfigId.KEY_TABLE_SIZE, 12],
			// [EzspConfigId.ZLL_GROUP_ADDRESSES, 0],
			// [EzspConfigId.ZLL_RSSI_THRESHOLD, 0],
			// [EzspConfigId.APS_UNICAST_MESSAGE_COUNT, 255],
			// [EzspConfigId.BROADCAST_TABLE_SIZE, 43],
			// [EzspConfigId.MAX_HOPS, 30],
			// [EzspConfigId.MAX_END_DEVICE_CHILDREN, 32],
			new ConfVal(EzspConfigId.PACKET_BUFFER_HEAP_SIZE, 255) // new
																	// ConfVal(EzspConfigId.PACKET_BUFFER_COUNT,
																	// 255)
	};

	/**
	 * Can only decrease "NCP Memory Allocation" configs at runtime from V9 on.
	 * 
	 * @see https://www.silabs.com/documents/public/release-notes/emberznet-release-notes-7.0.1.0.pdf
	 */
	private static ConfVal[] CONFIG_IDS_CURRENT = new ConfVal[] {
			new ConfVal(EzspConfigId.TC_REJOINS_USING_WELL_KNOWN_KEY_TIMEOUT_S, 90),
			new ConfVal(EzspConfigId.TRUST_CENTER_ADDRESS_CACHE_SIZE, 2),
			new ConfVal(EzspConfigId.FRAGMENT_DELAY_MS, 50),
			new ConfVal(EzspConfigId.PAN_ID_CONFLICT_REPORT_THRESHOLD, 2),
			new ConfVal(EzspConfigId.APPLICATION_ZDO_FLAGS,
					ZdoConfigFlags.APP_HANDLES_UNSUPPORTED_ZDO_REQUESTS
							| ZdoConfigFlags.APP_RECEIVES_SUPPORTED_ZDO_REQUESTS),
			new ConfVal(EzspConfigId.INDIRECT_TRANSMISSION_TIMEOUT, 7680),
			new ConfVal(EzspConfigId.END_DEVICE_POLL_TIMEOUT, 14), new ConfVal(EzspConfigId.SECURITY_LEVEL, 5),
			new ConfVal(EzspConfigId.STACK_PROFILE, 2), new ConfVal(EzspConfigId.FRAGMENT_WINDOW_SIZE, 1) };

	public static int RT_updateConfigList(EmberManager em)
	{
		ConfVal[] configs = em.getEZSPVersion() < 9 ? CONFIG_IDS_PRE_V9 : CONFIG_IDS_CURRENT;
		int r = 0 ;
		for (ConfVal cv : configs)
		{
			StringBuilder failedr = new StringBuilder();
			try
			{
				if(ezspSetConfigVal(em, cv.conf_id, cv.val, failedr))
					r ++ ;
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
		return r ;
	}

}
