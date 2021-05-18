package org.iottree.core.basic;

import org.iottree.core.UAVal;

public abstract class MemSeg
{
	long idx = 0 ;
	
	int len = 1 ;
	
	public MemSeg(long idx,int len)
	{
		this.idx = idx ;
		this.len = len ;
	}
	
	public abstract int getBitLen();
	
		
	public long getStartIdx()
	{
		return idx ;
	}
	
	public int getLen()
	{
		return len ;
	}
	
	public abstract Object getBuf() ;
	
	public abstract void setValBlock(long idx,byte[] bsdata,int bsoffset,int bslen) ;
	
	public abstract void setValBool(long idx,int bitpos,boolean v);
	
	public abstract boolean getValBool(long idx,int bitpos);
	
	public abstract void setValNumber(UAVal.ValTP tp,long idx,Number v);
	
	public abstract Number getValNumber(UAVal.ValTP tp,long idx);
	
	public static MemSeg createInstance(long idx,int len,int bitlen)
	{
		switch(bitlen)
		{
		case 8:
			return new MemSeg8(idx,len) ;
		case 16:
			return new MemSeg16(idx,len) ;
		}
		return null ;
	}
	
	public String toString()
	{
		return "["+this.idx+","+this.len+"]";
	}
}
