package org.iottree.driver.zigbee.zstack.msg_af;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.json.JSONObject;

public class DataConfirmAREQ_CB extends MsgRecv
{
	public static final int CMD0 = 0x44 ;
	public static final int CMD1 = 0x80 ;
	
	public DataConfirmAREQ_CB()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "AF_DATA_CONFIRM";
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
	
	public boolean getStatus()
	{
		return this.data[0]==0;
	}
	
	public int getStatusVal()
	{
		return this.data[0] & 0xFF;
	}
	
	/**
	 * Endpoint of the device
	 * @return
	 */
	public int getEndpoint()
	{
		return this.data[1] & 0xFF;
	}

	/**
	 * Specified the transaction sequence number of the message
	 * @return
	 */
	public int getTransId()
	{
		return this.data[2] & 0xFF;
	}
	
//	@Override
//	public String toString()
//	{
//		String ret = super.toString() ;
//		return ret + " status="+this.getStatus()+" ep="+this.getEndpoint()
//			+" transid="+this.getTransId();
//	}
	
	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("status",this.getStatusVal()).put("status_ok", this.getStatus())
				.putOpt("endpoint", this.getEndpoint())
				.put("trans_seq", this.getTransId());
	}
}
