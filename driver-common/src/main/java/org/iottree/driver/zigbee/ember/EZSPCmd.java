package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.ember.util.EZSPSleepMode;

/**
 * command or request
 * 
 * @author jason.zhu
 */
public abstract class EZSPCmd extends EZSPFrame
{
	public EZSPCmd(EmberManager emgr)
	{
		super(emgr) ;
	}

	/**
	 * 
	 * @return
	 */
	public EZSPSleepMode CMD_getSleepMode()
	{
		return new EZSPSleepMode(this.frmCtrlLow & 0b11) ;
	}
	
//	@Override
//	protected boolean unpackParams(byte[] bs, int offset, int len)
//	{
//		EZSPParams r = EZSPParams.createCmdByFrmID(this.getID()) ;
//		if(r==null)
//			return ;
//		if(!r.unpackParams(bs, offset, len))
//			return ;
//		this.params = r ;
//	}
	
	public String toString()
	{
		String r ="CMD*"+ super.toString() ;
		r += ";sleep_m:"+this.CMD_getSleepMode();
		return r ;
	}

	
}
