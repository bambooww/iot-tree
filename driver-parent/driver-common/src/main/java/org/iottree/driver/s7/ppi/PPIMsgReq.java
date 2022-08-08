package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;

public abstract class PPIMsgReq extends PPIMsg
{

	short da; //destination address  1byte
	
	short sa=0; //soruce address  1byte
	
	PPIMemTp memTp ;
	
	
	
	public abstract short getFC() ;
	

	public PPIMsgReq withSorAddr(short sa)
	{
		this.sa = sa ;
		return this ;
	}
	
	public PPIMsgReq withDestAddr(short da)
	{
		this.da = da ;
		return this ;
	}
	
	//public abstract int getOffsetBytes();

	public abstract int getRetOffsetBytes() ;
}
