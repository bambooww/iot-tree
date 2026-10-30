package org.iottree.driver.zigbee.zspec.event;

import org.iottree.driver.zigbee.zspec.*;

public class PermitJoinEvt implements ZSController.IEvent
{
	private int permit_join_dur = -1 ;
	private long permit_join_sdt = -1 ;
	
	public PermitJoinEvt(int dur)
	{
		this.permit_join_dur = dur ;
		this.permit_join_sdt = System.currentTimeMillis() ;
	}
	
	public int getDuration()
	{
		return this.permit_join_dur ;
	}
	
	public long getStartDT()
	{
		return this.permit_join_sdt ;
	}
	
	/**
	 * 获取当前PermitJoin剩余启用时间
	 * @return <=0表示没有时间
	 */
	public int getPermitJoinLeftSec()
	{
		if(permit_join_dur<=0)
			return 0 ;//no permit join
		long left = permit_join_dur - (System.currentTimeMillis()-permit_join_sdt) ;
		if(left<=0)
			return 0 ;
		return (int)left ;
	}
}
