package org.iottree.driver.omron.hostlink;

import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

public class HLAddrSeg
{
	String title ;
	
	int valStart = 0 ;
	
	int valEnd = -1 ;
	
	/**
	 * 地址位数
	 */
	int digitNum = -1 ;
	
	/**
	 * 是否有16位bit使用值——true支持 xxx.00 位引用
	 */
	boolean bHasBit = false;
	
	UAVal.ValTP[] valTPs = null ;
	
	boolean bWrite = true ;
	
	
	transient HLAddrDef belongTo ;
	
	HLAddrSeg(String title,int valstart,int valend,int digit_num,UAVal.ValTP[] tps,boolean b_write,boolean b_hasbit)
	{
		this.title = title ;
		this.valStart = valstart ;
		this.valEnd = valend ;
		this.digitNum = digit_num ;
		this.valTPs = tps ;
		this.bWrite = b_write ;
		this.bHasBit = b_hasbit ;
	}
	
	
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
	
	public boolean isHasBit()
	{
		return this.bHasBit ;
	}
	
	public boolean isWritable()
	{
		return this.bWrite ;
	}
	
	public int getDigitNum()
	{
		return digitNum ;
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
	
	public boolean matchAddr(int addr_num,int bit_num)
	{
		try
		{
			if(bit_num>=0)
			{
				if(!bHasBit)
					return false;
				//int bitp = Integer.parseInt(bit_str) ;
				if(bit_num<0||bit_num>15)
					return false;
			}
			
			//int addrn = Integer.parseInt(addr_num) ;
			if(addr_num>=this.valStart && addr_num<=this.valEnd)
				return true ;
			else
				return false ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return false;
		}
	}
	
	public boolean matchAddr(HLAddr addr)
	{
		int addrn = addr.getAddrNum() ;
		return addrn>=this.valStart && addrn<=this.valEnd;
	}
}
