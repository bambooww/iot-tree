package org.iottree.driver.mitsubishi;


import java.util.Arrays;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.DevAddr.ChkRes;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.driver.mitsubishi.fx.FxAddrDef;
import org.iottree.driver.mitsubishi.fx.FxAddrSeg;
import org.iottree.driver.mitsubishi.fx.MCModel;

public abstract class Addr extends DevAddr implements Comparable<Addr>
{
	private static List<String> splitPrefixNum(String str,StringBuilder failedr)
	{
		if(Convert.isNullOrTrimEmpty(str))
		{
			failedr.append("addr cannot be empty") ;
			return null ;
		}
		str = str.toUpperCase().trim() ;
		int n = str.length() ;
		char c = str.charAt(0) ;
		int i =0 ;
		if(!(c>='A'&&c<='Z'))
		{
			failedr.append("addr no prefix") ;
			return null ;
		}
		
		String prefix =null ;
		for(i = 1 ; i <n ; i ++)
		{
			c = str.charAt(i) ;
			if(!(c>='A'&&c<='Z'))
			{
				prefix = str.substring(0,i) ;
				String num = str.substring(i) ;
				if(Convert.isNullOrEmpty(num))
				{
					failedr.append("addr no number") ;
					return null ;
				}
				
				return Arrays.asList(prefix,num) ;
			}
		}
		failedr.append("addr no number") ;
		return null ;
	}
	
	protected String prefix = null ;
	
	protected int addrNum = -1 ;
	
	protected int digitNum = 3 ;
	
	protected boolean bOct = false;
	
	protected boolean bValBit = false;
	
	protected boolean bWritable = false;
	
	public Addr()
	{}
	
	protected Addr(String addr_str,ValTP vtp,String prefix,int addr_num,boolean b_valbit,int digit_num,boolean b_oct)
	{
		super(addr_str,vtp) ;
		//this.fxModel = fx_m ;
		this.prefix = prefix ;
		this.addrNum = addr_num ;
		this.bValBit = b_valbit ;
		this.digitNum = digit_num ;
		this.bOct = b_oct ;
	}
	
	public String getPrefix()
	{
		return prefix ;
	}
	
	public int getAddrNum()
	{
		return addrNum ;
	}
	
	public boolean isValBit()
	{
		return this.bValBit ;
	}
	
	public int getDigitNum()
	{
		return this.digitNum ;
	}
	
	public boolean isOctal()
	{
		return bOct ;
	}
	
	public abstract int getBytesInBase();
	
	public void setWritable(boolean bw)
	{
		this.bWritable = bw ;
	}
	
	public int getInBits()
	{
		return this.addrNum%8 ;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(this.prefix) ;
		String nstr = null ;
		if(bOct)
			nstr = Integer.toOctalString(addrNum) ;
		else
			nstr = Integer.toString(addrNum) ;
			
		int dn = digitNum - nstr.length() ;
		if(dn>0)
		{
			for(int i = 0 ; i < dn ; i ++)
				sb.append('0') ;
		}
		sb.append(nstr) ;
		return sb.toString() ;
	}
	
	public String toString()
	{
		return toCheckAdjStr() ;
	}
	
	@Override
	public DevAddr parseAddr(UADev dev,String str, ValTP vtp, StringBuilder failedr)
	{
		if(dev==null)
			throw new IllegalArgumentException("no UADev") ;
		MCModel fx_m = (MCModel)dev.getDrvDevModel() ;
		if(fx_m==null)
			throw new IllegalArgumentException("no FxModel") ;
		List<String> ss = splitPrefixNum(str,failedr) ;
		if(ss==null)
			return null ;
		String prefix = ss.get(0) ;
		return fx_m.transAddr(prefix, ss.get(1), vtp, failedr) ;
	}
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;

		List<String> ss = splitPrefixNum(addr, failedr) ;
		if(ss==null)
			return new ChkRes(-1,addr,vtp,"Invalid FxAddr="+addr);
		MCModel fxm = (MCModel)dev.getDrvDevModel() ;
		
		FxAddrDef addrdef = fxm.getAddrDef(ss.get(0)) ;
		if(addrdef==null)
		{
			return new ChkRes(-1,addr,vtp,"Invalid FxAddr no address def found");
		}
		FxAddrSeg seg = addrdef.findSeg(vtp,ss.get(1)) ;
		if(seg==null)
		{
			return new ChkRes(-1,addr,vtp,"FxAddr ["+ss.get(0)+ss.get(1)+"] is invalid or not match "+vtp.getStr());
		}
		
		//if(vtp!=ValTP.vt_bool)
		return CHK_RES_OK;
	}
	
	
	@Override
	public boolean isSupportGuessAddr()
	{
		return true;
	}
	
	

	@Override
	public DevAddr guessAddr(UADev dev,String str,ValTP vtp)
	{
		if(dev==null)
			return null ;
		MCModel fx_m = (MCModel)dev.getDrvDevModel() ;
		if(fx_m==null)
			return null ;
		StringBuilder failedr = new StringBuilder() ;
		List<String> ss = splitPrefixNum(str,failedr) ;
		if(ss==null)
			return null ;
		String prefix = ss.get(0) ;
		
		return fx_m.transAddr(prefix, ss.get(1), vtp, failedr) ;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}

	@Override
	public ValTP[] getSupportValTPs()
	{
		return null;
	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return bWritable;
	}

	@Override
	public int compareTo(Addr o)
	{
		return this.addrNum-o.addrNum;
	}
}
