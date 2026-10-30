package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.NwkFindAndRejoinCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkGetParamsCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkGetParamsRsp;
import org.iottree.driver.zigbee.ember.ezsp.NwkGetRadioChRsp;
import org.iottree.driver.zigbee.ember.ezsp.NwkInitCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkLeaveCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkPermitJoinCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkStateCmd;
import org.iottree.driver.zigbee.ember.ezsp.NwkStateRsp;
import org.iottree.driver.zigbee.ember.ezsp.SetConcentratorCmd;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.ezsp.GetEui64Cmd;
import org.iottree.driver.zigbee.ember.ezsp.GetEui64Rsp;
import org.iottree.driver.zigbee.ember.types.EmberInitSecState;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.EzspValueId;
import org.iottree.driver.zigbee.ember.types.NwkParams;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class NwkRR
{
	/**
	 * 
	 * @param em
	 * @param nwk_init_bitm NwkInitBitmask | NwkInitBitmask
	 * @return
	 * @throws Exception
	 */
	public static SLStatus nwkInit(EmberManager em,int nwk_init_bitm)
			throws Exception
	{
		NwkInitCmd cmd = new NwkInitCmd(em, nwk_init_bitm) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static NwkGetParamsRsp getNwkParams(EmberManager em) throws Exception
	{
		NwkGetParamsRR gvrr = new NwkGetParamsRR(em) ;
		if(!em.RT_synReqRsp(gvrr, null))
			return null ;
		return gvrr.getNwkGetParamsRsp() ;
	}
	
	public static SLStatus nwkLeave(EmberManager em)
			throws Exception
	{
		NwkLeaveCmd cmd = new NwkLeaveCmd(em) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static SLStatus findAndRejoinNwk(EmberManager em,boolean have_cur_nwk_key,long ch_mask) throws Exception
	{
		NwkFindAndRejoinCmd cmd = new NwkFindAndRejoinCmd(em,have_cur_nwk_key,ch_mask);
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static SLStatus permitJoining(EmberManager em,int duration) throws Exception
	{
		NwkPermitJoinCmd cmd = new NwkPermitJoinCmd(em,duration);
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	/**
	 * Sets the radio output power at which a node is operating. Ember radios have discrete power settings. 
	 * For a list of available power settings, see the technical specification for the RF communication module 
	 * in your Developer Kit. Note: Care should be taken when using this API on a running network, 
	 * as it will directly impact the established link qualities neighboring nodes have with the node on which it is called. 
	 * This can lead to disruption of existing routes and erratic network behavior.
	 * @param em
	 * @param power int8
	 * @return
	 * @throws Exception
	 */
	public static SLStatus setRadioPower(EmberManager em,int power) throws Exception
	{
		byte[] bs = new byte[] {(byte)power} ;
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.SET_RADIO_POWER,bs) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	/**
	 * Sets the channel to use for sending and receiving messages. For a list of available radio channels, 
	 * see the technical specification for the RF communication module in your Developer Kit. 
	 * Note: Care should be taken when using this API, as all devices on a network must use the same channel.
	 * 
	 * @param em
	 * @param channel
	 * @return
	 * @throws Exception
	 */
	public static SLStatus setRadioChannel(EmberManager em,int channel) throws Exception
	{
		byte[] bs = new byte[] {(byte)channel} ;
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.SET_RADIO_CHANNEL,bs) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	/**
	 * 
	 * @param em
	 * @return -1 is err
	 * @throws Exception
	 */
	public static int getRadioChannel(EmberManager em) throws Exception
	{
		Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.SET_RADIO_CHANNEL,null) ;
		CommonRR<NwkGetRadioChRsp> rr = new CommonRR<>(em, cmd,NwkGetRadioChRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return -1;
		return rr.getReturnRsp().getChannel() ;
	}
	
	/**
	 * Returns the EUI64 ID of the local node.
	 * @param em
	 * @return ieee_addr
	 * @throws Exception
	 */
	public static String getEui64(EmberManager em) throws Exception
	{
		GetEui64Cmd cmd = new GetEui64Cmd(em) ;
		CommonRR<GetEui64Rsp> rr = new CommonRR<>(em, cmd,GetEui64Rsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp().getIEEEAddr() ;
	}
	
	public static SLStatus setConcentrator(EmberManager em,boolean on,int concentrator_tp,int min_time,int max_time,
			int router_err_thre,int delivery_fail_thre,int max_hops) throws Exception
	{
		SetConcentratorCmd cmd = new SetConcentratorCmd(em,on,concentrator_tp,min_time,max_time,
				router_err_thre,delivery_fail_thre,max_hops);
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
	}
	
	public static NwkStateRsp ezspNwkStatus(EmberManager em) throws Exception
	{
		NwkStateCmd cmd = new NwkStateCmd(em) ;
		CommonRR<NwkStateRsp> rr = new CommonRR<>(em, cmd,NwkStateRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp() ;
	}
	
	/**
     * Forms a new network by becoming the coordinator.
     * @param parameters EmberNetworkParameters * Specification of the new network.
	 * @throws Exception 
     * @returns An SLStatus value indicating success or the reason for failure.
     */
	public static SLStatus formNetwork(EmberManager em,NwkParams params) throws Exception
	{
		EZSPBuffer buf = EZSPBuffer.alloc(NwkParams.STRUCT_LEN) ;
    	buf.writeEmberNwkParams(params);
    	Common_Cmd cmd = new Common_Cmd(em,EzspFrameID.FORM_NETWORK,buf.getBuffer()) ;
		SetCommonRR rr = new SetCommonRR(em, cmd) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.slstRsp.getSLStatus();
    }
	
	/**
     * Wrapper for `ezspSetValue`.
     *
     * Set NWK layer outgoing frame counter (intended for device restoration purposes).
     * Caveats:
     *   - Can only be called before NetworkInit / FormNetwork / JoinNetwork, when sl_zigbee_network_state()==SL_ZIGBEE_NO_NETWORK.
     *   - This function should be called before ::sl_zigbee_set_initial_security_state, and the SL_ZIGBEE_NO_FRAME_COUNTER_RESET
     *     bitmask should be added to the initial security bitmask when ::emberSetInitialSecurityState is called.
     *   - If used in multi-network context, be sure to call ::sl_zigbee_set_current_network() prior to calling this function.
     *
     * @param desiredValue The desired outgoing NWK frame counter value.
     *        This should needs to be less than MAX_INT32U_VALUE to ensure that rollover does not occur on the next encrypted transmission.
	 * @throws Exception 
     * @returns
     * - SL_STATUS_OK if calling context is valid (sl_zigbee_network_state() == SL_ZIGBEE_NO_NETWORK) and desiredValue < MAX_INT32U_VALUE.
     * - SL_STATUS_INVALID_STATE.
     */
    public static SLStatus ezspSetNWKFrameCounter(EmberManager em,int frameCounter) throws Exception
    {
    	byte[] vs = new byte[4] ;
    	DataUtil.setUInt32(frameCounter, vs, 0);
    	return SetValueRR.setValue(em, EzspValueId.NWK_FRAME_COUNTER, vs) ;
//        return await this.ezspSetValue(, 4, [
//            frameCounter & 0xff,
//            (frameCounter >> 8) & 0xff,
//            (frameCounter >> 16) & 0xff,
//            (frameCounter >> 24) & 0xff,
//        ]);
    }
}

class NwkGetParamsRR extends EZSPReqRsp
{
	NwkGetParamsCmd gvCmd = null ;
	NwkGetParamsRsp gvRsp = null ;
	
	public NwkGetParamsRR(EmberManager em)
	{
		super(em) ;
		
		gvCmd = new NwkGetParamsCmd(em) ;
		this.asReq(gvCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof NwkGetParamsRsp)
		{
			this.gvRsp = (NwkGetParamsRsp)m_recv ;
			return true ;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

	public NwkGetParamsRsp getNwkGetParamsRsp()
	{
		return this.gvRsp ;
	}
	
	public static NwkGetParamsRsp getNwkParams(EmberManager em) throws Exception
	{
		NwkGetParamsRR gvrr = new NwkGetParamsRR(em) ;
		if(!em.RT_synReqRsp(gvrr, null))
			return null ;
		return gvrr.getNwkGetParamsRsp() ;
	}
	
}

