package org.iottree.driver.zigbee.zstack;

import org.iottree.driver.zigbee.zspec.ZDOClusterId;

public class MsgDataReqZDO extends MsgData
{
	ZDOClusterId zdoClusterId ;
	
	public MsgDataReqZDO(ZDOClusterId cid,byte[] pld)
	{
		super(CmdTp.SREQ,SubSys.ZDO,cid.getIntVal()) ;
		this.zdoClusterId = cid ;
		this.data = pld ;
	}
	
	public ZDOClusterId getZDOClusterId()
	{
		return this.zdoClusterId ;
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
