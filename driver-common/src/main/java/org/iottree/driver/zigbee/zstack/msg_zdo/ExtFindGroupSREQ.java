package org.iottree.driver.zigbee.zstack.msg_zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.util.LatencyReq;
import org.iottree.driver.zigbee.zstack.MsgData;

/**
 * This command handles the ZDO extension find group message
 * 
 * @author jason.zhu
 */
public class ExtFindGroupSREQ extends MsgData
{
	public ExtFindGroupSREQ()
	{
		super(0x25,0x4A) ;
	}
	
	/**
	 * 
	 */
	public ExtFindGroupSREQ(int endpoint,int group_id)
	{
		super(0x25,0x4A) ;
		
		this.data = new byte[3] ;
		this.data[0] = (byte)endpoint ;
		DataUtil.setUInt16(group_id, this.data, 1);
	}
	
	@Override
	public String getTpName()
	{
		return "ZDO_EXT_FIND_GROUP";
	}
	
	@Override
	public int getDataLenMin()
	{
		return 3;
	}
	
	public int getEndpoint()
	{
		return this.data[0] & 0xFF ;
	}
	
	public int getGroupId()
	{
		return DataUtil.getUInt16(this.data, 1) ;
	}
	
	@Override
	public String toString()
	{
		String ret = super.toString() ;
		return ret + " endpoint="+this.getEndpoint()+" groupid="+this.getGroupId();
	}

}
