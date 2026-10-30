package org.iottree.driver.zigbee.ember.util;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.ember.ASHFrmData;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EZSPWaiter;
import org.iottree.driver.zigbee.ember.ezsp.MsgIncomeHandlerCB;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.IncomeMsgTp;
import org.iottree.driver.zigbee.ember.types.RxPacketInfo;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zcl.ZCLFrmClusterCmd;
import org.iottree.driver.zigbee.zcl.ZCLManager;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;

public class EZSPWaiterZCL extends EZSPWaiter
{
	static ILogger log = LoggerManager.getLogger(EZSPWaiterZCL.class) ;
	
	int tar_nwk_addr ;
	ApsFrame apsf ;
	int cmd_resp_id ;
	
	int zclSequence = -1 ;
	private ZCLFrame resp_frm = null;
	
	public EZSPWaiterZCL(int tar_nwk_addr, ApsFrame apsf,int cmd_resp_id,long to_ms)
	{
		super(to_ms);
		this.tar_nwk_addr=  tar_nwk_addr ;
		this.apsf = apsf ;
		this.cmd_resp_id = cmd_resp_id ;
	}
	
	public EZSPWaiterZCL asZclSequence(int zcl_seq)
	{
		this.zclSequence = zcl_seq ;
		return this;
	}

	@Override
	public boolean RT_chkRsp(ASHFrmData fd,EZSPRsp rsp)
	{
		if(!(rsp instanceof MsgIncomeHandlerCB))
			return false;
		
		MsgIncomeHandlerCB cb = (MsgIncomeHandlerCB)rsp ;
		IncomeMsgTp msgtp = cb.getIncomeMsgTp() ;
		RxPacketInfo rx_pk_inf = cb.getRxPacketInfo() ;
		
		if(this.tar_nwk_addr>=0 && rx_pk_inf.senderShortAddr!=this.tar_nwk_addr)
			return false;
		ApsFrame apsf = cb.getApsFrame() ;
		byte[] pld = cb.getMsgPayload() ;
		if (apsf.profileId == ZSpecConsts.HA_PROFILE_ID || apsf.profileId == ZSpecConsts.WILDCARD_PROFILE_ID
				|| (apsf.profileId == ZSpecConsts.GP_PROFILE_ID && msgtp != IncomeMsgTp.BROADCAST_LOOPBACK) ||
				// Shelly custom Clusters require a special profile ID
				apsf.profileId == ZSpecConsts.CUSTOM_SHELLY_PROFILE_ID ||
				// Control4 custom Clusters require a special profile ID
						apsf.profileId == ZSpecConsts.CUSTOM_CONTROL4_PROFILE_ID)
		{
			ZCLFrame zcl_f = null;
			if(pld!=null)
			{
				StringBuilder failedr = new StringBuilder() ;
				zcl_f = ZCLManager.unpackedFrame(pld, failedr) ;
			}
			if(zcl_f==null)
			{
				if(log.isWarnEnabled())
					log.warn("ZCLManager.unpackedFrame failed with pld="+Convert.byteArray2HexStr(pld,"-"));
			}
			if(zcl_f.getCmdID()!=this.cmd_resp_id)
				return false;
//			if(!(zcl_f instanceof ZCLFrmClusterCmd))
//			{
//				if(log.isWarnEnabled())
//					log.warn("ZCLManager.unpackedFrame not ZCLFrmClusterCmd="+zcl_f);
//			}
//			ZCLFrmClusterCmd fcc = (ZCLFrmClusterCmd)zcl_f ;
//			int cmdid = fcc.getCmdID() ;
//			if(cmdid!=this.cmd_resp_id)
//				return false;

			this.resp_frm = zcl_f ;
			return true ;
		}
		
		return false;
	}

	public ZCLFrame getRspZCLFrame()
	{
		return this.resp_frm ;
	}
}
