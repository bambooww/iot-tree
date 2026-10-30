package org.iottree.driver.zigbee.zspec;

import org.iottree.driver.zigbee.zspec.event.PermitJoinEvt;
import org.iottree.driver.zigbee.zspec.types.ZSStartResult;

public class ZSController
{
	public static interface IEvent {}
	
	public static final String PERMIT_JOIN = "permit_join" ; //evt_v = uint8 duration
	public static final String STACK_STATUS = "stack_status" ; //evt_v = uint8 status value 0=ok
	public static final String MSG_SENT = "msg_sent" ; //evt_v = ???
	public static final String ZDO_RSP = "zdo_rsp" ;
	public static final String INCOME_MSG = "income_msg" ;
	public static final String ZCL_PLD = "zcl_pld" ;
	public static final String TOUCHLINK_MSG = "touchlink_msg" ;
	public static final String TRUST_CENTER_JOIN = "trust_center_join" ;
	
	ZSCoor owner ;
	
	ZSSettings settings ;
	
	private PermitJoinEvt permitJoinEvt = null ;
	
	ZSController(ZSCoor owner,ZSSettings settings)
	{
		this.owner = owner ;
		this.settings = settings ;
	}
	
//	public ZSStartResult RT_startCtrl()
//	{
//		
//	}
	
	/**
	 * 获取当前PermitJoin剩余启用时间
	 * @return <=0表示没有时间
	 */
	public int RT_getPermitJoinLeftSec()
	{
		if(permitJoinEvt==null)
			return 0 ;
		return this.permitJoinEvt.getPermitJoinLeftSec() ;
	}
	
//	public void permitJoin(int seconds,Device device)
//	{
//        clearTimeout(this.permitJoinTimer);
//        this.permitJoinTimer = undefined;
//        this.permitJoinEnd = undefined;
//
//        if (time > 0) {
//            // never permit more than uint8, and never permit 255 that is often equal to "forever"
//            assert(time <= 254, "Cannot permit join for more than 254 seconds.");
//
//            await this.adapter.permitJoin(time, device?.networkAddress);
//            await this.#greenPower.permitJoin(time, device?.networkAddress);
//
//            const timeMs = time * 1000;
//            this.permitJoinEnd = Date.now() + timeMs;
//            this.permitJoinTimer = setTimeout((): void => {
//                this.emit("permitJoinChanged", {permitted: false});
//
//                this.permitJoinTimer = undefined;
//                this.permitJoinEnd = undefined;
//            }, timeMs);
//
//            this.emit("permitJoinChanged", {permitted: true, time});
//        } else {
//            logger.debug("Disable joining", NS);
//
//            await this.#greenPower.permitJoin(0);
//            await this.adapter.permitJoin(0);
//
//            this.emit("permitJoinChanged", {permitted: false});
//        }
//    }
	
	/**
	 * 统一事件回调
	 * @param evt_n
	 * @param evt_v
	 */
	void RT_onEventCB(String evt_n,IEvent evt_v)
	{
		switch(evt_n)
		{
		case PERMIT_JOIN:
			permitJoinEvt = (PermitJoinEvt)evt_v ;
			return ;
		}
	}
	
}
