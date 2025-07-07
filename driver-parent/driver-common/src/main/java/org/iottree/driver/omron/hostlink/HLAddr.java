package org.iottree.driver.omron.hostlink;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.driver.mitsubishi.fx.FxAddr;
import org.iottree.driver.mitsubishi.fx.FxAddrDef;
import org.iottree.driver.mitsubishi.fx.FxAddrSeg;
import org.iottree.driver.mitsubishi.fx.MCModel;

/**
 * like CIO00.00    W8
 * 
 * @author jason.zhu
 *
 */
public class HLAddr extends DevAddr implements Comparable<HLAddr>
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
	
	/**
	 * 0-15 , -1 is no bit
	 */
	protected int bitNum = -1 ;
	
	
	protected int digitNum = 3 ;
	
	protected boolean bWritable = false;
	
	HLModel fxModel ;
	
	transient HLAddrDef addrDef = null ;
	
	transient HLAddrSeg addrSeg = null ;
	
	public HLAddr()
	{}
	
	protected HLAddr(ValTP vtp,String prefix,int addr_num,int bit_num,int digit_num)
	{
		super(combineAddrStr(prefix,addr_num,bit_num,digit_num),vtp) ;
		//this.fxModel = fx_m ;
		this.prefix = prefix ;
		this.addrNum = addr_num ;
		this.bitNum = bit_num ;
		this.digitNum = digit_num ;
	}
	
	HLAddr asDef(HLAddrDef addr_def,HLAddrSeg seg)
	{
		this.addrDef = addr_def ;
		this.addrSeg = seg ;
		this.bWritable = this.addrSeg.isWritable() ;
		return this ;
	}
	
	public HLAddrDef getAddrDef()
	{
		return this.addrDef ;
	}
	
	public HLAddrSeg getAddrSeg()
	{
		return this.addrSeg ;
	}
	
	public boolean isWritable()
	{
		return this.bWritable ;
	}
//	
	
	public String getPrefix()
	{
		return prefix ;
	}
	
	public int getAddrNum()
	{
		return addrNum ;
	}
	
	public int getBitNum()
	{
		return this.bitNum ;
	}
	
	public boolean isBitVal()
	{
		if( this.bitNum>=0 )
			return true ;
		if(addrSeg.isValBitOnly())
			return true ;
		return false;
	}
	
	public int getDigitNum()
	{
		return this.digitNum ;
	}
	
	public void setWritable(boolean bw)
	{
		this.bWritable = bw ;
	}
	
	public static String combineAddrStr(String prefix,int addr_num,int bit_num,int digit_num)
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(prefix) ;
		String nstr = Integer.toString(addr_num) ;
			
		int dn = digit_num - nstr.length() ;
		if(dn>0)
		{
			for(int i = 0 ; i < dn ; i ++)
				sb.append('0') ;
		}
		sb.append(nstr) ;
		if(bit_num>=0)
		{
			if(bit_num<9)
				sb.append(".0"+bit_num) ;
			else
				sb.append("."+bit_num) ;
		}
		return sb.toString() ;
		
		//return prefix.toUpperCase()+addr_num+(bit_num>=0?"."+bit_num:"") ;
	}
	
	@Override
	public String toCheckAdjStr()
	{
		return combineAddrStr(this.prefix,this.addrNum,this.bitNum,this.digitNum) ;
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
		HLModel fx_m = (HLModel)dev.getDrvDevModel() ;
		if(fx_m==null)
			throw new IllegalArgumentException("no HLModel") ;
		List<String> ss = splitPrefixNum(str,failedr) ;
		if(ss==null)
			return null ;
		String prefix = ss.get(0) ;
		String addr = ss.get(1) ;
		int k = addr.indexOf(".") ;
		String bitstr = null ;
		if(k>0)
		{
			bitstr = addr.substring(k+1) ;
			addr = addr.substring(0,k) ;
		}
		return fx_m.transAddr(prefix, addr,bitstr, vtp, failedr) ;
	}
	
	@Override
	public ChkRes checkAddr(UADev dev,String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;

		List<String> ss = splitPrefixNum(addr, failedr) ;
		if(ss==null)
			return new ChkRes(-1,addr,vtp,"Invalid HLAddr="+addr);
		HLModel fxm = (HLModel)dev.getDrvDevModel() ;
		
		HLAddrDef addrdef = fxm.getAddrDef(ss.get(0)) ;
		if(addrdef==null)
		{
			return new ChkRes(-1,addr,vtp,"Invalid HLAddr no address def found");
		}
		if(vtp==null)
		{
			return new ChkRes(-1,addr,vtp,"Invalid HLAddr no ValTP found");
		}
		HLAddrSeg seg = addrdef.findSeg(vtp,ss.get(1)) ;
		if(seg==null)
		{
			return new ChkRes(-1,addr,vtp,"HLAddr ["+ss.get(0)+ss.get(1)+"] is invalid or not match "+vtp.getStr());
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
		HLModel fx_m = (HLModel)dev.getDrvDevModel() ;
		if(fx_m==null)
			return null ;
		StringBuilder failedr = new StringBuilder() ;
		List<String> ss = splitPrefixNum(str,failedr) ;
		if(ss==null)
			return null ;
		String prefix = ss.get(0) ;
		String addr = ss.get(1) ;
		int k = addr.indexOf(".") ;
		String bitstr = null ;
		
		if(k>0)
		{
			bitstr = addr.substring(k+1) ;
			addr = addr.substring(0,k) ;
			//if(vtp==null)
			vtp = ValTP.vt_bool ;
		}
		else
		{
			if(vtp==null)
				vtp = ValTP.vt_uint16 ;
			else if(!vtp.isNumberVT())
				vtp = ValTP.vt_uint16 ;
		}
		return fx_m.transAddr(prefix, addr,bitstr, vtp, failedr) ;
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
	public int compareTo(HLAddr o)
	{
		return this.addrNum-o.addrNum;
	}
}
