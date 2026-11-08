package org.iottree.driver.zigbee.zstack;

public class MsgDataReq extends MsgData
{
	public MsgDataReq(Msg.SubSys subsys,int cmd1,byte[] req_pld)
	{
		super(CmdTp.SREQ,subsys,cmd1) ;
		this.data = req_pld ;
	}
	
	
	@Override
	public int getDataLenMin()
	{
		return 0;
	}

	@Override
	public String getTpName()
	{
		return null;
	}

}
