package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;

public abstract class PPIMsgReq extends PPIMsg
{

	short da; //destination address  1byte
	
	short sa=0; //soruce address  1byte
	
	PPIMemTp memTp ;
	
	int offsetBytes = 0 ;
	
	int inBit = -1 ;//-1 is null
	
	//PPIMemValTp memValTp = null ;
	
	int bytesNum = 1 ;
	
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
	
	public int getOffsetBytes()
	{
		return this.offsetBytes ;
	}
	
	public int getInBits()
	{
		return this.inBit ;
	}

	public int getOffsetBits()
	{
		return this.offsetBytes*8 + (this.inBit>=0?this.inBit:0) ;
	}
	
	public boolean isBitReq()
	{
		return this.inBit>=0;
	}
	
	public int getBytesNum()
	{
		return this.bytesNum ;
	}

	public PPIMsgReq withAddr(String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		PPIAddr paddr = PPIAddr.parsePPIAddr(addr,vtp,failedr) ;
		if(paddr!=null)
		{
			this.memTp = paddr.getMemTp() ;
			this.offsetBytes = paddr.getOffsetBytes() ;
			this.inBit = paddr.getInBits() ;
			this.bytesNum = paddr.getBytesNum() ;
		}
		return this;
	}
	
	
	
	
	public PPIMsgReq withAddrByte(PPIMemTp mtp, int byteoffsets,int inbit,int bytesnum)
	{
		this.memTp = mtp ;
		this.offsetBytes = byteoffsets;
		this.inBit = inbit ;
		this.bytesNum = bytesnum ;
		return this;
	}
}
