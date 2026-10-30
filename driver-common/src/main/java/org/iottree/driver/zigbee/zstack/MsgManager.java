package org.iottree.driver.zigbee.zstack;

import java.util.HashMap;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zstack.msg_af.*;
import org.iottree.driver.zigbee.zstack.msg_appcnf.*;
import org.iottree.driver.zigbee.zstack.msg_sys.*;
import org.iottree.driver.zigbee.zstack.msg_zdo.*;
import org.iottree.driver.zigbee.zstack.msg_util.*;


public class MsgManager
{
	protected static ILogger log = LoggerManager.getLogger(MsgManager.class);
	
	private static HashMap<Integer,Msg> CMD2MSG = new HashMap<>() ;
	private static void reg_msg(Msg mr)
	{
		int cmd0 = mr.getCmd0() ;
		int cmd1 = mr.getCmd1() ;
		int cmd = (cmd0<<8)+cmd1 ;
		CMD2MSG.put(cmd,mr) ;
	}
	
	static
	{
		reg_msg(new MsgErrSREQ()) ;
		
		//SYS
		reg_msg(new PingSREQ()) ;
		reg_msg(new PingSRSP()) ;
		reg_msg(new ResetAREQ()) ;
		reg_msg(new ResetIndAREQ_CB()) ;
		reg_msg(new VersionSREQ()) ;
		reg_msg(new VersionSRSP()) ;
		reg_msg(new OsalNVItemInitSREQ()) ;
		reg_msg(new OsalNVItemInitSRSP()) ;
		reg_msg(new OsalNVLengthSREQ()) ;
		reg_msg(new OsalNVLengthSRSP()) ;
		reg_msg(new OsalNVReadSREQ()) ;
		reg_msg(new OsalNVReadSRSP()) ;
		reg_msg(new OsalNVReadExtSREQ()) ;
		reg_msg(new OsalNVReadExtSRSP()) ;
		reg_msg(new OsalNVWriteExtSREQ()) ;
		reg_msg(new OsalNVWriteExtSRSP()) ;
		
		//ZDO
		reg_msg(new MsgCBRegisterSREQ()) ;
		reg_msg(new MsgCBRegisterSRSP()) ;
		reg_msg(new NwkAddrSREQ()) ;
		reg_msg(new NwkAddrSRSP()) ;
		reg_msg(new NwkAddrRspAREQ_CB()) ;
		reg_msg(new IEEEAddrSREQ()) ;
		reg_msg(new IEEEAddrSRSP()) ;
		reg_msg(new IEEEAddrRspAREQ_CB()) ;
		reg_msg(new SimpleDescSREQ()) ;
		reg_msg(new SimpleDescSRSP()) ;
		reg_msg(new SimpleDescRspAREQ_CB()) ;
		reg_msg(new MgmtPermitJoinSREQ()) ;
		reg_msg(new MgmtPermitJoinSRSP()) ;
		reg_msg(new MgmtPermitJoinRspAREQ_CB()) ;
		reg_msg(new MgmtLeaveSREQ()) ;
		reg_msg(new MgmtLeaveSRSP()) ;
		reg_msg(new MgmtLeaveRspAREQ_CB()) ;
		reg_msg(new MgmtLQITableRspAREQ()) ;
		reg_msg(new EndDeviceAnnceIndSREQ_CB()) ;
		reg_msg(new NodeDescSREQ()) ;
		reg_msg(new NodeDescSRSP()) ;
		reg_msg(new NodeDescRspAREQ_CB()) ;
		
		reg_msg(new PowerDescSREQ()) ;
		reg_msg(new PowerDescSRSP()) ;
		reg_msg(new PowerDescRspAREQ_CB()) ;
		
		reg_msg(new BindSREQ()) ;
		reg_msg(new BindSRSP()) ;
		reg_msg(new BindAREQ_CB()) ;
		
		reg_msg(new ActiveEPRspAREQ_CB()) ;
		reg_msg(new ActiveEPSREQ()) ;
		reg_msg(new ActiveEPSRSP()) ;
		reg_msg(new MgmtLQISRSP()) ;
		reg_msg(new LeaveIndAREQ()) ;
		reg_msg(new TCDevIndAREQ_CB()) ;
		reg_msg(new StateChangeIndAREQ_CB()) ;
		reg_msg(new StartupFromAppSREQ()) ;
		reg_msg(new StartupFromAppSRSP()) ;
		reg_msg(new ExtFindGroupSREQ()) ;
		reg_msg(new ExtFindGroupSRSP()) ;
		reg_msg(new ExtRouterDiscSREQ()) ;
		reg_msg(new ExtRouterDiscSRSP()) ;
		reg_msg(new ExtNwkInfoSREQ()) ;
		reg_msg(new ExtNwkInfoSRSP()) ;
		reg_msg(new SrcRtgIndAREQ_CB()) ;
		
		//AF
		reg_msg(new IncomingMsgAREQ_CB()) ;
		reg_msg(new DataConfirmAREQ_CB()) ;
		reg_msg(new RegisterSREQ()) ;
		reg_msg(new RegisterSRSP()) ;
		reg_msg(new DataRequestExtSREQ()) ;
		reg_msg(new DataRequestExtSRSP()) ;
		reg_msg(new DataRequestSREQ()) ;
		reg_msg(new DataRequestSRSP()) ;
		
		//UTIL
		reg_msg(new GetDeviceInfoSREQ()) ;
		reg_msg(new GetDeviceInfoSRSP()) ;
		
		//APPCNF
		reg_msg(new BDBCommNotifAREQ_CB()) ;
	}
	
	static Msg createNewIns(int cmd0,int cmd1)
	{
		int cmd = (cmd0<<8)+cmd1 ;
		Msg mr = CMD2MSG.get(cmd) ;
		if(mr==null)
			return null ;
		try
		{
			return mr.getClass().getConstructor().newInstance() ;
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.debug(ee);
			return null ;
		}
	}
	
	/**
	 * state machine to read z-stack frame
	 * 
	 * this func must be called in loop
	 * 
	 * @param inputs
	 * @return
	 */
	public static Msg unpackedMsg(byte[] bs,StringBuilder failedr) throws Exception
	{
		int cmd0 = bs[2] & 0xFF;
		int cmd1 = bs[3] & 0xFF;
		Msg ret = createNewIns(cmd0,cmd1) ;
		if(ret==null)
		{
			failedr.append("no response instance created") ;
			return null ;
		}
		if(!ret.unpack(bs, failedr))
			return null;
		return ret ;
	}
}
