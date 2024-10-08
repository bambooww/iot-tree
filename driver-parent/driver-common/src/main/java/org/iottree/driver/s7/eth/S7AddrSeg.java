package org.iottree.driver.s7.eth;

import org.iottree.core.DevAddr.IAddrDefSeg;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

public class S7AddrSeg implements IAddrDefSeg
{
	//String title ;
	
	int valStart = 0 ;
	
	int valEnd = -1 ;
	
	/**
	 * 地址位数
	 */
	int digitNum = -1 ;
	
	boolean bValBit = false;
	
	boolean bAddrStepInt32 = false;
	
	UAVal.ValTP[] valTPs = null ;
	
	boolean bHex = false; // hex or dec
	
	boolean bWrite = true ;
	
	boolean bBitPos = false;
	
	transient S7AddrDef belongTo ;
	
	S7AddrSeg(int valstart,int valend,int digit_num,UAVal.ValTP[] tps,boolean b_write)
	{
		//this.title = title ;
		this.valStart = valstart ;
		this.valEnd = valend ;
		this.digitNum = digit_num ;
		this.valTPs = tps ;
		this.bWrite = b_write ;
		//this.bBitPos = b_bitpos ;
	}
	
	S7AddrSeg(int valstart,int valend,int digit_num,UAVal.ValTP[] tps)
	{
		//this.title = title ;
		this.valStart = valstart ;
		this.valEnd = valend ;
		this.digitNum = digit_num ;
		this.valTPs = tps ;
		//this.bBitPos = b_bitpos ;
	}
	
	public S7AddrSeg asHex(boolean b)
	{
		this.bHex = b ;
		return this ;
	}
	
	public S7AddrSeg asValBit(boolean b)
	{
		this.bValBit = b ;
		return this ;
	}
	
	public S7AddrSeg asAddrStepInt32(boolean b)
	{
		this.bAddrStepInt32 = b ;
		return this ;
	}

	public S7AddrSeg asBitPos(boolean b)
	{
		this.bBitPos = b ;
		return this ;
	}
	
	
	public int getValStart()
	{
		return valStart ;
	}
	
	public int getValEnd()
	{
		return valEnd ;
	}
	
	public boolean isValBit()
	{
		return this.bValBit ;
	}
	
	public boolean isWritable()
	{
		return this.bWrite ;
	}
	
	public int getDigitNum()
	{
		return digitNum ;
	}
	
	public boolean canBitPos()
	{
		return this.bBitPos ;
	}
	
	public UAVal.ValTP[] getValTPs()
	{
		return this.valTPs ;
	}
	
	public boolean matchValTP(ValTP vtp)
	{
		for(ValTP vt:this.valTPs)
		{
			if(vt==vtp)
				return true ;
		}
		return false ;
	}
	
//	public boolean matchVal(int val)
//	{
//		return val>=valStart && val<=valEnd ;
//	}
	
	public Integer matchAddr(String addr_num)
	{
		int addrn ;
		if(this.bHex)
			addrn = Integer.parseInt(addr_num, 16) ;
		else
			addrn = Integer.parseInt(addr_num) ;
		if(addrn>=this.valStart && addrn<=this.valEnd)
			return addrn ;
		else
			return null ;
	}
	
	public boolean matchAddr(S7Addr addr)
	{
		int addrn = addr.getOffsetBytes();//.getAddrIdx() ;
		return addrn>=this.valStart && addrn<=this.valEnd;
	}

	@Override
	public String getRangeFrom()
	{
		if(bHex)
			return this.belongTo.prefix+Convert.toIntDigitsStr(valStart,digitNum,16) ;
		else
			return this.belongTo.prefix+Convert.toIntDigitsStr(valStart,digitNum) ;
	}

	@Override
	public String getRangeTo()
	{
		if(bHex)
			return this.belongTo.prefix+Convert.toIntDigitsStr(valEnd,digitNum,16);
		else
			return this.belongTo.prefix+Convert.toIntDigitsStr(valEnd,digitNum) ;
	}

	@Override
	public String getSample()
	{
		return "";
	}
}
