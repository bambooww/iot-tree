package org.iottree.core.ecp.util;

import org.iottree.core.ecp.ECPInOut;

public class JtOfInOut extends Jt
{
	ECPInOut io ;
	
	public JtOfInOut(ECPInOut io)
	{
		super(JT_TP_IO+":"+io.getUID());
		this.io = io ;
	}

//	@Override
//	public Point getXY()
//	{
//		throw new RuntimeException("no impl") ;
//	}

	public ECPInOut getIO()
	{
		return this.io ;
	}
}
