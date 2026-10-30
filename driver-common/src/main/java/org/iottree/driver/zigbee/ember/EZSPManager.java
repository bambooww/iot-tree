package org.iottree.driver.zigbee.ember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.ember.ezsp.*;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.util.ICopyable;

public class EZSPManager
{
	private static HashMap<Integer,EZSPCmd> FRMID2CMD = new HashMap<>() ;
	private static HashMap<Integer,EZSPRsp> FRMID2RSP = new HashMap<>() ;
	
	private static void register_frm(EZSPCmd cmd,EZSPRsp rsp)
	{
		if(cmd!=null)
			FRMID2CMD.put(cmd.getFrmID(),cmd) ;
		if(rsp!=null)
			FRMID2RSP.put(rsp.getFrmID(),rsp) ;
	}
	
	static
	{
		// config
		register_frm(new VersionCmd(null),new VersionRsp(null)) ;
		register_frm(new NopCmd(null),new NopRsp(null)) ;
		register_frm(new EchoCmd(null),new EchoRsp(null)) ;
		register_frm(new GetConfigValCmd(null),new GetConfigValRsp(null)) ;
		register_frm(new SetConfigValCmd(null),new SetConfigValRsp(null)) ;
		register_frm(new GetLibStatusCmd(null),new GetLibStatusRsp(null)) ;
		
		register_frm(new GetValueCmd(null),new GetValueRsp(null)) ;
		register_frm(new GetExtValueCmd(null),new GetExtValueRsp(null)) ;
		register_frm(new AttrReadCmd(null),new AttrReadRsp(null)) ;
		register_frm(new SetPolicyCmd(null),new SLStatus_Rsp(null,SetPolicyCmd.FRAME_ID,SetPolicyCmd.FRAME_NAME)) ;
		register_frm(new SetValueCmd(null),new SLStatus_Rsp(null,SetValueCmd.FRAME_ID,SetValueCmd.FRAME_NAME)) ;
		register_frm(new AddEndpointCmd(null),new SLStatus_Rsp(null,AddEndpointCmd.FRAME_ID,AddEndpointCmd.FRAME_NAME)) ;
		register_frm(new SetFacCodeCmd(null),new SLStatus_Rsp(null,SetFacCodeCmd.FRAME_ID,SetFacCodeCmd.FRAME_NAME)) ;
		register_frm(new MulticastTableEntrySetCmd(null),new SLStatus_Rsp(null,MulticastTableEntrySetCmd.FRAME_ID,MulticastTableEntrySetCmd.FRAME_NAME)) ;
		register_frm(null,new MulticastTableEntryGetRsp(null)) ;
		
		//util
		register_frm(new GetEui64Cmd(null),new GetEui64Rsp(null)) ;
		register_frm(new GetNodeIdCmd(null),new GetNodeIdRsp(null)) ;
		register_frm(null,new ReadClearCountersRsp(null)) ;
		
		
		//network
		register_frm(new NwkInitCmd(null),new SLStatus_Rsp(null,NwkInitCmd.FRAME_ID,NwkInitCmd.FRAME_NAME)) ;
		register_frm(new NwkStateCmd(null),new NwkStateRsp(null)) ;
		register_frm(null,new StackStatusHandlerRspCB(null)) ;
		register_frm(new NwkGetParamsCmd(null),new NwkGetParamsRsp(null)) ;
		register_frm(new SetConcentratorCmd(null),new SLStatus_Rsp(null,SetConcentratorCmd.FRAME_ID,SetConcentratorCmd.FRAME_NAME)) ;
		
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.FORM_NETWORK.getIntVal(),"formNetwork")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.JOIN_NETWORK.getIntVal(),"joinNetwork")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.JOIN_NETWORK_DIRECTLY.getIntVal(),"joinNetworkDirectly")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.START_SCAN.getIntVal(),"startScan")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.STOP_SCAN.getIntVal(),"stopScan")) ;
		
		register_frm(new NwkLeaveCmd(null),new SLStatus_Rsp(null,NwkLeaveCmd.FRAME_ID,NwkLeaveCmd.FRAME_NAME)) ;
		register_frm(new NwkFindAndRejoinCmd(null),new SLStatus_Rsp(null,NwkFindAndRejoinCmd.FRAME_ID,NwkFindAndRejoinCmd.FRAME_NAME)) ;
		register_frm(new NwkPermitJoinCmd(null),new SLStatus_Rsp(null,NwkPermitJoinCmd.FRAME_ID,NwkPermitJoinCmd.FRAME_NAME)) ;
		register_frm(null,new NwkGetRadioChRsp(null)) ;
		
		//security
		register_frm(new SecManExportKeyCmd(null),new SecManExportKeyRsp(null)) ;
		register_frm(null,new GetNwkKeyInfRsp(null)) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.CLEAR_KEY_TABLE.getIntVal(),"clearKeyTable")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.ERASE_KEY_TABLE_ENTRY.getIntVal(),"eraseKeyTableEntry")) ;
		register_frm(null,new SLStatus_Rsp(null,EzspFrameID.SET_INITIAL_SECURITY_STATE.getIntVal(),"setInitialSecurityState")) ;
		register_frm(new SecManImpTransientKeyCmd(null),new SLStatus_Rsp(null,SecManImpTransientKeyCmd.FRAME_ID,SecManImpTransientKeyCmd.FRAME_NAME)) ;
		
		register_frm(null,new TCPostJoinHandlerRspCB(null)) ;
		
		register_frm(new Common_Cmd(null,EzspFrameID.CLEAR_TRANSIENT_LINK_KEYS,null),null) ;
		
		
		//messaging frames
		register_frm(new SetSrcRouteDiscModeCmd(null),new SetSrcRouteDiscModeRsp(null)) ;
		register_frm(null,new MsgSentHandlerRspCB(null)) ;
		register_frm(null,new MsgIncomeHandlerCB(null)) ;
		
		register_frm(new SendBroadcastCmd(null),new SendRsp(null,EzspFrameID.SEND_BROADCAST)) ;
		register_frm(new SendUnicastCmd(null),new SendRsp(null,EzspFrameID.SEND_UNICAST)) ;
		register_frm(new SendMulticastCmd(null),new SendRsp(null,EzspFrameID.SEND_MULTICAST)) ;
	}
	
	public static EZSPCmd createCmdByFrmID(EmberManager emgr,int frm_id)
	{
		EZSPCmd pm = FRMID2CMD.get(frm_id) ;
		if(pm==null)
			return null ;
		try
		{
			if(pm instanceof ICopyable<?>)
			{
				return (EZSPCmd)((ICopyable<?>) pm).copyMe(emgr) ;
			}
			return pm.getClass().getConstructor(EmberManager.class).newInstance(emgr) ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	
	public static EZSPRsp createRspByFrmID(EmberManager emgr,int frm_id)
	{
		EZSPRsp pm = FRMID2RSP.get(frm_id) ;
		if(pm==null)
			return null ;
		try
		{
			if(pm instanceof ICopyable<?>)
			{
				return (EZSPRsp)((ICopyable<?>) pm).copyMe(emgr) ;
			}
			return pm.getClass().getConstructor(EmberManager.class).newInstance(emgr) ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null ;
		}
	}
	
	public static List<EZSPCmd> listAllEZSPCmds()
	{
		ArrayList<EZSPCmd> rr = new ArrayList<>() ;
		rr.addAll(FRMID2CMD.values()) ;
		Collections.sort(rr, new Comparator<EZSPCmd>() {

			@Override
			public int compare(EZSPCmd o1, EZSPCmd o2)
			{
				return o1.getFrmID()-o2.getFrmID();
			}});
		return rr ;
	}
	
	
	
}
