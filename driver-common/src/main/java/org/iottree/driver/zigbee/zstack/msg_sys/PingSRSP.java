package org.iottree.driver.zigbee.zstack.msg_sys;

import org.iottree.driver.zigbee.zspec.util.Capabilities;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * Length = 0x02 Cmd0 = 0x61 Cmd1 = 0x01 Capabilities
 * @author jason.zhu
 *
 */
public class PingSRSP extends MsgRecv
{
	public static final int CMD0 = 0x61 ;
	public static final int CMD1 = 0x01 ;
	
	//int capability = 0 ;
	
	public PingSRSP()
	{
		super(CMD0 ,CMD1) ;
	}
	
	@Override
	public String getTpName()
	{
		return "SYS_PING";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 2;
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

	
	public Capabilities getCapability()
	{
		return new Capabilities((data[0] & 0xFF)+(data[1]<<8 & 0xFF00)) ;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		Capabilities cap = getCapability() ;
		return new JSONObject().put("cap_v",cap.getCapVal()).put("caps", cap.getOptionsList());
	}

//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " = ["+this.getCapability()+"]";
//	}

	

}
