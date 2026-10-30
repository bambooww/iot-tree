package org.iottree.driver.zigbee.zspec.zdo;

import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.zdo_def.TlvTag;

public class PermitJoiningReq extends ZDOCluster
{
	int duration;
	
	int authentication ;
	
	List<TlvTag> tlv_tags ;
	
	public PermitJoiningReq() {}
	
	public PermitJoiningReq(int duration,int authentication,List<TlvTag> tts)
	{
		this.duration = duration ;
		this.authentication = authentication ;
		this.tlv_tags = tts ;
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.PERMIT_JOINING_REQUEST;
	}

	
	@Override
	public void packTo(ZDOBuffer buf)
	{
		buf.writeUInt8(this.duration);
		buf.writeUInt8(this.authentication) ;
		buf.writeGlobalTLVs(tlv_tags);
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		this.duration = buf.readUInt16() ;
		this.authentication = buf.readUInt8() ;
		this.tlv_tags  =buf.readTLVs(null, false) ;
	}

}
