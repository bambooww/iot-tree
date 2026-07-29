package org.iottree.driver.mitsubishi.fx;

import org.iottree.core.DevAddr.IAddrDefSeg;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

public class FxAddrSeg implements IAddrDefSeg
{
	String title ;
	
	int valStart = 0 ;
	
	int valEnd = -1 ;
	
	/**
	 * 地址位数
	 */
	int digitNum = -1 ;
	
	boolean bValBit = false;
	
	boolean bAddrStepInt32 = false;
	
	UAVal.ValTP[] valTPs = null ;
	
	boolean bOctal = false;
	
	boolean bWrite = true ;
	
	int baseAddr ;
	
	/**
	 * mem_addr = baseAddr+valStart-baseValStart ;
	 */
	int baseValStart = 0 ;
	
	int baseAddrForceOnOff = -1 ;
	
	int extBaseValStart = -1 ;
	
	int extBaseAddrForceOnOff = -1 ;
	
	transient FxAddrDef belongTo ;
	
	FxAddrSeg(int baseaddr,String title,int valstart,int valend,int digit_num,UAVal.ValTP[] tps,boolean b_write)
	{
		this.baseAddr = baseaddr ;
		this.title = title ;
		this.valStart = valstart ;
		this.valEnd = valend ;
		this.digitNum = digit_num ;
		this.valTPs = tps ;
		this.bWrite = b_write ;
	}
	
	public FxAddrSeg asBaseValStart(int v)
	{
		this.baseValStart = v ;
		return this ;
	}
	
	public FxAddrSeg EXT_asBaseValStart(int v)
	{
		this.extBaseValStart = v ;
		return this ;
	}
	
	public FxAddrSeg EXT_asBaseAddrForceOnOff(int v)
	{
		this.extBaseAddrForceOnOff = v ;
		return this ;
	}
	
	public boolean isExtCmd()
	{
		return this.extBaseValStart>=0 || this.extBaseAddrForceOnOff >=0 ;
	}
	
	public FxAddrSeg asBaseAddrForceOnOff(int addr)
	{
		this.baseAddrForceOnOff = addr ;
		return this ;
	}
	
	public FxAddrSeg asOctal(boolean b)
	{
		this.bOctal = b ;
		return this ;
	}
	
	public FxAddrSeg asValBit(boolean b)
	{
		this.bValBit = b ;
		return this ;
	}
	
	public FxAddrSeg asAddrStepInt32(boolean b)
	{
		this.bAddrStepInt32 = b ;
		return this ;
	}
	
//	public AddrSeg asWritable(boolean b)
//	{
//		this.bWrite = b ;
//		return this ;
//	}
	
	public String getTitle()
	{
		return this.title ;
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
	
	public int getBaseAddr()
	{
		return this.baseAddr ;
	}
	
	public int calBytesInBase(int val)
	{
		if(this.bValBit)
			return (val-this.baseValStart)/8 ;//+ val%8>0?1:0 ;
		else if(this.bAddrStepInt32)
			return (val-this.baseValStart)*4 ;
		else
			return (val-this.baseValStart)*2 ;
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
		if(this.bOctal)
			addrn = Integer.parseInt(addr_num, 8) ;
		else
			addrn = Integer.parseInt(addr_num) ;
		if(addrn>=this.valStart && addrn<=this.valEnd)
			return addrn ;
		else
			return null ;
	}
	
	public boolean matchAddr(FxAddr addr)
	{
		int addrn = addr.getAddrNum() ;
		return addrn>=this.valStart && addrn<=this.valEnd;
	}

	@Override
	public String getRangeFrom()
	{
		if(bOctal)
			return this.belongTo.prefix+Convert.toIntDigitsStr(valStart,digitNum,8) ;
		else
			return this.belongTo.prefix+Convert.toIntDigitsStr(valStart,digitNum) ;
	}

	@Override
	public String getRangeTo()
	{
		if(bOctal)
			return this.belongTo.prefix+Convert.toIntDigitsStr(valEnd,digitNum,8) +"(Octal)";
		else
			return this.belongTo.prefix+Convert.toIntDigitsStr(valEnd,digitNum) ;
	}

	@Override
	public String getSample()
	{
		return "";
	}
}
