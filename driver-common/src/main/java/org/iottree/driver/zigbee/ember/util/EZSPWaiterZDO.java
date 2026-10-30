package org.iottree.driver.zigbee.ember.util;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.ember.ASHFrmData;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EZSPWaiter;
import org.iottree.driver.zigbee.ember.ezsp.MsgIncomeHandlerCB;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOConsts;
import org.iottree.driver.zigbee.zspec.ZDOResp;

public class EZSPWaiterZDO extends EZSPWaiter
{
	static ILogger log = LoggerManager.getLogger(EZSPWaiterZDO.class) ;
	
	ZDOClusterId resp_cid ;
	
	boolean hasZdoMessageOverhead ;
	
	MsgIncomeHandlerCB incomeMsg  = null;
	
	private ZDOResp zdoResp = null ;
	
	public EZSPWaiterZDO(ZDOClusterId resp_cid,boolean has_zdo_msg_overhead,long to_ms)
	{
		super(to_ms);
		this.resp_cid = resp_cid ;
		this.hasZdoMessageOverhead = has_zdo_msg_overhead ;
	}
	
	public ZDOClusterId getRespClusterId()
	{
		return this.resp_cid ;
	}
	
	public ZDOResp getZDOResp()
	{
		return this.zdoResp ;
	}

	@Override
	public boolean RT_chkRsp(ASHFrmData fd,EZSPRsp rsp)
	{
		if(!(rsp instanceof MsgIncomeHandlerCB))
			return false;
		
		MsgIncomeHandlerCB cb = (MsgIncomeHandlerCB)rsp ;
		ApsFrame apsf = cb.getApsFrame() ;
		byte[] pld = cb.getMsgPayload() ;
		if (apsf.profileId == ZDOConsts.ZDO_PROFILE_ID)
		{
			incomeMsg = cb ;
			if(apsf.clusterId!=resp_cid.getIntVal())
				return false;
			this.zdoResp = ZDOResp.parseResp(hasZdoMessageOverhead,resp_cid,pld) ;
			if(this.zdoResp==null)
			{
				if(log.isWarnEnabled())
					log.warn("unparsed ZDOCluster with id="+resp_cid.toHexStr());
				return false;
			}
			
			//this.emit(ZSController.ZDO_RSP, rspevt);
			return true ;
		}
		
		return false;
	}

	
}
