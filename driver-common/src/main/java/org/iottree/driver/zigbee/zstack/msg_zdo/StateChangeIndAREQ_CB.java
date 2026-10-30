package org.iottree.driver.zigbee.zstack.msg_zdo;

import org.iottree.driver.zigbee.zspec.util.DeviceState;
import org.iottree.driver.zigbee.zspec.util.StartupStatus;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class StateChangeIndAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xC0 ;
	
	public StateChangeIndAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_STATE_CHANGE_IND";
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
	
	
	public DeviceState getDeviceState()
	{
		return new DeviceState(this.data[0] & 0xFF) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		DeviceState ss = getDeviceState() ;
		JSONObject r = new JSONObject() ;
		if(ss==null)
			return r ;
		return r.put("device_state",ss.getStateV()).put("coor_started", ss.isZigbeeCoorStarted())
				.putOpt("state_t", ss.getStateT()) ;
	}
	
	
//	public String toString()
//	{
//		return super.toString() +" State="+this.getDeviceState() ;
//	}
}
