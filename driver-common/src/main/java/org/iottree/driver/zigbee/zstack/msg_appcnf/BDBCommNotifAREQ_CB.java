package org.iottree.driver.zigbee.zstack.msg_appcnf;

import org.iottree.driver.zigbee.zspec.util.BDBCommMode;
import org.iottree.driver.zigbee.zspec.util.BDBCommRemainModes;
import org.iottree.driver.zigbee.zspec.util.BDBCommStatus;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class BDBCommNotifAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x4F ;
	public static final int CMD1 = 0x80 ;
	
	public BDBCommNotifAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "APP_CNF_BDB_COMMISSIONING_NOTIFICATION";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 3;
	}
	
	public BDBCommStatus getStatus()
	{
		return new BDBCommStatus(this.data[0]&0xFF);
	}
	
	public BDBCommMode getMode()
	{
		return new BDBCommMode(this.data[1]&0xFF);
	}
	
	public BDBCommRemainModes getRemainModes()
	{
		return new BDBCommRemainModes(this.data[2]&0xFF);
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		BDBCommStatus st = getStatus();
		
		return new JSONObject().put("status",st.toString()).put("status_ok", st.isStatusOk())
				.put("mode",this.getMode()).put("remain_mode", getRemainModes());
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+getStatus()+" mode="+this.getMode()+" remain_modes="+this.getRemainModes();
//	}

}
