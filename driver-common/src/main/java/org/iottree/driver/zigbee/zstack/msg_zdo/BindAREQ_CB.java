package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

/**
 * This callback message is in response to the ZDO Bind Request.
 * 
 * @author jason.zhu
 */
public class BindAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x45 ;
	public static final int CMD1 = 0xA1 ;
	
	
	public BindAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_BIND_RSP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 3;
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

	/**
	 * The message’s source network address
	 * @return
	 */
	public int getSrcAddr()
	{
		return DataUtil.getUInt16(this.data, 0) ; //0-1
	}
	
	//This field indicates either SUCCESS or FAILURE.
	public boolean getStatus()
	{//Status is either Success (0) or Failure (1).
		return this.data[2]==0 ;
	}
	
	public int getStatusVal()
	{
		return this.data[2] & 0xFF;
	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.put("src_addr", this.getSrcAddr());
	}
}
