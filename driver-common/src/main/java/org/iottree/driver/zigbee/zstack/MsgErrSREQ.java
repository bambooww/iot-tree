package org.iottree.driver.zigbee.zstack;

import org.json.JSONObject;

/**
 * When an SREQ command from the Host Processor is not recognized by the ZNP,
 *  an error SRSP is returned
 *  
 *  @author jason.zhu
 *
 */
public class MsgErrSREQ extends MsgRecv
{
	public static final int CMD0 = 0x60 ;
	public static final int CMD1 = 0x00 ;
	
	public static final int ERR_CODE_INVALID_SUBSYSTEM = 0x01 ;
	public static final int ERR_CODE_INVALID_CMD_ID = 0x02 ;
	public static final int ERR_CODE_INVALID_PARAM = 0x03 ;
	public static final int ERR_CODE_INVALID_LENGTH = 0x04 ;
	
	public MsgErrSREQ()
	{
		this.cmd0 = CMD0 ;
		this.cmd1 = CMD1 ;
	}
	
	@Override
	public String getTpName()
	{
		return "CMD_ERR";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 3;
	}

	/**
	 * 0x01 0x02 0x03 0x04
	 * @return
	 */
	public int getErrCode()
	{
		return data[0] & 0xFF ;
	}
	
	public int getReqCmd0()
	{
		return data[1] & 0xFF ;
	}
	
	public int getReqCmd1()
	{
		return data[2] & 0xFF ;
	}

	@Override
	public JSONObject toRecvJO()
	{
		return new JSONObject().put("err_code",this.getErrCode())
				.putOpt("req_cmd0", this.getReqCmd0())
				.put("req_cmd1", this.getReqCmd1());
	}
}
