package org.iottree.driver.zigbee.ember.req_rsp;

import java.util.List;

import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.ReadClearCountersRsp;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;

public class UtilRR
{
	public static List<Integer> readAndClearCounters(EmberManager em) throws Exception
	{
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.READ_AND_CLEAR_COUNTERS,null) ;
		CommonRR<ReadClearCountersRsp> rr = new CommonRR<>(em, cmd,ReadClearCountersRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp().getCounters() ;
	}
}
