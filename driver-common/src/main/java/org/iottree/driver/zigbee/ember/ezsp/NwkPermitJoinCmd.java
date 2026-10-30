package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Tells the stack to allow other nodes to join the network with this node as their parent.
 *  Joining is initially disabled by default.
 *  
 * @author jason.zhu
 */
public class NwkPermitJoinCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0022 ;
	public static final String FRAME_NAME = "permitJoining" ;

	/**
	 *  A value of 0x00 disables joining. A value of 0xFF enables joining. Any other value enables joining 
	 *  for that number of seconds.
	 */
	int duration ; //uint8
	
	
	public NwkPermitJoinCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public NwkPermitJoinCmd(EmberManager emgr,int duration)
	{
		super(emgr) ;
		this.duration = duration ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME;
	}
	
	public int getDuration()
	{
		return this.duration ;
	}

	@Override
	protected int getParamsLen()
	{
		return 1;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		bs[offset] = (byte)this.duration;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		this.duration = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("duration", this.duration) ;
	}
}
