package org.iottree.core.basic;

import org.iottree.core.UAVal;

public abstract class MemSeg
{
	long idx = 0 ;
	
	int len = 1 ;
	
	ByteOrder byteOrd = ByteOrder.LittleEndian;
	
	public MemSeg(long idx,int len)
	{
		this.idx = idx ;
		this.len = len ;
	}
	
	public ByteOrder getByteOrder()
	{
		return byteOrd ;
	}
	
	public void setByteOrder(ByteOrder bo)
	{
		this.byteOrd = bo ;
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
	
	public abstract void setValNumber(UAVal.ValTP tp,long idx,Number v,ByteOrder bo);
	
	public abstract Number getValNumber(UAVal.ValTP tp,long idx,ByteOrder bo);
	
	public abstract byte[] getBytes(long idx,int len);//(long idx,int len) ;
	
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
