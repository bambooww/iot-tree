package org.iottree.driver.zigbee.ember.req_rsp;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.AddEndpointCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkInitCmd;
import org.iottree.driver.zigbee.ember.ezsp.SLStatus_Rsp;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.MulticastTableEntryGetRsp;
import org.iottree.driver.zigbee.ember.ezsp.MulticastTableEntrySetCmd;
import org.iottree.driver.zigbee.ember.ezsp.SetPolicyCmd;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.EzspPolicyId;
import org.iottree.driver.zigbee.ember.types.IEEE802154CcaMode;
import org.iottree.driver.zigbee.ember.types.MulticastTableEntry;
import org.iottree.driver.zigbee.ember.types.NwkInitBitmask;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * 通用的Set参数请求，返回都是SLStatus
 * 
 * @author jason.zhu
 */
public class SetCommonRR extends EZSPReqRsp
{
	EZSPCmd spCmd = null;
	SLStatus_Rsp slstRsp = null;

	public SetCommonRR(EmberManager em, EzspFrameID frm_id, byte[] cmd_bs)
	{
		super(em);
		ArrayList<Integer> r ;
		spCmd = new Common_Cmd(em, frm_id, cmd_bs);
		this.asReq(spCmd, false, 5000);
	}
	
	public SetCommonRR(EmberManager em, EZSPCmd cmd)
	{
		super(em);
		spCmd = cmd;
		this.asReq(spCmd, false, 5000);
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if (m_recv instanceof SLStatus_Rsp)
		{
			this.slstRsp = (SLStatus_Rsp) m_recv;
			return true;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}

	public boolean isSLStatusOk()
	{
		if (this.slstRsp == null)
			return false;
		return this.slstRsp.getSLStatus().isOk();
	}
	
	public SLStatus getSLStatus()
	{
		if (this.slstRsp == null)
			return null;
		return this.slstRsp.getSLStatus();
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

	public static boolean setRadioIeee802154CcaMode(EmberManager em, IEEE802154CcaMode cca_mode) throws Exception
	{
		byte[] vs = new byte[] { (byte) cca_mode.getIntVal() };
		SetCommonRR rr = new SetCommonRR(em, EzspFrameID.SET_RADIO_IEEE802154_CCA_MODE, vs);
		if (!em.RT_synReqRsp(rr, null))
			return false;
		return rr.slstRsp.getSLStatus().isOk();
	}

	public static SLStatus addEndpoint(EmberManager em,int endpoint,int profileid,
			int deviceid,int device_ver,List<Integer> in_clusters,List<Integer> out_clusters)
		throws Exception
	{
		AddEndpointCmd cmd = new AddEndpointCmd(em, endpoint, profileid,
				 deviceid, device_ver, in_clusters,out_clusters) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static SLStatus setMulticastTableEntry(EmberManager em,int idx,MulticastTableEntry entry)
		throws Exception
	{
		MulticastTableEntrySetCmd cmd = new MulticastTableEntrySetCmd(em, idx,entry) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static MulticastTableEntryGetRsp getMulticastTableEntry(EmberManager em,int idx)
			throws Exception
	{
		byte[] vs = new byte[] {(byte)idx} ;
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.GET_MULTICAST_TABLE_ENTRY ,vs) ;
		CommonRR<MulticastTableEntryGetRsp> rr = new CommonRR<>(em, cmd,MulticastTableEntryGetRsp.class) ;
			if (!em.RT_synReqRsp(rr, null))
				return null;
			return rr.getReturnRsp() ;
	}
}
