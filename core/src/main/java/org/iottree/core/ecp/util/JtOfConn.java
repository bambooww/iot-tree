package org.iottree.core.ecp.util;

import org.iottree.core.ecp.ECPConn;

public class JtOfConn extends Jt
{
	ECPConn conn ;
	
	PtId midPtId ;
	
	public JtOfConn(ECPConn conn,PtId mid_ptid)
	{
		super(JT_TP_CONN+":"+conn.getId()+":"+mid_ptid.id);
		this.conn = conn ;
		this.midPtId = mid_ptid ;
	}

	public ECPConn getConn()
	{
		return this.conn ;
	}
	
	public PtId getMidPtId()
	{
		return this.midPtId ;
	}
	
}
