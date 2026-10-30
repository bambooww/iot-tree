package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.util.StartupStatus;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class StartupFromAppSRSP extends MsgRecv
{
	public static final int CMD0 = 0x65 ;
	public static final int CMD1 = 0x40 ;
	
	public StartupFromAppSRSP()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_STARTUP_FROM_APP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 1;
	}

	@Override
	protected boolean checkCmd()
	{
		if(this.cmd0!=CMD0)
			return false;
		if(this.cmd1!=CMD1)
			return false;
		return true ;
	}

	public StartupStatus getStatus()
	{
		return new StartupStatus(this.data[0]&0xFF) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		StartupStatus ss = getStatus() ;
		JSONObject r = new JSONObject() ;
		if(ss==null)
			return r ;
		return r.put("status",ss.getStatus()).put("status_ok", ss.isStatusOk()).putOpt("status_t", ss.getStatusT()) ;
	}
	
//	@Override
//	public String toString()
//	{
//		return super.toString()+" "+this.getStatus();
//	}
}
